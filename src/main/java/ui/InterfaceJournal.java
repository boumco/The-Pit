package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import moteur.EvenementMarche;
import moteur.FaitDivers;
import ui.Couleur.COULEUR;

public class InterfaceJournal {

    private static final int NB_COLONNES = 5;
    private static final int NB_BREF = 4;
    private static final String[] RUBRIQUES = {"SOCIETE", "LOCAL", "VILLE", "CHRONIQUE", "FAITS"};
    private static final Set<Integer> derniersSlots = new HashSet<>();

    public static String afficher(EvenementMarche une, List<EvenementMarche> marche, List<FaitDivers> divers) {
        Random r = new Random();
        int[] taille = tailleTerminal();
        int largeur = taille[0];
        int hauteur = taille[1];
        int col = Math.max(16, (largeur - (NB_COLONNES + 1)) / NB_COLONNES);

        List<Article> faits = new ArrayList<>();
        for (FaitDivers fait : divers) {
            faits.add(new Article(fait.getTitre(), fait.getExtrait(), fait.getRubrique()));
        }
        Collections.shuffle(faits, r);

        List<Article> infosBourse = new ArrayList<>();
        if (une != null) {
            infosBourse.add(articleMarche(une, r));
        }
        if (marche != null) {
            for (EvenementMarche event : marche) {
                infosBourse.add(articleMarche(event, r));
            }
        }

        int nbSlots = 1 + NB_COLONNES + NB_BREF;
        Article[] page = new Article[nbSlots];
        Set<Integer> slotsPris = new HashSet<>();
        for (Article info : infosBourse) {
            int slot = tirerSlot(nbSlots, slotsPris, r);
            slotsPris.add(slot);
            page[slot] = info;
        }
        derniersSlots.clear();
        derniersSlots.addAll(slotsPris);

        int f = 0;
        for (int s = 0; s < nbSlots; s++) {
            if (page[s] == null && f < faits.size()) {
                page[s] = faits.get(f);
                f++;
            }
        }

        int lignesReservees = 12 + 6 + 2 + NB_BREF + 3;
        int maxWrap = Math.max(6, hauteur - lignesReservees);

        StringBuilder sb = new StringBuilder();
        sb.append(cadre(largeur));
        sb.append(ligneTexte("", largeur));
        sb.append(masthead(largeur));
        sb.append(ligneTexte(Couleur.colorer(centrer(
            "\"Ce que le diable lit au petit-dejeuner\"     1€     16 pages     Tirage de l'enfer",
            largeur - 2), false, COULEUR.GRIS), largeur));
        sb.append(ligneTexte("", largeur));
        sb.append(separateur(largeur));

        Article aLaUne = page[0];
        if (aLaUne != null) {
            sb.append(ligneTexte(Couleur.colorer("  A LA UNE   /   " + aLaUne.rubrique, false, COULEUR.ROUGE), largeur));
            sb.append(ligneTexte(Couleur.colorer("  " + aLaUne.titre.toUpperCase(), false, COULEUR.CYAN), largeur));
            for (String ligne : wrap(aLaUne.extrait, largeur - 8, 5)) {
                sb.append(ligneTexte("  " + Couleur.colorer(ligne, false, COULEUR.BLANC), largeur));
            }
            sb.append(separateur(largeur));
        }

        List<List<String>> colonnes = new ArrayList<>();
        for (int c = 0; c < NB_COLONNES; c++) {
            Article art = page[1 + c];
            colonnes.add(art != null ? colonneArticle(art, col, maxWrap) : new ArrayList<>());
        }

        int haut = maxWrap + 3;
        for (List<String> colonne : colonnes) {
            haut = Math.max(haut, colonne.size());
        }
        for (int i = 0; i < haut; i++) {
            StringBuilder ligne = new StringBuilder();
            ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
            for (int c = 0; c < NB_COLONNES; c++) {
                ligne.append(caseCol(colonnes.get(c), i, col));
                if (c < NB_COLONNES - 1) {
                    ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
                }
            }
            ligne.append(Couleur.colorer("│", false, COULEUR.GRIS));
            sb.append(ligneTexteSansBords(ligne.toString(), largeur));
        }
        sb.append(separateur(largeur));
        sb.append(ligneTexte(Couleur.colorer("  EN BREF", false, COULEUR.JAUNE), largeur));
        int debutBref = 1 + NB_COLONNES;
        for (int i = debutBref; i < nbSlots; i++) {
            Article a = page[i];
            if (a == null) {
                continue;
            }
            sb.append(ligneTexte("  " + Couleur.colorer("▸ " + a.titre, false, COULEUR.CYAN)
                + Couleur.colorer("  — " + raccourcir(a.extrait, largeur - 28), false, COULEUR.GRIS), largeur));
        }
        sb.append(ligneTexte("", largeur));
        sb.append(cadreBas(largeur));
        return sb.toString();
    }

