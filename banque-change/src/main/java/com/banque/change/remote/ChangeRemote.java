package com.banque.change.remote;

import java.sql.Date;
import java.util.List;
import com.banque.change.entity.Change;
import jakarta.ejb.Remote;

@Remote
public interface ChangeRemote {
    
    public List<Change> findByDevise(String devise);
    public List<Change> all();
    public List<String> getDevisesUniques();
    public Change getChangeActuel(String devise, Date date);
    
}
