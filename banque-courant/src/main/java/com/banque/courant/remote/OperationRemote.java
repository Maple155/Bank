package com.banque.courant.remote;

import java.util.List;

import com.banque.courant.entity.OperationCourant;

import jakarta.ejb.Remote;

@Remote
public interface OperationRemote {
    
    public OperationCourant find(int id);
    public List<OperationCourant> all();
    public double getSoldeActuel(int compte_id);
}
