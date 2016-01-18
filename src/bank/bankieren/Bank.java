package bank.bankieren;

import bank.centrale.ICentraleBank;
import fontys.observer.BasicPublisher;
import fontys.util.NumberDoesntExistException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Bank extends BasicPublisher implements IBank, ISecureBank, AutoCloseable {

    private final Map<Integer, IRekeningTbvBank> accounts = new HashMap<>();
    private final Collection<IKlant> clients = new ArrayList<>();
    private final String name;
    private final ICentraleBank centraleBank;

    public Bank(ICentraleBank centraleBank, String name) throws RemoteException, MalformedURLException, NotBoundException {
        super(new String[]{});
        UnicastRemoteObject.exportObject(this, 0);

        this.centraleBank = centraleBank;
        this.name = name;

        centraleBank.registerBank(name, this);
    }

    public int openRekening(String name, String city) throws RemoteException {
        if (name.equals("") || city.equals("")) return -1;

        int rekeningNr = centraleBank.getUniqueRekNr(this.name);
        IRekeningTbvBank account = new Rekening(rekeningNr, getKlant(name, city), Money.EURO);
        accounts.put(rekeningNr, account);
        addProperty(rekeningNr + "");
        return rekeningNr;
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
        IRekeningTbvBank dest_account = (IRekeningTbvBank) getRekening(dst);

        if (source_account != null && dest_account != null) {
            return internalTransfer(money, source_account, dest_account);
        }

        try {
            return centraleBank.maakOver(src, dst, money);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean internalTransfer(Money money, IRekeningTbvBank source_account, IRekeningTbvBank dest_account) {
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

    @Override
    public boolean muteer(int nr, Money money) throws RemoteException {
        IRekeningTbvBank rekening = (IRekeningTbvBank) getRekening(nr);
        boolean succes = rekening.muteer(money);
        if (succes) inform(this, nr + "", null, rekening);
        return succes;
    }

    @Override
    public void close() throws Exception {
        UnicastRemoteObject.unexportObject(this, true);
    }
}
