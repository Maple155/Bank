package com.banque.change.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;

public class Change implements Serializable{
    private static final long serialVersionUID = 1L;

    private int id;

    private String devise;

    private String dateDebut;

    private String dateFin;

    private int cours;

    public Change() {
    }
    
    public Change(String devise, String dateDebut, String dateFin, int cours) {
        this.devise = devise;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.cours = cours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
    
    public int getCours() {
        return cours;
    }

    public void setCours(int cours) {
        this.cours = cours;
    }

}