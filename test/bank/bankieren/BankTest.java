package bank.bankieren;

import bank.centrale.CentraleBank;
import fontys.util.NumberDoesntExistException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BankTest {
    private final static String NAME = "ING";

    IBank bank;

    @Before
    public void setUp() throws Exception {
        bank = new Bank(new CentraleBank(), NAME);
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
        /**
         * @param nr
         * @return de bankrekening met nummer nr mits bij deze bank bekend, anders null
         */

        int i = bank.openRekening("waty", "tilburg");
        IRekening rekening = bank.getRekening(i);
        assertNotNull(rekening);

        // other (invalid) renkening nr should return null
        for (int j = 1; j < 10000; j++) assertNull(bank.getRekening(i + j));
    }

    @Test
    public void testMaakOver() throws Exception {
        /**
         * er wordt bedrag overgemaakt van de bankrekening met nummer bron naar de
         * bankrekening met nummer bestemming, mits het afschrijven van het bedrag
         * van de rekening met nr bron niet lager wordt dan de kredietlimiet van deze
         * rekening
         *
         * @param bron
         * @param bestemming ongelijk aan bron
         * @param bedrag     is groter dan 0
         * @return <b>true</b> als de overmaking is gelukt, anders <b>false</b>
         * @throws NumberDoesntExistException als een van de twee bankrekeningnummers onbekend is
         */

        // bestemming ongelijk aan bron
        try {
            bank.maakOver(1, 1, new Money(100, Money.EURO));
            Assert.fail();
        } catch (RuntimeException ignored) {
        }

        // @param bedrag is groter dan 0
        try {
            bank.maakOver(1, 2, new Money(-100, Money.EURO));
            Assert.fail();
        } catch (RuntimeException ignored) {
        }


        // @throws NumberDoesntExistException als een van de twee bankrekeningnummers onbekend is
        int rekening1 = bank.openRekening("waty", "tilburg");
        Money money = new Money(100, Money.EURO);
        try {
            bank.maakOver(rekening1, 1337, money);
            Assert.fail();
        } catch (NumberDoesntExistException ignored) {
        }

        try {
            bank.maakOver(1337, rekening1, money);
            Assert.fail();
        } catch (NumberDoesntExistException ignored) {
        }

        int rekening2 = bank.openRekening("waty2", "tilburg2");

        // @return <b>true</b> als de overmaking is gelukt, anders <b>false</b>
        int kred_limit = bank.getRekening(rekening1).getKredietLimietInCenten();
        assertTrue(bank.maakOver(rekening1, rekening2, new Money(kred_limit * -1, Money.EURO)));
        assertFalse(bank.maakOver(rekening1, rekening2, new Money(kred_limit * -1, Money.EURO)));

        assertEquals(kred_limit, bank.getRekening(rekening1).getSaldo().getCents());
        assertEquals(kred_limit * -1, bank.getRekening(rekening2).getSaldo().getCents());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(NAME, bank.getName());
    }
}