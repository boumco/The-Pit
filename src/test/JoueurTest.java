package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.Entreprise;
import modele.Joueur;
import modele.Portefeuille;
import modele.TypeEntreprise;

public class JoueurTest {

    Joueur joueur;
    Entreprise entreprise;

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
        ArrayList<Entreprise> liste = new ArrayList<>();
        entreprise = new Entreprise("Total", 10.0, TypeEntreprise.PETROLIER, "Entreprise petroliere");
        liste.add(entreprise);
        Portefeuille portefeuille = new Portefeuille(liste);
        joueur = new Joueur("Alice", 100.0, 50.0, portefeuille);
    }

    @Test
    public void testAcheterAvecCashSuffisant() {
        System.out.println("testAcheterAvecCashSuffisant");
        joueur.acheter(entreprise, 5);
        assertEquals(50.0, joueur.getCash());
        assertEquals(50.0, joueur.getDette());
        assertEquals(5, joueur.getPortefeuille().getQuantite(entreprise));
    }

    @Test
    public void testAcheterAvecCashInsuffisant() {
        System.out.println("testAcheterAvecCashInsuffisant");
        joueur.acheter(entreprise, 15);
        assertEquals(0.0, joueur.getCash());
        assertEquals(100.0, joueur.getDette());
        assertEquals(15, joueur.getPortefeuille().getQuantite(entreprise));
    }

    @Test
    public void testVendreReussit() {
        System.out.println("testVendreReussit");
        joueur.acheter(entreprise, 4);
        boolean resultat = joueur.vendre(entreprise, 2);
        assertTrue(resultat);
        assertEquals(80.0, joueur.getCash());
        assertEquals(2, joueur.getPortefeuille().getQuantite(entreprise));
    }

    @Test
    public void testVendreEchoue() {
        System.out.println("testVendreEchoue");
        boolean resultat = joueur.vendre(entreprise, 1);
        assertFalse(resultat);
        assertEquals(100.0, joueur.getCash());
        assertEquals(0, joueur.getPortefeuille().getQuantite(entreprise));
    }

    @Test
    public void testRembourserDette() {
        System.out.println("testRembourserDette");
        joueur.rembourserDette(30.0);
        assertEquals(70.0, joueur.getCash());
        assertEquals(20.0, joueur.getDette());
    }

    @Test
    public void testAppliquerInterets() {
        System.out.println("testAppliquerInterets");
        joueur.appliquerInterets(0.10);
        assertEquals(55.0, joueur.getDette());
    }

    @Test
    public void testAGagne() {
        System.out.println("testAGagne");
        assertFalse(joueur.aGagne());
        joueur.rembourserDette(50.0);
        assertTrue(joueur.aGagne());
    }

    @Test
    public void testGetValeurNette() {
        System.out.println("testGetValeurNette");
        assertEquals(50.0, joueur.getValeurNette());
    }

}
