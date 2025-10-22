package com.banque.courant.dto;

import java.io.Serializable;

public class DirectionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String libelle;
    private int niveau;
    
    // Constructeurs
    public DirectionDTO() {}
    
    public DirectionDTO(int id, String libelle, int niveau) {
        this.id = id;
        this.libelle = libelle;
        this.niveau = niveau;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }
    
    @Override
    public String toString() {
        return "DirectionDTO{id=" + id + ", libelle='" + libelle + "', niveau=" + niveau + '}';
    }
}