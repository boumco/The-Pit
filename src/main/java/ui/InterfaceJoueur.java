package ui;

import modele.Joueur;
import moteur.EvenementPerso;
import ui.Couleur.COULEUR;

public class InterfaceJoueur {

    public static String genererMenuJoueur(int jour, Joueur joueur) {
        return genererMenuJoueur(jour, joueur, 0, null);
    }

    public static String genererMenuJoueur(int jour, Joueur joueur, int selection) {
        return genererMenuJoueur(jour, joueur, selection, null);
    }

    public static String genererMenuJoueur(int jour, Joueur joueur, int selection, EvenementPerso eventPerso) {
        String bourse = colorerSiSelection("1 . Bourse", selection == 0);
        String journal = colorerSiSelection("2 . Journal (1€)", selection == 1);
        String dormir = colorerSiSelection("3 . Allez Dormir", selection == 2);
        String quitter = colorerSiSelection("4 . Quitter", selection == 3);
        String[] box = boiteTelegramme(eventPerso);

        String[] lignes = {
            "         ____________________________________________________________",
            "        /_____/_____/_____/_____/_____/_____/_____/_____/_____/_____/",
            "       ._. ___________           Jour : " + jour + "/15",
            "       | | | /(...   |    NOM : " + joueur.getNom(),
            "       |_| |/ ,-(_`; |",
            "       |-| |\\ )_  _; |  SOLDE : " + String.format("%.2f", joueur.getCash()) + " €",
            "       | | |(\\[_][_])|",
            "       |_| | |  L  | |  DETTE : " + String.format("%.2f", joueur.getDette()) + " €",
            "       ._. | | \\-_/  |",
            "       | | |_________|",
            "       |_|",
            "       |-|",
            "       | |",
            "       |_|   " + bourse + "                        " + journal,
            "       ._.",
            "       | |",
            "       |_|   " + dormir + "                  " + quitter,
            "       |-|",
            "       | |  ______________________________________________________",
            "       |_| /_____/_____/_____/_____/_____/_____/_____/_____/_____/"
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lignes.length; i++) {
            String ligne = padVisible(lignes[i], 74);
            if (i < box.length) {
                ligne = ligne + "  " + box[i];
            }
            sb.append(ligne).append('\n');
        }
        return sb.toString();
    }

    private static String[] boiteTelegramme(EvenementPerso event) {
        int largeur = 36;
        COULEUR bord = COULEUR.GRIS;
        String titre = "TELEGRAMME";
        String[] corps;
        if (event == null) {
            corps = new String[] {"Aucune nouvelle personnelle."};
        } else {
            bord = "Malus".equals(event.getInfluence()) ? COULEUR.ROUGE : COULEUR.VERT;
            titre = "Malus".equals(event.getInfluence()) ? "TELEGRAMME  /!\\" : "TELEGRAMME  +";
            corps = wrap(event.toString(), largeur - 4);
        }
        String[] lignes = new String[corps.length + 3];
        lignes[0] = Couleur.colorer("╔" + "═".repeat(largeur - 2) + "╗", false, bord);
        lignes[1] = bordure(Couleur.colorer(padVisible(titre, largeur - 4), false, bord), largeur, bord);
        for (int i = 0; i < corps.length; i++) {
            lignes[i + 2] = bordure(Couleur.colorer(padVisible(corps[i], largeur - 4), false, COULEUR.BLANC), largeur, bord);
        }
        lignes[lignes.length - 1] = Couleur.colorer("╚" + "═".repeat(largeur - 2) + "╝", false, bord);
        return lignes;
    }

    private static String bordure(String contenu, int largeur, COULEUR bord) {
        return Couleur.colorer("║ ", false, bord) + contenu + Couleur.colorer(" ║", false, bord);
    }

    // A retenir : on aligne les lignes sans compter les codes couleur ANSI.
    private static String padVisible(String texte, int largeur) {
        int visible = texte.replaceAll("\\u001b\\[[0-9;]*m", "").length();
        if (visible >= largeur) {
            return texte;
        }
        return texte + " ".repeat(largeur - visible);
    }

    private static String[] wrap(String texte, int largeur) {
        String[] mots = texte.split(" ");
        java.util.ArrayList<String> lignes = new java.util.ArrayList<>();
        StringBuilder ligne = new StringBuilder();
        for (String mot : mots) {
            if (ligne.length() == 0) {
                ligne.append(mot);
            } else if (ligne.length() + 1 + mot.length() <= largeur) {
                ligne.append(' ').append(mot);
            } else {
                lignes.add(ligne.toString());
                ligne = new StringBuilder(mot);
            }
        }
        if (ligne.length() > 0) {
            lignes.add(ligne.toString());
        }
        if (lignes.size() > 5) {
            return lignes.subList(0, 5).toArray(new String[0]);
        }
        return lignes.toArray(new String[0]);
    }

    private static String colorerSiSelection(String texte, boolean selectionne) {
        if (!selectionne) {
            return texte;
        }
        return "\u001b[47;30m" + texte + "\u001b[0m";
    }
}
