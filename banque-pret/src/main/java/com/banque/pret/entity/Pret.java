package com.banque.pret.entity;

import jakarta.persistence.*;
// import com.banque.courant.entity.*;
import com.banque.entity.*;
import java.sql.Date;

@Entity
@Table(name = "pret")
public class Pret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "taux", nullable = false)
    private Double taux;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "date_accord", nullable = false)
    private Date date_accord; 
    
    @Column(name = "statut", nullable = false)
    private String statut;

    // Constructors
    public Pret() {}
    
    public Pret(Double montant, Double taux, Client client, Date date_accord, String statut) 
    {
        this.montant = montant;
        this.taux = taux;
        this.client = client;
        this.date_accord = date_accord;
        this.statut = statut;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate_accord() {
        return date_accord;
    }

    public void setDate_accord(Date date_accord) {
        this.date_accord = date_accord;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}