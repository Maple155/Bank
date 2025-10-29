package com.banque.pret.dao;

import com.banque.entity.TypesStatut;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class TypeStatutDAO {
 
    
    private EntityManager em;
    
    public TypeStatutDAO(EntityManager em) {
        this.em = em;
    }

    public TypesStatut findById(int id) {
        return em.find(TypesStatut.class, id);
    }

    public void save(TypesStatut typesStatut) {
        if (typesStatut.getId() == 0) {
            em.persist(typesStatut);
        } else {
            em.merge(typesStatut);
        }
    }

    public List<TypesStatut> findAll() {
        return em.createQuery("SELECT ts FROM TypesStatut ts", TypesStatut.class).getResultList();
    }

    public TypesStatut findByType(String type) {
        try {
            String jpql = "SELECT ts FROM TypesStatut ts WHERE ts.type = :type";
            return em.createQuery(jpql, TypesStatut.class)
                    .setParameter("type", type)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }
}
