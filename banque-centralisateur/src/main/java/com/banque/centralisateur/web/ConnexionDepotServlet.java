package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.banque.courant.entity.*;
import com.banque.courant.ejb.*;
import com.banque.courant.dao.*;
import com.banque.centralisateur.model.*;
import com.banque.centralisateur.ejb.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/connexionDepot")
public class ConnexionDepotServlet extends HttpServlet {

    @Inject private CompteDepotEJB compteDepotEJB;
    @Inject private OperationDepotEJB operationDepotEJB;

    @EJB private ClientDAO clientDAO;
    @EJB private CompteCourantDAO compteCourantDAO;
    @EJB private OperationDAO operationDAO;
    @EJB private OperationServiceEJB operationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int compteId = Integer.parseInt(req.getParameter("compte"));
            CompteCourant compteCourant = compteCourantDAO.findById(compteId);

            if (compteCourant == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
                return;
            }

            List<CompteDepot> comptes = compteDepotEJB.getComptesByClient(compteCourant.getClient().getId());
            req.setAttribute("compte", compteCourant);
            req.setAttribute("comptes", comptes);
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors du chargement de la page : " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action manquante");
            return;
        }

        switch (action.toLowerCase()) {
            case "login": 
                handleLogin(req, resp);
                break;
            case "register": 
                handleRegister(req, resp);
                break;
            default: 
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue : " + action);
                break;
        }
    }

    /** 🔹 Connexion à un compte dépôt */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // try {
            String numero = req.getParameter("numero");
            int compteCourantId = Integer.parseInt(req.getParameter("courant").toString());

            CompteDepot compteDepot = compteDepotEJB.getComptesByNumero(numero);
            CompteCourant compteCourant = compteCourantDAO.findById(compteCourantId);

            if (compteDepot == null) {
                req.setAttribute("error", "Compte dépôt introuvable");
                req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
                return;
            }

            double solde = operationDepotEJB.getSoldeByCompte(compteDepot.getId());
            List<OperationDepot> operations = operationDepotEJB.getOperationsByCompte(compteDepot.getId());

            req.setAttribute("operations", operations);
            req.setAttribute("solde", solde);
            req.setAttribute("compte", compteCourant);
            req.setAttribute("compteDepot", compteDepot);
            req.setAttribute("message", "Connexion réussie !");
            req.getRequestDispatcher("/operationDepot.jsp").forward(req, resp);

        // } catch (Exception e) {
        //     req.setAttribute("error", "Erreur lors de la connexion : " + e.getMessage());                
        //     // req.setAttribute("message", "ato ve ?");
        //     req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
        // }
    }

    /** 🔹 Création d’un nouveau compte dépôt */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int compteCourantId = Integer.parseInt(req.getParameter("courant"));
            CompteCourant compteCourant = compteCourantDAO.findById(compteCourantId);

            if (compteCourant == null) {
                req.setAttribute("error", "Compte courant introuvable");
                req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
                return;
            }

            String codeSecret = req.getParameter("pwd");
            if (codeSecret == null || codeSecret.isBlank()) {
                req.setAttribute("error", "Le mot de passe est obligatoire");
                req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
                return;
            }

            CompteDepot nouveauDepot = new CompteDepot();
            nouveauDepot.setClientId(compteCourant.getClient().getId());
            nouveauDepot.setCodeSecret(codeSecret);
            nouveauDepot.setDateOuverture(LocalDateTime.now());
            nouveauDepot.setEtat("ouvert");
            compteDepotEJB.createCompteDepot(nouveauDepot);

            List<CompteDepot> comptes = compteDepotEJB.getComptesByClient(compteCourant.getClient().getId());

            req.setAttribute("compte", compteCourant);
            req.setAttribute("comptes", comptes);
            req.setAttribute("message", "Inscription réussie ! Connectez-vous pour continuer.");
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
        }
    }
}
