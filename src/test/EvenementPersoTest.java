package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moteur.EvenementPerso;

public class EvenementPersoTest {

    EvenementPerso evenementBonus;
    EvenementPerso evenementMalusArgent;
    EvenementPerso evenementMalusJour;

    @BeforeAll
    public static void beforeAllTests() {
        System.out.println("Start test series");
    }

    @AfterAll
    public static void afterAllTests() {
        System.out.println("End test series");
    }

    @BeforeEach
    public void beforeATest() {
        evenementBonus = new EvenementPerso("Loterie", 100, 0);
        evenementMalusArgent = new EvenementPerso("Amende", -50, 0);
        evenementMalusJour = new EvenementPerso("Grippe", 0, 2);
    }

    @Test
    public void testSetInfluenceBonus() {
        System.out.println("testSetInfluenceBonus");
        evenementBonus.setInfluence();
        assertEquals("Bonus", evenementBonus.influence);
    }

    @Test
    public void testSetInfluenceMalusArgent() {
        System.out.println("testSetInfluenceMalusArgent");
        evenementMalusArgent.setInfluence();
        assertEquals("Malus", evenementMalusArgent.influence);
    }

    @Test
    public void testSetInfluenceMalusJour() {
        System.out.println("testSetInfluenceMalusJour");
        evenementMalusJour.setInfluence();
        assertEquals("Malus", evenementMalusJour.influence);
    }

    @Test
    public void testIntitule() {
        System.out.println("testIntitule");
        assertEquals("Loterie", evenementBonus.intitule);
    }

}
