
package bank.internettoegang;

import bank.bankieren.Bank;
import bank.bankieren.IBank;
import bank.bankieren.Money;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BankiersessieTest {
    public static final String HENK = "HENK";
    public static final String TILBURG = "TILBURG";
    public static final String TEST_1 = "TEST1";

    IBalie balie;
    IBank bank;
    IBankiersessie bankiersessie;
    private String rekening;

    @Before
    public void setUp() throws Exception {
        bank = new Bank("TestBank");
        balie = new Balie(bank);
        rekening = balie.openRekening(HENK, TILBURG, TEST_1);

        bankiersessie = balie.logIn(rekening, TEST_1);
    }

    @Test
    public void testIsGeldig() throws Exception {
        assertTrue(bankiersessie.isGeldig());
    }

    @Test
    public void testMaakOver() throws Exception {
        String HENK2 = "HENK2";
        String newRekening = balie.openRekening(HENK2, TILBURG, TEST_1);
        IBankiersessie newSession = balie.logIn(newRekening, TEST_1);

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

//        assertFalse(bankiersessie.isGeldig());
    }
}