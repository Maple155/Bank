package com.banque.pret.dao;

import com.banque.pret.entity.*;
import com.banque.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class PretDAO {
    @PersistenceContext
    private EntityManager em;

    public Pret findById(int id) {
        return em.find(Pret.class, id);
    }

    public void save(Pret pret) {
        if (pret.getId() == 0) {
            em.persist(pret);
        } else {
            em.merge(pret);
        }
    }

    public List<Pret> findAll() {
        return em.createQuery("SELECT p FROM Pret p", Pret.class).getResultList();
    }
}