package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moteur.EvenementMarche;

public class EvenementMarcheTest {

    EvenementMarche evenementMalus;
    EvenementMarche evenementNeutre;
    EvenementMarche evenementBonus;

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
        evenementMalus = new EvenementMarche("Crise petroliere", -2, "Total");
        evenementNeutre = new EvenementMarche("Stable", 0, "Apple");
        evenementBonus = new EvenementMarche("Bonne nouvelle", 3, "Orange");
    }

    @Test
    public void testSetInfluenceMalus() {
        System.out.println("testSetInfluenceMalus");
        evenementMalus.setInfluence();
        assertEquals("Malus", evenementMalus.influence);
    }

    @Test
    public void testSetInfluenceNeutre() {
        System.out.println("testSetInfluenceNeutre");
        evenementNeutre.setInfluence();
        assertEquals("Neutre", evenementNeutre.influence);
    }

    @Test
    public void testSetInfluenceBonus() {
        System.out.println("testSetInfluenceBonus");
        evenementBonus.setInfluence();
        assertEquals("Bonus", evenementBonus.influence);
    }

    @Test
    public void testGetNomEntreprise() {
        System.out.println("testGetNomEntreprise");
        assertEquals("Total", evenementMalus.getNomEntreprise());
    }

    @Test
    public void testToString() {
        System.out.println("testToString");
        assertEquals("Total :Crise petroliere", evenementMalus.toString());
    }

}
