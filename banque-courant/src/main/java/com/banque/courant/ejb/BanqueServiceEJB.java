package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.BanqueRemote;
import com.banque.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BanqueServiceEJB implements BanqueRemote{
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @Override
    public Banque find(int id) {
        return em.find(Banque.class, id);
    }

    @Override
    public List<Banque> all() {
        return em.createQuery("SELECT bq FROM Banque bq", Banque.class).getResultList();
    }
}