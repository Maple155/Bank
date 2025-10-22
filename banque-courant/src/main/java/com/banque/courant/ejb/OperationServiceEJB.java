package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.OperationRemote;
import com.banque.courant.dao.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class OperationServiceEJB implements OperationRemote{
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @EJB
    private OperationDAO operationDAO;

    @Override
    public OperationCourant find(int id) {
        return em.find(OperationCourant.class, id);
    }

    @Override
    public List<OperationCourant> all() {
        return em.createQuery("SELECT op FROM OperationCourant op", OperationCourant.class).getResultList();
    }

    @Override
    public double getSoldeActuel(int compte_id) {
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
}