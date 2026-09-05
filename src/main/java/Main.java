
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import moteur.Classement;
import moteur.Partie;

public class Main {
    public static void main(String[] args){
        boolean terminer = false;
        Scanner scanner = new Scanner(System.in);
        String choix;

        while(!terminer){  
            try (BufferedReader menu = new BufferedReader(new FileReader("data/Menu.txt"))) {
                String menu_line;
                while ((menu_line = menu.readLine()) != null) {
                    System.out.println(menu_line);
                }
            } catch (IOException m) {
                m.printStackTrace();
            }
            System.out.print("Choix : ");
            choix = scanner.next();
            if(choix.equals("1")){
                Partie.lancerPartie();
            } else if(choix.equals("2")){
                try {
                    Classement classement = new Classement();
                    classement.afficherScore();
                } catch (IOException e) {
                    System.out.println("Impossible de lire le classement");
                }
                System.out.println("Appuyer sur 'x' pour quitter.");
                while (!scanner.next().equals("x")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.err.println("Erreur interrompu");
                    }
                }
            } else if(choix.equals("3")){
                terminer = true;
            } else {
                System.out.println("La saisie est incorrecte");
            }
            
        }
        scanner.close();
    }
}