package com.banque.change.ejb;

import com.banque.change.entity.*;
import com.banque.change.remote.ChangeRemote;
import jakarta.ejb.Stateless;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ChangeServiceEJB implements ChangeRemote {
    // @PersistenceContext(unitName = "banquePU")
    // private EntityManager em;

    @Override
    public List<Change> all() {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("data/data.txt");

            if (inputStream == null) {
                throw new FileNotFoundException("Fichier data/data.txt non trouvé dans les ressources");
            }

            return ChangeFileReader.readChangeFile(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getDevisesUniques() {
        List<Change> changes = all();
        List<String> devises = new ArrayList<>();

        if (changes == null) {
            return devises;
        }

        for (Change change : changes) {
            String devise = change.getDevise();
            if (devise != null && !devise.trim().isEmpty() && !devises.contains(devise)) {
                devises.add(devise);
            }
        }

        return devises;
    }

    @Override
    public Change getChangeActuel(String devise, Date date) {
        List<Change> changes = all();

        if (changes == null || changes.isEmpty() || devise == null || date == null) {
            return null;
        }

        return changes.stream()
                .filter(change -> devise.equals(change.getDevise()))
                .filter(change -> {
                    try {
                        // Convertir les dates String en java.sql.Date
                        Date dateDebut = Date.valueOf(change.getDateDebut());
                        Date dateFin = change.getDateFin() != null && !change.getDateFin().equalsIgnoreCase("NULL")
                                ? Date.valueOf(change.getDateFin())
                                : null;

                        return isDateInRange(date, dateDebut, dateFin);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Format de date invalide pour le change: " + change.getDevise());
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    private boolean isDateInRange(Date date, Date dateDebut, Date dateFin) {
        // Vérifier que la date est après ou égale à dateDebut
        boolean afterStart = date.compareTo(dateDebut) >= 0;

        // Vérifier que la date est avant ou égale à dateFin (si dateFin n'est pas null)
        boolean beforeEnd = dateFin == null || date.compareTo(dateFin) <= 0;

        return afterStart && beforeEnd;
    }

    @Override
    public List<Change> findByDevise(String devise) {
        try {
            List<Change> changes = all();
            List<Change> result = new ArrayList<>();

            for (Change change : changes) {
                if (change.getDevise().equalsIgnoreCase(devise)) {
                    result.add(change);
                }
            }

            return result;
        } catch (Exception e) {

            return null;
        }
    }

}