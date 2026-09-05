package ui;

public class Couleur {

    /**
     * Affiche le texte tout de suite.
     * @param fond true = couleur sur le fond, false = couleur sur le texte
     */
    public static void println(String texte, boolean fond, COULEUR couleur) {
        println(texte, fond, couleur, 0);
    }

    /**
     * Affiche le texte.
     * @param fond true = couleur sur le fond, false = couleur sur le texte
     * @param tempsMs delai entre chaque lettre (0 = instantane)
     */
    public static void println(String texte, boolean fond, COULEUR couleur, int tempsMs) {
        String debut;
        if (fond) {
            debut = "\u001b[" + couleur.getCodeFond() + ";30m";
            if (couleur == COULEUR.NOIR || couleur == COULEUR.BLEU || couleur == COULEUR.ROUGE
                    || couleur == COULEUR.MAGENTA || couleur == COULEUR.GRIS) {
                debut = "\u001b[" + couleur.getCodeFond() + ";37m";
            }
        } else {
            debut = "\u001b[" + couleur.getCodeTexte() + "m";
        }
        String fin = "\u001b[0m";

        if (tempsMs <= 0) {
            System.out.println(debut + texte + fin);
            return;
        }

        System.out.print(debut);
        for (int i = 0; i < texte.length(); i++) {
            System.out.print(texte.charAt(i));
            System.out.flush();
            try {
                Thread.sleep(tempsMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(fin);
    }
}
