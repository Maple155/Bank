package com.banque.centralisateur.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OperationDepot {

    private int id;
    private int compte_id; // correspond à Compte_id côté .NET
    private double montant;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateOperation;

    public OperationDepot() {
    }

    public OperationDepot(int compte_id, double montant, LocalDateTime dateOperation) {
        this.compte_id = compte_id;
        this.montant = montant;
        this.dateOperation = dateOperation;
    }
    
    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompte_id() {
        return compte_id;
    }

    public void setCompte_id(int compte_id) {
        this.compte_id = compte_id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDateTime dateOperation) {
        this.dateOperation = dateOperation;
    }
}
