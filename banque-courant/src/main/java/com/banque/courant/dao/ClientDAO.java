package com.banque.courant.dao;

import com.banque.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class ClientDAO {

    @PersistenceContext(unitName = "banquePU") // nom de ton persistence-unit
    private EntityManager em;

    public ClientDAO(EntityManager em) {
        this.em = em;
    }

    public Client findById(int id) {
        return em.find(Client.class, id);
    }

    public void save(Client client) {
        if (client.getId() == 0) {
            em.persist(client);
        } else {
            em.merge(client);
        }
    }

    public List<Client> findAll() {
        return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }

    public Client findByEmail(String email) {
        try {
            String jpql = "SELECT c FROM Client c WHERE c.email = :email";
            return em.createQuery(jpql, Client.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }

}
