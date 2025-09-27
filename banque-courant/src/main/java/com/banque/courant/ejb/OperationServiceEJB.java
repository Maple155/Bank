package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.dao.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class OperationServiceEJB {
    @PersistenceContext
    private EntityManager em;

    @EJB
    private OperationDAO operationDAO;

    public OperationCourant find(int id) {
        return em.find(OperationCourant.class, id);
    }

    public List<OperationCourant> all() {
        return em.createQuery("SELECT op FROM OperationCourant op", OperationCourant.class).getResultList();
    }

    public double getSoldeActuel(int compte_id) {
        List<OperationCourant> operation = operationDAO.findByCompte(compte_id);
        double solde = 0.0;

        if (operation == null) {
            solde = 0.0;
        } else {
            for (OperationCourant operationCourant : operation) {
                solde += operationCourant.getMontant();
            }
        }

        return solde;
    }
}