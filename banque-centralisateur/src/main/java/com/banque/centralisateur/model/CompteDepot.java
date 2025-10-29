package com.banque.centralisateur.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

public class CompteDepot {

    private int id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String numero;

    private String codeSecret;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateOuverture;

    private int clientId;

    private String etat;

    public CompteDepot() {
    }

    public CompteDepot(int id, String numero, String codeSecret, LocalDateTime dateOuverture, int clientId) {
        this.id = id;
        this.numero = numero;
        this.codeSecret = codeSecret;
        this.dateOuverture = dateOuverture;
        this.clientId = clientId;
        this.etat = "ouvert";
    }

    public CompteDepot(String numero, String codeSecret, LocalDateTime dateOuverture, int clientId, String etat) {
        this.numero = numero;
        this.codeSecret = codeSecret;
        this.dateOuverture = dateOuverture;
        this.clientId = clientId;
        this.etat = etat;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodeSecret() {
        return codeSecret;
    }

    public void setCodeSecret(String codeSecret) {
        this.codeSecret = codeSecret;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
    
}
