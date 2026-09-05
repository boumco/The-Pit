package ui;

import java.util.ArrayList;
import java.util.List;

import moteur.EvenementMarche;
import moteur.FaitDivers;
import ui.Couleur.COULEUR;

public class InterfaceJournal {

    private static final int LARGEUR_ECRAN = 120;
    private static final int LARGEUR = 96;
    private static final int COL = 30;

    public static String afficher(EvenementMarche une, List<EvenementMarche> marche, List<FaitDivers> divers) {
        String pad = " ".repeat(Math.max(0, (LARGEUR_ECRAN - LARGEUR) / 2));
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(cadre(pad));
        sb.append(pad).append(cellulePleine("")).append('\n');
        sb.append(masthead(pad));
        sb.append(pad).append(ligneTexte(Couleur.colorer("  \"Ce que le diable lit au petit-dejeuner\"   ·   1€   ·   Tirage de l'enfer", false, COULEUR.GRIS))).append('\n');
        sb.append(pad).append(cellulePleine("")).append('\n');
        sb.append(separateur(pad));

        if (une != null) {
            sb.append(pad).append(ligneTexte(Couleur.colorer("  A LA UNE", false, COULEUR.ROUGE))).append('\n');
            sb.append(pad).append(ligneTexte(Couleur.colorer("  " + titreMarche(une), false, couleurMarche(une)))).append('\n');
            for (String ligne : wrap(une.intitule, LARGEUR - 8)) {
                sb.append(pad).append(ligneTexte("  " + Couleur.colorer(ligne, false, COULEUR.BLANC))).append('\n');
            }
            sb.append(separateur(pad));
        }

        List<String> colGauche = colonneFait(divers.size() > 0 ? divers.get(0) : null);
        List<String> colCentre = colonneMarche(marche.size() > 0 ? marche.get(0) : null);
        List<String> colDroite = colonneFait(divers.size() > 1 ? divers.get(1) : null);
        int haut = Math.max(colGauche.size(), Math.max(colCentre.size(), colDroite.size()));
        for (int i = 0; i < haut; i++) {
            sb.append(pad).append(Couleur.colorer("│", false, COULEUR.GRIS));
            sb.append(caseCol(colGauche, i));
            sb.append(Couleur.colorer("│", false, COULEUR.GRIS));
            sb.append(caseCol(colCentre, i));
            sb.append(Couleur.colorer("│", false, COULEUR.GRIS));
            sb.append(caseCol(colDroite, i));
            sb.append(Couleur.colorer("│", false, COULEUR.GRIS)).append('\n');
        }
        sb.append(separateur(pad));
        sb.append(pad).append(ligneTexte(Couleur.colorer("  EN BREF", false, COULEUR.JAUNE))).append('\n');
        for (int i = 2; i < divers.size() && i < 5; i++) {
            FaitDivers f = divers.get(i);
            sb.append(pad).append(ligneTexte("  " + Couleur.colorer("▸ " + f.getTitre(), false, COULEUR.CYAN)
                + Couleur.colorer("  — " + raccourcir(f.getExtrait(), 52), false, COULEUR.GRIS))).append('\n');
        }
        sb.append(cadreBas(pad));
        return sb.toString();
    }

    private static String masthead(String pad) {
        String[] lignes = {
            "              ██████╗ ██╗████████╗     ██████╗██╗  ██╗██████╗  ██████╗ ███╗   ██╗",
            "              ██╔══██╗██║╚══██╔══╝    ██╔════╝██║  ██║██╔══██╗██╔═══██╗████╗  ██║",
            "              ██████╔╝██║   ██║       ██║     ███████║██████╔╝██║   ██║██╔██╗ ██║",
            "              ██╔═══╝ ██║   ██║       ██║     ██╔══██║██╔══██╗██║   ██║██║╚██╗██║",
            "              ██║     ██║   ██║       ╚██████╗██║  ██║██║  ██║╚██████╔╝██║ ╚████║",
            "              ╚═╝     ╚═╝   ╚═╝        ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝"
        };
        StringBuilder sb = new StringBuilder();
        for (String ligne : lignes) {
            sb.append(pad).append(ligneTexte(Couleur.colorer(ligne, false, COULEUR.ROUGE))).append('\n');
        }
        sb.append(pad).append(ligneTexte(Couleur.colorer("                         C H R O N I C L E   ·   EDITION DU MATIN", false, COULEUR.JAUNE))).append('\n');
        return sb.toString();
    }

