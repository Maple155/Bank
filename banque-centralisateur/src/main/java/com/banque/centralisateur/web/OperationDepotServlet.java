package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.courant.ejb.*;
import com.banque.entity.*;
import com.banque.centralisateur.ejb.CompteDepotEJB;
import com.banque.centralisateur.ejb.OperationDepotEJB;
import com.banque.centralisateur.model.CompteDepot;
import com.banque.centralisateur.model.OperationDepot;
import com.banque.courant.dao.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/operationDepot")
public class OperationDepotServlet extends HttpServlet {

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

    // @Override
    // protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    //         throws ServletException, IOException {

    //     int compte_id = Integer.valueOf(req.getParameter("compte").toString());
    //     CompteCourant compte = compteCourantDAO.findById(compte_id);

    //     req.setAttribute("compte", compte);
    //     req.getRequestDispatcher("/operation.jsp").forward(req, resp);
    // }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        double montant = Double.valueOf(request.getParameter("montant").toString());
        int compte_id = Integer.valueOf(request.getParameter("compteDepot").toString());
        int compteCourant_id = Integer.valueOf(request.getParameter("compte").toString());
        CompteDepot compteDepot = compteDepotEJB.getCompteDepot(compte_id);
        CompteCourant compteCourant = compteCourantDAO.findById(compteCourant_id);
        double solde = ODE.getSoldeByCompte(compte_id);
        double half = solde - ((solde * 50) / 100);
        LocalDateTime currDate = LocalDateTime.now();

        request.setAttribute("compte", compteCourant);
        request.setAttribute("compteDepot", compteDepot);

        if ("crediter".equals(action)) {
            OperationDepot opd = new OperationDepot(compte_id, montant, currDate);
            ODE.createOperation(opd);
            
            solde = ODE.getSoldeByCompte(compte_id);

            List<OperationDepot> operationDepots = ODE.getOperationsByCompte(compteDepot.getId());

            request.setAttribute("operations", operationDepots);
            request.setAttribute("solde", solde);
            request.setAttribute("message", "Creditation reussi");

            request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
            return;
        } else if ("debiter".equals(action)) {

            if (solde < montant || half < montant) {
                request.setAttribute("error", "Vous ne pouvez pas debiter qu'une montant inferieur ou egale à " + half + " MGA ");

                List<OperationDepot> operationDepots = ODE.getOperationsByCompte(compteDepot.getId());

                request.setAttribute("operations", operationDepots);
                solde = ODE.getSoldeByCompte(compte_id);

                request.setAttribute("solde", solde);
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            } else {
                montant = montant * -1;
                OperationDepot opd = new OperationDepot(compte_id, montant, currDate);
                ODE.createOperation(opd);

                solde = ODE.getSoldeByCompte(compte_id);
                List<OperationDepot> operationDepots = ODE.getOperationsByCompte(compteDepot.getId());

                request.setAttribute("operations", operationDepots);
                request.setAttribute("solde", solde);
                request.setAttribute("message", "Debite avec succes");

                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            }
        }
    }
}