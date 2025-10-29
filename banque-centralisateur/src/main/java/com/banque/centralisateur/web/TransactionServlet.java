package com.banque.centralisateur.web;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.banque.courant.entity.CompteCourant;
import com.banque.courant.remote.CompteCourantRemote;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.remote.TransactionRemote;
import com.banque.courant.remote.UtilisateurRemote;
import com.banque.courant.dto.ActionRoleDTO;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    
    @EJB
    private CompteCourantRemote compteCourantService;

    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/OperationServiceEJB!com.banque.courant.remote.OperationRemote")
    private OperationRemote OSE;

    @EJB(lookup = "java:global/banque-ear-1.0-SNAPSHOT/com.banque-banque-courant-1.0-SNAPSHOT/TransactionServiceEJB!com.banque.courant.remote.TransactionRemote")
    private TransactionRemote TSE;

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/transaction.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int compteId = Integer.parseInt(req.getParameter("compte"));
        CompteCourant compte = compteCourantService.find(compteId);
        double solde = OSE.getSoldeActuel(compteId);

        req.setAttribute("compte", compte);
        req.setAttribute("solde", solde);
        forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int compteId = Integer.parseInt(request.getParameter("compte"));
        CompteCourant compte = compteCourantService.find(compteId);
        double solde = OSE.getSoldeActuel(compteId);
        String action = request.getParameter("action");

        request.setAttribute("compte", compte);

        HttpSession session = request.getSession(false);
        UtilisateurDTO utilisateurConnecte = null;
        UtilisateurRemote utilisateurRemote = null;

        UtilisateurDTO utilisateur = null;
        List<DirectionDTO> directions = null;
        List<ActionRoleDTO> actionRoles = null;
        boolean isRole = false;

        if (session != null) {
            Object o = session.getAttribute("user");
            if (o instanceof UtilisateurDTO) {
                utilisateurConnecte = (UtilisateurDTO) o;
                utilisateurRemote = (UtilisateurRemote) session.getAttribute("sessionUtilisateur");

                utilisateur = utilisateurRemote.getUtilisateurConnecte();
                directions = utilisateurRemote.getDirections();
                actionRoles = utilisateurRemote.getActionRoles();

                for (ActionRoleDTO actionRoleDTO : actionRoles) {
                    if (actionRoleDTO.getNomTable().equalsIgnoreCase("operation_courant")
                            && actionRoleDTO.getRole() == utilisateur.getRole()) {
                        isRole = true;
                        break;
                    }
                }
            }
        }

        if (isRole == false) {
            request.setAttribute("error", "Vous ne pouvez pas effectuer cette operation");
            return;
        } else {

            try {
                switch (action) {
                    case "search":
                        handleSearch(request, solde);
                        break;
                    case "send":
                        handleSend(request, solde, compte);
                        break;
                    default:
                        request.setAttribute("error", "Action inconnue");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Erreur inattendue");
            }
        }

        forward(request, response);
    }

    private void handleSearch(HttpServletRequest request, double solde) {
        String numero = request.getParameter("numero");
        CompteCourant receiver = compteCourantService.findByNumero(numero);

        request.setAttribute("solde", solde);

        if (receiver == null) {
            request.setAttribute("error", "Le destinataire n'existe pas");
        } else if (request.getAttribute("compte") != null &&
                ((CompteCourant) request.getAttribute("compte")).getNumero().equals(receiver.getNumero())) {
            request.setAttribute("error", "Le destinataire ne peut pas être vous-même");
        } else {
            request.setAttribute("receiver", receiver);
            request.setAttribute("message", "Envoyer de l'argent à Mr/Mme " + receiver.getClient().getNom());
        }
    }

    private void handleSend(HttpServletRequest request, double solde, CompteCourant sender) {
        int receiverId = Integer.parseInt(request.getParameter("receiver"));
        CompteCourant receiver = compteCourantService.find(receiverId);
        double montant = Double.parseDouble(request.getParameter("montant"));
        double reste = solde - montant;

        request.setAttribute("receiver", receiver);
        request.setAttribute("solde", solde);

        if (montant <= 0) {
            request.setAttribute("error", "Le montant doit être supérieur à 0");
        } else if (reste < 0) {
            request.setAttribute("error", "Vous n'avez pas les fonds nécessaires");
        } else {
            TSE.effectuerTransfert(sender, receiver, montant);
            solde = OSE.getSoldeActuel(sender.getId());
            request.setAttribute("solde", solde);
            request.setAttribute("message", "Transfert réussi");
        }
    }
}
