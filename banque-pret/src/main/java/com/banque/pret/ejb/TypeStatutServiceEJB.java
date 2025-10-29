package com.banque.pret.ejb;

import com.banque.entity.TypesStatut;
import com.banque.pret.dao.TypeStatutDAO;
import com.banque.pret.remote.TypeStatutRemote;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TypeStatutServiceEJB implements TypeStatutRemote {

    @PersistenceContext(unitName = "banquePretPU")
    private EntityManager em;

    private TypeStatutDAO typeStatutDAO;

    @PostConstruct
    public void init() {
        typeStatutDAO = new TypeStatutDAO(em);
    }

    @Override
    public TypesStatut findById(int id) {
        return typeStatutDAO.findById(id);
    }

    @Override
    public void save(TypesStatut typesStatut) {
        typeStatutDAO.save(typesStatut);
    }

    @Override
    public List<TypesStatut> findAll() {
        return typeStatutDAO.findAll();
    }

    @Override
    public TypesStatut findByType(String type) {
        return typeStatutDAO.findByType(type);
    }
}
