package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.dao.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OperationServiceEJB implements OperationRemote{
    
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @Override
    public OperationCourant find(int id) {
        OperationDAO operationDAO = new OperationDAO(em);
        return operationDAO.findById(id);
    }

    @Override
    public List<OperationCourant> all() {
        OperationDAO operationDAO = new OperationDAO(em);
        return operationDAO.findAll();
    }

    @Override
    public OperationDAO getOperationDAO () {
        return new OperationDAO(em);
    }

    @Override
    public double getSoldeActuel(int compte_id) {
        OperationDAO operationDAO = new OperationDAO(em);
        List<OperationCourant> operation = operationDAO.findByCompte(compte_id);
        double solde = 0.0;

        if (operation == null) {
            solde = 0.0;
        } else {
            for (OperationCourant operationCourant : operation) {
                if (operationCourant.getIsValidate()) {
                    solde += operationCourant.getMontant();
                }
            }
        }

        return solde;
    }

    @Override
    public void save (OperationCourant operationCourant) {
        OperationDAO operationDAO = new OperationDAO(em);
        operationDAO.save(operationCourant);
    }

    @Override
    public List<OperationCourant> findByCompte (int compte_id) {
        OperationDAO operationDAO = new OperationDAO(em);
        return operationDAO.findByCompte(compte_id);
    }
}