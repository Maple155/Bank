package com.banque.centralisateur.web;

import com.banque.centralisateur.session.SessionCentralisateurRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @EJB
    private SessionCentralisateurRemote sessionCentralisateur;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            String sessionId = (String) httpSession.getAttribute("sessionId");
            if (sessionId != null) {
                sessionCentralisateur.supprimerSession(sessionId);
            }
            httpSession.invalidate();
        }
        
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}