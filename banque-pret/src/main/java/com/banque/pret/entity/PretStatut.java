package com.banque.pret.entity;

import jakarta.persistence.*;
import com.banque.courant.entity.*;
import com.banque.entity.TypesStatut;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "pret_statut")
public class PretStatut implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pret", nullable = false)
    private Pret pret;

    @ManyToOne
    @JoinColumn(name = "statut", nullable = false)
    private TypesStatut statut;
    
    @Column(name = "date_statut", nullable = false)
    private Date date_statut;

    public PretStatut() {
    }

    public PretStatut(Pret pret, TypesStatut statut, Date date_statut) {
        this.pret = pret;
        this.statut = statut;
        this.date_statut = date_statut;
    } 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Pret getPret() {
        return pret;
    }

    public void setPret(Pret pret) {
        this.pret = pret;
    }
    
    public TypesStatut getStatut() {
        return statut;
    }

    public void setStatut(TypesStatut statut) {
        this.statut = statut;
    }
    
    public Date getDate_statut() {
        return date_statut;
    }

    public void setDate_statut(Date date_statut) {
        this.date_statut = date_statut;
    }
}
