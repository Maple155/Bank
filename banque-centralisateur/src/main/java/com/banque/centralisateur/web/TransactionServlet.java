package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import com.banque.courant.dao.CompteCourantDAO;
import com.banque.courant.ejb.OperationServiceEJB;
import com.banque.courant.entity.CompteCourant;
import com.banque.centralisateur.ejb.CompteDepotEJB;
import com.banque.centralisateur.ejb.OperationDepotEJB;
import com.banque.entity.Client;
import com.banque.courant.dao.ClientDAO;
import com.banque.courant.ejb.TransactionServiceEJB;
import com.banque.courant.dao.TransactionDAO;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

    @Inject private CompteDepotEJB compteDepotEJB;
    @Inject private OperationDepotEJB ODE;

    @EJB private ClientDAO clientDAO;
    @EJB private CompteCourantDAO compteCourantDAO;
    @EJB private OperationServiceEJB OSE;
    @EJB private TransactionDAO transactionDAO;
    @EJB private TransactionServiceEJB TSE;

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/transaction.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);
        double solde = OSE.getSoldeActuel(compteId);

        req.setAttribute("compte", compte);
        req.setAttribute("solde", solde);
        forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int compteId = Integer.parseInt(request.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);
        double solde = OSE.getSoldeActuel(compteId);
        String action = request.getParameter("action");

        request.setAttribute("compte", compte);

        try {
            switch (action) {
                case "search":
                    handleSearch(request, solde);
                    break;
                case "send":
                    handleSend(request, solde, compte);
                    break;
                default:
                    request.setAttribute("error", "Action inconnue");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur inattendue");
        }

        forward(request, response);
    }

    private void handleSearch(HttpServletRequest request, double solde) {
        String numero = request.getParameter("numero");
        CompteCourant receiver = compteCourantDAO.findByNumero(numero);

        request.setAttribute("solde", solde);

        if (receiver == null) {
            request.setAttribute("error", "Le destinataire n'existe pas");
        } else if (request.getAttribute("compte") != null &&
                   ((CompteCourant) request.getAttribute("compte")).getNumero().equals(receiver.getNumero())) {
            request.setAttribute("error", "Le destinataire ne peut pas être vous-même");
        } else {
            request.setAttribute("receiver", receiver);
            request.setAttribute("message", "Envoyer de l'argent à Mr/Mme " + receiver.getClient().getNom());
        }
    }

    private void handleSend(HttpServletRequest request, double solde, CompteCourant sender) {
        int receiverId = Integer.parseInt(request.getParameter("receiver"));
        CompteCourant receiver = compteCourantDAO.findById(receiverId);
        double montant = Double.parseDouble(request.getParameter("montant"));
        double reste = solde - montant;

        request.setAttribute("receiver", receiver);
        request.setAttribute("solde", solde);

        if (montant <= 0) {
            request.setAttribute("error", "Le montant doit être supérieur à 0");
        } else if (reste < 0) {
            request.setAttribute("error", "Vous n'avez pas les fonds nécessaires");
        } else {
            TSE.effectuerTransfert(sender, receiver, montant);
            solde = OSE.getSoldeActuel(sender.getId());
            request.setAttribute("solde", solde);
            request.setAttribute("message", "Transfert réussi");
        }
    }
}
