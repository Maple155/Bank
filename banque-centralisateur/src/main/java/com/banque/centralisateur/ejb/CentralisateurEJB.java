package com.banque.centralisateur.ejb;

import jakarta.ejb.Stateless;

@Stateless
public class CentralisateurEJB {
    public String status() {
        return "Centralisateur opérationnel";
    }
}