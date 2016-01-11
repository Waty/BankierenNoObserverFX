package bank.internettoegang;

import bank.bankieren.Bank;
import bank.bankieren.IBank;
import bank.bankieren.Money;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BankiersessieTest {
    public static final String HENK = "HENK";
    public static final String TILBURG = "TILBURG";
    public static final String TEST = "TEST";

    IBalie balie;
    IBank bank;
    IBankiersessie bankiersessie;
    private String rekening;

    @Before
    public void setUp() throws Exception {
        bank = new Bank("TestBank");
        balie = new Balie(bank);
        rekening = balie.openRekening(HENK, TILBURG, TEST);
        bankiersessie = balie.logIn(rekening, TEST);
    }

    @Test
    public void testIsGeldig() throws Exception {
        assertTrue(bankiersessie.isGeldig());
    }

    @Test
    public void testMaakOver() throws Exception {
        String henk = "HENK";
        String newRekening = balie.openRekening(henk, TILBURG, TEST);
        IBankiersessie newSession = balie.logIn(newRekening, TEST);
        assertTrue(bankiersessie.maakOver(newSession.getRekening().getNr(), new Money(1, Money.EURO)));
    }

    @Test
    public void testGetRekening() throws Exception {
        assertNotNull(bankiersessie.getRekening());
    }

    @Test
    public void testLogUit() throws Exception {
        assertTrue(bankiersessie.isGeldig());
        bankiersessie.logUit();
        //assertFalse(bankiersessie.isGeldig());
    }
}