package ui;

import java.util.ArrayList;
import java.util.List;

import modele.Bougie;

public class GraphiqueBougies {

    private static final String VERT = "\u001b[92m";
    private static final String ROUGE = "\u001b[91m";
    private static final String GRIS = "\u001b[90m";
    private static final String RESET = "\u001b[0m";

    public static List<String> dessiner(List<Bougie> historique, int largeur, int hauteur) {
        List<String> lignes = new ArrayList<>();
        if (historique == null || historique.isEmpty() || largeur < 12 || hauteur < 6) {
            for (int i = 0; i < hauteur; i++) {
                lignes.add(" ".repeat(largeur));
            }
            return lignes;
        }

        int largeurAxe = 8;
        int largeurZone = largeur - largeurAxe;
        int pasBougie = 3;
        int maxBougies = Math.max(1, largeurZone / pasBougie);
        int debut = Math.max(0, historique.size() - maxBougies);
        List<Bougie> visibles = historique.subList(debut, historique.size());

        double min = visibles.get(0).getBas();
        double max = visibles.get(0).getHaut();
        for (Bougie b : visibles) {
            min = Math.min(min, b.getBas());
            max = Math.max(max, b.getHaut());
        }
        if (max - min < 0.01) {
            max += 1;
            min -= 1;
        }

        char[][] grille = new char[hauteur][largeurZone];
        int[][] teinte = new int[hauteur][largeurZone];
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeurZone; x++) {
                grille[y][x] = (y % 4 == 0) ? '·' : ' ';
                teinte[y][x] = 0;
            }
        }

        // A retenir : on convertit un prix en numero de ligne.
        // ligne 0 = prix max (haut du graphe), derniere ligne = prix min.
        // Formule : (max - prix) / (max - min) * (hauteur - 1)
        for (int i = 0; i < visibles.size(); i++) {
            Bougie b = visibles.get(i);
            int col = 1 + i * pasBougie;
            if (col >= largeurZone) {
                break;
            }
            int yHaut = lignePourPrix(b.getHaut(), min, max, hauteur);
            int yBas = lignePourPrix(b.getBas(), min, max, hauteur);
            int yOpen = lignePourPrix(b.getOuverture(), min, max, hauteur);
            int yClose = lignePourPrix(b.getCloture(), min, max, hauteur);
            int yCorpsHaut = Math.min(yOpen, yClose);
            int yCorpsBas = Math.max(yOpen, yClose);
            int couleur = b.estHausse() ? 1 : 2;

            for (int y = yHaut; y <= yBas; y++) {
                poser(grille, teinte, col, y, '│', couleur);
            }
            if (yCorpsHaut == yCorpsBas) {
                poser(grille, teinte, col, yCorpsHaut, '█', couleur);
            } else {
                for (int y = yCorpsHaut; y <= yCorpsBas; y++) {
                    poser(grille, teinte, col, y, '█', couleur);
                    if (col + 1 < largeurZone) {
                        poser(grille, teinte, col + 1, y, '█', couleur);
                    }
                }
            }
        }

        for (int y = 0; y < hauteur; y++) {
            StringBuilder ligne = new StringBuilder();
            int couleurEnCours = -1;
            for (int x = 0; x < largeurZone; x++) {
                int c = teinte[y][x];
                if (c != couleurEnCours) {
                    if (couleurEnCours != -1) {
                        ligne.append(RESET);
                    }
                    if (c == 1) {
                        ligne.append(VERT);
                    } else if (c == 2) {
                        ligne.append(ROUGE);
                    } else if (grille[y][x] == '·') {
                        ligne.append(GRIS);
                    }
                    couleurEnCours = c;
                }
                ligne.append(grille[y][x]);
            }
            ligne.append(RESET);
            double prixLigne = max - (max - min) * y / (double) (hauteur - 1);
            ligne.append(GRIS);
            ligne.append(String.format(" %6.1f", prixLigne));
            ligne.append(RESET);
            lignes.add(ajusterLargeur(ligne.toString(), largeur));
        }
        return lignes;
    }

    private static int lignePourPrix(double prix, double min, double max, int hauteur) {
        int ligne = (int) Math.round((max - prix) / (max - min) * (hauteur - 1));
        if (ligne < 0) {
            return 0;
        }
        if (ligne >= hauteur) {
            return hauteur - 1;
        }
        return ligne;
    }

    private static void poser(char[][] grille, int[][] teinte, int x, int y, char dessin, int couleur) {
        if (y < 0 || y >= grille.length || x < 0 || x >= grille[0].length) {
            return;
        }
        grille[y][x] = dessin;
        teinte[y][x] = couleur;
    }

    private static String ajusterLargeur(String ligne, int largeurVisible) {
        int visible = longueurVisible(ligne);
        if (visible < largeurVisible) {
            return ligne + " ".repeat(largeurVisible - visible);
        }
        return ligne;
    }

    static int longueurVisible(String texte) {
        return texte.replaceAll("\\u001b\\[[0-9;]*m", "").length();
    }
}
