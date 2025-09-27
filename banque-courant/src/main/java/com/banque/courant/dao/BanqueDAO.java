package com.banque.courant.dao;

import com.banque.courant.entity.Banque;
import com.banque.entity.Client;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BanqueDAO {

    @PersistenceContext(unitName = "banquePU") // nom de ton persistence-unit
    private EntityManager em;

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
