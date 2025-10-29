package com.banque.courant.remote;

import java.util.List;

import com.banque.courant.entity.CompteCourant;
import com.banque.entity.Client;

import jakarta.ejb.Remote;

@Remote
public interface CompteCourantRemote {
    
    public CompteCourant find(int id);
    public List<CompteCourant> all();
    void save(CompteCourant compteCourant);
    CompteCourant findCompte(Client client, String numero, String password);
    CompteCourant findByNumero(String numero);
    List<CompteCourant> findByClient(Client client);
}
