package moteur;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import modele.Entreprise;
import modele.Joueur;
import modele.Portefeuille;
import ui.Couleur;
import ui.Couleur.COULEUR;
import ui.InterfaceJoueur;
import ui.SaisieFleches;

public class Partie {

    private static Scanner scInputJoueur = new Scanner(System.in);
    private static Joueur joueurActuel;
    private static List<Entreprise> listeEntreprises = DataLoader.chargerEntreprise();
    private static Classement classement = new Classement();
    private static Journal j = new Journal("data/Journal.txt");
    
    public void attendre(int ms){
            try{
                Thread.sleep(1);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }

        }

    public static Joueur lancerPartie(){
        int jour = 1;

        System.out.println("Quel est votre prénom ?");
        System.out.print("Prénom : ");
        joueurActuel = new Joueur(scInputJoueur.next(), 100, 1000, new Portefeuille<Entreprise>(listeEntreprises));

        afficherHistoire();
        attendrePasser();
        clearScreen();

        


        while (jour < 16) { 
            clearScreen();
            boolean finJournee = false;
            if (jour!=1) {
                joueurActuel.payerLoyer();
               
            }
            Random rdm = new Random();
            if(rdm.nextInt(4) == 2){
                EvenementPerso event = EvenementPerso.genererEvenement();
                
                if(event.getInfluence().equals("Malus")){
                    Couleur.println(event.toString(), false, COULEUR.ROUGE);
                    joueurActuel.setCash(joueurActuel.getCash()+event.getArgent());
                    jour = jour + event.getJour();
                } else {
                    Couleur.println(event.toString(), false, COULEUR.VERT);
                    joueurActuel.setCash(joueurActuel.getCash()+event.getArgent());
                    jour = jour - event.getJour();
                }
            }
            while(!finJournee){
                int choixJoueur = SaisieFleches.choisirGrille(2, 2, index -> {
                    clearScreen();
                    System.out.print(InterfaceJoueur.genererMenuJoueur(jour, joueurActuel, index));
                });
                if (choixJoueur == 0) {
                    choix1(listeEntreprises);
                } else if (choixJoueur == 1) {
                    journal();
                } else if (choixJoueur == 2) {
                    dormir();
                    finJournee = true;
                } else if (choixJoueur == 3) {
                    return null;
                }
            }
            j.changementValeurEntreprise(listeEntreprises);
            jour ++;
        }
        if (joueurActuel.aGagne()) {
            try {
                classement.ajouterScore(joueurActuel.getNom(), joueurActuel.getCash() - joueurActuel.getDette());
            } catch (IOException e) {
                System.err.println("Erreur Input");
            }
            System.out.println("Vous avez remboursé votre dette !");
        } else {
            System.out.println("Malheureusement, vous avez du remboursé votre dette en nature...");
        }
        System.out.println("Fin de la partie.");
        return joueurActuel;
    }

    private static void afficherHistoire(){
        clearScreen();
        try(FileReader histoire = new FileReader("data/Histoire.txt")) {
            StringBuilder texte = new StringBuilder();
            int histoire_valeur;
            while((histoire_valeur = histoire.read()) != -1){
                texte.append((char) histoire_valeur);
            }
            Couleur.println(texte.toString(), false, COULEUR.JAUNE, 8);
        }
        catch (IOException h) {
            h.printStackTrace();
        }
    }

    private static void choix1(List<Entreprise> listeEntreprises){
        boolean saisieValide = false;
        for (Entreprise e : listeEntreprises) {
            System.out.println(e.getNom() + " : " + e.getValeurAction() + "€");
        }
        System.out.print("\n 1. Acheter\n 2. Vendre\n 3. Fermer\nChoix :");
        while (!saisieValide) {
            int choixJoueur = scInputJoueur.nextInt();
            if (choixJoueur == 1) {
                acheter();
                saisieValide = true;
            } else if (choixJoueur == 2) {
                vendre();
                saisieValide = true;
            } else if (choixJoueur == 3){
                System.out.println("Retour.");
                saisieValide = true;
            } else {
                System.out.println("Ce n'est pas possible de choisir cette réponse.");
            }
        }
        System.out.println();
    }

