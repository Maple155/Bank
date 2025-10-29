package com.banque.courant.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable{
    private static final long serialVersionUID = 1L;
    
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

    @Column(name = "isValidate", nullable = false)
    private boolean isValidate;
    
    public Transaction() {
    }
    
    public Transaction(CompteCourant sender, CompteCourant receiver, double montant, Date dateTransaction, boolean isValidate) {
        this.sender = sender;
        this.receiver = receiver;
        this.montant = montant;
        this.dateTransaction = dateTransaction;
        this.isValidate = isValidate;
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

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean isValidate) {
        this.isValidate = isValidate;
    }

}