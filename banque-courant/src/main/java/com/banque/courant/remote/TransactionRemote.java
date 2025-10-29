package com.banque.courant.remote;

import java.util.List;

import com.banque.courant.entity.CompteCourant;
import com.banque.courant.entity.Transaction;
import jakarta.ejb.Remote;

@Remote
public interface TransactionRemote {

    public Transaction find(int id);
    public List<Transaction> all();
    public void effectuerTransfert(CompteCourant compte, CompteCourant receiver, double montant);
    void save(Transaction transaction);
    List<Transaction> findBySender(int sender_id);
    List<Transaction> findByReceiver(int receiver_id);
} 