    private static void acheter(){
        List<String> libelles = new ArrayList<>();
        for (Entreprise e : listeEntreprises) {
            libelles.add(e.getNom() + " : " + String.format("%.2f", e.getValeurAction()) + "€");
        }
        int index = SaisieFleches.choisirListe(libelles);
        Entreprise entrepriseChoisie = listeEntreprises.get(index);

        System.out.println("Nombre de parts a acheter chez " + entrepriseChoisie.getNom() + " : ");
        int nombrePart = scInputJoueur.nextInt();
        if (nombrePart <= 0) {
            System.out.println("Le nombre de parts doit etre positif.");
            return;
        }
        if (!joueurActuel.acheter(entrepriseChoisie, nombrePart)) {
            System.out.println("Achat impossible : vous n'avez pas assez d'argent ("
                + String.format("%.2f", joueurActuel.getCash()) + " €, besoin de "
                + String.format("%.2f", nombrePart * entrepriseChoisie.getValeurAction()) + " €).");
            return;
        }
        System.out.println("Vous venez d'acheté " + nombrePart + " part(s) dans l'entreprise " + entrepriseChoisie.getNom() + ".");
    }

    private static void vendre(){
        System.out.print("Chez quel entreprise souhaitez-vous vendre une/des action(s) ?\n Nom de l'entreprise : ");
        Entreprise entrepriseChoisie = entrepriseValide();
        System.out.print("Combien d'action souhaitez-vous acheter ?\n Nombre de part disponible : " + joueurActuel.getPortefeuille().getQuantite(entrepriseChoisie) + "\n Nombre de part à vendre : ");
        int nombrePart = scInputJoueur.nextInt();
        while (!joueurActuel.vendre(entrepriseChoisie, nombrePart)) {
            System.out.print("Le nombre de part saisie est incorrect.\nNombre de part : ");
            nombrePart = scInputJoueur.nextInt();
        }
        System.out.println("Vous venez de vendre " + nombrePart + " part(s) dans l'entreprise " + entrepriseChoisie.getNom() + ".");
    }

    private static Entreprise entrepriseValide(){
        String nomEntreprise = scInputJoueur.next();
        Entreprise entrepriseChoisie = null;
        boolean valide = false;
        while (!valide) {
            for (Entreprise entreprise : listeEntreprises) {
                if (entreprise.getNom().equals(nomEntreprise)) {
                    valide = true;
                    entrepriseChoisie = entreprise;
                }
            }
            if (!valide) {
                System.out.println("Le nom de l'entreprise n'est pas correcte. Veuillez le réécrire.");
                nomEntreprise = scInputJoueur.next();
            }
        }
        return entrepriseChoisie;
    }

    private static void journal(){
        ArrayList<String> ligneFichier = DataLoader.lireCSV("data/events_marche.csv");
        ArrayList<EvenementMarche> event = DataLoader.chargerEventMarcheListe(ligneFichier);
        j.setEvenementMarches(event);
        try {
            j.afficherInformation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        Couleur.println("Entree pour revenir", false, COULEUR.JAUNE);
        SaisieFleches.attendreEntree();
    }

    private static void dormir(){
        try {
            afficherTXT("Dormir1.txt",0);
            Thread.sleep(1000);
            afficherTXT("Dormir2.txt",0);
            Thread.sleep(1000);
            afficherTXT("Dormir3.txt",0);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("Erreur, affichage interrompu");
        }
    }

    private static void afficherTXT(String nomFichier, int temps){
        try(FileReader fichier = new FileReader("data/" + nomFichier)) {
            int fichier_valeur;
            while((fichier_valeur = fichier.read()) != -1){
                char c = (char) fichier_valeur;
                System.out.print(c);
                Thread.sleep(temps);
            }
            System.out.println();
        }
        catch (IOException h) {
            h.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Erreur, affichage interrompu");
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void attendrePasser(){
        if (scInputJoueur.hasNextLine()) {
            scInputJoueur.nextLine();
        }
        SaisieFleches.attendreEntree();
    }

}

