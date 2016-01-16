package bank.internettoegang;

import bank.bankieren.Bank;
import bank.centrale.CentraleBank;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BalieTest {

    private IBalie balie;

    @Before
    public void setUp() throws Exception {
        balie = new Balie(new Bank(new CentraleBank(), "ING"));
    }

    @Test
    public void testOpenRekening() throws Exception {
        /**
         * creatie van een nieuwe bankrekening; het gegenereerde bankrekeningnummer is
         * identificerend voor de nieuwe bankrekening en heeft een saldo van 0 euro
         *
         * @param naam       van de eigenaar van de nieuwe bankrekening
         * @param plaats     de woonplaats van de eigenaar van de nieuwe bankrekening
         * @param wachtwoord van het account waarmee er toegang kan worden verkregen
         *                   tot de nieuwe bankrekening
         * @return null zodra naam of plaats een lege string of wachtwoord minder dan
         * vier of meer dan acht karakters lang is en anders de gegenereerde
         * accountnaam(8 karakters lang) waarmee er toegang tot de nieuwe bankrekening
         * kan worden verkregen
         */

        assertNull(balie.openRekening("", "tilburg", "1234567"));
        assertNull(balie.openRekening("waty", "", "1234567"));
        assertNull(balie.openRekening("waty", "tilburg", "123"));
        assertNull(balie.openRekening("waty", "tilburg", "123456789"));
        String loginCode = balie.openRekening("waty", "tilburg", "1234568");
        assertNotNull(loginCode);
        assertEquals(8, loginCode.length());
    }

    @Test
    public void testLogIn() throws Exception {
        /**
         * er wordt een sessie opgestart voor het login-account met de naam
         * accountnaam mits het wachtwoord correct is
         *
         * @param accountnaam
         * @param wachtwoord
         * @return de gegenereerde sessie waarbinnen de gebruiker
         * toegang krijgt tot de bankrekening die hoort bij het betreffende login-
         * account mits accountnaam en wachtwoord matchen, anders null
         */

        assertNull(balie.logIn(null, "pls"));
        assertNull(balie.logIn("waty", null));

        String code = balie.openRekening("waty", "tilburg", "1234567");
        IBankiersessie sessie = balie.logIn(code, "1234567");
        assertNotNull(sessie);
        assertTrue(sessie.isGeldig());
    }
}