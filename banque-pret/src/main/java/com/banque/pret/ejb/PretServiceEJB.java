package com.banque.pret.ejb;

import com.banque.pret.entity.*;
import com.banque.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PretServiceEJB {
    @PersistenceContext
    private EntityManager em;

    public Pret findPret(int id) {
        return em.find(Pret.class, id);
    }

    public void savePret(Pret pret) {
        if (pret.getId() == 0) {
            em.persist(pret);
        } else {
            em.merge(pret);
        }
    }

    public List<Pret> allPrets() {
        return em.createQuery("SELECT p FROM Pret p", Pret.class).getResultList();
    }

    public void saveRemboursement(Remboursement remboursement) {
        if (remboursement.getId() == 0) {
            em.persist(remboursement);
        } else {
            em.merge(remboursement);
        }
    }

    public List<Remboursement> remboursementsPret(int pretId) {
        return em.createQuery("SELECT r FROM Remboursement r WHERE r.pret.id = :pretId", Remboursement.class)
                .setParameter("pretId", pretId)
                .getResultList();
    }
}