package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
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
import java.util.List;

@WebServlet("/operation")
public class OperationCourantServlet extends HttpServlet {

    @EJB
    private ClientDAO clientDAO;
    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB
    private OperationDAO operationDAO;
    @EJB
    private OperationServiceEJB OSE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compte_id = Integer.valueOf(req.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);

        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/operation.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        double montant = Double.valueOf(request.getParameter("montant").toString());
        int compte_id = Integer.valueOf(request.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);
        double solde = OSE.getSoldeActuel(compte_id);
        Date currDate = Date.valueOf(LocalDate.now());

        response.getWriter().write("<h1> 0 </h1>");
        if ("crediter".equals(action)) {
            OperationCourant opc = new OperationCourant(compte, montant, currDate);
            operationDAO.save(opc);
            
            solde = OSE.getSoldeActuel(compte_id);

            request.setAttribute("solde", solde);
            request.setAttribute("compte", compte);
            request.setAttribute("message", "Creditation reussi");

            response.getWriter().write("<h1> 1 </h1>");
            request.getRequestDispatcher("/client.jsp").forward(request, response);
            return;
        } else if ("debiter".equals(action)) {

            response.getWriter().write("<h1> 4 </h1>");

            if (solde < montant) {
                request.setAttribute("compte", compte);
                request.setAttribute("error", "Vous ne pouvez pas debiter cette montant votre solde est de " + solde + " Ar ");

                response.getWriter().write("<h1> 2 </h1>");
                request.getRequestDispatcher("/operation.jsp").forward(request, response);
                return;
            } else {
                montant = montant * -1;
                OperationCourant opc = new OperationCourant(compte, montant, currDate);
                operationDAO.save(opc);

                solde = OSE.getSoldeActuel(compte_id);

                request.setAttribute("solde", solde);
                request.setAttribute("compte", compte);
                request.setAttribute("message", "Debite avec succes");

                response.getWriter().write("<h1> 3 </h1>");
                request.getRequestDispatcher("/client.jsp").forward(request, response);
                return;
            }
        }
    }
}