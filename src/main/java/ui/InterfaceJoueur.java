package ui;
 
import modele.Joueur;
 
public class InterfaceJoueur {
 
    public static String genererMenuJoueur(int jour, Joueur joueur) {
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
        sb.append("       |_|   1 . Bourse                        2 . Journal          \n");
        sb.append("       ._.                                                          \n");
        sb.append("       | |                                                          \n");
        sb.append("       |_|   3 . Allez Dormir                  4 . Quitter          \n");
        sb.append("       |-|                                                          \n");
        sb.append("       | |  ______________________________________________________ \n");
        sb.append("       |_| /_____/_____/_____/_____/_____/_____/_____/_____/_____/ \n");
 
        return sb.toString();
    }
}