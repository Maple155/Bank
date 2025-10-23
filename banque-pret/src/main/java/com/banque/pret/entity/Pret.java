package com.banque.pret.entity;

import jakarta.persistence.*;
import com.banque.courant.entity.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "pret")
public class Pret implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "taux", nullable = false)
    private Double taux;

    @ManyToOne
    @JoinColumn(name = "compte_courant", nullable = false)
    private CompteCourant compteCourant;
    
    @Column(name = "date_accord", nullable = false)
    private Date date_accord; 

    @Column(name = "nbrMois", nullable = false)
    private int nbrMois; 

    // Constructors
    public Pret() {}
    
    public Pret(Double montant, Double taux, CompteCourant compteCourant, Date date_accord) 
    {
        this.montant = montant;
        this.taux = taux;
        this.compteCourant = compteCourant;
        this.date_accord = date_accord;
    }

    public Pret(Double montant, Double taux, CompteCourant compteCourant, Date date_accord, int nbrMois) {
        this.montant = montant;
        this.taux = taux;
        this.compteCourant = compteCourant;
        this.date_accord = date_accord;
        this.nbrMois = nbrMois;
    }
    
    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Double getTaux() {
        return taux;
    }

    public void setTaux(Double taux) {
        this.taux = taux;
    }

    public Date getDate_accord() {
        return date_accord;
    }

    public void setDate_accord(Date date_accord) {
        this.date_accord = date_accord;
    }

    public CompteCourant getCompteCourant() {
        return compteCourant;
    }

    public void setCompteCourant(CompteCourant compteCourant) {
        this.compteCourant = compteCourant;
    }

    public int getNbrMois() {
        return nbrMois;
    }

    public void setNbrMois(int nbrMois) {
        this.nbrMois = nbrMois;
    }
}