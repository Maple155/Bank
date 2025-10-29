package com.banque.courant.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "banque")
public class Banque implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "capital", nullable = false)
    private double capital;

    @Column(name = "taux_depot", nullable = false)
    private double tauxDepot;

    @Column(name = "taux_pret", nullable = false)
    private double tauxPret;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "siege", nullable = false)
    private String siege;

    public Banque() {
    }
    
    public Banque(double capital, double tauxDepot, double tauxPret, String nom, String siege) {
        this.capital = capital;
        this.tauxDepot = tauxDepot;
        this.tauxPret = tauxPret;
        this.nom = nom;
        this.siege = siege;
    }    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getCapital() { return capital; }
    public void setCapital(double capital) { this.capital = capital; }

    public double getTauxPret() { return tauxPret; }
    public void setTauxPret(double tauxPret) { this.tauxPret = tauxPret; }

    public double getTauxDepot() { return tauxDepot; }
    public void setTauxDepot(double tauxDepot) { this.tauxDepot = tauxDepot; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSiege() { return siege; }
    public void setSiege(String siege) { this.siege = siege; }
}