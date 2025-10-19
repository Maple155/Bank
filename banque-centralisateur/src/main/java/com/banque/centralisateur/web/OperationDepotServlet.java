package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.banque.courant.entity.CompteCourant;
import com.banque.courant.dao.CompteCourantDAO;
import com.banque.centralisateur.ejb.CompteDepotEJB;
import com.banque.centralisateur.ejb.OperationDepotEJB;
import com.banque.centralisateur.model.CompteDepot;
import com.banque.centralisateur.model.OperationDepot;
import com.banque.courant.dao.ClientDAO;
import com.banque.courant.ejb.OperationServiceEJB;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @EJB
    private OperationServiceEJB operationService;

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

            double montant = Double.parseDouble(request.getParameter("montant"));
            int compteDepotId = Integer.parseInt(request.getParameter("compteDepot"));
            int compteCourantId = Integer.parseInt(request.getParameter("compte"));

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
            } else if (montant <= 0) {
                request.setAttribute("error", "Le montant doit être supérieur à 0");
                request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                return;
            }
            
            
            List<OperationDepot> operationDepots = operationDepotEJB.getAllOperations();
            double solde = operationDepotEJB.getSoldeByCompte(compteDepotId);
            double half = solde / 2; // 50% du solde autorisé
            LocalDateTime currDate = LocalDateTime.now();

            // Toujours définir les comptes pour la JSP
            request.setAttribute("compte", compteCourant);
            request.setAttribute("compteDepot", compteDepot);

            switch (action.toLowerCase()) {
                case "crediter": {
                    // Création d'une opération crédit
                    OperationDepot opd = new OperationDepot(compteDepotId, montant, currDate);
                    operationDepotEJB.createOperation(opd);

                    // operationDepotEJB.crediterCompte(compteDepot, compteCourant, montant, currDate);

                    refreshDepotData(request, compteDepot);
                    request.setAttribute("message", "Créditation réussie !");
                    request.getRequestDispatcher("/operationDepot.jsp").forward(request, response);
                }
                    break;

                case "debiter": {
                    int diff = 0;

                    if (operationDepots.isEmpty()) {
                        diff = 31; // autoriser le premier retrait
                    } else {
                        if (operationDepotEJB.checkDebitOperation(operationDepots)) {
                            LocalDateTime date2 = operationDepotEJB.getLastDebitDate(operationDepots);
                            diff = (int) ChronoUnit.DAYS.between(date2, currDate);   
                        } else{
                            diff = 31;
                        }
                    }
                    
                    if (diff < 30) {
                        request.setAttribute("error", "Vous ne pouvez débiter qu'une fois par mois");                        
                    } else if (montant > half) {
                        request.setAttribute("error", "Vous ne pouvez débiter qu’un montant ≤ " + half + " MGA");
                    } else {
                        // Création d'une opération débit
                        OperationDepot opd = new OperationDepot(compteDepotId, -montant, currDate);
                        operationDepotEJB.createOperation(opd);

                        // operationDepotEJB.debiterCompte(compteDepot, compteCourant, montant, currDate);
                        
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
        double solde = operationDepotEJB.getSoldeByCompte(compteDepot.getId());
        List<OperationDepot> operations = operationDepotEJB.getOperationsByCompte(compteDepot.getId());
        int sommeDate = 0;

        /*
         * Calcule de l'ensemble des interet obtenu par le compte 2% annuel
         * ex : 
         * 
         * if sommeDate == 365 -> 1
         * interet == 2 * 1
         * 
         * if sommeDate == 730 -> 2
         * interet == 2 * 2 
         */
        if (!operations.isEmpty() || operations != null) {
            LocalDateTime date1 = operations.get(0).getDateOperation();
            LocalDateTime date2 = null;
            for (int i = 1; i < operations.size(); i++) {
                date2 = operations.get(i).getDateOperation();
                sommeDate += (int) ChronoUnit.DAYS.between(date2, date1);

                date1 = date2;
            }

            date2 = LocalDateTime.now();
            sommeDate += (int) ChronoUnit.DAYS.between(date2, date1);
        }

        int nbAnnee = sommeDate / 365;
        double interet = (2 * Double.valueOf(String.valueOf(nbAnnee))) / 100;
        double soldeInteret = solde + ((solde * interet) / 100);

        request.setAttribute("nbAnnee", nbAnnee);
        request.setAttribute("interet", interet);
        request.setAttribute("solde", solde);
        request.setAttribute("soldeInteret", soldeInteret);
        request.setAttribute("operations", operations);
    }
}
