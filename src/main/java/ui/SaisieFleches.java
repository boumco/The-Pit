package ui;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntConsumer;

import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;

public class SaisieFleches {

    public static int choisirListe(List<String> libelles) {
        return boucle(libelles.size(), 0, (index, action) -> {
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
        return choisirGrille(colonnes, lignes, 0, affichage);
    }

    public static int choisirGrille(int colonnes, int lignes, int indexInitial, IntConsumer affichage) {
        int total = colonnes * lignes;
        return boucle(total, indexInitial, (index, action) -> {
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
        boucle(1, 0, (index, action) -> index, index -> { });
    }

    public static String lireTexte(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        StringBuilder saisie = new StringBuilder();
        try {
            Terminal terminal = ConsoleBrut.obtenir();
            terminal.enterRawMode();
            terminal.echo(false);
            NonBlockingReader reader = terminal.reader();
            while (true) {
                int octet = reader.read();
                if (octet == -1) {
                    break;
                }
                if (octet == '\r' || octet == '\n') {
                    System.out.println();
                    break;
                }
                if (octet == 127 || octet == 8) {
                    if (saisie.length() > 0) {
                        saisie.deleteCharAt(saisie.length() - 1);
                        System.out.print("\b \b");
                        System.out.flush();
                    }
                    continue;
                }
                if (octet >= 32 && octet < 127) {
                    saisie.append((char) octet);
                    System.out.print((char) octet);
                    System.out.flush();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le texte", e);
        } finally {
            ConsoleBrut.restaurerModeNormal();
        }
        return saisie.toString().trim();
    }

    public static int lireEntier(String prompt) {
        while (true) {
            String texte = lireTexte(prompt);
            try {
                return Integer.parseInt(texte);
            } catch (NumberFormatException e) {
                System.out.println("Entre un nombre.");
            }
        }
    }

    private static int boucle(int taille, int indexInitial, Navigateur navigateur, IntConsumer affichage) {
        BlockingQueue<ActionTouche> file = new LinkedBlockingQueue<>();
        LecteurTouches lecteur = null;
        Thread threadClavier = null;
        try (ConsoleBrut console = new ConsoleBrut()) {
            lecteur = new LecteurTouches(file, console);
            threadClavier = new Thread(lecteur, "clavier");
            threadClavier.setDaemon(true);
            threadClavier.start();

            int index = Math.max(0, Math.min(indexInitial, Math.max(0, taille - 1)));
            affichage.accept(index);
            while (true) {
                ActionTouche action = file.take();
                if (action == ActionTouche.ENTREE) {
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
            return indexInitial;
        } finally {
            if (lecteur != null) {
                lecteur.arreter();
            }
            if (threadClavier != null) {
                try {
                    threadClavier.join(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            ConsoleBrut.restaurerModeNormal();
        }
    }

    @FunctionalInterface
    private interface Navigateur {
        int suivant(int index, ActionTouche action);
    }
}
