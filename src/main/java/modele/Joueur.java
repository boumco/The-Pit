package modele;

public class Joueur extends Personnage {

    private double cash;
    private double dette;
    private Portefeuille<Entreprise> portefeuille;

    public Joueur(String nom, double cash, double dette, Portefeuille<Entreprise> portefeuille) {
        super(nom);
        this.cash = cash;
        this.dette = dette;
        this.portefeuille = portefeuille;
    }

    public boolean acheter(Entreprise e, int quantite) {
        double cout = quantite * e.getValeurAction();
        if (cout > cash) {
            return false;
        }
        cash -= cout;
        portefeuille.ajouter(e, quantite);
        return true;
    }

    public boolean vendre(Entreprise e, int quantite) {
        if (portefeuille.retirer(e, quantite)) {
            cash += quantite * e.getValeurAction();
            return true;
        }
        return false;
    }

    public void rembourserDette(double montant) {
        double montantEffectif = Math.min(montant, Math.min(cash, dette));
        cash -= montantEffectif;
        dette -= montantEffectif;
    }

    public void appliquerInterets(double taux) {
        dette += dette * taux;
    }

    public double getValeurNette() {
        return cash - dette;
    }

    public double getValeurPortefeuille() {
        return portefeuille.getValeurTotale();
    }

    public boolean aGagne() {
        return dette <= 0;
    }

    public double getCash() {
        return cash;
    }

    public double getDette() {
        return dette;
    }
    
    public void setCash(double cash) {
        this.cash = cash;
    }

    public void setDette(double dette) {
        this.dette = dette;
    }

    public void setPortefeuille(Portefeuille<Entreprise> portefeuille) {
        this.portefeuille = portefeuille;
    }

    public Portefeuille getPortefeuille() {
        return portefeuille;
    }

    public void payerLoyer() {
        this.cash -= 15;
        System.out.println("Vous venez de payer 15€ de loyer");
        
    }

}
