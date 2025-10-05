package com.banque.pret.dao;

import com.banque.courant.entity.*;
import com.banque.entity.TypesStatut;
import com.banque.pret.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PretStatutDAO {
    
    @PersistenceContext(unitName = "banquePretPU")
    private EntityManager em;

    public PretStatut findById(int id) {
        return em.find(PretStatut.class, id);
    }

    public void save(PretStatut pretStatut) {
        if (pretStatut.getId() == 0) {
            em.persist(pretStatut);
        } else {
            em.merge(pretStatut);
        }
    }

    public List<PretStatut> findAll() {
        return em.createQuery("SELECT ps FROM PretStatut ps", PretStatut.class).getResultList();
    }

    public PretStatut findByType(String type) {
        try {
            String jpql = "SELECT ps FROM PretStatut ps WHERE ps.statut.type = :type";
            return em.createQuery(jpql, PretStatut.class)
                    .setParameter("type", type)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }

    public PretStatut findByPret(int pretId) {
        try {
            String jpql = "SELECT ps FROM PretStatut ps WHERE ps.pret.id = :pretId";
            return em.createQuery(jpql, PretStatut.class)
                    .setParameter("pretId", pretId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }

    public PretStatut getPretWithStatutActuel(int pretId) {
        try {
            String jpql = "SELECT ps FROM PretStatut ps " +
                          "WHERE ps.pret.id = :id " +
                          "ORDER BY ps.date_statut DESC";
            return em.createQuery(jpql, PretStatut.class)
                    .setParameter("id", pretId)
                    .setMaxResults(1) // on prend uniquement le plus récent
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    // 1. Tous les prêts d’un compte avec leur statut actuel
    @SuppressWarnings("unchecked")
    public List<PretStatut> getPretsAvecStatutActuelByCompte(int compteId) {
        String sql = "SELECT DISTINCT ON (ps.pret) ps.* " +
                    "FROM pret_statut ps " +
                    "JOIN pret p ON ps.pret = p.id " +
                    "JOIN compte_courant c ON p.compte_courant = c.id " +
                    "WHERE c.id = :compteId " +
                    "ORDER BY ps.pret, ps.date_statut DESC, ps.id DESC";

        return em.createNativeQuery(sql, PretStatut.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }   

    @SuppressWarnings("unchecked")
    public List<PretStatut> getPretsAvecStatutActuelByClient(int clientId) {
        String sql = "SELECT DISTINCT ON (ps.pret) ps.* " +
                     "FROM pret_statut ps " +
                     "JOIN pret p ON ps.pret = p.id " +
                     "JOIN compte_courant c ON p.compte_courant = c.id " +
                     "WHERE c.client_id = :clientId " +  // <- filtrer par client
                     "ORDER BY ps.pret, ps.date_statut DESC, ps.id DESC";
    
        return (List<PretStatut>) em.createNativeQuery(sql, PretStatut.class)
                                    .setParameter("clientId", clientId)
                                    .getResultList();
    }    

    // 2. Tous les prêts d’un compte avec un statut actuel donné
    @SuppressWarnings("unchecked")
    public List<PretStatut> getPretsByCompteAndStatut(int compteId, int statutId) {
        String sql = "SELECT DISTINCT ON (ps.pret) ps.* " +
                    "FROM pret_statut ps " +
                    "JOIN pret p ON ps.pret = p.id " +
                    "JOIN compte_courant c ON p.compte_courant = c.id " +
                    "WHERE c.id = :compteId " +
                    "AND ps.statut = :statutId " +
                    "ORDER BY ps.pret, ps.date_statut DESC, ps.id DESC";

        return em.createNativeQuery(sql, PretStatut.class)
                .setParameter("compteId", compteId)
                .setParameter("statutId", statutId)
                .getResultList();
    }
    
}
