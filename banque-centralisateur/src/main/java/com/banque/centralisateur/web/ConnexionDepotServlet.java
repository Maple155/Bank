package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.banque.courant.entity.*;
import com.banque.courant.remote.CompteCourantRemote;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.remote.UtilisateurRemote;

import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.centralisateur.model.*;
import com.banque.centralisateur.service.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/connexionDepot")
public class ConnexionDepotServlet extends HttpServlet {

    @Inject
    private CompteDepotService compteDepotService;
    @Inject
    private OperationDepotService operationDepotService;

    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/CompteCourantServiceEJB!com.banque.courant.remote.CompteCourantRemote")
    private CompteCourantRemote compteCourantService;

    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/OperationServiceEJB!com.banque.courant.remote.OperationRemote")
    private OperationRemote operationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int compteId = Integer.parseInt(req.getParameter("compte"));
            CompteCourant compteCourant = compteCourantService.find(compteId);

            if (compteCourant == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
                return;
            }

            List<CompteDepot> comptes = compteDepotService.getComptesByClient(compteCourant.getClient().getId());
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

        HttpSession session = req.getSession(false);
        UtilisateurDTO utilisateurConnecte = null;
        UtilisateurRemote utilisateurRemote = null;

        UtilisateurDTO utilisateur = null;
        List<DirectionDTO> directions = null;
        List<ActionRoleDTO> actionRoles = null;
        boolean isRole = false;

        if (session != null) {
            Object o = session.getAttribute("user");
            if (o instanceof UtilisateurDTO) {
                utilisateurConnecte = (UtilisateurDTO) o;
                utilisateurRemote = (UtilisateurRemote) session.getAttribute("sessionUtilisateur");

                utilisateur = utilisateurRemote.getUtilisateurConnecte();
                directions = utilisateurRemote.getDirections();
                actionRoles = utilisateurRemote.getActionRoles();

                for (ActionRoleDTO actionRoleDTO : actionRoles) {
                    if (actionRoleDTO.getNomTable().equalsIgnoreCase("operation_depot")
                            && actionRoleDTO.getRole() == utilisateur.getRole()) {
                        isRole = true;
                        break;
                    }
                }
            }
        }

        if (isRole == false) {
            int compteId = Integer.parseInt(req.getParameter("courant"));
            CompteCourant compteCourant = compteCourantService.find(compteId);

            List<CompteDepot> comptes = compteDepotService.getComptesByClient(compteCourant.getClient().getId());
            req.setAttribute("compte", compteCourant);
            req.setAttribute("comptes", comptes);
            req.setAttribute("error", "Vous ne pouvez pas effectuer cette operation");
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
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

        CompteDepot compteDepot = compteDepotService.getComptesByNumero(numero);
        CompteCourant compteCourant = compteCourantService.find(compteCourantId);

        if (compteDepot == null) {
            req.setAttribute("error", "Compte dépôt introuvable");
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
            return;
        }

        double solde = operationDepotService.getSoldeByCompte(compteDepot.getId());
        List<OperationDepot> operations = operationDepotService.getOperationsByCompte(compteDepot.getId());

        refreshDepotData(req, compteDepot);

        req.setAttribute("operations", operations);
        req.setAttribute("solde", solde);
        req.setAttribute("compte", compteCourant);
        req.setAttribute("compteDepot", compteDepot);
        req.setAttribute("message", "Connexion réussie !");
        req.getRequestDispatcher("/operationDepot.jsp").forward(req, resp);

        // } catch (Exception e) {
        // req.setAttribute("error", "Erreur lors de la connexion : " + e.getMessage());
        // // req.setAttribute("message", "ato ve ?");
        // req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
        // }
    }

    /** 🔹 Création d’un nouveau compte dépôt */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int compteCourantId = Integer.parseInt(req.getParameter("courant"));
            CompteCourant compteCourant = compteCourantService.find(compteCourantId);

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
            compteDepotService.createCompteDepot(nouveauDepot);

            List<CompteDepot> comptes = compteDepotService.getComptesByClient(compteCourant.getClient().getId());

            req.setAttribute("compte", compteCourant);
            req.setAttribute("comptes", comptes);
            req.setAttribute("message", "Inscription réussie ! Connectez-vous pour continuer.");
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            req.getRequestDispatcher("/connexionDepot.jsp").forward(req, resp);
        }
    }

    private void refreshDepotData(HttpServletRequest request, CompteDepot compteDepot) {
        List<OperationDepot> operations = operationDepotService.getOperationsByCompte(compteDepot.getId());
        if (operations == null) {
            operations = new ArrayList<>();
        }

        double solde = operationDepotService.getSoldeByCompte(compteDepot.getId());

        double interetTotal = 0.0;
        double tauxAnnuel = 0.02;

        if (!operations.isEmpty()) {
            // Trier les opérations par date
            operations.sort((o1, o2) -> o1.getDateOperation().compareTo(o2.getDateOperation()));

            // On initialise le solde temporaire avec le solde réel avant intérêts
            double soldeTemp = solde;

            // On part de la date de la première opération
            LocalDateTime datePrecedente = operations.get(0).getDateOperation();

            for (OperationDepot op : operations) {
                // Nombre de jours depuis la dernière opération
                long jours = ChronoUnit.DAYS.between(datePrecedente, op.getDateOperation());

                // Calcul de l'intérêt sur cette période
                interetTotal += soldeTemp * tauxAnnuel * jours / 365.0;

                // Mettre à jour le solde temporaire avec l'opération actuelle
                soldeTemp += op.getMontant();
                datePrecedente = op.getDateOperation();
            }

            // Intérêt jusqu'à aujourd'hui depuis la dernière opération
            // long joursRestants = ChronoUnit.DAYS.between(datePrecedente,
            // LocalDateTime.now());
            // interetTotal += soldeTemp * tauxAnnuel * joursRestants / 365.0;
        }

        double soldeInteret = solde + interetTotal;

        solde = Math.round(solde * 100.0) / 100.0;
        soldeInteret = Math.round(soldeInteret * 100.0) / 100.0;
        interetTotal = Math.round(interetTotal * 100.0) / 100.0;

        request.setAttribute("soldeInteret", soldeInteret);
        request.setAttribute("interet", interetTotal);
    }

}