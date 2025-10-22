package com.banque.courant.dto;

import java.io.Serializable;

public class UtilisateurDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nom;
    private String mdp;
    private DirectionDTO direction; // Référence à DirectionDTO au lieu de l'entité
    private int role;
    
    // Constructeurs
    public UtilisateurDTO() {}
    
    public UtilisateurDTO(int id, String nom, String mdp, DirectionDTO direction, int role) {
        this.id = id;
        this.nom = nom;
        this.mdp = mdp;
        this.direction = direction;
        this.role = role;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    
    public DirectionDTO getDirection() { return direction; }
    public void setDirection(DirectionDTO direction) { this.direction = direction; }
    
    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }
    
    @Override
    public String toString() {
        return "UtilisateurDTO{id=" + id + ", nom='" + nom + "', direction=" + direction + ", role=" + role + '}';
    }
}