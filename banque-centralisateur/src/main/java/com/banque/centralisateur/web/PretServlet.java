package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.Remboursement;
import com.banque.entity.*;
import com.banque.courant.dao.*;
import com.banque.courant.ejb.OperationServiceEJB;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/pret")
public class PretServlet extends HttpServlet {

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB
    private OperationDAO operationDAO;
    @EJB
    private OperationServiceEJB OSE;
    @EJB 
    private PretDAO pretDAO;
    @EJB
    private PretServiceEJB PSE;
    @EJB 
    private BanqueDAO banqueDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compte_id = Integer.valueOf(req.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);

        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/pret.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        double montant = Double.valueOf(request.getParameter("montant").toString());
        int compte_id = Integer.valueOf(request.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);
        Pret pretImpaye = PSE.getPretsImpayesByCompte(compte_id);
        // Banque banque = banqueDAO.findById(1);
        Date currDate = Date.valueOf(LocalDate.now());
        // double soldeActuel = OSE.getSoldeActuel(compte_id);

        // if (soldeActuel < montant) {
        //     request.setAttribute("compte", compte);
        //     request.setAttribute("error", "Votre solde actuel est inferieur à " + montant);
        //     request.getRequestDispatcher("/Pret.jsp").forward(request, response);            
        // }

        if ("demander".equals(action)) {
            if (pretImpaye != null) {
                double resteApaye = PSE.resteAPaye(pretImpaye.getId());

                request.setAttribute("compte", compte);
                request.setAttribute("error", "Vous avez encore un pret de " + resteApaye + " MGA à rembourser");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            } else {
                Pret pret = new Pret(montant, 24.0, compte, currDate, "en_cours");
                pretDAO.save(pret);

                request.setAttribute("compte", compte);
                request.setAttribute("message", "Pret reussi avec succes");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            }
        } else if ("rembourser".equals(action)) {

            if (pretImpaye == null) {

                request.setAttribute("compte", compte);
                request.setAttribute("error", "Vous n'avez plus de pret à rembourser");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            } else {
                double resteApaye = PSE.resteAPaye(pretImpaye.getId());
                double newReste = resteApaye - montant;
                if (newReste < 0 ) {
                    double montantApaye = montant - newReste;

                    PSE.rembourserPret(pretImpaye, compte, montantApaye, currDate, operationDAO);

                    pretImpaye.setStatut("rembourse");
                    pretImpaye.setDate_accord(currDate);
                    pretDAO.save(pretImpaye);
                } else {

                    PSE.rembourserPret(pretImpaye, compte, montant, currDate, operationDAO);

                    if (newReste == 0 ) {
                        pretImpaye.setStatut("rembourse");
                        pretImpaye.setDate_accord(currDate);
                        pretDAO.save(pretImpaye);
                    }
                }

                request.setAttribute("compte", compte);
                request.setAttribute("message", "Vous avez rembourser une partie de votre pret");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            }
        }
    }
}
