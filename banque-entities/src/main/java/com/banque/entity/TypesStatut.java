package com.banque.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "types_statut")
public class TypesStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type", nullable = false)
    private String type;

    public TypesStatut() {}

    public TypesStatut(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
