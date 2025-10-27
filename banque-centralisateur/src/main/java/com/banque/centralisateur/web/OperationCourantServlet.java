package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.banque.change.entity.Change;
import com.banque.change.remote.ChangeRemote;
import com.banque.courant.dao.*;
import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.courant.ejb.*;
import com.banque.courant.entity.*;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.remote.UtilisateurRemote;
import com.banque.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.*;
import com.banque.pret.remote.PretRemote;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import java.util.Arrays;

@WebServlet("/operation")
public class OperationCourantServlet extends HttpServlet {

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
    private ChangeRemote changeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);
        req.setAttribute("compte", compte);
        List<String> devises = null;
        List<String> deviseREST = null;
        try {
            deviseREST = getDevises();
            changeService = changeRemotLookup();
            devises = changeService.getDevisesUniques();
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Impossible de récupérer les devises : " + e.getMessage());
        }

        req.setAttribute("devisesRest", deviseREST);
        req.setAttribute("devises", devises);
        req.getRequestDispatcher("/operation.jsp").forward(req, resp);
        // Toujours forward vers JSP
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        double montant = Double.parseDouble(request.getParameter("montant"));
        String devise = request.getParameter("devise");
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
                handleCredit(request, response, compte, montant, dateOperation, devise);
                break;
            case "debiter":
                handleDebit(request, response, compte, montant, solde, dateOperation, devise);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                break;
        }
    }

    private void handleCredit(HttpServletRequest request, HttpServletResponse response,
            CompteCourant compte, double montant, Date dateOperation, String devise)
            throws ServletException, IOException {

        try {

            changeService = changeRemotLookup();

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

                    for (ActionRoleDTO actionRoleDTO : actionRoles) {
                        if (actionRoleDTO.getNomTable().equalsIgnoreCase("operation_courant")
                                && actionRoleDTO.getRole() == utilisateur.getRole()) {

                            Change currChange = changeService.getChangeActuel(devise, dateOperation);

                            if (!devise.equalsIgnoreCase("MGA")) {
                                montant = montant * currChange.getCours();
                            }

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

                            operationDAO.save(new OperationCourant(compte, montant, date, true));
                            double solde = operationService.getSoldeActuel(compte.getId());
                            prepareClientView(request, compte, solde, "Crédit effectué avec succès");
                            request.getRequestDispatcher("/client.jsp").forward(request, response);
                            return;
                        }
                    }
                }
            }
            request.setAttribute("compte", compte);
            request.setAttribute("error",
                    "Vous ne pouvez pas effectuer cette operation");
            request.getRequestDispatcher("/operation.jsp").forward(request, response);
            return;

        } catch (Exception e) {
        }

    }

    private void handleDebit(HttpServletRequest request, HttpServletResponse response,
            CompteCourant compte, double montant, double solde, Date dateOperation, String devise)
            throws ServletException, IOException {

        try {

            changeService = changeRemotLookup();

            HttpSession session = request.getSession(false);
            UtilisateurDTO utilisateurConnecte = null;
            UtilisateurRemote utilisateurRemote = null;

            UtilisateurDTO utilisateur = null;
            List<DirectionDTO> directions = null;
            List<ActionRoleDTO> actionRoles = null;

            boolean role = false;

            if (session != null) {
                Object o = session.getAttribute("user");
                if (o instanceof UtilisateurDTO) {
                    utilisateurConnecte = (UtilisateurDTO) o;
                    utilisateurRemote = (UtilisateurRemote) session.getAttribute("sessionUtilisateur");

                    utilisateur = utilisateurRemote.getUtilisateurConnecte();
                    directions = utilisateurRemote.getDirections();
                    actionRoles = utilisateurRemote.getActionRoles();

                    for (ActionRoleDTO actionRoleDTO : actionRoles) {
                        if (actionRoleDTO.getNomTable().equalsIgnoreCase("operation_courant")
                                && actionRoleDTO.getRole() == utilisateur.getRole()) {
                            role = true;
                            break;
                        }
                    }
                }
            }

            if (role == false) {
                request.setAttribute("compte", compte);
                request.setAttribute("error",
                        "Vous ne pouvez pas effectuer cette operation");
                request.getRequestDispatcher("/operation.jsp").forward(request, response);
                return;
            }

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

            Change currChange = changeService.getChangeActuel(devise, dateOperation);

            if (!devise.equalsIgnoreCase("MGA")) {
                montant = montant * currChange.getCours();
            }

            // Date date = Date.valueOf(LocalDate.now());
            Date date = dateOperation;
            operationDAO.save(new OperationCourant(compte, -montant, date, true));

            double nouveauSolde = operationService.getSoldeActuel(compte.getId());
            prepareClientView(request, compte, nouveauSolde, "Débit effectué avec succès");
            request.getRequestDispatcher("/client.jsp").forward(request, response);
        } catch (Exception e) {
        }
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

        request.setAttribute("utilisateur", utilisateur);
        request.setAttribute("directions", directions);
        request.setAttribute("actionRoles", actionRoles);
    }

    private void addPretDetailsToRequest(HttpServletRequest request, List<PretStatut> pretStatuts) {
        if (pretStatuts == null || pretStatuts.isEmpty())
            return;

        for (PretStatut pretStatut : pretStatuts) {
            Pret pret = pretDAO.findById(pretStatut.getPret().getId());
            List<Remboursement> remboursements = pretDAO.getRemboursementByPret(pret.getId());
            double resteAPaye = pretService.resteAPaye(pret.getId());

            request.setAttribute("pretImpaye_" + pretStatut.getId(), pret);
            request.setAttribute("remboursements_" + pretStatut.getId(), remboursements);
            request.setAttribute("resteAPaye_" + pretStatut.getId(), resteAPaye);
        }
    }

    protected ChangeRemote changeRemotLookup() {
        try {
            Properties props = new Properties();
            props.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(javax.naming.Context.PROVIDER_URL, "http-remoting://localhost:8280"); // port HTTP ou remoting
            props.put(javax.naming.Context.SECURITY_PRINCIPAL, "test"); // si login WildFly
            props.put(javax.naming.Context.SECURITY_CREDENTIALS, "test");

            InitialContext ctx = new InitialContext(props);

            changeService = (ChangeRemote) ctx.lookup(
                    "ejb:/banque-change-1.0-SNAPSHOT/ChangeServiceEJB!com.banque.change.remote.ChangeRemote");

            return changeService;
        } catch (Exception e) {
            // TODO: handle exception

            return null;
        }
    }

    protected List<String> getDevises() {
        try {
            URL url = new URL("http://localhost:8280/banque-change/api/change/devises");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            Gson gson = new Gson();
            String[] devises = gson.fromJson(sb.toString(), String[].class);
            return Arrays.asList(devises);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // protected List<Change> getChangesByDevise(String devise) {
    //     try {
    //         URL url = new URL("http-remoting://localhost:8280/banque-change/api/change/" + devise);
    //         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //         conn.setRequestMethod("GET");
    //         conn.setRequestProperty("Accept", "application/json");

    //         if (conn.getResponseCode() != 200) {
    //             throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
    //         }

    //         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    //         StringBuilder sb = new StringBuilder();
    //         String output;
    //         while ((output = br.readLine()) != null) {
    //             sb.append(output);
    //         }
    //         conn.disconnect();

    //         ObjectMapper mapper = new ObjectMapper(); 
    //         return Arrays.asList(mapper.readValue(sb.toString(), Change[].class));

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return List.of();
    //     }
    // }

}
