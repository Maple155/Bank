package com.banque.courant.dao;

import com.banque.courant.entity.Banque;
import jakarta.persistence.EntityManager;
import java.util.List;

public class BanqueDAO {

    private EntityManager em;

    public BanqueDAO(EntityManager em) {
        this.em = em;
    }

    public Banque findById(int id) {
        return em.find(Banque.class, id);
    }

    public void save(Banque banque) {
        if (banque.getId() == 0) {
            em.persist(banque);
        } else {
            em.merge(banque);
        }
    }

    public List<Banque> findAll() {
        return em.createQuery("SELECT bq FROM Banque bq", Banque.class).getResultList();
    }
}
