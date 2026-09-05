package moteur;

import modele.Entreprise;

import java.util.Random;


public class Bourse {
    public static final String CHEMIN = "data";
    private Entreprise ink;
    private EvenementMarche event;
    Random randomNumbers = new Random();
/*La valeur est un chiffre entre -3 et 3, sachant que -3 est une énorme baisse de l'action et 3 est une forte augmentation de cette dernière et 0 ne change quasiment pas l'action.
*/
    Bourse(Entreprise ink, EvenementMarche event ){
        this.ink = ink;
        this.event = event;
    }

    public boolean inflation(Entreprise ink, int valeur){
        if(valeur < 0){
            ink.setValeurAction(ink.getValeurAction() - ( ink.getValeurAction() * 1*((randomNumbers.nextInt(valeur * valeur )*10) / 100.0)));
            return true;
        } else if (valeur > 0){
            ink.setValeurAction(ink.getValeurAction() + ( ink.getValeurAction() * 1*((randomNumbers.nextInt(valeur * valeur )*10) / 100.0)));
            return true;
        } 
        else {
            Random plusOuMoins = new Random();
            if(plusOuMoins.nextInt(2)+1 == 1){
                ink.setValeurAction(ink.getValeurAction() - (randomNumbers.nextInt(2) + 1));
                return true;
            } else {
                ink.setValeurAction(ink.getValeurAction() + (randomNumbers.nextInt(2) + 1));
                return true;
            }
        }
    }


}
