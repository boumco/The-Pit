package moteur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import modele.Entreprise;
import modele.TypeEntreprise;


public class DataLoader {

    public static String file_path = System.getProperty("user.dir") + File.separator + "data";
    public static String fichierEntreprise = "entreprises.csv";

    public static List<Entreprise> chargerEntreprise(){
        List<Entreprise> listeEntreprises = new ArrayList<>();
        try(Scanner sc = new Scanner(new File(file_path+ File.separator + fichierEntreprise))){
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Scanner scData = new Scanner(line);
                scData.useDelimiter(",");
                String nomEntreprise = scData.next();
                double valeurAction = scData.nextDouble();
                TypeEntreprise type = TypeEntreprise.valueOf(scData.next().toUpperCase());
                String description = scData.next();
                Entreprise entreprise = new Entreprise(nomEntreprise,valeurAction,type,description);
                listeEntreprises.add(entreprise);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found");
        } catch (NoSuchElementException e) {
            System.err.println("Erreur de format dans le fichier CSV : " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.err.println("Erreur de format de variable");
        }
        return listeEntreprises;
    }



    public static ArrayList<EvenementPerso> chargerEventPersoListe(String[] ligne){
        ArrayList<EvenementPerso> retour = new ArrayList<>();

        for(int indice = 0 ; indice < ligne.length; indice ++){
            EvenementPerso p = DataLoader.chargerEventPerso(ligne[indice]);
            retour.add(p);
        }
        return retour;
    }

    /*public static ArrayList<EvenementMarche> chargerEventMarcheListe(String[] ligne){
        ArrayList<EvenementMarche> retour = new ArrayList<>();
        for(int indice = 0 ; indice < ligne.length; indice ++){
            EvenementMarche p = DataLoader.chargerEventMarche(ligne[indice]);
            retour.add(p);
        }
        return retour;
    }*/

    public static ArrayList<EvenementMarche> chargerEventMarcheListe(ArrayList<String> lignes){
        ArrayList<EvenementMarche> retour = new ArrayList<>();
        for(int indice = 0 ; indice < lignes.size(); indice ++){
            EvenementMarche p = DataLoader.chargerEventMarche(lignes.get(indice));
            retour.add(p);
        }
        return retour;
    }

    public static EvenementPerso chargerEventPerso(String ligne){
        EvenementPerso retour;
        String[] decoupage = ligne.split(";");
        String valeurTexte = decoupage[1].trim();
        int argent = Integer.parseInt(valeurTexte);
        retour = new EvenementPerso(decoupage[0], argent, Integer.parseInt(decoupage[2]));
        return retour;
    }

    public static EvenementMarche chargerEventMarche(String ligne){
        EvenementMarche retour;
        String[] decoupage = ligne.split(";");
        String valeurTexte = decoupage[1].trim();
        int chiffre = Integer.parseInt(valeurTexte);
        retour = new EvenementMarche(decoupage[0], chiffre,decoupage[2]);
        return retour;
    }


    public static ArrayList<FaitDivers> chargerFaitsDivers(){
        ArrayList<FaitDivers> faits = new ArrayList<>();
        ArrayList<String> lignes = lireCSV(file_path + File.separator + "faits_divers.csv");
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne == null || ligne.isBlank()) {
                continue;
            }
            String[] parts = ligne.split(";", 3);
            if (parts.length < 3) {
                continue;
            }
            faits.add(new FaitDivers(parts[0], parts[1], parts[2]));
        }
        return faits;
    }

    public static ArrayList<String> lireCSV(String nomFichier){
        // Renvoie les lignes d'un fichier CSV.
        ArrayList<String>  retour = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))){
            br.readLine();
            String ligne;

            while((ligne = br.readLine()) != null){
                retour.add(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retour;
    }

}
