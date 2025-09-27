package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BanqueServiceEJB {
    @PersistenceContext
    private EntityManager em;

    public Banque find(int id) {
        return em.find(Banque.class, id);
    }

    public List<Banque> all() {
        return em.createQuery("SELECT bq FROM Banque bq", Banque.class).getResultList();
    }
}