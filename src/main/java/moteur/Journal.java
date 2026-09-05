package moteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import modele.Entreprise;
import modele.Joueur;
import modele.TypeEntreprise;
import ui.InterfaceJournal;

public class Journal {

    private String affichageJournal;
    private ArrayList<EvenementMarche> evenementMarches;
    private ArrayList<EvenementMarche> evenementsNeutresDuJour;
    private ArrayList<EvenementMarche> evenementsNonNeutresDuJour;
    private String pageDuJour;
    private boolean editionPrete;
    private boolean journalAcheteAujourdhui;

    public Journal(String affichageJounal, ArrayList<EvenementMarche> evenementMarches){
        this.affichageJournal = affichageJounal;
        this.evenementMarches = evenementMarches;
        this.evenementsNeutresDuJour = new ArrayList<>();
        this.evenementsNonNeutresDuJour = new ArrayList<>();
        this.editionPrete = false;
        this.journalAcheteAujourdhui = false;
    }

    public Journal(String affichageJournal){
        this(affichageJournal,null);
    }

    public static void afficherJournal(){
        try(FileReader histoire = new FileReader("data/Journal.txt")) {
            int journal_valeur;
            while((journal_valeur = histoire.read()) != -1){
                char c = (char) journal_valeur;
                System.out.print(c);
            }
            System.out.println();
        }
        catch (IOException h) {
            h.printStackTrace();
        }
    }

    public String getAffichageJournal() {
        return affichageJournal;
    }

    public void setEvenementsNonNeutres(ArrayList<EvenementMarche> evenementsNonNeutres) {
        this.evenementsNonNeutresDuJour = evenementsNonNeutres;
    }


    public void setEvenementsNeutres(ArrayList<EvenementMarche> evenementsNeutres) {
        this.evenementsNeutresDuJour = evenementsNeutres;
    }

    public void ajouterEvenementsNeutres(EvenementMarche evenement){
        this.evenementsNeutresDuJour.add(evenement);
    }

    public void ajouterEvenementsNonNeutres(EvenementMarche evenement){
        this.evenementsNonNeutresDuJour.add(evenement);
    }

    public void viderInfoDuJour(){
        this.evenementsNeutresDuJour.clear();
        this.evenementsNonNeutresDuJour.clear();
    }

    public void setEvenementMarches(ArrayList<EvenementMarche> evenementMarches) {
        this.evenementMarches = evenementMarches;
    }
        
    public void printLogoJournal() throws IOException{
        boolean fin = false;
        Scanner sc = new Scanner(System.in);

        while(!fin){
            try(BufferedReader journalLogo = new BufferedReader(new FileReader(this.getAffichageJournal()))){
                String line;
                line  = journalLogo.readLine() ;
                while(line != null){
                    System.out.println(line);
                    line = journalLogo.readLine();
                }
                fin = true;
            } catch(IOException m){
                    m.printStackTrace();
                }
        }
    }


    public void preparerEditionDuJour() {
        if (editionPrete) {
            return;
        }
        ArrayList<String> lignes = DataLoader.lireCSV("data/events_marche.csv");
        this.evenementMarches = DataLoader.chargerEventMarcheListe(lignes);
        this.viderInfoDuJour();

        if (this.evenementMarches == null || this.evenementMarches.isEmpty()) {
            this.pageDuJour = "Aucun evenement a afficher aujourd'hui.\n";
            this.editionPrete = true;
            return;
        }

        ArrayList<EvenementMarche> neutres = new ArrayList<>();
        ArrayList<EvenementMarche> nonNeutres = new ArrayList<>();
        for (int indice = 0; indice < this.evenementMarches.size(); indice++) {
            EvenementMarche event = this.evenementMarches.get(indice);
            event.setInfluence();
            if (event.influence.equals("Neutre")) {
                neutres.add(event);
            } else {
                nonNeutres.add(event);
            }
        }

        Random r = new Random();
        ArrayList<EvenementMarche> uneDuJour = new ArrayList<>();
        for (int indiceNeutre = 0; indiceNeutre < 3; indiceNeutre++) {
            if (!neutres.isEmpty()) {
                int index = r.nextInt(neutres.size());
                EvenementMarche eventChoisi = neutres.get(index);
                this.ajouterEvenementsNeutres(eventChoisi);
                uneDuJour.add(eventChoisi);
                neutres.remove(index);
            }
        }
        for (int indiceNonNeutres = 0; indiceNonNeutres < 2; indiceNonNeutres++) {
            if (!nonNeutres.isEmpty()) {
                int index = r.nextInt(nonNeutres.size());
                EvenementMarche eventChoisi = nonNeutres.get(index);
                this.ajouterEvenementsNonNeutres(eventChoisi);
                uneDuJour.add(0, eventChoisi);
                nonNeutres.remove(index);
            }
        }

        EvenementMarche une = uneDuJour.isEmpty() ? null : uneDuJour.get(0);
        ArrayList<EvenementMarche> autresMarche = new ArrayList<>();
        if (uneDuJour.size() > 1) {
            autresMarche.add(uneDuJour.get(1));
        }

        ArrayList<FaitDivers> pool = DataLoader.chargerFaitsDivers();
        ArrayList<FaitDivers> choisis = new ArrayList<>();
        int maxFaits = Math.min(5, pool.size());
        for (int i = 0; i < maxFaits && !pool.isEmpty(); i++) {
            choisis.add(pool.remove(r.nextInt(pool.size())));
        }

        this.pageDuJour = InterfaceJournal.afficher(une, autresMarche, choisis);
        this.editionPrete = true;
    }

