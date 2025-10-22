package com.banque.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "action_role")
public class Action_role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "nom_table", nullable = false)
    private String nom_table;

    public String getNom_table() {
        return nom_table;
    }

    public void setNom_table(String nom_table) {
        this.nom_table = nom_table;
    }

    @Column(name = "role", nullable = false)
    private int role;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Column(name = "action", nullable = false)
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Action_role() {
    }
    
    
    public Action_role(String nom_table, int role, String action) {
        this.nom_table = nom_table;
        this.role = role;
        this.action = action;
    }
    
}
