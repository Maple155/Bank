package com.banque.courant.dao;

import com.banque.courant.entity.*;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OperationDAO {

    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    public OperationCourant findById(Long id) {
        return em.find(OperationCourant.class, id);
    }

    public void save(OperationCourant operationCourant) {
        if (operationCourant.getId() == 0) {
            em.persist(operationCourant);
        } else {
            em.merge(operationCourant);
        }
    }

    public List<OperationCourant> findAll() {
        return em.createQuery("SELECT op FROM OperationCourant op", OperationCourant.class).getResultList();
    }

    public List<OperationCourant> findByCompte(int compte_id) {
        try {
            String jpql = "SELECT op FROM OperationCourant op WHERE op.compte.id = :compteID";

            return em.createQuery(jpql, OperationCourant.class)
                    .setParameter("compteID", compte_id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}