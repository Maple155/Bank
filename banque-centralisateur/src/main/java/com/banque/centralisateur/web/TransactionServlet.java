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

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    
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
    @EJB
    private TransactionDAO transactionDAO;
    @EJB
    private TransactionServiceEJB TSE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compte_id = Integer.valueOf(req.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);        
        double solde = OSE.getSoldeActuel(compte_id);

        req.setAttribute("compte", compte);
        req.setAttribute("solde", solde);
        req.getRequestDispatcher("/transaction.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int compte_id = Integer.valueOf(request.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);        
        double solde = OSE.getSoldeActuel(compte_id);

        String action = request.getParameter("action").toString();
        
        request.setAttribute("compte", compte);
        if ("search".equals(action)) {
            String numero = request.getParameter("numero").toString();
            CompteCourant receiver = compteCourantDAO.findByNumero(numero);
            
            if (receiver == null) {
                request.setAttribute("solde", solde);
                request.setAttribute("error", "Le destinataire n'existe pas");
                request.getRequestDispatcher("/transaction.jsp").forward(request, response);                
            } else {
                if (compte.getNumero().equals(receiver.getNumero())) {
                    request.setAttribute("solde", solde);
                    request.setAttribute("error", "Le destinataire ne peut pas etre vous meme");
                    request.getRequestDispatcher("/transaction.jsp").forward(request, response);
                } else {
                    request.setAttribute("receiver", receiver);
                    request.setAttribute("solde", solde);
                    request.setAttribute("message", "Envoyer de l'argent à Mr / Mme " + receiver.getClient().getNom());
                    request.getRequestDispatcher("/transaction.jsp").forward(request, response);
                }
            }

        } else if ("send".equals(action)) {
            int receiver_id = Integer.valueOf(request.getParameter("receiver").toString());
            CompteCourant receiver = compteCourantDAO.findById(receiver_id);
            double montant = Double.valueOf(request.getParameter("montant").toString());
            double resteArgent = solde - montant;
            
            if (montant < 0) {
                request.setAttribute("receiver", receiver);
                request.setAttribute("solde", solde);
                request.setAttribute("error", "Le montant de doit pas etre inferieur a 0");
                request.getRequestDispatcher("/transaction.jsp").forward(request, response);
            } else if (resteArgent < 0) {
                request.setAttribute("receiver", receiver);
                request.setAttribute("solde", solde);
                request.setAttribute("error", "Vous n'avez pas les fond necessaire");
                request.getRequestDispatcher("/transaction.jsp").forward(request, response);
            } else {
                try {
                    TSE.effectuerTransfert(compte, receiver, montant);
                
                    solde = OSE.getSoldeActuel(compte_id);
                
                    request.setAttribute("receiver", receiver);
                    request.setAttribute("solde", solde);
                    request.setAttribute("message", "Transfert réussi");
                    request.getRequestDispatcher("/transaction.jsp").forward(request, response);
                
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("receiver", receiver);
                    request.setAttribute("solde", solde);
                    request.setAttribute("error", "Erreur lors du transfert");
                    request.getRequestDispatcher("/transaction.jsp").forward(request, response);
                }
            }
        }
    }
}