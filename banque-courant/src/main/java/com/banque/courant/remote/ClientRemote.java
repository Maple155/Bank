package com.banque.courant.remote;

import java.util.List;
import com.banque.entity.Client;
import jakarta.ejb.Remote;

@Remote
public interface ClientRemote {
    
    public Client find(int id);
    public List<Client> all();
    Client findByEmail(String email);
    void save(Client client);

}
