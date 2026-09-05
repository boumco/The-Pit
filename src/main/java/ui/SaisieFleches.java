package ui;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntConsumer;

public class SaisieFleches {

    public static int choisirListe(List<String> libelles) {
        return boucle(libelles.size(), (index, action) -> {
            if (action == ActionTouche.HAUT) {
                return (index - 1 + libelles.size()) % libelles.size();
            }
            if (action == ActionTouche.BAS) {
                return (index + 1) % libelles.size();
            }
            return index;
        }, index -> {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Fleches haut/bas puis Entree pour valider.\n");
            for (int i = 0; i < libelles.size(); i++) {
                if (i == index) {
                    Couleur.println(libelles.get(i), true, Couleur.COULEUR.BLANC);
                } else {
                    System.out.println(libelles.get(i));
                }
            }
        });
    }

    public static int choisirGrille(int colonnes, int lignes, IntConsumer affichage) {
        int total = colonnes * lignes;
        return boucle(total, (index, action) -> {
            int ligne = index / colonnes;
            int col = index % colonnes;
            if (action == ActionTouche.HAUT && ligne > 0) {
                return index - colonnes;
            }
            if (action == ActionTouche.BAS && ligne < lignes - 1) {
                return index + colonnes;
            }
            if (action == ActionTouche.GAUCHE && col > 0) {
                return index - 1;
            }
            if (action == ActionTouche.DROITE && col < colonnes - 1) {
                return index + 1;
            }
            return index;
        }, affichage::accept);
    }

    public static void attendreEntree() {
        boucle(1, (index, action) -> index, index -> { });
    }

    private static int boucle(int taille, Navigateur navigateur, IntConsumer affichage) {
        BlockingQueue<ActionTouche> file = new LinkedBlockingQueue<>();
        try (ConsoleBrut console = new ConsoleBrut()) {
            LecteurTouches lecteur = new LecteurTouches(file, console);
            Thread threadClavier = new Thread(lecteur, "clavier");
            threadClavier.setDaemon(true);
            threadClavier.start();

            int index = 0;
            affichage.accept(index);
            while (true) {
                ActionTouche action = file.take();
                if (action == ActionTouche.ENTREE) {
                    lecteur.arreter();
                    return index;
                }
                int suivant = navigateur.suivant(index, action);
                if (suivant != index) {
                    index = suivant;
                    affichage.accept(index);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'ouvrir le terminal en mode brut", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    @FunctionalInterface
    private interface Navigateur {
        int suivant(int index, ActionTouche action);
    }
}
