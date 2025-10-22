package com.banque.centralisateur.session;

import com.banque.courant.remote.UtilisateurRemote;
import jakarta.ejb.Remote;

@Remote
public interface SessionCentralisateurRemote {
    void enregistrerSession(String sessionId, UtilisateurRemote sessionUtilisateur);
    UtilisateurRemote getSessionUtilisateur(String sessionId);
    void supprimerSession(String sessionId);
    boolean estSessionValide(String sessionId);
}