package com.banque.courant.entity;

import jakarta.persistence.*;
import com.banque.entity.*;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "compte_courant")
public class CompteCourant implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numero", unique = true, nullable = false)
    private String numero;

    @Column(name = "date_ouverture", nullable = false)
    private Date dateOuverture;

    // Relation avec Client (Many comptes pour 1 client)
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)   
    private Client client;

    @Column(name = "code_secret", nullable = false)
    private String code_secret;

    @Column(name = "etat", nullable = false)
    private String etat;

    // Constructeurs
    public CompteCourant() {}

    public CompteCourant(String numero, Date dateOuverture, Client client) {
        this.numero = numero;
        this.dateOuverture = dateOuverture;
        this.client = client;
    }

    public CompteCourant(String numero, Date dateOuverture, Client client, String code_secret, String etat) {
        this.numero = numero;
        this.dateOuverture = dateOuverture;
        this.client = client;
        this.code_secret = code_secret;
        this.etat = etat;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Date getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(Date dateOuverture) { this.dateOuverture = dateOuverture; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getCode_secret() { return code_secret; }
    public void setCode_secret(String code_secret) { this.code_secret = code_secret; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}
