package score;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableauScores {

    private static final String CHEMIN_CSV = "data/scores.csv";

    public static List<EntreeScore> lire() {
        List<EntreeScore> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CHEMIN_CSV))) {
            String ligne = reader.readLine(); // on saute l'en-tête
            while ((ligne = reader.readLine()) != null) {
                if (ligne.isBlank()) {
                    continue;
                }
                String[] champs = ligne.split(",");
                String nom = champs[0];
                double cashFinal = Double.parseDouble(champs[1]);
                double detteRestante = Double.parseDouble(champs[2]);
                boolean victoire = Boolean.parseBoolean(champs[3]);
                scores.add(new EntreeScore(nom, cashFinal, detteRestante, victoire));
            }
        } catch (IOException e) {
            System.out.println("Aucun score enregistré pour le moment.");
        }

        return scores;
    }

    public static void ajouter(EntreeScore entree) {
        boolean fichierExiste = new java.io.File(CHEMIN_CSV).exists();

        try (FileWriter writer = new FileWriter(CHEMIN_CSV, true)) {
            if (!fichierExiste) {
                writer.write("nom,cashFinal,detteRestante,victoire\n");
            }
            writer.write(entree.toLigneCsv() + "\n");
        } catch (IOException e) {
            System.out.println("Impossible d'enregistrer le score.");
        }
    }

    public static void afficher() {
        List<EntreeScore> scores = lire();

        if (scores.isEmpty()) {
            System.out.println("Aucun score enregistré pour le moment.");
            return;
        }

        // tri par cash final décroissant, meilleur score en premier
        scores.sort((a, b) -> Double.compare(b.getCashFinal(), a.getCashFinal()));

        System.out.println("===== TABLEAU DES SCORES =====");
        System.out.printf("%-15s %-12s %-15s %-10s%n", "Nom", "Cash final", "Dette restante", "Résultat");
        System.out.println("-------------------------------------------------------------");

        for (EntreeScore e : scores) {
            String resultat = e.isVictoire() ? "Victoire" : "Défaite";
            System.out.printf("%-15s %-12.2f %-15.2f %-10s%n",
                    e.getNom(), e.getCashFinal(), e.getDetteRestante(), resultat);
        }

        System.out.println("===============================");
    }
}