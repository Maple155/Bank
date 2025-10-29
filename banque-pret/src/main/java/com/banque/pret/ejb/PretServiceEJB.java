package com.banque.pret.ejb;

import com.banque.pret.entity.*;
import com.banque.pret.remote.PretRemote;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.entity.CompteCourant;
import com.banque.courant.entity.OperationCourant;
import com.banque.entity.TypesStatut;
import com.banque.pret.dao.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Stateless
public class PretServiceEJB implements PretRemote {

    @PersistenceContext(unitName = "banquePretPU")
    private EntityManager em;

    @Override
    public Pret findPret(int id) {
        PretDAO pretDAO = new PretDAO(em);
        return pretDAO.findById(id);
    }

    @Override
    public void savePret(Pret pret) {
        PretDAO pretDAO = new PretDAO(em);
        pretDAO.save(pret);
    }

    @Override
    public List<Pret> allPrets() {
        PretDAO pretDAO = new PretDAO(em);
        return pretDAO.findAll();
    }

    @Override
    public void saveRemboursement(Remboursement remboursement) {
        PretDAO pretDAO = new PretDAO(em);
        pretDAO.saveRemboursement(remboursement);
    }

    @Override
    public List<Remboursement> remboursementsPret(int pretId) {
        PretDAO pretDAO = new PretDAO(em);
        return pretDAO.getRemboursementByPret(pretId);
    }

    @Override
    public PretStatut findPretStatut(int id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.findById(id);
    }

    @Override
    public void savePretStatut(PretStatut pretStatut) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        pretStatutDAO.save(pretStatut);
    }

    @Override
    public List<PretStatut> allPretStatuts() {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.findAll();
    }

    @Override
    public PretStatut findByType(String type) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.findByType(type);
    }

    @Override
    public PretStatut findByPret(int pret_id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.findByPret(pret_id);
    }

    @Override
    public PretStatut pretWithStatutActuel(int pret_id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.getPretWithStatutActuel(pret_id);
    }

    @Override
    public List<PretStatut> pretsAvecStatutActuelByCompte(int compte_id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.getPretsAvecStatutActuelByCompte(compte_id);
    }

    @Override
    public List<PretStatut> pretsAvecStatutActuelByClient(int client_id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.getPretsAvecStatutActuelByClient(client_id);
    }

    @Override
    public List<PretStatut> getPretsByCompteAndStatut(int compte_id, int statut_id) {
        PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
        return pretStatutDAO.getPretsByCompteAndStatut(compte_id, statut_id);
    }

    @Override
    public List<Pret> findByCompte(int compte) {
        PretDAO pretDAO = new PretDAO(em);
        return pretDAO.findByCompte(compte);
    }

    @Override
    public List<Remboursement> getRemboursementByPret(int pret_id) {
        PretDAO pretDAO = new PretDAO(em);
        return pretDAO.getRemboursementByPret(pret_id);
    }

    @Override
    public PretStatut getPretsImpayesByCompte(int compte_id) {
        try {
            PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
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

    @Override
    public List<PretStatut> getPretsImpayesListByCompte(int compte_id) {
        try {
            PretStatutDAO pretStatutDAO = new PretStatutDAO(em);
            List<PretStatut> pretStatuts = new ArrayList<>();
            List<PretStatut> prets = pretStatutDAO.getPretsAvecStatutActuelByCompte(compte_id);
            for (PretStatut pret : prets) {
                if (pret.getStatut().getType().equalsIgnoreCase("En cours")) {
                    pretStatuts.add(pret);
                }
            }
            return pretStatuts;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public double resteAPaye(int pret_id) {
        PretDAO pretDAO = new PretDAO(em);
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

    @Override
    public void rembourserPret(Pret pret, CompteCourant compteCourant, double montant, Date currDate,
            OperationDAO operationDAO) {
        try {
            PretDAO pretDAO = new PretDAO(em);
            Remboursement remboursement = new Remboursement(pret, montant, currDate);
            pretDAO.saveRemboursement(remboursement);

            OperationCourant operation = new OperationCourant(compteCourant, (montant * -1), currDate, true);
            operationDAO.save(operation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public void demanderPret(double montant, CompteCourant compte, Date currDate,
    // int moisRemboursement) {
    @Override
    public void demanderPret(Pret pret) {
        try {
            // Pret pret = new Pret(montant, 24.0, compte, currDate, moisRemboursement);
            PretDAO pretDAO = new PretDAO(em);
            TypeStatutDAO typeStatutDAO = new TypeStatutDAO(em);
            PretStatutDAO pretStatutDAO = new PretStatutDAO(em);

            pretDAO.save(pret);

            TypesStatut type = typeStatutDAO.findByType("En cours");
            // TypesStatut type = typeStatutDAO.findByType("En attente");

            // PretStatut pretStatut = new PretStatut(pret, type, currDate);
            PretStatut pretStatut = new PretStatut(pret, type, pret.getDate_accord());
            pretStatutDAO.save(pretStatut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pret getLatestPret(int compteId) {
        PretDAO pretDAO = new PretDAO(em);

        List<Pret> prets = pretDAO.findByCompte(compteId);
        if (prets == null || prets.isEmpty()) {
            return null;
        }

        return prets.stream()
                .max(Comparator.comparing(Pret::getDate_accord))
                .orElse(null);
    }

}