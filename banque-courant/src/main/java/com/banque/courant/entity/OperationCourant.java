package com.banque.courant.entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_courant")
public class OperationCourant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private CompteCourant compte;

    @Column(name = "montant", nullable = false)
    private double montant;

    @Column(name = "date_operation", nullable = false)
    private Date dateOperation;

    // Constructeurs
    public OperationCourant() {}

    public OperationCourant(CompteCourant compte, double montant, Date dateOperation) {
        this.compte = compte;
        this.montant = montant;
        this.dateOperation = dateOperation;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public CompteCourant getCompte() { return compte; }
    public void setCompte(CompteCourant compte) { this.compte = compte; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Date getDateOperation() { return dateOperation; }
    public void setDateOperation(Date dateOperation) { this.dateOperation = dateOperation; }
}
