package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import moteur.EvenementMarche;
import moteur.FaitDivers;
import ui.Couleur.COULEUR;

public class InterfaceJournal {

    private static final int LARGEUR_ECRAN = 120;
    private static final int LARGEUR = 117;
    private static final int COL = 28;
    private static final int NB_COLONNES = 4;
    private static int dernierEmplacement = -1;

    public static String afficher(EvenementMarche une, List<EvenementMarche> marche, List<FaitDivers> divers) {
        String pad = " ".repeat(Math.max(0, (LARGEUR_ECRAN - LARGEUR) / 2));
        StringBuilder sb = new StringBuilder();
        Random r = new Random();

        sb.append('\n');
        sb.append(cadre(pad));
        sb.append(pad).append(ligneTexte("")).append('\n');
        sb.append(masthead(pad));
        sb.append(pad).append(ligneTexte(Couleur.colorer("  \"Ce que le diable lit au petit-dejeuner\"     1€     12 pages     Tirage de l'enfer", false, COULEUR.GRIS))).append('\n');
        sb.append(pad).append(ligneTexte("")).append('\n');
        sb.append(separateur(pad));

        FaitDivers uneVisible = divers.size() > 0 ? divers.get(0) : null;
        if (uneVisible != null) {
            sb.append(pad).append(ligneTexte(Couleur.colorer("  A LA UNE   /   " + uneVisible.getRubrique(), false, COULEUR.ROUGE))).append('\n');
            sb.append(pad).append(ligneTexte(Couleur.colorer("  " + uneVisible.getTitre().toUpperCase(), false, COULEUR.CYAN))).append('\n');
            for (String ligne : wrap(uneVisible.getExtrait(), LARGEUR - 8)) {
                sb.append(pad).append(ligneTexte("  " + Couleur.colorer(ligne, false, COULEUR.BLANC))).append('\n');
            }
            sb.append(separateur(pad));
        }

        List<List<String>> colonnes = new ArrayList<>();
        int faitIndex = 1;
        for (int c = 0; c < NB_COLONNES; c++) {
            FaitDivers fait = faitIndex < divers.size() ? divers.get(faitIndex) : null;
            faitIndex++;
            colonnes.add(colonneFait(fait));
        }

        List<EvenementMarche> brieves = new ArrayList<>();
        if (une != null) {
            brieves.add(une);
        }
        if (marche != null) {
            brieves.addAll(marche);
        }

        // A retenir : les infos bourse sont en gris et changent de colonne chaque jour,
        // jamais celle de la veille, pour ne pas sauter aux yeux.
        int emplacement = nouvelEmplacement(NB_COLONNES, r);
        for (int i = 0; i < brieves.size(); i++) {
            int slot = (emplacement + i * 2) % NB_COLONNES;
            if (i > 0 && slot == emplacement) {
                slot = (slot + 1) % NB_COLONNES;
            }
            colonnes.get(slot).add("");
            colonnes.get(slot).addAll(colonneMarcheDiscrete(brieves.get(i)));
        }

        int haut = 0;
        for (List<String> col : colonnes) {
            haut = Math.max(haut, col.size());
        }
        haut = Math.max(haut, 8);
        for (int i = 0; i < haut; i++) {
            sb.append(pad).append(Couleur.colorer("│", false, COULEUR.GRIS));
            for (int c = 0; c < NB_COLONNES; c++) {
                sb.append(caseCol(colonnes.get(c), i));
                if (c < NB_COLONNES - 1) {
                    sb.append(Couleur.colorer("│", false, COULEUR.GRIS));
                }
            }
            sb.append(Couleur.colorer("│", false, COULEUR.GRIS)).append('\n');
        }
        sb.append(separateur(pad));
        sb.append(pad).append(ligneTexte(Couleur.colorer("  EN BREF", false, COULEUR.JAUNE))).append('\n');
        for (int i = faitIndex; i < divers.size() && i < faitIndex + 4; i++) {
            FaitDivers f = divers.get(i);
            sb.append(pad).append(ligneTexte("  " + Couleur.colorer("▸ " + f.getTitre(), false, COULEUR.CYAN)
                + Couleur.colorer("  — " + raccourcir(f.getExtrait(), 70), false, COULEUR.GRIS))).append('\n');
        }
        sb.append(pad).append(ligneTexte("")).append('\n');
        sb.append(cadreBas(pad));
        return sb.toString();
    }

    private static int nouvelEmplacement(int max, Random r) {
        int choix = r.nextInt(max);
        if (max > 1 && choix == dernierEmplacement) {
            choix = (choix + 1 + r.nextInt(max - 1)) % max;
        }
        dernierEmplacement = choix;
        return choix;
    }

    private static String masthead(String pad) {
        String[] lignes = {
            "     ██████╗ ██╗████████╗     ██████╗██╗  ██╗██████╗  ██████╗ ███╗   ██╗    ██╗ ██████╗██╗     ███████╗",
            "     ██╔══██╗██║╚══██╔══╝    ██╔════╝██║  ██║██╔══██╗██╔═══██╗████╗  ██║    ██║██╔════╝██║     ██╔════╝",
            "     ██████╔╝██║   ██║       ██║     ███████║██████╔╝██║   ██║██╔██╗ ██║    ██║██║     ██║     █████╗  ",
            "     ██╔═══╝ ██║   ██║       ██║     ██╔══██║██╔══██╗██║   ██║██║╚██╗██║    ██║██║     ██║     ██╔══╝  ",
            "     ██║     ██║   ██║       ╚██████╗██║  ██║██║  ██║╚██████╔╝██║ ╚████║    ██║╚██████╗███████╗███████╗",
            "     ╚═╝     ╚═╝   ╚═╝        ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝    ╚═╝ ╚═════╝╚══════╝╚══════╝"
        };
        StringBuilder sb = new StringBuilder();
        for (String ligne : lignes) {
            sb.append(pad).append(ligneTexte(Couleur.colorer(raccourcir(ligne, LARGEUR - 4), false, COULEUR.ROUGE))).append('\n');
        }
        sb.append(pad).append(ligneTexte(Couleur.colorer("                    C H R O N I C L E          EDITION DU MATIN          12 PAGES", false, COULEUR.JAUNE))).append('\n');
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

    private static List<String> colonneMarcheDiscrete(EvenementMarche event) {
        List<String> lignes = new ArrayList<>();
        if (event == null) {
            return lignes;
        }
        lignes.add(Couleur.colorer("entrefilet", false, COULEUR.GRIS));
        lignes.add(Couleur.colorer(raccourcir(titreMarcheDiscret(event), COL - 2), false, COULEUR.GRIS));
        lignes.addAll(colorerLignes(wrap(event.intitule, COL - 2), COULEUR.GRIS));
        return lignes;
    }

    private static List<String> colorerLignes(List<String> textes, COULEUR couleur) {
        List<String> out = new ArrayList<>();
        for (String t : textes) {
            out.add(Couleur.colorer(t, false, couleur));
        }
        return out;
    }

    private static String titreMarcheDiscret(EvenementMarche e) {
        e.setInfluence();
        return e.getNomEntreprise() + " : note de marche";
    }

    private static String caseCol(List<String> col, int i) {
        String texte = i < col.size() ? col.get(i) : "";
        int visible = longueurVisible(texte);
        if (visible < COL) {
            texte = texte + " ".repeat(COL - visible);
        }
        return texte;
    }

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
        if (lignes.size() > 6) {
            return lignes.subList(0, 6);
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
