package com.banque.centralisateur.web;

import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.courant.remote.UtilisateurRemote;
import com.banque.centralisateur.session.SessionCentralisateurRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.naming.Context;
import javax.naming.InitialContext;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private SessionCentralisateurRemote sessionCentralisateur;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/connexionUser.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        
        String login = req.getParameter("login");
        String mdp = req.getParameter("mdp");

        try {
            UtilisateurRemote sessionUtilisateur = lookupUtilisateurEJB();

            if (sessionUtilisateur.login(login, mdp)) {

                // mettre en session sessionUtilisateur
                UtilisateurDTO utilisateur = sessionUtilisateur.getUtilisateurConnecte();

                // créer / récupérer la session et stocker l'utilisateur
                HttpSession session = req.getSession(true);
                session.setAttribute("sessionUtilisateur", sessionUtilisateur);
                
                session.setAttribute("user", utilisateur);
                session.setAttribute("actionRoles", sessionUtilisateur.getActionRoles());
                session.setAttribute("directions", sessionUtilisateur.getDirections());

                // durée d'inactivité : 30 minutes (exemple)
                // session.setMaxInactiveInterval(30 * 60);

                for (ActionRoleDTO actions : sessionUtilisateur.getActionRoles()) {
                    System.out.println("actions : " + actions.toString());
                    resp.getWriter().println("actions : " + actions.toString());
                }

                // req.getRequestDispatcher("/connexion").forward(req, resp);
                resp.sendRedirect(req.getContextPath() + "/connexion");

            } else {
                req.setAttribute("error", "Login ou mot de passe incorrect");
                req.getRequestDispatcher("/connexionUser.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors de la connexion : " + e.getMessage());
            req.getRequestDispatcher("/connexionUser.jsp").forward(req, resp);
        }
    }

    private UtilisateurRemote lookupUtilisateurEJB() throws Exception {
        Context ctx = new InitialContext();

        // JNDI name pour WildFly/JBoss
        // Format: java:global/<ear-name>/<module-name>/<ejb-name>!<interface-name>
        return (UtilisateurRemote) ctx.lookup(
                "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/UtilisateurServiceEJB!com.banque.courant.remote.UtilisateurRemote");
    }
}