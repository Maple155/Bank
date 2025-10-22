package com.banque.courant.remote;

import java.util.List;

import com.banque.courant.entity.CompteCourant;

import jakarta.ejb.Remote;

@Remote
public interface CompteCourantRemote {
    
    public CompteCourant find(int id);
    public List<CompteCourant> all();
}
