package bank.centrale;

import bank.bankieren.Bank;
import bank.bankieren.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CentraleBankTest {

    private CentraleBank centraleBank;
    private Bank ing;
    private Bank rabo;

    @Before
    public void setUp() throws Exception {
        centraleBank = new CentraleBank();
        ing = new Bank(centraleBank, "ING");
        rabo = new Bank(centraleBank, "Rabo");
    }

    @After
    public void tearDown() throws Exception {
        rabo.close();
        ing.close();
        centraleBank.close();
    }

    @Test
    public void testGetUniqueRekNr() throws Exception {
        int r1 = centraleBank.getUniqueRekNr(rabo);
        int r2 = centraleBank.getUniqueRekNr(ing);
        assertNotEquals(r1, r2);
    }

    @Test
    public void testMaakOver() throws Exception {
        int r1 = ing.openRekening("Waty", "Tilburg");
        int r2 = rabo.openRekening("Sjoerd", "Tilburg");
        assertNotEquals(r1, r2);

        boolean result = centraleBank.maakOver(r1, r2, new Money(100_00, Money.EURO));
        assertTrue(result);

        assertEquals(-100_00, ing.getRekening(r1).getSaldo().getCents());
        assertEquals(100_00, rabo.getRekening(r2).getSaldo().getCents());
    }
}