package modele;

public class Personnage {

    protected String nom;

    public Personnage(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public String sePresenter() {
        return "Je m'appelle " + nom + ".";
    }

    @Override
    public String toString() {
        return nom;
    }

}
