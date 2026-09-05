package ui;

import java.util.List;

import modele.Entreprise;
import modele.Joueur;
import ui.Couleur.COULEUR;

public class InterfaceBourse {

    private static final int LARGEUR_ECRAN = 120;
    private static final int LARGEUR_CADRE = 92;
    private static final int HAUTEUR_GRAPHE = 16;

    public static String afficher(Entreprise entreprise, Joueur joueur, int boutonSelectionne) {
        int parts = joueur.getPortefeuille().getQuantite(entreprise);
        String pad = " ".repeat(Math.max(0, (LARGEUR_ECRAN - LARGEUR_CADRE) / 2));
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");

        sb.append(pad).append(lignePleine('─', '╭', '╮')).append('\n');
        sb.append(pad).append(ligneContenu(Couleur.colorer(entreprise.getNom(), false, COULEUR.CYAN)
            + Couleur.colorer("  ·  " + entreprise.getType().toString().toLowerCase(), false, COULEUR.GRIS))).append('\n');
        sb.append(pad).append(ligneContenu("")).append('\n');

        int largeurGraphe = LARGEUR_CADRE - 4;
        List<String> graphe = GraphiqueBougies.dessiner(entreprise.getHistorique(), largeurGraphe, HAUTEUR_GRAPHE);
        for (String ligneGraphe : graphe) {
            sb.append(pad).append(ligneContenu(ligneGraphe)).append('\n');
        }

        sb.append(pad).append(ligneContenu("")).append('\n');
        String infos = Couleur.colorer("Prix ancien ", false, COULEUR.GRIS)
            + Couleur.colorer(String.format("%.2f€", entreprise.getAncienneValeur()), false, COULEUR.JAUNE)
            + "     "
            + Couleur.colorer("Prix actuel ", false, COULEUR.GRIS)
            + Couleur.colorer(String.format("%.2f€", entreprise.getValeurAction()), false, COULEUR.VERT)
            + "     "
            + Couleur.colorer("NB actions ", false, COULEUR.GRIS)
            + Couleur.colorer(String.valueOf(parts), false, COULEUR.BLANC);
        sb.append(pad).append(ligneContenu(infos)).append('\n');
        sb.append(pad).append(lignePleine('─', '╰', '╯')).append('\n');
        sb.append('\n');
        sb.append(pad).append(ligneBoutons(boutonSelectionne)).append('\n');
        return sb.toString();
    }

    private static String ligneBoutons(int selection) {
        String[] labels = {" Acheter ", " Vendre ", " SUIVANT ", " Precedent ", " X "};
        COULEUR[] couleurs = {COULEUR.VERT, COULEUR.ROUGE, COULEUR.CYAN, COULEUR.CYAN, COULEUR.ROUGE};
        StringBuilder ligne = new StringBuilder();
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                ligne.append("   ");
            }
            if (i == selection) {
                ligne.append(Couleur.colorer(labels[i], true, COULEUR.BLANC));
            } else {
                ligne.append(Couleur.colorer(labels[i], false, couleurs[i]));
            }
        }
        return ligne.toString();
    }

    private static String lignePleine(char trait, char gauche, char droite) {
        return Couleur.colorer(gauche + String.valueOf(trait).repeat(LARGEUR_CADRE - 2) + droite, false, COULEUR.BLEU);
    }

    private static String ligneContenu(String contenu) {
        int visible = GraphiqueBougies.longueurVisible(contenu);
        int interieur = LARGEUR_CADRE - 4;
        if (visible < interieur) {
            contenu = contenu + " ".repeat(interieur - visible);
        }
        return Couleur.colorer("│ ", false, COULEUR.BLEU) + contenu + Couleur.colorer(" │", false, COULEUR.BLEU);
    }
}
