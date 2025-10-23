package com.banque.pret.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "remboursement")
public class Remboursement implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pret_id", nullable = false)
    private Pret pret;

    @Column(name = "montant",nullable = false)
    private Double montant;

    @Column(name = "date_remboursement", nullable = false)
    private Date dateRemboursement;

    public Remboursement() {}

    public Remboursement(Pret pret, Double montant, Date dateRemboursement) {
        this.pret = pret;
        this.montant = montant;
        this.dateRemboursement = dateRemboursement;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Pret getPret() { return pret; }
    public void setPret(Pret pret) { this.pret = pret; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Date getDateRemboursement() { return dateRemboursement; }
    public void setDateRemboursement(Date dateRemboursement) { this.dateRemboursement = dateRemboursement; }
}
