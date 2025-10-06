package com.banque.pret.ejb;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;

public class PDFservice {

        public static void genererContratPret(
                        OutputStream outputStream,
                        String nomPreteur,
                        String nomEmprunteur,
                        String montantChiffres,
                        String montantLettres,
                        String lieu,
                        LocalDate dateSignature,
                        LocalDate dateLimite) {
                try {

                        double montant = Double.valueOf(montantChiffres);
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                        symbols.setGroupingSeparator(' ');

                        DecimalFormat df = new DecimalFormat("#,###", symbols);
                        String montantFormate = df.format(montant);
                        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                        PdfWriter.getInstance(document, outputStream);
                        document.open();

                        // Styles de texte
                        Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);
                        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

                        // Titre
                        Paragraph titre = new Paragraph("Contrat de prêt", titreFont);
                        titre.setAlignment(Element.ALIGN_CENTER);
                        document.add(titre);
                        document.add(Chunk.NEWLINE);

                        // Parties
                        document.add(new Paragraph("ENTRE :", boldFont));
                        document.add(new Paragraph(nomPreteur + ", ci-après désigné(e) « le prêteur ».", normalFont));
                        document.add(Chunk.NEWLINE);

                        document.add(new Paragraph("ET :", boldFont));
                        document.add(new Paragraph(nomEmprunteur + ", ci-après désigné(e) « l’emprunteur ».",
                                        normalFont));
                        document.add(Chunk.NEWLINE);

                        // Article 1
                        document.add(new Paragraph("ARTICLE 1. OBJET DU CONTRAT", boldFont));
                        document.add(new Paragraph(
                                        "Le présent contrat a pour objet d’acter le prêt d’argent consenti par le prêteur à l’emprunteur "
                                                        + "et à en formaliser les conditions de remboursement.",
                                        normalFont));
                        document.add(Chunk.NEWLINE);

                        document.add(new Paragraph("Ce jour, le prêteur remet à l’emprunteur la somme de "
                                        + montantFormate + " MGA (" + montantLettres + ").", normalFont));
                        document.add(Chunk.NEWLINE);

                        // Article 2
                        document.add(new Paragraph("ARTICLE 2. REMBOURSEMENT", boldFont));
                        document.add(new Paragraph(
                                        "L’emprunteur s’engage à rembourser la somme prêtée dans son intégralité avant le "
                                                        + "terme défini par les deux parties, au plus tard le "
                                                        + dateLimite.format(java.time.format.DateTimeFormatter
                                                                        .ofPattern("dd/MM/yyyy"))
                                                        + ".",
                                        normalFont));
                        document.add(Chunk.NEWLINE);

                        // Article 3
                        document.add(new Paragraph("ARTICLE 3. DÉCÈS DE L’EMPRUNTEUR", boldFont));
                        document.add(new Paragraph(
                                        "En cas de décès de l’emprunteur avant d’avoir remboursé la somme, "
                                                        + "ses héritiers seront tenus solidairement à la restitution.",
                                        normalFont));
                        document.add(Chunk.NEWLINE);

                        // Article 4
                        document.add(new Paragraph("ARTICLE 4. INEXÉCUTION DU CONTRAT", boldFont));
                        document.add(new Paragraph(
                                        "En cas de retard dans le remboursement du prêt, des pénalités pourront être appliquées, "
                                                        + "sans excéder le taux prévu par le législateur.",
                                        normalFont));
                        document.add(Chunk.NEWLINE);

                        // Lieu et date
                        document.add(new Paragraph("Fait à " + lieu + ", le "
                                        + dateSignature.format(
                                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        + " en deux exemplaires.", normalFont));
                        document.add(Chunk.NEWLINE);
                        document.add(Chunk.NEWLINE);

                        // Signatures
                        PdfPTable table = new PdfPTable(2);
                        table.setWidthPercentage(100);
                        table.addCell(celluleSignature("Signature du prêteur"));
                        table.addCell(celluleSignature("Signature de l’emprunteur"));
                        document.add(table);

                        document.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private static PdfPCell celluleSignature(String texte) {
                PdfPCell cell = new PdfPCell(new Phrase(texte));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setFixedHeight(60f);
                return cell;
        }
}