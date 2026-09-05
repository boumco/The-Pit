package modele;

public class Bougie {
    private final double ouverture;
    private final double haut;
    private final double bas;
    private final double cloture;

    public Bougie(double ouverture, double haut, double bas, double cloture) {
        this.ouverture = ouverture;
        this.haut = haut;
        this.bas = bas;
        this.cloture = cloture;
    }

    public double getOuverture() {
        return ouverture;
    }

    public double getHaut() {
        return haut;
    }

    public double getBas() {
        return bas;
    }

    public double getCloture() {
        return cloture;
    }

    public boolean estHausse() {
        return cloture >= ouverture;
    }
}
