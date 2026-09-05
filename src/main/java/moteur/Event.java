package moteur;

public abstract class Event {
    public String intitule;
    public  String influence; //L'influence est soit "Bonus" ou soit "Malus" ou "Neutre"
    


    abstract void setInfluence();

    

    
}
