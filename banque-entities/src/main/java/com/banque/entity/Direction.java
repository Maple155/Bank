package com.banque.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "direction")
public class Direction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "niveau", nullable = false)
    private int niveau;

    public Direction() {
    }

    public Direction(String libelle, int niveau) {
        this.libelle = libelle;
        this.niveau = niveau;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}
