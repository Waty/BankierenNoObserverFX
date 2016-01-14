package bank.centrale;

import bank.bankieren.ISecureBank;
import bank.bankieren.Money;
import fontys.util.NumberDoesntExistException;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CentraleBank extends UnicastRemoteObject implements ICentraleBank, AutoCloseable {
    final Map<Integer, ISecureBank> data = new HashMap<>();

    protected CentraleBank() throws RemoteException {
        LocateRegistry.createRegistry(12345).rebind("centralbank", this);
    }

    public static void main(String[] arg) {
        try (CentraleBank ignored = new CentraleBank()) {
            System.out.println("CB has started");
            new Scanner(System.in).next();
            System.out.println("CB is closing");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getUniqueRekNr(ISecureBank bank) throws RemoteException {
        synchronized (data) {
            for (int i = 0; ; i++) {
                if (!data.containsKey(i)) {
                    data.put(i, bank);
                    return i;
                }
            }
        }
    }

    @Override
    public boolean maakOver(int source, int destination, Money money) throws RemoteException, NumberDoesntExistException {
        ISecureBank src = data.get(source);
        if (src == null) throw new NumberDoesntExistException("account " + source + " is unknown");
        ISecureBank dst = data.get(destination);
        if (dst == null) throw new NumberDoesntExistException("account " + destination + " is unknown");

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
