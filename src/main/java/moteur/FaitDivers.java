package moteur;

public class FaitDivers {
    private final String titre;
    private final String extrait;
    private final String rubrique;

    public FaitDivers(String titre, String extrait, String rubrique) {
        this.titre = titre;
        this.extrait = extrait;
        this.rubrique = rubrique;
    }

    public String getTitre() {
        return titre;
    }

    public String getExtrait() {
        return extrait;
    }

    public String getRubrique() {
        return rubrique;
    }
}
