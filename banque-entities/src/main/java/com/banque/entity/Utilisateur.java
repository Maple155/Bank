package com.banque.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "nom", nullable = false)
    private String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Column(name = "mdp", nullable = false)
    private String mdp;

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }


    @ManyToOne
    @JoinColumn(name = "direction", nullable = false)
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Column(name = "role", nullable = false)
    private int role;
    
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Utilisateur() {
    }
    
    public Utilisateur(String nom, String mdp, Direction direction, int role) {
        this.nom = nom;
        this.mdp = mdp;
        this.direction = direction;
        this.role = role;
    }

}