    public void afficherInformation() {
        if (!editionPrete) {
            preparerEditionDuJour();
        }
        System.out.print(pageDuJour);
    }

    public boolean acheterJournal(Joueur joueur) {
        if (journalAcheteAujourdhui) {
            return true;
        }
        if (!joueur.payer(1)) {
            return false;
        }
        journalAcheteAujourdhui = true;
        return true;
    }

    public void nouvelleJournee() {
        editionPrete = false;
        pageDuJour = null;
        journalAcheteAujourdhui = false;
        viderInfoDuJour();
    }

    public void changementValeurEntreprise(List<Entreprise> listeEntreprises){
        // Boucle pour les événements neutres
        for (int indiceNeutre = 0; indiceNeutre < this.evenementsNeutresDuJour.size(); indiceNeutre++) {
            try {
                EvenementMarche m = this.evenementsNeutresDuJour.get(indiceNeutre);
                m.inflation(listeEntreprises);
            } catch (Exception exception) {
                // Si l'entreprise n'est pas dans la liste r, on ignore et on passe au suivant
            }
        }
        
        //  Boucle pour les événements nonNeutre (ceux qui font beaucoup bouger les prix !)
        for (int indiceNonNeutre = 0; indiceNonNeutre < this.evenementsNonNeutresDuJour.size(); indiceNonNeutre++) {
            try {
                EvenementMarche m = this.evenementsNonNeutresDuJour.get(indiceNonNeutre);
                m.inflation(listeEntreprises);
            } catch (Exception exception) {
                
            }
        }
    }

    public static void main(String[] args) {
             Entreprise e1 = new Entreprise("Petrolz", 78, TypeEntreprise.PETROLIER, "Multinational");
             Entreprise e2 = new Entreprise("Energie3000", 30, TypeEntreprise.ENERGIE, "electricite");
             ArrayList<Entreprise> r = new ArrayList<>();
             r.add(e1);
             r.add(e2);
             
             System.out.println("AVANT INFLATION");
             System.out.println(e1.toString());
             System.out.println(e2.toString());
             
             Journal j = new Journal("data/Journal.txt");
             ArrayList<String> ligneFichier = DataLoader.lireCSV("data/events_marche.csv");
             ArrayList<EvenementMarche> event = DataLoader.chargerEventMarcheListe(ligneFichier);
             j.setEvenementMarches(event);
             
             try {
                j.afficherInformation();
             } catch (IOException e) {
                e.printStackTrace();
             }

             // Boucle pour les événements neutres
             for (int indiceNeutre = 0; indiceNeutre < j.evenementsNeutresDuJour.size(); indiceNeutre++) {
                 try {
                     EvenementMarche m = j.evenementsNeutresDuJour.get(indiceNeutre);
                     m.inflation(r);
                 } catch (Exception exception) {
                     // Si l'entreprise n'est pas dans la liste r, on ignore et on passe au suivant
                 }
             }
             
             //  Boucle pour les événements nonNeutre (ceux qui font beaucoup bouger les prix !)
             for (int indiceNonNeutre = 0; indiceNonNeutre < j.evenementsNonNeutresDuJour.size(); indiceNonNeutre++) {
                 try {
                     EvenementMarche m = j.evenementsNonNeutresDuJour.get(indiceNonNeutre);
                     m.inflation(r);
                 } catch (Exception exception) {
                     
                 }
             }

             System.out.println("APRES INFLATION");
             System.out.println(e1.toString());
             System.out.println(e2.toString());
        }

}






