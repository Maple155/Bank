package com.banque.pret.dao;

import com.banque.courant.entity.*;
import com.banque.pret.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PretDAO {

    @PersistenceContext(unitName = "banquePretPU")
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

    public void saveRemboursement(Remboursement remboursement) {
        if (remboursement.getId() == 0) {
            em.persist(remboursement);
        } else {
            em.merge(remboursement);
        }
    }

    public List<Pret> findAll() {
        return em.createQuery("SELECT p FROM Pret p", Pret.class).getResultList();
    }

    public List<Pret> findByCompte(int compte) {
        try {
            String jpql = "SELECT p FROM Pret p WHERE p.compteCourant.id = :compteId";
            return em.createQuery(jpql, Pret.class)
                    .setParameter("compteId", compte)
                    .getResultList();
        } catch (NoResultException e) {
            return null; 
        }
    }

    public List<Remboursement> getRemboursementByPret(int pret_id) {
        try {
            String jpql = "SELECT r FROM Remboursement r WHERE r.pret.id = :pretId";
            return em.createQuery(jpql, Remboursement.class)
                    .setParameter("pretId", pret_id)
                    .getResultList();
        } catch (NoResultException e) {
            return null; 
        }
    }
}