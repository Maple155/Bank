package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.courant.ejb.*;
import com.banque.entity.*;
import com.banque.courant.dao.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.banque.centralisateur.model.*;
import com.banque.centralisateur.ejb.*;

@WebServlet("/connexionDepot")
public class ConnexionDepotServlet extends HttpServlet {

    @Inject
    private CompteDepotEJB compteDepotEJB;
    @Inject
    private OperationDepotEJB ODE;

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB
    private OperationDAO operationDAO;
    @EJB 
    private OperationServiceEJB OSE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compteId = Integer.valueOf(req.getParameter("compte").toString());
        CompteCourant compteCourant = compteCourantDAO.findById(compteId);

        List<CompteDepot> comptes = compteDepotEJB.getComptesByClient(compteCourant.getClient().getId());

        req.setAttribute("compte", compteCourant);
        req.setAttribute("comptes", comptes);
        req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            try {
                String numero = request.getParameter("numero");
                int compteCourantId = Integer.valueOf(request.getParameter("courant").toString());
    
                CompteDepot compteDepot = compteDepotEJB.getComptesByNumero(numero);
                CompteCourant compteCourant = compteCourantDAO.findById(compteCourantId);
    
                double solde = ODE.getSoldeByCompte(compteDepot.getId());
                List<OperationDepot> operationDepots = ODE.getOperationsByCompte(compteDepot.getId());

                request.setAttribute("operations", operationDepots);
                request.setAttribute("solde", solde);
                request.setAttribute("compte", compteCourant);
                request.setAttribute("compteDepot", compteDepot);
                request.setAttribute("message", "Connection reussi");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else if ("register".equals(action)) {
            try {
                int compteCourantId = Integer.valueOf(request.getParameter("courant").toString());
                CompteCourant compteCourant = compteCourantDAO.findById(compteCourantId);
    
                String pwd = request.getParameter("pwd");
    
                CompteDepot compteDepot = new CompteDepot();
                compteDepot.setClientId(compteCourant.getClient().getId());
                compteDepot.setCodeSecret(pwd);
                compteDepot.setDateOuverture(LocalDateTime.now());
                compteDepotEJB.createCompteDepot(compteDepot);
    
                List<CompteDepot> comptes = compteDepotEJB.getComptesByClient(compteCourant.getClient().getId());
    
                request.setAttribute("compte", compteCourant);
                request.setAttribute("comptes", comptes);
                request.setAttribute("message", "Inscription réussie, connectez-vous !");
                request.getRequestDispatcher("/connexionDepot.jsp").forward(request, response);                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Si aucune action valide
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
        }

    }

}
