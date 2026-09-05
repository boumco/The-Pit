package moteur;

import java.util.List;

import modele.Entreprise;

public class EvenementMarche extends Event{
    public int chiffre; // Le chiffre entre -3 et 3 inclus.
    public String nomEntreprise;

    public EvenementMarche(String intitule , int chiffre , String nomEntreprise){
        this.intitule = intitule;
        this.chiffre = chiffre;
        this.nomEntreprise = nomEntreprise;
        

    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    

    public void setInfluence(){
        if(chiffre < 0 ){
            this.influence = "Malus";
        }else if(chiffre == 0){
            this.influence = "Neutre";
        }else{
            this.influence = "Bonus";
        }
    }

    public String toString(){
        return this.nomEntreprise+" :" + this.intitule;
    }


     public Entreprise trouverEntreprise(List<Entreprise> entreprise) throws EntrepriseInexistante {
       
        int indice = 0;
        while(indice < entreprise.size()){
            if(entreprise.get(indice).getNom().equals(this.getNomEntreprise())){
                return entreprise.get(indice);
            }
            indice ++;
        }
        throw new EntrepriseInexistante("L'evenement est associé a aucune entreprise");
    }

    public void inflation(List<Entreprise> entreprise) throws EntrepriseInexistante {
        Entreprise e =   this.trouverEntreprise(entreprise);
        e.inflation(this.chiffre);
    }



    
}
