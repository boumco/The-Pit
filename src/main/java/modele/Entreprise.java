package modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Entreprise implements Actif{
    
    private  String nom;
    private double valeurAction;
    private double ancienneValeur;
    private TypeEntreprise type;
    private String description;
    private final List<Bougie> historique = new ArrayList<>();

    public Entreprise(String nom, double valeurAction, TypeEntreprise type, String description){
        this.nom = nom;
        this.valeurAction = valeurAction;
        this.ancienneValeur = valeurAction;
        this.type = type; 
        this.description = description;
        genererHistoriqueInitial();
   }

    public Entreprise(String nom, double valeurAction, TypeEntreprise type){
        this(nom, valeurAction, type, "");
    }

    public double getValeurAction() {
        return valeurAction;
    }

    public void setValeurAction(double valeurAction) {
        enregistrerBougie(this.valeurAction, valeurAction);
        this.ancienneValeur = this.valeurAction;
        this.valeurAction = valeurAction;
    }

    public double getAncienneValeur() {
        return ancienneValeur;
    }

    public List<Bougie> getHistorique() {
        return Collections.unmodifiableList(historique);
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

        double nouvelle = this.valeurAction * (1.0 + (pourcentage / 100.0));
        if (nouvelle < 0.5) {
            nouvelle = 0.5;
        }
        enregistrerBougie(this.valeurAction, nouvelle);
        this.ancienneValeur = this.valeurAction;
        this.valeurAction = nouvelle;
    }

    private void genererHistoriqueInitial() {
        Random r = new Random(nom.hashCode());
        double prix = Math.max(1.0, valeurAction * 0.72);
        for (int i = 0; i < 17; i++) {
            double ouverture = prix;
            double variation = (r.nextDouble() - 0.42) * prix * 0.07;
            double cloture = Math.max(0.8, ouverture + variation);
            ajouterBougie(ouverture, cloture, r);
            prix = cloture;
        }
        ajouterBougie(prix, valeurAction, r);
        ancienneValeur = prix;
    }

    private void enregistrerBougie(double ouverture, double cloture) {
        if (ouverture <= 0 || cloture <= 0 || ouverture == cloture && historique.isEmpty()) {
            return;
        }
        ajouterBougie(ouverture, cloture, new Random());
        if (historique.size() > 24) {
            historique.remove(0);
        }
    }

    private void ajouterBougie(double ouverture, double cloture, Random r) {
        double haut = Math.max(ouverture, cloture) * (1.0 + r.nextDouble() * 0.025);
        double bas = Math.min(ouverture, cloture) * (1.0 - r.nextDouble() * 0.025);
        if (bas < 0.3) {
            bas = 0.3;
        }
        historique.add(new Bougie(ouverture, haut, bas, cloture));
    }


}
