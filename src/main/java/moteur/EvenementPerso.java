package moteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class EvenementPerso extends Event {

    public int argent;
    public int jour;

    public EvenementPerso(String nom, int argent, int jour) {
        this.intitule = nom;
        this.argent = argent;
        this.jour = jour;
    }

    @Override
    public void setInfluence() {
        if (this.argent < 0 || this.jour >= 1) {
            this.influence = "Malus";
        } else {
            this.influence = "Bonus";
        }
    }

    public static EvenementPerso chargerDepuisLigne(String ligne) {
        String[] decoupage = ligne.split(";");
        int argentLu = Integer.parseInt(decoupage[1].trim());
        int jourLu = Integer.parseInt(decoupage[2].trim());
        return new EvenementPerso(decoupage[0], argentLu, jourLu);
    }

    public static EvenementPerso genererEvenement() {
        ArrayList<EvenementPerso> listeEvenements = new ArrayList<>();
        try (BufferedReader lecteur = new BufferedReader(new FileReader("data/event_perso.csv"))) {
            lecteur.readLine();
            String ligne = lecteur.readLine();
            while (ligne != null) {
                listeEvenements.add(chargerDepuisLigne(ligne));
                ligne = lecteur.readLine();
            }
        } catch (IOException e) {
            System.out.println("Impossible de lire le fichier event_perso.csv");
        }
        Random random = new Random();
        int index = random.nextInt(listeEvenements.size());
        EvenementPerso evenementChoisi = listeEvenements.get(index);
        evenementChoisi.setInfluence();
        return evenementChoisi;
    }
    
    public int getArgent() {
        return argent;
    }

    public void setArgent(int argent) {
        this.argent = argent;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public String getInfluence(){
        return this.influence;
    }
    public String toString() {
        return this.intitule + " : " + this.argent + " euros, " + this.jour + " jour(s) perdu(s)";
    }

}