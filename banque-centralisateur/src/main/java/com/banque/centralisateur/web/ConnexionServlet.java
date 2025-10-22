package com.banque.centralisateur.web;

import com.banque.courant.remote.UtilisateurRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.banque.courant.dao.*;
import com.banque.courant.dto.*;
import com.banque.courant.ejb.*;
import com.banque.courant.entity.*;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.remote.TransactionRemote;
import com.banque.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.*;
import com.banque.pret.remote.PretRemote;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB
    private OperationDAO operationDAO;
    @EJB
    private PretDAO pretDAO;
    @EJB
    private BanqueDAO banqueDAO;
    @EJB
    private TransactionDAO transactionDAO;

    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-centralisateur-1.0-SNAPSHOT/OperationServiceEJB!com.banque.courant.remote.OperationRemote")
    private OperationRemote operationService;
    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-centralisateur-1.0-SNAPSHOT/PretServiceEJB!com.banque.pret.remote.PretRemote")
    private PretRemote pretService;
    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-centralisateur-1.0-SNAPSHOT/TransactionServiceEJB!com.banque.courant.remote.TransactionRemote")
    private TransactionRemote transactionService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<CompteCourant> comptes = compteCourantDAO.findAll();
        req.setAttribute("comptes", comptes);
        req.getRequestDispatcher("/connexion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "register":
                handleRegister(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UtilisateurDTO utilisateurConnecte = null;
        UtilisateurRemote utilisateurRemote = null;

        UtilisateurDTO utilisateur = null;
        List<DirectionDTO> directions = null;
        List<ActionRoleDTO> actionRoles = null;

        if (session != null) {
            Object o = session.getAttribute("user");
            if (o instanceof UtilisateurDTO) {
                utilisateurConnecte = (UtilisateurDTO) o;
                utilisateurRemote = (UtilisateurRemote) session.getAttribute("sessionUtilisateur");

                utilisateur = utilisateurRemote.getUtilisateurConnecte();
                directions = utilisateurRemote.getDirections();
                actionRoles = utilisateurRemote.getActionRoles();
                
            }
        }

        request.setAttribute("utilateur", utilisateur);
        request.setAttribute("directions", directions);
        request.setAttribute("actionRoles", actionRoles);

        // TRAITEMENT DU LOGIN CLIENT (toujours exécuté)
        String numero = request.getParameter("numero");
        CompteCourant compte = compteCourantDAO.findByNumero(numero);

        if (compte == null) {
            request.setAttribute("error", "Compte introuvable !");
            request.getRequestDispatcher("/connexion.jsp").forward(request, response);
            return;
        }

        // Récupérer les données du client
        double solde = operationService.getSoldeActuel(compte.getId());
        List<OperationCourant> operations = operationDAO.findByCompte(compte.getId());
        List<Pret> prets = pretDAO.findByCompte(compte.getId());
        PretStatut pretUnique = pretService.getPretsImpayesByCompte(compte.getId());
        List<PretStatut> pretStatuts = pretService.getPretsImpayesListByCompte(compte.getId());

        addPretDetailsToRequest(request, pretStatuts);

        Pret pretImpaye = null;
        List<Remboursement> remboursements = null;
        double resteAPayer = 0.0;

        if (pretUnique != null) {
            pretImpaye = pretDAO.findById(pretUnique.getPret().getId());
            remboursements = pretDAO.getRemboursementByPret(pretImpaye.getId());
            resteAPayer = pretService.resteAPaye(pretImpaye.getId());
        }

        List<Transaction> transactionsSender = transactionDAO.findBySender(compte.getId());
        List<Transaction> transactionsReceiver = transactionDAO.findByReceiver(compte.getId());

        // AJOUTER LES DONNÉES CLIENT À LA REQUÊTE
        request.setAttribute("sender", transactionsSender);
        request.setAttribute("receiver", transactionsReceiver);
        request.setAttribute("solde", solde);
        request.setAttribute("compte", compte);
        request.setAttribute("operationsCourant", operations);
        request.setAttribute("prets", prets);
        request.setAttribute("pretImpaye", pretImpaye);
        request.setAttribute("remboursements", remboursements);
        request.setAttribute("resteAPaye", resteAPayer);
        request.setAttribute("message", "Connexion réussie");
        request.setAttribute("estClient", true);

        request.getRequestDispatcher("/client.jsp").forward(request, response);
    }

    private void addPretDetailsToRequest(HttpServletRequest request, List<PretStatut> pretStatuts) {
        if (pretStatuts == null || pretStatuts.isEmpty())
            return;

        request.setAttribute("pretStatus", pretStatuts);

        for (PretStatut pretStatut : pretStatuts) {
            Pret pret = pretDAO.findById(pretStatut.getPret().getId());
            List<Remboursement> remboursements = pretDAO.getRemboursementByPret(pret.getId());
            double resteAPayer = pretService.resteAPaye(pret.getId());

            request.setAttribute("pretImpaye_" + pretStatut.getId(), pret);
            request.setAttribute("remboursements_" + pretStatut.getId(), remboursements);
            request.setAttribute("resteAPaye_" + pretStatut.getId(), resteAPayer);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String adresse = request.getParameter("adresse");
        String dateStr = request.getParameter("date");
        String pwd = request.getParameter("pwd");

        if (nom == null || prenom == null || email == null || pwd == null) {
            request.setAttribute("error", "Tous les champs sont obligatoires !");
            request.getRequestDispatcher("/connexion.jsp").forward(request, response);
            return;
        }

        Date dateNaissance = Date.valueOf(dateStr);
        Client client = new Client(nom, prenom, email, adresse, dateNaissance);
        clientDAO.save(client);

        client = clientDAO.findByEmail(email);

        CompteCourant compte = new CompteCourant();
        compte.setClient(client);
        compte.setCode_secret(pwd);
        compte.setDateOuverture(Date.valueOf(LocalDate.now()));
        compte.setEtat("ouvert");
        compteCourantDAO.save(compte);

        List<CompteCourant> comptes = compteCourantDAO.findAll();
        request.setAttribute("comptes", comptes);
        request.setAttribute("message", "Inscription réussie, connectez-vous !");
        request.getRequestDispatcher("/connexion.jsp").forward(request, response);
    }
}