    private static int[] tailleTerminal() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            int largeur = terminal.getWidth();
            int hauteur = terminal.getHeight();
            if (largeur < 100) {
                largeur = 120;
            }
            if (largeur > 240) {
                largeur = 240;
            }
            if (hauteur < 30) {
                hauteur = 40;
            }
            if (hauteur > 80) {
                hauteur = 80;
            }
            return new int[]{largeur, hauteur};
        } catch (Exception e) {
            return new int[]{120, 40};
        }
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

    private static String masthead(int largeur) {
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
            sb.append(ligneTexte(Couleur.colorer(centrer(ligne, largeur - 2), false, COULEUR.ROUGE), largeur));
        }
        sb.append(ligneTexte(Couleur.colorer(centrer(
            "C H R O N I C L E          EDITION DU MATIN          16 PAGES",
            largeur - 2), false, COULEUR.JAUNE), largeur));
        return sb.toString();
    }

    private static List<String> colonneArticle(Article article, int col, int maxWrap) {
        List<String> lignes = new ArrayList<>();
        lignes.add(Couleur.colorer(article.rubrique, false, COULEUR.MAGENTA));
        lignes.add(Couleur.colorer(raccourcir(article.titre.toUpperCase(), col - 2), false, COULEUR.CYAN));
        lignes.add("");
        lignes.addAll(colorerLignes(wrap(article.extrait, col - 2, maxWrap), COULEUR.BLANC));
        return lignes;
    }

    private static List<String> colorerLignes(List<String> textes, COULEUR couleur) {
        List<String> out = new ArrayList<>();
        for (String t : textes) {
            out.add(Couleur.colorer(t, false, couleur));
        }
        return out;
    }

    private static String caseCol(List<String> col, int i, int largeurCol) {
        String texte = i < col.size() ? col.get(i) : "";
        int visible = longueurVisible(texte);
        if (visible < largeurCol) {
            texte = texte + " ".repeat(largeurCol - visible);
        }
        return texte;
    }

    private static List<String> wrap(String texte, int largeur, int maxLignes) {
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
        if (lignes.size() > maxLignes) {
            return lignes.subList(0, maxLignes);
        }
        return lignes;
    }

    private static String raccourcir(String texte, int max) {
        if (texte.length() <= max) {
            return texte;
        }
        return texte.substring(0, Math.max(0, max - 1)) + "…";
    }

    private static String centrer(String texte, int largeur) {
        int visible = longueurVisible(texte);
        int pad = Math.max(0, (largeur - visible) / 2);
        return " ".repeat(pad) + texte;
    }

    private static String cadre(int largeur) {
        return Couleur.colorer("╔" + "═".repeat(largeur - 2) + "╗", false, COULEUR.GRIS) + "\n";
    }

    private static String cadreBas(int largeur) {
        return Couleur.colorer("╚" + "═".repeat(largeur - 2) + "╝", false, COULEUR.GRIS) + "\n";
    }

    private static String separateur(int largeur) {
        return Couleur.colorer("╠" + "═".repeat(largeur - 2) + "╣", false, COULEUR.GRIS) + "\n";
    }

    private static String ligneTexte(String contenu, int largeur) {
        int interieur = largeur - 2;
        int visible = longueurVisible(contenu);
        if (visible < interieur) {
            contenu = contenu + " ".repeat(interieur - visible);
        }
        return Couleur.colorer("║", false, COULEUR.GRIS) + contenu + Couleur.colorer("║", false, COULEUR.GRIS) + "\n";
    }

    private static String ligneTexteSansBords(String contenu, int largeur) {
        int visible = longueurVisible(contenu);
        if (visible < largeur) {
            contenu = contenu + " ".repeat(largeur - visible);
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
