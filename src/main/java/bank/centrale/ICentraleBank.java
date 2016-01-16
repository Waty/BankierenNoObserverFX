package bank.centrale;

import bank.bankieren.ISecureBank;
import bank.bankieren.Money;
import fontys.util.NumberDoesntExistException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICentraleBank extends Remote {
    int getUniqueRekNr(ISecureBank bank) throws RemoteException;

    boolean maakOver(int source, int destination, Money amount) throws RemoteException, NumberDoesntExistException;
}
