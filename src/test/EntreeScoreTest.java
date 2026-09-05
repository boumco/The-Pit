package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import score.EntreeScore;

public class EntreeScoreTest {

    EntreeScore scoreVictoire;
    EntreeScore scoreDefaite;

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
        scoreVictoire = new EntreeScore("Alice", 500.0, 0.0, true);
        scoreDefaite = new EntreeScore("Bob", 50.0, 200.0, false);
    }

    @Test
    public void testGetNom() {
        System.out.println("testGetNom");
        assertEquals("Alice", scoreVictoire.getNom());
    }

    @Test
    public void testGetCashFinal() {
        System.out.println("testGetCashFinal");
        assertEquals(500.0, scoreVictoire.getCashFinal());
    }

    @Test
    public void testGetDetteRestante() {
        System.out.println("testGetDetteRestante");
        assertEquals(200.0, scoreDefaite.getDetteRestante());
    }

    @Test
    public void testIsVictoire() {
        System.out.println("testIsVictoire");
        assertTrue(scoreVictoire.isVictoire());
        assertFalse(scoreDefaite.isVictoire());
    }

    @Test
    public void testToLigneCsv() {
        System.out.println("testToLigneCsv");
        assertEquals("Alice,500.0,0.0,true", scoreVictoire.toLigneCsv());
        assertEquals("Bob,50.0,200.0,false", scoreDefaite.toLigneCsv());
    }

}
