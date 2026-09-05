package ui;
 
import modele.Joueur;
 
public class InterfaceJoueur {
 
    public static String genererMenuJoueur(int jour, Joueur joueur) {
        return genererMenuJoueur(jour, joueur, 0);
    }

    public static String genererMenuJoueur(int jour, Joueur joueur, int selection) {
        String bourse = colorerSiSelection("1 . Bourse", selection == 0);
        String journal = colorerSiSelection("2 . Journal", selection == 1);
        String dormir = colorerSiSelection("3 . Allez Dormir", selection == 2);
        String quitter = colorerSiSelection("4 . Quitter", selection == 3);

        StringBuilder sb = new StringBuilder();
 
        sb.append("         ____________________________________________________________  \n");
        sb.append("        /_____/_____/_____/_____/_____/_____/_____/_____/_____/_____/  \n");
        sb.append("       ._. ___________           Jour : ").append(jour).append("/15\n");
        sb.append("       | | | /(...   |    NOM : ").append(joueur.getNom()).append("\n");
        sb.append("       |_| |/ ,-(_`; |                                              \n");
        sb.append("       |-| |\\ )_  _; |  SOLDE : ").append(String.format("%.2f", joueur.getCash())).append(" €\n");
        sb.append("       | | |(\\[_][_])|                                              \n");
        sb.append("       |_| | |  L  | |  DETTE : ").append(String.format("%.2f", joueur.getDette())).append(" €\n");
        sb.append("       ._. | | \\-_/  |                                              \n");
        sb.append("       | | |_________|                                              \n");
        sb.append("       |_|                                                         \n");
        sb.append("       |-|                                                          \n");
        sb.append("       | |                                                          \n");
        sb.append("       |_|   ").append(bourse).append("                        ").append(journal).append("\n");
        sb.append("       ._.                                                          \n");
        sb.append("       | |                                                          \n");
        sb.append("       |_|   ").append(dormir).append("                  ").append(quitter).append("\n");
        sb.append("       |-|                                                          \n");
        sb.append("       | |  ______________________________________________________ \n");
        sb.append("       |_| /_____/_____/_____/_____/_____/_____/_____/_____/_____/ \n");
 
        return sb.toString();
    }

    private static String colorerSiSelection(String texte, boolean selectionne) {
        if (!selectionne) {
            return texte;
        }
        return "\u001b[47;30m" + texte + "\u001b[0m";
    }
}