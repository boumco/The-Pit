package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import moteur.EvenementMarche;
import moteur.FaitDivers;
import ui.Couleur.COULEUR;

public class InterfaceJournal {

    private static final int LARGEUR = 120;
    private static final int COL = 22;
    private static final int NB_COLONNES = 5;
    private static final String[] RUBRIQUES = {"SOCIETE", "LOCAL", "VILLE", "CHRONIQUE", "FAITS"};
    private static final Set<Integer> derniersSlots = new HashSet<>();

    public static String afficher(EvenementMarche une, List<EvenementMarche> marche, List<FaitDivers> divers) {
        Random r = new Random();
        List<Article> articles = new ArrayList<>();
        for (FaitDivers fait : divers) {
            articles.add(new Article(fait.getTitre(), fait.getExtrait(), fait.getRubrique()));
        }
        Collections.shuffle(articles, r);

        List<EvenementMarche> infosBourse = new ArrayList<>();
        if (une != null) {
            infosBourse.add(une);
        }
        if (marche != null) {
            infosBourse.addAll(marche);
        }

        int nbSlots = 1 + NB_COLONNES + 3;
        Set<Integer> slotsPris = new HashSet<>();
        for (EvenementMarche event : infosBourse) {
            int slot = tirerSlot(nbSlots, slotsPris, r);
            slotsPris.add(slot);
            Article cache = articleMarche(event, r);
            if (slot < articles.size()) {
                articles.add(slot, cache);
            } else {
                articles.add(cache);
            }
        }
        derniersSlots.clear();
        derniersSlots.addAll(slotsPris);

        StringBuilder sb = new StringBuilder();
        sb.append(cadre());
        sb.append(ligneTexte(""));
        sb.append(masthead());
        sb.append(ligneTexte(Couleur.colorer("  \"Ce que le diable lit au petit-dejeuner\"     1€     16 pages     Tirage de l'enfer", false, COULEUR.GRIS)));
        sb.append(ligneTexte(""));
        sb.append(separateur());

        if (!articles.isEmpty()) {
            Article aLaUne = articles.get(0);
            sb.append(ligneTexte(Couleur.colorer("  A LA UNE   /   " + aLaUne.rubrique, false, COULEUR.ROUGE)));
            sb.append(ligneTexte(Couleur.colorer("  " + aLaUne.titre.toUpperCase(), false, COULEUR.CYAN)));
            for (String ligne : wrap(aLaUne.extrait, LARGEUR - 8)) {
                sb.append(ligneTexte("  " + Couleur.colorer(ligne, false, COULEUR.BLANC)));
            }
            sb.append(separateur());
        }

        List<List<String>> colonnes = new ArrayList<>();
        for (int c = 0; c < NB_COLONNES; c++) {
            int index = 1 + c;
            colonnes.add(index < articles.size() ? colonneArticle(articles.get(index)) : new ArrayList<>());
        }

        int haut = 8;
        for (List<String> col : colonnes) {
            haut = Math.max(haut, col.size());
        }
        for (int i = 0; i < haut; i++) {
            StringBuilder ligne = new StringBuilder();
            ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
            for (int c = 0; c < NB_COLONNES; c++) {
                ligne.append(caseCol(colonnes.get(c), i));
                if (c < NB_COLONNES - 1) {
                    ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
                }
            }
            ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
            sb.append(ligneTexteSansBords(ligne.toString()));
        }
        sb.append(separateur());
        sb.append(ligneTexte(Couleur.colorer("  EN BREF", false, COULEUR.JAUNE)));
        int debutBref = 1 + NB_COLONNES;
        for (int i = debutBref; i < articles.size(); i++) {
            Article a = articles.get(i);
            sb.append(ligneTexte("  " + Couleur.colorer("▸ " + a.titre, false, COULEUR.CYAN)
                + Couleur.colorer("  — " + raccourcir(a.extrait, 78), false, COULEUR.GRIS)));
        }
        sb.append(ligneTexte(""));
        sb.append(cadreBas());
        return sb.toString();
    }

