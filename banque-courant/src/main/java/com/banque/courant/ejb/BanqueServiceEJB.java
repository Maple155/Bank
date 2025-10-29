package com.banque.courant.ejb;

import com.banque.courant.dao.BanqueDAO;
import com.banque.courant.entity.*;
import com.banque.courant.remote.BanqueRemote;
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
        BanqueDAO banqueDAO = new BanqueDAO(em);
        return banqueDAO.findById(id);
    }

    @Override
    public List<Banque> all() {
        BanqueDAO banqueDAO = new BanqueDAO(em);
        return banqueDAO.findAll();
    }

    @Override
    public void save(Banque banque) {
        BanqueDAO banqueDAO = new BanqueDAO(em);
        banqueDAO.save(banque);
    }
}