package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.banque.courant.entity.*;
import com.banque.entity.*;
import com.banque.courant.dao.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/listeClient")
public class ListeClientServlet extends HttpServlet {

    @EJB
    private ClientDAO clientDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Client> clientsList = clientDAO.findAll();

        req.setAttribute("clients", clientsList);
        req.getRequestDispatcher("/listClient.jsp").forward(req, resp);
    }
}
