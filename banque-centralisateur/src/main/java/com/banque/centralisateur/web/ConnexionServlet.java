package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.courant.ejb.*;
import com.banque.entity.*;
import com.banque.pret.dao.PretDAO;
import com.banque.pret.ejb.PretServiceEJB;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.PretStatut;
import com.banque.pret.entity.Remboursement;
import com.banque.courant.dao.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

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
    private TransactionDAO transactionDAO;
    @EJB
    private TransactionServiceEJB TSE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<CompteCourant> comptes = compteCourantDAO.findAll();

        req.setAttribute("comptes", comptes);
        req.getRequestDispatcher("/connexion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            String numero = request.getParameter("numero");

            CompteCourant compte = compteCourantDAO.findByNumero(numero);

            double solde = OSE.getSoldeActuel(compte.getId());

            List<OperationCourant> operationsCourant = operationDAO.findByCompte(compte.getId());

            List<Pret> prets = pretDAO.findByCompte(compte.getId());
            PretStatut pret = PSE.getPretsImpayesByCompte(compte.getId());
            List<PretStatut> pretStatuts = PSE.getPretsImpayesListByCompte(compte.getId());

            if (!pretStatuts.isEmpty()|| pretStatuts != null) {
                request.setAttribute("pretStatus", pretStatuts);
                
                for (PretStatut pretStatut : pretStatuts) {
                    Pret tempPretImpaye = pretDAO.findById(pretStatut.getPret().getId());
                    List<Remboursement> tempRemboursements = pretDAO.getRemboursementByPret(tempPretImpaye.getId());
                    double tempResteAPayePret = PSE.resteAPaye(tempPretImpaye.getId());

                    request.setAttribute("pretImpaye_" + pretStatut.getId(), tempPretImpaye);
                    request.setAttribute("remboursements_" + pretStatut.getId(), tempRemboursements);
                    request.setAttribute("resteAPaye_" + pretStatut.getId(), tempResteAPayePret);
                    
                }
            }

            Pret pretImpaye = null;

            List<Remboursement> remboursements = null;
            double resteAPayePret = 0.0;
            if (pret != null) {
                pretImpaye = pretDAO.findById(pret.getPret().getId());
                remboursements = pretDAO.getRemboursementByPret(pretImpaye.getId());
                resteAPayePret = PSE.resteAPaye(pretImpaye.getId());
            }

            List<Transaction> transactionsSender = transactionDAO.findBySender(compte.getId());
            List<Transaction> transactionsReceiver = transactionDAO.findByReceiver(compte.getId());

            request.setAttribute("sender", transactionsSender);
            request.setAttribute("receiver", transactionsReceiver);
            request.setAttribute("solde", solde);
            request.setAttribute("compte", compte);
            request.setAttribute("operationsCourant", operationsCourant);
            request.setAttribute("pretImpaye", pretImpaye);
            request.setAttribute("prets", prets);
            request.setAttribute("remboursements", remboursements);
            request.setAttribute("resteAPaye", resteAPayePret);
            request.setAttribute("message", "Connection reussi");
            request.getRequestDispatcher("/client.jsp").forward(request, response);
            
        } else if ("register".equals(action)) {

            String nom = request.getParameter("nom").toString();
            String prenom = request.getParameter("prenom").toString();
            String email = request.getParameter("email").toString();
            String adresse = request.getParameter("adresse").toString();
            String dateStr = request.getParameter("date").toString();
            Date date = Date.valueOf(dateStr);

            String pwd = request.getParameter("pwd");

            Client client = new Client(nom, prenom, email, adresse, date);
            clientDAO.save(client);
            client = clientDAO.findByEmail(email);

            CompteCourant compteCourant = new CompteCourant();
            compteCourant.setId(0);
            compteCourant.setClient(client);
            compteCourant.setCode_secret(pwd);
            compteCourant.setDateOuverture(Date.valueOf(LocalDate.now()));
            compteCourantDAO.save(compteCourant);

            List<CompteCourant> comptes = compteCourantDAO.findAll();
            request.setAttribute("comptes", comptes);
            request.setAttribute("message", "Inscription réussie, connectez-vous !");
            request.getRequestDispatcher("/connexion.jsp").forward(request, response);

        } else {
            // Si aucune action valide
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
        }

    }
}
