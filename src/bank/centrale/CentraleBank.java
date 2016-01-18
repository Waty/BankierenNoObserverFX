package bank.centrale;

import bank.bankieren.ISecureBank;
import bank.bankieren.Money;
import fontys.util.NumberDoesntExistException;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class CentraleBank extends UnicastRemoteObject implements ICentraleBank, AutoCloseable {
    final Map<Integer, String> rekeningen = new HashMap<>();
    private final Map<String, ISecureBank> banken = new HashMap<>();

    public CentraleBank() throws RemoteException {
    }

    @Override
    public int getUniqueRekNr(String bankName) throws RemoteException {
        synchronized (rekeningen) {
            for (int i = 0; ; i++) {
                if (!rekeningen.containsKey(i)) {
                    rekeningen.put(i, bankName);
                    return i;
                }
            }
        }
    }

    /**
     * Reigsters the ISecureBank instance in the banken map
     *
     * @param bankName name of the bank
     * @param bank     instance of the RMI proxy
     */
    @Override
    public void registerBank(String bankName, ISecureBank bank) {
        banken.put(bankName, bank);
    }

    ISecureBank getBankForRekening(int rekNr) throws NumberDoesntExistException {
        String bankName = rekeningen.get(rekNr);
        if (bankName == null) throw new NumberDoesntExistException("account " + rekNr + " is unknown");
        return banken.get(bankName);
    }

    @Override
    public boolean maakOver(int source, int destination, Money money) throws RemoteException, NumberDoesntExistException {
        ISecureBank src = getBankForRekening(source);
        ISecureBank dst = getBankForRekening(destination);

        Money negative = Money.difference(new Money(0, money.getCurrency()), money);
        boolean success = src.muteer(source, negative);
        if (!success) return false;

        success = dst.muteer(destination, money);

        if (!success) { // rollback
            src.muteer(source, money);
        }

        return success;
    }

    @Override
    public void close() throws NoSuchObjectException {
        UnicastRemoteObject.unexportObject(this, true);
    }
}
