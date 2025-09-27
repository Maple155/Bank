package com.banque.courant.entity;

import jakarta.persistence.*;
import com.banque.entity.*;
import java.sql.Date;

@Entity
@Table(name = "banque")
public class Banque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "capital", nullable = false)
    private double capital;

    public Banque() {
    }

    public Banque(double capital) {
        this.capital = capital;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getCapital() { return capital; }
    public void setCapital(double capital) { this.capital = capital; }
}