package modele;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Portefeuille<T extends Actif> {

    private final Map<T, Integer> parts;

    public Portefeuille(List<T> listEntreprises) {
        this.parts = new HashMap<>();
        for (T e : listEntreprises) {
            parts.put(e, 0);
        }
    }

    public void ajouter(T e, int quantite) {
        parts.put(e, parts.get(e) + quantite);
    }

    public boolean retirer(T e, int quantite) {
        if (parts.get(e) - quantite >= 0) {
            parts.put(e, parts.get(e) - quantite);
            return true;
        }
        return false;
    }

    public int getQuantite(T e) {
        return parts.get(e);
    }


    public double getValeurTotale() {
        double total = 0.0;
        for (Map.Entry<T, Integer> entry : parts.entrySet()) {
            total += entry.getKey().getValeurAction() * entry.getValue();
        }
        return total;
    }

    public Map<T, Integer> getParts() {
        return parts;
    }
}