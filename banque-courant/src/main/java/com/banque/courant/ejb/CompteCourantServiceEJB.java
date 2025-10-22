package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.CompteCourantRemote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantServiceEJB implements CompteCourantRemote{
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @Override
    public CompteCourant find(int id) {
        return em.find(CompteCourant.class, id);
    }

    @Override
    public List<CompteCourant> all() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }
}