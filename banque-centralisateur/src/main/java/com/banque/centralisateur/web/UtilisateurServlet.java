package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

import com.banque.courant.remote.UtilisateurRemote;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.UtilisateurDTO;

@WebServlet("/utilisateur")
public class UtilisateurServlet extends HttpServlet {
    
    // SIMPLIFIER - enlever le lookup complexe
    @EJB
    private UtilisateurRemote utilisateurService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        req.getRequestDispatcher("/connexionUser.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    
        String nom = req.getParameter("login");
        String mdp = req.getParameter("mdp");

        boolean isTrue = utilisateurService.login(nom, mdp);

        if (isTrue) {
            List<DirectionDTO> directions = utilisateurService.getDirections();
            List<ActionRoleDTO> actionRoles = utilisateurService.getActionRoles();
            UtilisateurDTO user = utilisateurService.getUtilisateurConnecte();

            req.setAttribute("directions", directions);
            req.setAttribute("actionRoles", actionRoles);
            req.setAttribute("user", user);

            resp.sendRedirect(req.getContextPath() + "/connexion");
            return;
        }   

        req.setAttribute("error", "Nom ou mot de passe incorrect");
        req.getRequestDispatcher("/connexionUser.jsp").forward(req, resp);
    }
}