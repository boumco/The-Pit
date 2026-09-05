package moteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import  moteur.Partie;

public class Classement {

    public void ajouterScore(String nom, double montant) throws IOException {
        FileWriter fichier = new FileWriter("data/classement.csv", true);
        fichier.write(nom + ";" + montant + "\n");
        fichier.close();
    }

    public void afficherScore() throws IOException {
        BufferedReader fichier = new BufferedReader(
            new FileReader("data/classement.csv")
        );
        String ligne;
        fichier.readLine();
        while ((ligne = fichier.readLine()) != null) {
            String[] informations = ligne.split(";");
            String nom = informations[0];
            double montant = Double.parseDouble(informations[1]);
            System.out.println(nom + " : " + montant + " €");
        }
        fichier.close();
    }

}