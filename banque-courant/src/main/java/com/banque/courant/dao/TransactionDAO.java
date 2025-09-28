package com.banque.courant.dao;

import com.banque.courant.entity.*;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TransactionDAO {
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    public Transaction findById(Long id) {
        return em.find(Transaction.class, id);
    }

    public void save(Transaction transaction) {
        if (transaction.getId() == 0) {
            em.persist(transaction);
        } else {
            em.merge(transaction);
        }
    }

    public List<Transaction> findAll() {
        return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
    }

    public List<Transaction> findBySender(int sender_id) {
        try {
            String jpql = "SELECT op FROM Transaction op WHERE op.sender.id = :senderID";

            return em.createQuery(jpql, Transaction.class)
                    .setParameter("senderID", sender_id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Transaction> findByReceiver(int receiver_id) {
        try {
            String jpql = "SELECT op FROM Transaction op WHERE op.receiver.id = :receiverID";

            return em.createQuery(jpql, Transaction.class)
                    .setParameter("sreceiverID", receiver_id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
