package com.banque.courant.dto;

import java.io.Serializable;

public class ActionRoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nomTable;
    private String action;
    private int role;
    
    // Constructeurs
    public ActionRoleDTO() {}
    
    public ActionRoleDTO(int id, String nomTable, String action, int role) {
        this.id = id;
        this.nomTable = nomTable;
        this.action = action;
        this.role = role;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNomTable() { return nomTable; }
    public void setNomTable(String nomTable) { this.nomTable = nomTable; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }
    
    @Override
    public String toString() {
        return "ActionRoleDTO{id=" + id + ", nomTable='" + nomTable + "', action='" + action + "', role=" + role + '}';
    }
}