package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.TransactionRemote;
import com.banque.courant.dao.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class TransactionServiceEJB implements TransactionRemote{
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private OperationDAO operationDAO;

    @Override
    public Transaction find(int id) {
        return em.find(Transaction.class, id);
    }

    @Override
    public List<Transaction> all() {
        return em.createQuery("SELECT op FROM Transaction op", Transaction.class).getResultList();
    }
    
    @Override
    public void effectuerTransfert(CompteCourant compte, CompteCourant receiver, double montant) {
        montant = montant * -1;

        OperationCourant debit = new OperationCourant();
        debit.setCompte(compte);
        debit.setMontant(montant);
        debit.setDateOperation(Date.valueOf(LocalDate.now()));
        debit.setIsValidate(true);
        operationDAO.save(debit);

        OperationCourant credit = new OperationCourant();
        credit.setCompte(receiver);
        credit.setMontant((montant * -1));
        credit.setDateOperation(Date.valueOf(LocalDate.now()));
        credit.setIsValidate(true);
        operationDAO.save(credit);

        Transaction transaction = new Transaction();
        transaction.setSender(compte);
        transaction.setReceiver(receiver);
        transaction.setMontant((montant * -1));
        transaction.setDateTransaction(Date.valueOf(LocalDate.now()));
        transaction.setValidate(true);
        transactionDAO.save(transaction);
    }
}
