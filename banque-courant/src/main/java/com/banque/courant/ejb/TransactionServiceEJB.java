package com.banque.courant.ejb;

import com.banque.courant.entity.*;
import com.banque.courant.remote.TransactionRemote;
import com.banque.courant.dao.*;
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

    @Override
    public Transaction find(int id) {
        TransactionDAO transactionDAO = new TransactionDAO(em);
        return transactionDAO.findById(id);
    }

    @Override
    public List<Transaction> all() {
        TransactionDAO transactionDAO = new TransactionDAO(em);
        return transactionDAO.findAll();
    }
    
    @Override 
    public void save (Transaction transaction) {
        TransactionDAO transactionDAO = new TransactionDAO(em);
        transactionDAO.save(transaction);
    }

    @Override
    public List<Transaction> findBySender (int sender_id) {
        TransactionDAO transactionDAO = new TransactionDAO(em);
        return transactionDAO.findBySender(sender_id);
    }

    @Override
    public List<Transaction> findByReceiver (int receiver_id) {
        TransactionDAO transactionDAO = new TransactionDAO(em);
        return transactionDAO.findByReceiver(receiver_id);
    }

    @Override
    public void effectuerTransfert(CompteCourant compte, CompteCourant receiver, double montant) {

        TransactionDAO transactionDAO = new TransactionDAO(em);
        OperationDAO operationDAO = new OperationDAO(em);

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
