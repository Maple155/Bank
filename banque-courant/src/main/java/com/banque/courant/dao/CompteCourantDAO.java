package com.banque.courant.dao;

import com.banque.courant.entity.*;
import com.banque.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class CompteCourantDAO {

    private EntityManager em;

    public CompteCourantDAO(EntityManager em) {
        this.em = em;
    }

    public CompteCourant findById(int id) {
        return em.find(CompteCourant.class, id);
    }

    public void save(CompteCourant compte) {
        if (compte.getId() == 0) {
            em.persist(compte);
        } else {
            em.merge(compte);
        }
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    public CompteCourant findCompte(Client client, String numero, String password) {
        try {
            String jpql = "SELECT c FROM CompteCourant c " +
            "WHERE c.numero = :numero " +
            "AND c.code_secret = :password " +  // correspond à la propriété Java code_secret
            "AND c.client = :client";
            
            return em.createQuery(jpql, CompteCourant.class)
                     .setParameter("numero", numero)
                     .setParameter("password", password)
                     .setParameter("client", client)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CompteCourant findByNumero(String numero) {
        try {
            String jpql = "SELECT c FROM CompteCourant c " +
            "WHERE c.numero = :numero ";
            
            return em.createQuery(jpql, CompteCourant.class)
                     .setParameter("numero", numero)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CompteCourant> findByClient(Client client) {
        try {
            String jpql = "SELECT c FROM CompteCourant c " +
            "WHERE c.client.id = :clientId ";
            
            return em.createQuery(jpql, CompteCourant.class)
                     .setParameter("clientId", client.getId())
                     .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
}