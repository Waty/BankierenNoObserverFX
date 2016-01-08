package bank.bankieren;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class BankTest {
    private final static String NAME = "ING";

    IBank bank;

    @Before
    public void setUp() throws Exception {
        bank = new Bank(NAME);
    }

    @Test
    public void testOpenRekening() throws Exception {
        /**
         * creatie van een nieuwe bankrekening met een identificerend rekeningnummer;
         * alleen als de klant, geidentificeerd door naam en plaats, nog niet bestaat
         * wordt er ook een nieuwe klant aangemaakt
         *
         * @param naam   van de eigenaar van de nieuwe bankrekening
         * @param plaats de woonplaats van de eigenaar van de nieuwe bankrekening
         * @return -1 zodra naam of plaats een lege string en anders het nummer van de
         * gecreeerde bankrekening
         */

        assertEquals(-1, bank.openRekening("", "tilburg"));
        assertEquals(-1, bank.openRekening("waty", ""));

        assertNotEquals(-1, bank.openRekening("waty", "tilburg"));
    }

    @Test
    public void testGetRekening() throws Exception {

    }

    @Test
    public void testMaakOver() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(NAME, bank.getName());
    }
}