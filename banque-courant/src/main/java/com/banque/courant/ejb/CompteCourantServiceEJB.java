package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantServiceEJB {
    @PersistenceContext
    private EntityManager em;

    public CompteCourant find(int id) {
        return em.find(CompteCourant.class, id);
    }

    public List<CompteCourant> all() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }
}