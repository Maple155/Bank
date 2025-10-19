package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.banque.courant.entity.CompteCourant;
import com.banque.courant.dao.CompteCourantDAO;
import com.banque.courant.dao.BanqueDAO;
import com.banque.courant.dao.ClientDAO;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.ejb.OperationServiceEJB;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.dao.PretStatutDAO;
import com.banque.pret.dao.TypeStatutDAO;
import com.banque.pret.ejb.PDFservice;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.PretStatut;
import com.banque.pret.entity.Remboursement;
import com.banque.entity.TypesStatut;

@WebServlet("/pret")
public class PretServlet extends HttpServlet {

    @EJB private ClientDAO clientDAO;
    @EJB private CompteCourantDAO compteCourantDAO;
    @EJB private OperationDAO operationDAO;
    @EJB private OperationServiceEJB OSE;
    @EJB private PretDAO pretDAO;
    @EJB private PretServiceEJB PSE;
    @EJB private BanqueDAO banqueDAO;
    @EJB private TypeStatutDAO typeStatutDAO;
    @EJB private PretStatutDAO pretStatutDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);
        String download = req.getParameter("download");

        if ("1".equals(download)) {
            genererPDF(req, resp, compte);
        } else {
            req.setAttribute("compte", compte);
            req.getRequestDispatcher("/pret.jsp").forward(req, resp);
        }
    }

    private void genererPDF(HttpServletRequest req, HttpServletResponse resp, CompteCourant compte)
        throws IOException {
    
        HttpSession session = req.getSession();
        Pret pret = (Pret) session.getAttribute("pret");

        // int pretId = Integer.parseInt(pretParam);
        // Pret pret = pretDAO.findById(pretId);

        // if (pret == null) {
        //     resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Prêt introuvable pour l'ID : " + pretId);
        //     return;
        // }

        String montant_str = req.getParameter("montant");

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=contrat_pret.pdf");

        PDFservice.genererContratPret(
                resp.getOutputStream(),
                "Ma Banque",
                compte.getClient().getNom() + " " + compte.getClient().getPrenom(),
                pret.getMontant().toString(),
                montant_str + " MGA",
                "Antananarivo",
                pret.getDate_accord().toLocalDate(),
                LocalDate.now().plusMonths(pret.getNbrMois()));

        resp.flushBuffer();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantDAO.findById(compteId);
        Date dateOperation = Date.valueOf(req.getParameter("date").toString());
        // Date currDate = Date.valueOf(LocalDate.now());
        Date currDate = dateOperation;

        if (compte.getEtat().equalsIgnoreCase("ferme")) {
            req.setAttribute("compte", compte);
            req.setAttribute("error", "Ce compte est ferme");
            req.getRequestDispatcher("/pret.jsp").forward(req, resp);
        }

        switch (action.toLowerCase()) {
            case "demander":
                demanderPret(req, resp, compte, currDate);
                break;
            case "rembourser":
                rembourserPret(req, resp, compte, currDate);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue : " + action);
                break;
        }
    }

    private void demanderPret(HttpServletRequest req, HttpServletResponse resp, CompteCourant compte, Date currDate)
            throws ServletException, IOException {

        double montant = Double.parseDouble(req.getParameter("montant"));
        String montant_str = req.getParameter("montant_str");
        int moisRemboursement = Integer.parseInt(req.getParameter("mois"));

        if (montant <= 0 || moisRemboursement <= 0) {
            req.setAttribute("error", "Montant et durée invalides");
            return;
        }
        
        // PretStatut pretImpaye = PSE.getPretsImpayesByCompte(compte.getId());
        // List<PretStatut> pretStatuts = PSE.getPretsImpayesListByCompte(compte.getId());

        // if (pretImpaye != null) {
        //     double resteApaye = PSE.resteAPaye(pretImpaye.getPret().getId());
        //     req.setAttribute("compte", compte);
        //     req.setAttribute("error", "Vous avez encore un prêt de " + resteApaye + " MGA à rembourser.");
        // }
        
        else {
            Pret pretCree = new Pret(montant, 24.0, compte, currDate, moisRemboursement);
            // PSE.demanderPret(montant, compte, currDate, moisRemboursement);
            PSE.demanderPret(pretCree);

            HttpSession session = req.getSession();
            session.setAttribute("pret", pretCree);

            req.setAttribute("message", "Prêt réussi avec succès");
            req.setAttribute("montant_str", montant_str);
            req.setAttribute("downloadPDF", true);
            
        }  
            
        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/pret.jsp").forward(req, resp);
    }

    private void rembourserPret(HttpServletRequest req, HttpServletResponse resp, CompteCourant compte, Date currDate)
            throws ServletException, IOException {

        int pretId = Integer.parseInt(req.getParameter("pret"));
        double montant = Double.parseDouble(req.getParameter("montant"));

        Pret pret = pretDAO.findById(pretId);
        double resteApaye = PSE.resteAPaye(pret.getId());
        
        if (resteApaye <= 0) {
            req.setAttribute("compte", compte);
            req.setAttribute("error", "Vous n'avez plus de prêt à rembourser");
        } else {
            double nouveauReste = resteApaye - montant;
            double montantAPayer = montant;

            double solde = OSE.getSoldeActuel(compte.getId());
            
            if (nouveauReste < 0) montantAPayer = montant + nouveauReste;
            
            if (montantAPayer > solde) { 
                req.setAttribute("error", "Votre solde est insuffisant pour ce montant");
            } else {
                PSE.rembourserPret(pret, compte, montantAPayer, currDate, operationDAO);
                
                if (nouveauReste <= 0) {
                    TypesStatut type = typeStatutDAO.findByType("Rembourse");
                    PretStatut statut = new PretStatut(pret, type, currDate);
                    pretStatutDAO.save(statut);
                    req.setAttribute("message", "Vous avez remboursé la totalité de votre prêt");
                } else {
                    req.setAttribute("message", "Vous avez remboursé une partie de votre prêt");
                }
            }
        }

        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/pret.jsp").forward(req, resp);

    }
}
