package com.banque.courant.ejb;

import com.banque.courant.dao.CompteCourantDAO;
import com.banque.courant.entity.*;
import com.banque.courant.remote.CompteCourantRemote;
import com.banque.entity.Client;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class CompteCourantServiceEJB implements CompteCourantRemote{

    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @Override
    public CompteCourant find(int id) {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        return compteCourantDAO.findById(id);
    }

    @Override
    public List<CompteCourant> all() {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        return compteCourantDAO.findAll();
    }

    @Override
    public void save(CompteCourant compteCourant) {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        compteCourantDAO.save(compteCourant);
    }

    @Override 
    public CompteCourant findCompte(Client client, String numero, String password) {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        return compteCourantDAO.findCompte(client, numero, password);
    }

    @Override
    public CompteCourant findByNumero (String numero) {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        return compteCourantDAO.findByNumero(numero);
    }

    @Override
    public List<CompteCourant> findByClient (Client client) {
        CompteCourantDAO compteCourantDAO = new CompteCourantDAO(em);
        return compteCourantDAO.findByClient(client);
    } 

}