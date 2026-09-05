package modele;

import java.util.Objects;
import java.util.Random;

import modele.Actif;

public class Entreprise implements Actif{
    
    private  String nom;
    private double valeurAction;
    private TypeEntreprise type;
    private String description;

    public Entreprise(String nom, double valeurAction, TypeEntreprise type, String description){
        this.nom = nom;
        this.valeurAction = valeurAction;
        this.type = type; 
        this.description = description;
   }

    public double getValeurAction() {
        return valeurAction;
    }

    public void setValeurAction(double valeurAction) {
        this.valeurAction = valeurAction;
    }

    public String getNom() {
        return nom;
    }

    public TypeEntreprise getType() {
        return type;
    }

    
    

    public String getDescription() {
        return description;
    }

    public String toString(){
        String retour = "L'entreprise " + this.nom + " est une entreprise dans le secteur " + this.type.toString().toLowerCase() + " qui a une valeur de " + this.valeurAction + " euros.";
        return retour;
    }

    public boolean equals(Object o){
        if (o == null){
            return false;
        }
        if(o == this ){
            return true;
        }
       
        if(!(o instanceof Entreprise)){
            return false;
        }
        Entreprise e = (Entreprise) o;

        if(this.getNom().equals(e.getNom())){
            if(this.getValeurAction() == e.getValeurAction()){
                if(this.getType().equals(e.getType())){
                    if(this.getDescription().equals(e.getDescription())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(nom);
    }

     public void inflation(int chiffre){
        Random r = new Random();
        int pourcentage = 0;
        
        if(chiffre == -3){
             pourcentage = r.nextInt(-25, -14); 
        } else if(chiffre == -2){
             pourcentage = r.nextInt(-15, -9);
        } else if(chiffre == -1){
            pourcentage = r.nextInt(-10, -4);
        } else if(chiffre == 0){
             pourcentage = r.nextInt(-5, 10);
        } else if(chiffre == 1){
              pourcentage = r.nextInt(10, 20);
        } else if(chiffre == 2){
              pourcentage = r.nextInt(20, 35);
        } else if(chiffre == 3){
              pourcentage = r.nextInt(35, 60);
        }

        this.valeurAction = this.valeurAction * (1.0 + (pourcentage / 100.0));
    }


}
