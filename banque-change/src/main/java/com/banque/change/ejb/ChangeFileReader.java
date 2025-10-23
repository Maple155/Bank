package com.banque.change.ejb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import com.banque.change.entity.Change;

public class ChangeFileReader {

    public static List<Change> readChangeFile(String filePath) throws IOException {
        List<Change> changes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return readChangeFileFromBufferedReader(br);
        }
    }

    // NOUVELLE méthode qui accepte InputStream
    public static List<Change> readChangeFile(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            return readChangeFileFromBufferedReader(br);
        }
    }

    // Méthode commune pour les deux cas
    private static List<Change> readChangeFileFromBufferedReader(BufferedReader br) throws IOException {
        List<Change> changes = new ArrayList<>();
        String line;

        while ((line = br.readLine()) != null) {
            // Ignorer les lignes vides
            if (line.trim().isEmpty()) {
                continue;
            }

            // Séparer les champs par le point-virgule
            String[] fields = line.split(";");

            if (fields.length == 4) {
                try {
                    String devise = fields[0].trim();
                    String dateDebut = fields[1].trim();
                    String dateFin = fields[2].trim();
                    int cours = Integer.parseInt(fields[3].trim());

                    Change change = new Change(devise, dateDebut, dateFin, cours);
                    changes.add(change);

                } catch (Exception e) {
                    System.err.println("Erreur de parsing pour la ligne: " + line);
                    e.printStackTrace();
                }
            } else {
                System.err.println("Ligne mal formatée: " + line);
            }
        }

        return changes;
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        try {
            List<Change> changes = readChangeFile("changes.txt");

            // Afficher les résultats
            for (Change change : changes) {
                System.out.println("Devise: " + change.getDevise() +
                        ", Date début: " + change.getDateDebut() +
                        ", Date fin: " + change.getDateFin() +
                        ", Cours: " + change.getCours());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}