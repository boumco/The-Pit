package score;

public class EntreeScore {
    private final String nom;
    private final double cashFinal;
    private final double detteRestante;
    private final boolean victoire;

    public EntreeScore(String nom, double cashFinal, double detteRestante, boolean victoire) {
        this.nom = nom;
        this.cashFinal = cashFinal;
        this.detteRestante = detteRestante;
        this.victoire = victoire;
    }

    public String getNom() {
        return nom;
    }

    public double getCashFinal() {
        return cashFinal;
    }

    public double getDetteRestante() {
        return detteRestante;
    }

    public boolean isVictoire() {
        return victoire;
    }

    public String toLigneCsv() {
        return nom + "," + cashFinal + "," + detteRestante + "," + victoire;
    }
}