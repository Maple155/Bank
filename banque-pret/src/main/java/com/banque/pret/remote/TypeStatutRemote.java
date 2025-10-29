package com.banque.pret.remote;

import com.banque.entity.TypesStatut;
import java.util.List;
import jakarta.ejb.Remote;

@Remote
public interface TypeStatutRemote {
    TypesStatut findById(int id);
    void save(TypesStatut typesStatut);
    List<TypesStatut> findAll();
    TypesStatut findByType(String type);
}
