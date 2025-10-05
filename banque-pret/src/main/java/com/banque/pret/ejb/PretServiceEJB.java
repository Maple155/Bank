package com.banque.pret.ejb;

import com.banque.pret.entity.*;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.entity.CompteCourant;
import com.banque.courant.entity.OperationCourant;
import com.banque.entity.TypesStatut;
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
    @EJB
    private PretStatutDAO pretStatutDAO;
    @EJB
    private TypeStatutDAO typeStatutDAO;

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

    public PretStatut getPretsImpayesByCompte(int compte_id) {
        try {
            List<PretStatut> prets = pretStatutDAO.getPretsAvecStatutActuelByCompte(compte_id);
            for (PretStatut pret : prets) {
                if (pret.getStatut().getType().equalsIgnoreCase("En cours")) {
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
        try {
            Remboursement remboursement = new Remboursement(pret, montant, currDate);
            pretDAO.saveRemboursement(remboursement);
    
            OperationCourant operation = new OperationCourant(compteCourant, (montant * -1), currDate);
            operationDAO.save(operation);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void demanderPret(double montant, CompteCourant compte, Date currDate) {
        try {
            Pret pret = new Pret(montant, 24.0, compte, currDate);
            pretDAO.save(pret);
    
            // TypesStatut type = typeStatutDAO.findByType("En cours");
            TypesStatut type = typeStatutDAO.findByType("En attente");
    
            PretStatut pretStatut = new PretStatut(pret, type, currDate);
            pretStatutDAO.save(pretStatut);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}