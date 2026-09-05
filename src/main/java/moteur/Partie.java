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
import ui.InterfaceBourse;
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
        joueurActuel = new Joueur(SaisieFleches.lireTexte("Prénom : "), 100, 1000, new Portefeuille<Entreprise>(listeEntreprises));

        afficherHistoire();
        attendrePasser();
        clearScreen();

        


        while (jour < 16) { 
            clearScreen();
            boolean finJournee = false;
            if (jour!=1) {
                joueurActuel.payerLoyer();
               
            }
            EvenementPerso eventPersoDuJour = null;
            Random rdm = new Random();
            if(rdm.nextInt(4) == 2){
                eventPersoDuJour = EvenementPerso.genererEvenement();
                if(eventPersoDuJour.getInfluence().equals("Malus")){
                    joueurActuel.setCash(joueurActuel.getCash()+eventPersoDuJour.getArgent());
                    jour = jour + eventPersoDuJour.getJour();
                } else {
                    joueurActuel.setCash(joueurActuel.getCash()+eventPersoDuJour.getArgent());
                    jour = jour - eventPersoDuJour.getJour();
                }
            }
            j.preparerEditionDuJour();
            final int jourCourant = jour;
            final EvenementPerso telegramme = eventPersoDuJour;
            while(!finJournee){
                int choixJoueur = SaisieFleches.choisirGrille(2, 2, index -> {
                    clearScreen();
                    System.out.print(InterfaceJoueur.genererMenuJoueur(jourCourant, joueurActuel, index, telegramme));
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
            j.nouvelleJournee();
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
        int indexEntreprise = 0;
        int bouton = 0;
        boolean ouvert = true;
        while (ouvert) {
            final Entreprise entreprise = listeEntreprises.get(indexEntreprise);
            bouton = SaisieFleches.choisirGrille(5, 1, bouton, selection -> {
                clearScreen();
                System.out.print(InterfaceBourse.afficher(entreprise, joueurActuel, selection));
            });
            if (bouton == 0) {
                acheterChez(entreprise);
            } else if (bouton == 1) {
                vendreChez(entreprise);
            } else if (bouton == 2) {
                indexEntreprise = (indexEntreprise + 1) % listeEntreprises.size();
            } else if (bouton == 3) {
                indexEntreprise = (indexEntreprise - 1 + listeEntreprises.size()) % listeEntreprises.size();
            } else {
                ouvert = false;
            }
        }
    }

    private static void acheterChez(Entreprise entrepriseChoisie){
        System.out.println();
        int nombrePart = SaisieFleches.lireEntier("Nombre de parts a acheter chez " + entrepriseChoisie.getNom() + " : ");
        if (nombrePart <= 0) {
            System.out.println("Le nombre de parts doit etre positif.");
            SaisieFleches.attendreEntree();
            return;
        }
        if (!joueurActuel.acheter(entrepriseChoisie, nombrePart)) {
            System.out.println("Achat impossible : vous n'avez pas assez d'argent ("
                + String.format("%.2f", joueurActuel.getCash()) + " €, besoin de "
                + String.format("%.2f", nombrePart * entrepriseChoisie.getValeurAction()) + " €).");
            SaisieFleches.attendreEntree();
            return;
        }
        System.out.println("Vous venez d'acheter " + nombrePart + " part(s) dans " + entrepriseChoisie.getNom() + ".");
        SaisieFleches.attendreEntree();
    }

    private static void vendreChez(Entreprise entrepriseChoisie){
        System.out.println();
        int nombrePart = SaisieFleches.lireEntier("Parts disponibles : "
            + joueurActuel.getPortefeuille().getQuantite(entrepriseChoisie)
            + "   Nombre de parts a vendre : ");
        if (!joueurActuel.vendre(entrepriseChoisie, nombrePart)) {
            System.out.println("Vente impossible : nombre de parts incorrect.");
            SaisieFleches.attendreEntree();
            return;
        }
        System.out.println("Vous venez de vendre " + nombrePart + " part(s) dans " + entrepriseChoisie.getNom() + ".");
        SaisieFleches.attendreEntree();
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
        if (!j.acheterJournal(joueurActuel)) {
            System.out.println("Pas assez d'argent : le journal coute 1€.");
            SaisieFleches.attendreEntree();
            return;
        }
        clearScreen();
        j.afficherInformation();
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
        SaisieFleches.attendreEntree();
    }

}

