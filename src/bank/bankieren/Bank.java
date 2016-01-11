package bank.bankieren;

import fontys.observer.BasicPublisher;
import fontys.util.NumberDoesntExistException;

import java.util.*;

public class Bank extends BasicPublisher implements IBank {

    private final Map<Integer, IRekeningTbvBank> accounts;
    private final Collection<IKlant> clients;
    private final String name;
    private int nieuwReknr;

    public Bank(String name) {
        super(new String[]{});
        accounts = new HashMap<>();
        clients = new ArrayList<>();
        nieuwReknr = 100000000;
        this.name = name;
    }

    public int openRekening(String name, String city) {
        if (name.equals("") || city.equals("")) return -1;

        IKlant klant = getKlant(name, city);
        synchronized (accounts) {
            IRekeningTbvBank account = new Rekening(nieuwReknr, klant, Money.EURO);
            accounts.put(nieuwReknr, account);
            addProperty(nieuwReknr + "");
            nieuwReknr++;
            return nieuwReknr - 1;
        }
    }

    private IKlant getKlant(String name, String city) {
        Optional<IKlant> any = clients.stream().filter(k -> k.getNaam().equals(name) && k.getPlaats().equals(city)).findAny();
        if (any.isPresent()) return any.get();

        IKlant klant = new Klant(name, city);
        clients.add(klant);
        return klant;
    }

    public IRekening getRekening(int nr) {
        return accounts.get(nr);
    }

    public boolean maakOver(int src, int dst, Money money) throws NumberDoesntExistException {
        if (src == dst) throw new RuntimeException("cannot transfer money to your own account");
        if (!money.isPositive()) throw new RuntimeException("money must be positive");

        IRekeningTbvBank source_account = (IRekeningTbvBank) getRekening(src);
        if (source_account == null) throw new NumberDoesntExistException("account " + src + " unknown at " + name);

        IRekeningTbvBank dest_account = (IRekeningTbvBank) getRekening(dst);
        if (dest_account == null) throw new NumberDoesntExistException("account " + dst + " unknown at " + name);

        synchronized (accounts) {
            Money negative = Money.difference(new Money(0, money.getCurrency()), money);
            boolean success = source_account.muteer(negative);
            if (!success) return false;

            success = dest_account.muteer(money);

            if (success) {
                inform(this, dest_account.getNr() + "", null, dest_account);
                inform(this, source_account.getNr() + "", null, source_account);
            } else { // rollback
                source_account.muteer(money);
            }

            return success;
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
