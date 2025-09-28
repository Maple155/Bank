package com.banque.pret.ejb;

import com.banque.pret.entity.*;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.entity.CompteCourant;
import com.banque.courant.entity.OperationCourant;
import com.banque.pret.dao.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.sql.Date;
import java.util.List;

@Stateless
public class PretServiceEJB {
    @PersistenceContext
    private EntityManager em;
    @EJB 
    private PretDAO pretDAO;
    // @EJB
    // private OperationDAO operationDAO;

    public Pret findPret(int id) {
        return em.find(Pret.class, id);
    }

    public void savePret(Pret pret) {
        if (pret.getId() == 0) {
            em.persist(pret);
        } else {
            em.merge(pret);
        }
    }

    public List<Pret> allPrets() {
        return em.createQuery("SELECT p FROM Pret p", Pret.class).getResultList();
    }

    public void saveRemboursement(Remboursement remboursement) {
        if (remboursement.getId() == 0) {
            em.persist(remboursement);
        } else {
            em.merge(remboursement);
        }
    }

    public List<Remboursement> remboursementsPret(int pretId) {
        return em.createQuery("SELECT r FROM Remboursement r WHERE r.pret.id = :pretId", Remboursement.class)
                .setParameter("pretId", pretId)
                .getResultList();
    }

    public Pret getPretsImpayesByCompte(int compte_id) {
        try {
            List<Pret> prets = pretDAO.findByCompte(compte_id);
            for (Pret pret : prets) {
                if (pret.getStatut().equals("en_cours")) {
                    return pret;
                }
            }
        } catch (NoResultException e) {
            return null; 
        }
        return null;
    }

    public double resteAPaye (int pret_id) {
        Pret pret = pretDAO.findById(pret_id);
        List<Remboursement> remboursements = pretDAO.getRemboursementByPret(pret_id);

        double montantApaye = pret.getMontant() + ((pret.getMontant() * pret.getTaux()) / 100);
        double rembourse = 0.0;
        if (remboursements != null) {
            for (Remboursement remboursement : remboursements) {
                rembourse += remboursement.getMontant();
            }            
        }

        return montantApaye - rembourse;
    }

    public void rembourserPret (Pret pret, CompteCourant compteCourant, double montant, Date currDate, OperationDAO operationDAO) {
        Remboursement remboursement = new Remboursement(pret, montant, currDate);
        pretDAO.saveRemboursement(remboursement);

        OperationCourant operation = new OperationCourant(compteCourant, (montant * -1), currDate);
        operationDAO.save(operation);
    }

}