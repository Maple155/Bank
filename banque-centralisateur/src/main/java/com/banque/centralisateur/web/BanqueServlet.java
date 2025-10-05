package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.dao.PretStatutDAO;
import com.banque.pret.dao.TypeStatutDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.PretStatut;
import com.banque.centralisateur.ejb.CompteDepotEJB;
import com.banque.centralisateur.ejb.OperationDepotEJB;
import com.banque.centralisateur.model.CompteDepot;
import com.banque.courant.dao.*;
import com.banque.courant.ejb.CompteCourantServiceEJB;
import com.banque.courant.ejb.OperationServiceEJB;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/banque")
public class BanqueServlet extends HttpServlet {
    @Inject
    private CompteDepotEJB compteDepotEJB;
    @Inject
    private OperationDepotEJB ODE;

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB 
    private CompteCourantServiceEJB compteEJB;
    @EJB 
    private OperationDAO operationDAO;
    @EJB 
    private OperationServiceEJB OSE;
    @EJB 
    private PretDAO pretDAO;
    @EJB
    private PretServiceEJB PSE;
    @EJB 
    private BanqueDAO banqueDAO;
    @EJB 
    private PretStatutDAO pretStatutDAO;
    @EJB
    private TypeStatutDAO typeStatutDAO;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null) {
            List<Client> clientsList = clientDAO.findAll();
            req.setAttribute("clients", clientsList);
            req.getRequestDispatcher("/listeClient.jsp").forward(req, resp);
            return;
        }

        int pretId = Integer.parseInt(req.getParameter("pret"));
        Pret currPret = pretDAO.findById(pretId);

        TypesStatut type = "valider".equalsIgnoreCase(action)
                ? typeStatutDAO.findByType("En cours")
                : typeStatutDAO.findByType("Refuse");

        PretStatut pretStatut = new PretStatut(currPret, type, Date.valueOf(LocalDate.now()));
        pretStatutDAO.save(pretStatut);

        int clientId = Integer.parseInt(req.getParameter("client"));
        afficherDetailsClient(req, resp, clientId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int clientId = Integer.valueOf(req.getParameter("client").toString());
        afficherDetailsClient(req, resp, clientId);        
    }

    private void afficherDetailsClient(HttpServletRequest req, HttpServletResponse resp, int clientId)
        throws ServletException, IOException {

        Client client = clientDAO.findById(clientId);
        double soldeTotal = 0.0;
        double soldeCourant = 0.0;
        double resteApaye = 0.0;
        double soldeDepot = 0.0;

        List<PretStatut> prets = new ArrayList<>();
        List<CompteCourant> compteCourants = compteCourantDAO.findByClient(client);
        for (CompteCourant compteCourant : compteCourants) {
            double temp = OSE.getSoldeActuel(compteCourant.getId());
            soldeTotal += temp;
            soldeCourant += temp;

            PretStatut pretImpaye = PSE.getPretsImpayesByCompte(compteCourant.getId());
            if (pretImpaye != null) {
                double temp1 = PSE.resteAPaye(pretImpaye.getPret().getId());
                soldeTotal += temp1;
                resteApaye += temp1;
                prets.add(pretImpaye);
            }
        }

        List<CompteDepot> compteDepots = compteDepotEJB.getComptesByClient(clientId);
        for (CompteDepot compteDepot : compteDepots) {
            double temp2 = ODE.getSoldeByCompte(compteDepot.getId());
            soldeTotal += temp2;
            soldeDepot += temp2;
        }

        List<PretStatut> allPret = pretStatutDAO.getPretsAvecStatutActuelByClient(clientId);

        req.setAttribute("client", client);
        req.setAttribute("compteCourants", compteCourants);
        req.setAttribute("compteDepots", compteDepots);
        req.setAttribute("prets", prets);
        req.setAttribute("allPrets", allPret);
        req.setAttribute("soldeTotal", soldeTotal);
        req.setAttribute("soldeCourant", soldeCourant);
        req.setAttribute("soldePret", resteApaye);
        req.setAttribute("soldeDepot", soldeDepot);

        req.getRequestDispatcher("/detailsClient.jsp").forward(req, resp);
    }

}