    private static int tirerSlot(int max, Set<Integer> deja, Random r) {
        List<Integer> possibles = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if (!deja.contains(i) && !derniersSlots.contains(i)) {
                possibles.add(i);
            }
        }
        if (possibles.isEmpty()) {
            for (int i = 0; i < max; i++) {
                if (!deja.contains(i)) {
                    possibles.add(i);
                }
            }
        }
        if (possibles.isEmpty()) {
            return r.nextInt(max);
        }
        return possibles.get(r.nextInt(possibles.size()));
    }

    private static Article articleMarche(EvenementMarche event, Random r) {
        event.setInfluence();
        String rubrique = RUBRIQUES[r.nextInt(RUBRIQUES.length)];
        String titre = raccourcir(event.intitule, 42);
        return new Article(titre, event.intitule, rubrique);
    }

    private static String masthead() {
        String[] lignes = {
            " ██████╗ ██╗████████╗     ██████╗██╗  ██╗██████╗  ██████╗ ███╗   ██╗    ██╗ ██████╗██╗     ███████╗",
            " ██╔══██╗██║╚══██╔══╝    ██╔════╝██║  ██║██╔══██╗██╔═══██╗████╗  ██║    ██║██╔════╝██║     ██╔════╝",
            " ██████╔╝██║   ██║       ██║     ███████║██████╔╝██║   ██║██╔██╗ ██║    ██║██║     ██║     █████╗  ",
            " ██╔═══╝ ██║   ██║       ██║     ██╔══██║██╔══██╗██║   ██║██║╚██╗██║    ██║██║     ██║     ██╔══╝  ",
            " ██║     ██║   ██║       ╚██████╗██║  ██║██║  ██║╚██████╔╝██║ ╚████║    ██║╚██████╗███████╗███████╗",
            " ╚═╝     ╚═╝   ╚═╝        ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝    ╚═╝ ╚═════╝╚══════╝╚══════╝"
        };
        StringBuilder sb = new StringBuilder();
        for (String ligne : lignes) {
            sb.append(ligneTexte(Couleur.colorer(ligne, false, COULEUR.ROUGE)));
        }
        sb.append(ligneTexte(Couleur.colorer("                 C H R O N I C L E          EDITION DU MATIN          16 PAGES", false, COULEUR.JAUNE)));
        return sb.toString();
    }

    private static List<String> colonneArticle(Article article) {
        List<String> lignes = new ArrayList<>();
        lignes.add(Couleur.colorer(article.rubrique, false, COULEUR.MAGENTA));
        lignes.add(Couleur.colorer(raccourcir(article.titre.toUpperCase(), COL - 2), false, COULEUR.CYAN));
        lignes.add("");
        lignes.addAll(colorerLignes(wrap(article.extrait, COL - 2), COULEUR.BLANC));
        return lignes;
    }

    private static List<String> colorerLignes(List<String> textes, COULEUR couleur) {
        List<String> out = new ArrayList<>();
        for (String t : textes) {
            out.add(Couleur.colorer(t, false, couleur));
        }
        return out;
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
        if (lignes.size() > 7) {
            return lignes.subList(0, 7);
        }
        return lignes;
    }

    private static String raccourcir(String texte, int max) {
        if (texte.length() <= max) {
            return texte;
        }
        return texte.substring(0, Math.max(0, max - 1)) + "…";
    }

    private static String cadre() {
        return Couleur.colorer("╔" + "═".repeat(LARGEUR - 2) + "╗", false, COULEUR.GRIS) + "\n";
    }

    private static String cadreBas() {
        return Couleur.colorer("╚" + "═".repeat(LARGEUR - 2) + "╝", false, COULEUR.GRIS) + "\n";
    }

    private static String separateur() {
        return Couleur.colorer("╠" + "═".repeat(LARGEUR - 2) + "╣", false, COULEUR.GRIS) + "\n";
    }

    private static String ligneTexte(String contenu) {
        int interieur = LARGEUR - 2;
        int visible = longueurVisible(contenu);
        if (visible < interieur) {
            contenu = contenu + " ".repeat(interieur - visible);
        }
        return Couleur.colorer("║", false, COULEUR.GRIS) + contenu + Couleur.colorer("║", false, COULEUR.GRIS) + "\n";
    }

    private static String ligneTexteSansBords(String contenu) {
        int visible = longueurVisible(contenu);
        if (visible < LARGEUR) {
            contenu = contenu + " ".repeat(LARGEUR - visible);
        }
        return contenu + "\n";
    }

    private static int longueurVisible(String texte) {
        return texte.replaceAll("\\u001b\\[[0-9;]*m", "").length();
    }

    private static class Article {
        final String titre;
        final String extrait;
        final String rubrique;

        Article(String titre, String extrait, String rubrique) {
            this.titre = titre;
            this.extrait = extrait;
            this.rubrique = rubrique;
        }
    }
}
