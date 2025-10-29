package com.banque.courant.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "operation_courant")
public class OperationCourant implements Serializable{
    private static final long serialVersionUID = 1L;

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

    @Column(name = "isValidate", nullable = false)
    private Boolean isValidate;
    
    // Constructeurs
    public OperationCourant() {}

    public OperationCourant(CompteCourant compte, double montant, Date dateOperation, Boolean isValidate) {
        this.compte = compte;
        this.montant = montant;
        this.dateOperation = dateOperation;
        this.isValidate = isValidate;
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

    public Boolean getIsValidate() { return isValidate; }
    public void setIsValidate(Boolean isValidate) { this.isValidate = isValidate; }
}
