package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.TypeEntreprise;

public class EntrepriseTest {
    Entreprise e1;
    Entreprise e2;
    Entreprise e3;

    @BeforeAll
	public static void beforeAllTests() {System.out.println("Start test series");}
	@AfterAll
	public static void afterAllTests() {System.out.println("End test series");}

    @BeforeEach
	public void beforeATest() {
		e1 = new Entreprise("Total", 75.03, TypeEntreprise.PETROLIER);
		e2 = new Entreprise("Apple", 5, TypeEntreprise.TECHNOLOGIQUE);
		e3 = new Entreprise("Total", 75.03, TypeEntreprise.PETROLIER);
		
	}


    @Test
	public void testEquals() {
		System.out.println("testEquals");
		assertTrue(e1.equals(e2));
		assertTrue(e1.equals(e3));
		assertFalse(e2.equals(e3));
		assertTrue(e3.equals(e1));


    
}

}
