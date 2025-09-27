package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.pret.*;
import com.banque.entity.*;
import com.banque.courant.dao.*;
import com.banque.courant.ejb.OperationServiceEJB;

import jakarta.servlet.http.*;
import java.io.IOException;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int compte_id = Integer.valueOf(req.getParameter("compte").toString());
        CompteCourant compte = compteCourantDAO.findById(compte_id);

        req.setAttribute("compte", compte);
        req.getRequestDispatcher("/Pret.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}
