package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.banque.courant.entity.CompteCourant;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.remote.UtilisateurRemote;
import com.banque.courant.dao.CompteCourantDAO;
import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.centralisateur.ejb.CompteDepotEJB;
import com.banque.centralisateur.ejb.OperationDepotEJB;
import com.banque.centralisateur.model.CompteDepot;
import com.banque.centralisateur.model.OperationDepot;
import com.banque.courant.dao.ClientDAO;
import com.banque.courant.ejb.OperationServiceEJB;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/operationDepot")
public class OperationDepotServlet extends HttpServlet {

    @Inject
    private CompteDepotEJB compteDepotEJB;
    @Inject
    private OperationDepotEJB operationDepotEJB;

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/OperationServiceEJB!com.banque.courant.remote.OperationRemote")
    private OperationRemote operationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            String action = request.getParameter("action");
            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action manquante");
                return;
            }

            HttpSession session = request.getSession(false);
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
                request.setAttribute("error", "Vous ne pouvez pas effectuer cette operation");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            } 
            
            double montant = Double.parseDouble(request.getParameter("montant"));
            int compteDepotId = Integer.parseInt(request.getParameter("compteDepot"));
            int compteCourantId = Integer.parseInt(request.getParameter("compte"));
            Date dateOperation = Date.valueOf(request.getParameter("date").toString());
            CompteDepot compteDepot = compteDepotEJB.getCompteDepot(compteDepotId);
            CompteCourant compteCourant = compteCourantDAO.findById(compteCourantId);

            if (compteDepot == null || compteCourant == null) {
                request.setAttribute("error", "Compte dépôt ou compte courant introuvable");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            } else if (compteCourant.getEtat().equalsIgnoreCase("ferme")) {
                request.setAttribute("error", "Ce compte est ferme");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            } else if (montant < 0) {
                request.setAttribute("error", "Le montant doit être supérieur à 0");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            }

            List<OperationDepot> operationDepots = operationDepotEJB.getAllOperations();
            double solde = operationDepotEJB.getSoldeByCompte(compteDepotId);
            double half = solde / 2;
            // LocalDateTime currDate = LocalDateTime.now();
            LocalDateTime currDate = dateOperation.toLocalDate().atStartOfDay();

            request.setAttribute("compte", compteCourant);
            request.setAttribute("compteDepot", compteDepot);

            switch (action.toLowerCase()) {
                case "crediter": {

                    // OperationDepot opd = new OperationDepot(compteDepotId, montant, currDate);
                    // operationDepotEJB.createOperation(opd);

                    double soldeCourant = operationService.getSoldeActuel(compteCourantId);

                    if (soldeCourant < montant) {
                        request.setAttribute("error", "Solde insuffisant dans le compte courant");
                    } else {
                        operationDepotEJB.crediterCompte(compteDepot, compteCourant, montant,
                                currDate);
                        request.setAttribute("message", "Créditation réussie !");
                    }

                    refreshDepotData(request, compteDepot);
                    request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                }
                    break;

                case "debiter": {
                    int diff = 0;

                    if (operationDepots.isEmpty()) {
                        diff = 31;
                    } else {
                        if (operationDepotEJB.checkDebitOperation(operationDepots)) {
                            LocalDateTime date2 = operationDepotEJB.getLastDebitDate(operationDepots);
                            diff = (int) ChronoUnit.DAYS.between(date2, currDate);
                        } else {
                            diff = 31;
                        }
                    }

                    if (diff < 28) {
                        request.setAttribute("error", "Vous ne pouvez débiter qu'une fois par mois");
                    } else if (solde < 0) {
                        request.setAttribute("error", "Vous n'avez aucun fond à votre disposition");
                    } else if (montant > half) {
                        request.setAttribute("error", "Vous ne pouvez débiter qu'un montant ≤ " + half + " MGA");
                    } else {

                        // OperationDepot opd = new OperationDepot(compteDepotId, -montant, currDate);
                        // operationDepotEJB.createOperation(opd);

                        operationDepotEJB.debiterCompte(compteDepot, compteCourant, montant,
                                currDate);

                        request.setAttribute("message", "Débit effectué avec succès !");
                    }

                    refreshDepotData(request, compteDepot);
                    request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                }
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue : " + action);
                    break;
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Montant ou identifiant invalide");
            request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue : " + e.getMessage());
            request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
        }
    }

    /**
     * Méthode utilitaire pour rafraîchir le solde et les opérations du compte dépôt
     */
    private void refreshDepotData(HttpServletRequest request, CompteDepot compteDepot) {
        List<OperationDepot> operations = operationDepotEJB.getOperationsByCompte(compteDepot.getId());
        if (operations == null) {
            operations = new ArrayList<>();
        }

        double solde = operationDepotEJB.getSoldeByCompte(compteDepot.getId());

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

        // Arrondi à 2 décimales
        solde = Math.round(solde * 100.0) / 100.0;
        soldeInteret = Math.round(soldeInteret * 100.0) / 100.0;
        interetTotal = Math.round(interetTotal * 100.0) / 100.0;

        request.setAttribute("solde", solde);
        request.setAttribute("soldeInteret", soldeInteret);
        request.setAttribute("interet", interetTotal);
        request.setAttribute("operations", operations);
    }

}
