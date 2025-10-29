package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.banque.courant.entity.*;
import com.banque.courant.remote.ClientRemote;
import com.banque.courant.remote.CompteCourantRemote;
import com.banque.courant.remote.OperationRemote;
import com.banque.entity.*;
import com.banque.pret.entity.*;
import com.banque.pret.remote.PretRemote;
import com.banque.pret.remote.TypeStatutRemote;
import com.banque.centralisateur.model.CompteDepot;
import com.banque.centralisateur.service.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/banque")
public class BanqueServlet extends HttpServlet {

    @Inject private CompteDepotService compteDepotService;
    @Inject private OperationDepotService operationDepotService;
 
    @EJB 
    private TypeStatutRemote typeStatutService;
    @EJB 
    private ClientRemote clientService;
    @EJB
    private CompteCourantRemote compteCourantService;
    @EJB(lookup="java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/OperationServiceEJB!com.banque.courant.remote.OperationRemote") 
    private OperationRemote operationService;
    @EJB(lookup="java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-pret-1.0-SNAPSHOT/PretServiceEJB!com.banque.pret.remote.PretRemote")    
    private PretRemote pretService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        // 🔹 Si aucune action : afficher la liste des clients
        if (action == null) {
            List<Client> clientsList = clientService.all();
            req.setAttribute("clients", clientsList);
            req.getRequestDispatcher("/listeClient.jsp").forward(req, resp);
            return;
        }

        try {
            int pretId = Integer.parseInt(req.getParameter("pret"));
            int clientId = Integer.parseInt(req.getParameter("client"));

            switch (action.toLowerCase()) {
                case "valider": 
                    mettreAJourStatutPret(pretId, "En cours");
                    break;
                case "refuser": 
                    mettreAJourStatutPret(pretId, "Refuse");
                    break;
                default: {
                    req.setAttribute("error", "Action inconnue : " + action);
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
            }

            afficherDetailsClient(req, resp, clientId);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Paramètres invalides : " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int clientId = Integer.parseInt(req.getParameter("client"));
            afficherDetailsClient(req, resp, clientId);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Identifiant client invalide");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    /**
     * Met à jour le statut d’un prêt avec un type spécifique (En cours / Refusé)
     */
    private void mettreAJourStatutPret(int pretId, String typeStatut) {
        Pret pret = pretService.findPret(pretId);
        if (pret == null) return;

        TypesStatut type = typeStatutService.findByType(typeStatut);
        PretStatut pretStatut = new PretStatut(pret, type, Date.valueOf(LocalDate.now()));
        pretService.savePretStatut(pretStatut);
    }

    /**
     * Affiche les détails d’un client : comptes, prêts, soldes totaux
     */
    private void afficherDetailsClient(HttpServletRequest req, HttpServletResponse resp, int clientId)
            throws ServletException, IOException {

        Client client = clientService.find(clientId);
        if (client == null) {
            req.setAttribute("error", "Client introuvable");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }

        double soldeTotal = 0.0;
        double soldeCourant = 0.0;
        double resteAPayer = 0.0;
        double soldeDepot = 0.0;

        List<PretStatut> pretsImpayes = new ArrayList<>();

        // 🔹 Comptes courants
        List<CompteCourant> comptesCourants = compteCourantService.findByClient(client);
        for (CompteCourant compte : comptesCourants) {
            double soldeActuel = operationService.getSoldeActuel(compte.getId());
            soldeCourant += soldeActuel;
            soldeTotal += soldeActuel;

            PretStatut pretImpaye = pretService.getPretsImpayesByCompte(compte.getId());
            if (pretImpaye != null) {
                double reste = pretService.resteAPaye(pretImpaye.getPret().getId());
                resteAPayer += reste;
                soldeTotal += reste;
                pretsImpayes.add(pretImpaye);
            }
        }

        List<CompteDepot> comptesDepots = compteDepotService.getComptesByClient(clientId);
        for (CompteDepot depot : comptesDepots) {
            double soldeDepotActuel = operationDepotService.getSoldeByCompte(depot.getId());
            soldeDepot += soldeDepotActuel;
            soldeTotal += soldeDepotActuel;
        }

        List<PretStatut> tousLesPrets = pretService.pretsAvecStatutActuelByClient(clientId);

        req.setAttribute("client", client);
        req.setAttribute("compteCourants", comptesCourants);
        req.setAttribute("compteDepots", comptesDepots);
        req.setAttribute("prets", pretsImpayes);
        req.setAttribute("allPrets", tousLesPrets);
        req.setAttribute("soldeTotal", soldeTotal);
        req.setAttribute("soldeCourant", soldeCourant);
        req.setAttribute("soldePret", resteAPayer);
        req.setAttribute("soldeDepot", soldeDepot);

        req.getRequestDispatcher("/detailsClient.jsp").forward(req, resp);
    }
}
