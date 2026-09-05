package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import moteur.Event;
import moteur.EvenementMarche;
import moteur.EvenementPerso;

public class EventTest {

    @BeforeAll
    public static void beforeAllTests() {
        System.out.println("Start test series");
    }

    @AfterAll
    public static void afterAllTests() {
        System.out.println("End test series");
    }

    @Test
    public void testPolymorphismeEvenementMarche() {
        System.out.println("testPolymorphismeEvenementMarche");
        Event event = new EvenementMarche("Hausse", 2, "Total");
        EvenementMarche marche = (EvenementMarche) event;
        marche.setInfluence();
        assertEquals("Bonus", event.influence);
    }

    @Test
    public void testPolymorphismeEvenementPerso() {
        System.out.println("testPolymorphismeEvenementPerso");
        Event event = new EvenementPerso("Heritage", 500, 0);
        EvenementPerso perso = (EvenementPerso) event;
        perso.setInfluence();
        assertEquals("Bonus", event.influence);
    }

    @Test
    public void testPolymorphismeMalusMarche() {
        System.out.println("testPolymorphismeMalusMarche");
        Event event = new EvenementMarche("Baisse", -1, "Apple");
        EvenementMarche marche = (EvenementMarche) event;
        marche.setInfluence();
        assertEquals("Malus", event.influence);
    }

    @Test
    public void testPolymorphismeMalusPerso() {
        System.out.println("testPolymorphismeMalusPerso");
        Event event = new EvenementPerso("Accident", -100, 1);
        EvenementPerso perso = (EvenementPerso) event;
        perso.setInfluence();
        assertEquals("Malus", event.influence);
    }

}
