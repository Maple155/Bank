package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ClientServiceEJB {
    @PersistenceContext
    private EntityManager em;

    public Client find(int id) {
        return em.find(Client.class, id);
    }

    public List<Client> all() {
        return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }
}