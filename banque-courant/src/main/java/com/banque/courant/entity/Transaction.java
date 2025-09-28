package com.banque.courant.entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private CompteCourant sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private CompteCourant receiver;

    @Column(name = "montant", nullable = false)
    private double montant;

    @Column(name = "date_transaction", nullable = false)
    private Date dateTransaction;

    public Transaction() {
    }
    
    public Transaction(CompteCourant sender, CompteCourant receiver, double montant, Date dateTransaction) {
        this.sender = sender;
        this.receiver = receiver;
        this.montant = montant;
        this.dateTransaction = dateTransaction;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CompteCourant getSender() {
        return sender;
    }

    public void setSender(CompteCourant sender) {
        this.sender = sender;
    }

    public CompteCourant getReceiver() {
        return receiver;
    }

    public void setReceiver(CompteCourant receiver) {
        this.receiver = receiver;
    }
    
    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
}