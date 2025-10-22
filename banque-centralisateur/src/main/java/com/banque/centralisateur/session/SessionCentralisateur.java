package com.banque.centralisateur.session;

import com.banque.courant.remote.UtilisateurRemote;
import jakarta.ejb.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SessionCentralisateur implements SessionCentralisateurRemote {
    
    private final Map<String, UtilisateurRemote> sessions = new ConcurrentHashMap<>();

    @Override
    public void enregistrerSession(String sessionId, UtilisateurRemote sessionUtilisateur) {
        sessions.put(sessionId, sessionUtilisateur);
        System.out.println("Session enregistrée: " + sessionId);
    }

    @Override
    public UtilisateurRemote getSessionUtilisateur(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void supprimerSession(String sessionId) {
        sessions.remove(sessionId);
        System.out.println("Session supprimée: " + sessionId);
    }

    @Override
    public boolean estSessionValide(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}