package com.banque.courant.remote;

import java.util.List;
import com.banque.courant.entity.Banque;
import jakarta.ejb.Remote;

@Remote
public interface BanqueRemote {
    
    public Banque find(int id);

    public List<Banque> all();

    void save(Banque banque);
}
