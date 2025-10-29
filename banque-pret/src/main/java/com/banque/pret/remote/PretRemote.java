package com.banque.pret.remote;

import java.sql.Date;
import java.util.List;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.entity.CompteCourant;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.PretStatut;
import com.banque.pret.entity.Remboursement;
import jakarta.ejb.Remote;

@Remote
public interface PretRemote {

    Pret findPret(int id);

    void savePret(Pret pret);

    List<Pret> allPrets();

    void saveRemboursement(Remboursement remboursement);

    List<Remboursement> remboursementsPret(int pretId);

    PretStatut getPretsImpayesByCompte(int compte_id);

    List<PretStatut> getPretsImpayesListByCompte(int compte_id);

    double resteAPaye(int pret_id);

    void rembourserPret(Pret pret, CompteCourant compteCourant, double montant, Date currDate,
            OperationDAO operationDAO);

    void demanderPret(Pret pret);

    Pret getLatestPret(int compteId);

    PretStatut findPretStatut(int id);

    void savePretStatut(PretStatut pretStatut);

    List<PretStatut> allPretStatuts();

    PretStatut findByType(String type);

    PretStatut findByPret(int pret_id);

    PretStatut pretWithStatutActuel(int pret_id);

    List<PretStatut> pretsAvecStatutActuelByCompte(int compte_id);

    List<PretStatut> pretsAvecStatutActuelByClient(int client_id);

    List<PretStatut> getPretsByCompteAndStatut(int compte_id, int statut_id);

    List<Pret> findByCompte(int compte);

    List<Remboursement> getRemboursementByPret(int pret_id);
    
}
