package bank.internettoegang;

import bank.bankieren.IBank;
import bank.bankieren.IRekening;
import bank.bankieren.Money;
import fontys.observer.BasicPublisher;
import fontys.util.InvalidSessionException;
import fontys.util.NumberDoesntExistException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;

public class Bankiersessie extends BasicPublisher implements IBankiersessie, Observer {
    private long laatsteAanroep;
    private int reknr;
    private IBank bank;

    public Bankiersessie(int reknr, IBank bank) throws RemoteException {
        super(new String[]{});
        UnicastRemoteObject.exportObject(this, 0);
        laatsteAanroep = System.currentTimeMillis();
        this.reknr = reknr;
        this.bank = bank;
        bank.addObserver(this);
    }

    public boolean isGeldig() {
        return System.currentTimeMillis() - laatsteAanroep < GELDIGHEIDSDUUR;
    }

    @Override
    public boolean maakOver(int bestemming, Money bedrag) throws NumberDoesntExistException, InvalidSessionException, RemoteException {
        updateLaatsteAanroep();

        if (reknr == bestemming) throw new RuntimeException("source and destination must be different");
        if (!bedrag.isPositive()) throw new RuntimeException("amount must be positive");

        return bank.maakOver(reknr, bestemming, bedrag);
    }

    private void updateLaatsteAanroep() throws InvalidSessionException {
        if (!isGeldig()) throw new InvalidSessionException("session has been expired");

        laatsteAanroep = System.currentTimeMillis();
    }

    @Override
    public IRekening getRekening() throws InvalidSessionException, RemoteException {
        updateLaatsteAanroep();

        return bank.getRekening(reknr);
    }

    @Override
    public void logUit() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
        bank.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println(o);
        if (((IRekening) o).getNr() == reknr) {
            Bankiersessie.this.inform(Bankiersessie.this, null, null, o);
        }
    }
}