    private static List<String> colonneFait(FaitDivers fait) {
        List<String> lignes = new ArrayList<>();
        if (fait == null) {
            return lignes;
        }
        lignes.add(Couleur.colorer(fait.getRubrique(), false, COULEUR.MAGENTA));
        lignes.add(Couleur.colorer(raccourcir(fait.getTitre().toUpperCase(), COL - 2), false, COULEUR.CYAN));
        lignes.add("");
        lignes.addAll(colorerLignes(wrap(fait.getExtrait(), COL - 2), COULEUR.BLANC));
        return lignes;
    }

    private static List<String> colonneMarche(EvenementMarche event) {
        List<String> lignes = new ArrayList<>();
        if (event == null) {
            return lignes;
        }
        lignes.add(Couleur.colorer("MARCHES", false, COULEUR.JAUNE));
        lignes.add(Couleur.colorer(raccourcir(titreMarche(event), COL - 2), false, couleurMarche(event)));
        lignes.add("");
        lignes.addAll(colorerLignes(wrap(event.intitule, COL - 2), COULEUR.BLANC));
        return lignes;
    }

    private static List<String> colorerLignes(List<String> textes, COULEUR couleur) {
        List<String> out = new ArrayList<>();
        for (String t : textes) {
            out.add(Couleur.colorer(t, false, couleur));
        }
        return out;
    }

    private static String titreMarche(EvenementMarche e) {
        e.setInfluence();
        if (e.chiffre >= 2) {
            return e.getNomEntreprise().toUpperCase() + " FLAMBE EN BOURSE";
        }
        if (e.chiffre == 1) {
            return e.getNomEntreprise() + " surprend la place";
        }
        if (e.chiffre == 0) {
            return e.getNomEntreprise() + " : seance sans eclat";
        }
        if (e.chiffre == -1) {
            return "Coup de mou pour " + e.getNomEntreprise();
        }
        return e.getNomEntreprise().toUpperCase() + " DANS LA TOURMENTE";
    }

    private static COULEUR couleurMarche(EvenementMarche e) {
        if (e.chiffre > 0) {
            return COULEUR.VERT;
        }
        if (e.chiffre < 0) {
            return COULEUR.ROUGE;
        }
        return COULEUR.JAUNE;
    }

    private static String caseCol(List<String> col, int i) {
        String texte = i < col.size() ? col.get(i) : "";
        int visible = longueurVisible(texte);
        if (visible < COL) {
            texte = texte + " ".repeat(COL - visible);
        }
        return texte;
    }

    // A retenir : on coupe le texte en lignes de N caracteres sans casser le cadre du journal.
    private static List<String> wrap(String texte, int largeur) {
        List<String> lignes = new ArrayList<>();
        if (texte == null || texte.isBlank()) {
            return lignes;
        }
        String[] mots = texte.split(" ");
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
            return lignes.subList(0, 5);
        }
        return lignes;
    }

    private static String raccourcir(String texte, int max) {
        if (texte.length() <= max) {
            return texte;
        }
        return texte.substring(0, Math.max(0, max - 1)) + "…";
    }

    private static String cadre(String pad) {
        return pad + Couleur.colorer("╔" + "═".repeat(LARGEUR - 2) + "╗", false, COULEUR.GRIS) + "\n";
    }

    private static String cadreBas(String pad) {
        return pad + Couleur.colorer("╚" + "═".repeat(LARGEUR - 2) + "╝", false, COULEUR.GRIS) + "\n";
    }

    private static String separateur(String pad) {
        return pad + Couleur.colorer("╠" + "═".repeat(LARGEUR - 2) + "╣", false, COULEUR.GRIS) + "\n";
    }

    private static String cellulePleine(String ignore) {
        return ligneTexte("");
    }

    private static String ligneTexte(String contenu) {
        int interieur = LARGEUR - 2;
        int visible = longueurVisible(contenu);
        if (visible < interieur) {
            contenu = contenu + " ".repeat(interieur - visible);
        }
        return Couleur.colorer("║", false, COULEUR.GRIS) + contenu + Couleur.colorer("║", false, COULEUR.GRIS);
    }

    private static int longueurVisible(String texte) {
        return texte.replaceAll("\\u001b\\[[0-9;]*m", "").length();
    }
}
