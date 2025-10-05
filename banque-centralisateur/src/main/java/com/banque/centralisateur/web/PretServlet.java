package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.dao.PretStatutDAO;
import com.banque.pret.dao.TypeStatutDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.PretStatut;
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
    @EJB
    private TypeStatutDAO typeStatutDAO;
    @EJB
    private PretStatutDAO pretStatutDAO;

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
        PretStatut pretImpaye = PSE.getPretsImpayesByCompte(compte_id);
        Pret temp = null;

        if (pretImpaye != null) {
            temp = pretDAO.findById(pretImpaye.getPret().getId());
        }

        // Banque banque = banqueDAO.findById(1);
        Date currDate = Date.valueOf(LocalDate.now());
        // double soldeActuel = OSE.getSoldeActuel(compte_id);

        // if (soldeActuel < montant) {
        // request.setAttribute("compte", compte);
        // request.setAttribute("error", "Votre solde actuel est inferieur à " +
        // montant);
        // request.getRequestDispatcher("/Pret.jsp").forward(request, response);
        // }

        if ("demander".equals(action)) {
            if (temp != null) {
                double resteApaye = PSE.resteAPaye(temp.getId());

                request.setAttribute("compte", compte);
                request.setAttribute("error", "Vous avez encore un pret de " + resteApaye + " MGA à rembourser : " + pretImpaye.getStatut().getType() + " " + pretImpaye.getPret().getMontant() );
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            } else {
                PSE.demanderPret(montant, compte, currDate);

                request.setAttribute("compte", compte);
                request.setAttribute("message", "Pret reussi avec succes");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            }
        } else if ("rembourser".equals(action)) {

            if (temp == null) {

                request.setAttribute("compte", compte);
                request.setAttribute("error", "Vous n'avez plus de pret à rembourser");
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            } else {
                double resteApaye = PSE.resteAPaye(temp.getId());
                double newReste = resteApaye - montant;
                request.setAttribute("message", "Vous avez rembourser une partie de votre pret");
                if (newReste < 0) {
                    double montantApaye = montant - newReste;

                    PSE.rembourserPret(temp, compte, montantApaye, currDate, operationDAO);

                    TypesStatut type = typeStatutDAO.findByType("Rembourse");

                    PretStatut pretStatut = new PretStatut(temp, type, currDate);
                    pretStatutDAO.save(pretStatut);
                    request.setAttribute("message", "Vous avez rembourser la totalite de votre pret");
                } else {

                    PSE.rembourserPret(temp, compte, montant, currDate, operationDAO);

                    if (newReste == 0) {
                        TypesStatut type = typeStatutDAO.findByType("Rembourse");

                        PretStatut pretStatut = new PretStatut(temp, type, currDate);
                        pretStatutDAO.save(pretStatut);

                        request.setAttribute("message", "Vous avez rembourser la totalite de votre pret");
                    }
                }

                request.setAttribute("compte", compte);
                request.getRequestDispatcher("/pret.jsp").forward(request, response);
            }
        }
    }
}
