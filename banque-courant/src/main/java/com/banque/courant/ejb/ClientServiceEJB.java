package com.banque.courant.ejb;

import com.banque.courant.dao.ClientDAO;
import com.banque.courant.remote.ClientRemote;
import com.banque.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ClientServiceEJB implements ClientRemote{

    @PersistenceContext(unitName = "banquePU") // nom de ton persistence-unit
    private EntityManager em;

    @Override
    public Client find(int id) {
        ClientDAO clientDAO = new ClientDAO(em);
        return clientDAO.findById(id);
    }

    @Override
    public List<Client> all() {
        ClientDAO clientDAO = new ClientDAO(em);
        return clientDAO.findAll();
    }

    @Override
    public Client findByEmail(String email) { 
        ClientDAO clientDAO = new ClientDAO(em);
        return clientDAO.findByEmail(email);
    }

    @Override
    public void save (Client client) {
        ClientDAO clientDAO = new ClientDAO(em);
        clientDAO.save(client);
    }

}