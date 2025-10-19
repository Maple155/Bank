package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.banque.courant.dao.*;
import com.banque.courant.ejb.*;
import com.banque.courant.entity.*;
import com.banque.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.*;

@WebServlet("/operation")
public class OperationCourantServlet extends HttpServlet {

    @EJB private ClientDAO clientDAO;
    @EJB private CompteCourantDAO compteCourantDAO;
    @EJB private OperationDAO operationDAO;
    @EJB private OperationServiceEJB operationService;
    @EJB private PretDAO pretDAO;
    @EJB private PretServiceEJB pretService;
    @EJB private BanqueDAO banqueDAO;
    @EJB private TransactionDAO transactionDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);

        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/operation.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        double montant = Double.parseDouble(request.getParameter("montant"));
        int compteId = Integer.parseInt(request.getParameter("compte"));
        Date dateOperation = Date.valueOf(request.getParameter("date").toString());
        CompteCourant compte = compteCourantDAO.findById(compteId);
        double solde = operationService.getSoldeActuel(compteId);

        if (compte == null) {
            request.setAttribute("error", "Compte introuvable !");
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;
        } else if (compte.getEtat().equalsIgnoreCase("ferme")) {
            request.setAttribute("compte", compte);
            request.setAttribute("error",
                    "Ce compte n'est plus ouvert");
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "crediter": 
                handleCredit(request, response, compte, montant, dateOperation);
                break;
            case "debiter": 
                handleDebit(request, response, compte, montant, solde, dateOperation);
                break;
            default: 
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                break;
        }
    }

    /** ---------------------- HANDLERS ---------------------- **/

    private void handleCredit(HttpServletRequest request, HttpServletResponse response,
                              CompteCourant compte, double montant, Date dateOperation)
            throws ServletException, IOException {

        // Date date = Date.valueOf(LocalDate.now());
        Date date = dateOperation;
        int minimum = 0;
        if (montant <= 0) {
            request.setAttribute("compte", compte);
            request.setAttribute("error",
                    "Le montant à crediter doit etre positif et superieur à " + minimum);
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;
        }

        operationDAO.save(new OperationCourant(compte, montant, date));
        double solde = operationService.getSoldeActuel(compte.getId());
        prepareClientView(request, compte, solde, "Crédit effectué avec succès");
        request.getRequestDispatcher("/client.jsp").forward(request, response);
    }

    private void handleDebit(HttpServletRequest request, HttpServletResponse response,
                             CompteCourant compte, double montant, double solde, Date dateOperation)
            throws ServletException, IOException {
        
        int minimum = 0;
        
        if (solde < montant) {
            request.setAttribute("compte", compte);
            request.setAttribute("error",
                    "Solde insuffisant : votre solde actuel est de " + solde + " MGA.");
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;
        } else if (montant <= 0) {
            request.setAttribute("compte", compte);
            request.setAttribute("error",
                    "Le montant à debiter doit etre positif et superieur à " + minimum);
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;
        }

        // Date date = Date.valueOf(LocalDate.now());
        Date date = dateOperation;
        operationDAO.save(new OperationCourant(compte, -montant, date));

        double nouveauSolde = operationService.getSoldeActuel(compte.getId());
        prepareClientView(request, compte, nouveauSolde, "Débit effectué avec succès");
        request.getRequestDispatcher("/client.jsp").forward(request, response);
    }

    /** ---------------------- UTILITAIRES ---------------------- **/

    private void prepareClientView(HttpServletRequest request, CompteCourant compte,
                                   double solde, String message) {

        List<OperationCourant> operations = operationDAO.findByCompte(compte.getId());
        List<Pret> prets = pretDAO.findByCompte(compte.getId());
        List<PretStatut> pretStatuts = pretService.getPretsImpayesListByCompte(compte.getId());
        PretStatut pretUnique = pretService.getPretsImpayesByCompte(compte.getId());

        // Ajouter les informations de prêts impayés
        addPretDetailsToRequest(request, pretStatuts);

        Pret pretImpaye = null;
        List<Remboursement> remboursements = null;
        double resteAPaye = 0.0;

        if (pretUnique != null) {
            pretImpaye = pretDAO.findById(pretUnique.getPret().getId());
            remboursements = pretDAO.getRemboursementByPret(pretImpaye.getId());
            resteAPaye = pretService.resteAPaye(pretImpaye.getId());
        }

        List<Transaction> sender = transactionDAO.findBySender(compte.getId());
        List<Transaction> receiver = transactionDAO.findByReceiver(compte.getId());

        request.setAttribute("compte", compte);
        request.setAttribute("solde", solde);
        request.setAttribute("operationsCourant", operations);
        request.setAttribute("prets", prets);
        request.setAttribute("pretImpaye", pretImpaye);
        request.setAttribute("remboursements", remboursements);
        request.setAttribute("resteAPaye", resteAPaye);
        request.setAttribute("pretStatus", pretStatuts);
        request.setAttribute("sender", sender);
        request.setAttribute("receiver", receiver);
        request.setAttribute("message", message);
    }

    private void addPretDetailsToRequest(HttpServletRequest request, List<PretStatut> pretStatuts) {
        if (pretStatuts == null || pretStatuts.isEmpty()) return;

        for (PretStatut pretStatut : pretStatuts) {
            Pret pret = pretDAO.findById(pretStatut.getPret().getId());
            List<Remboursement> remboursements = pretDAO.getRemboursementByPret(pret.getId());
            double resteAPaye = pretService.resteAPaye(pret.getId());

            request.setAttribute("pretImpaye_" + pretStatut.getId(), pret);
            request.setAttribute("remboursements_" + pretStatut.getId(), remboursements);
            request.setAttribute("resteAPaye_" + pretStatut.getId(), resteAPaye);
        }
    }
}
