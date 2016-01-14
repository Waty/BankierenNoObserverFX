package bank.bankieren;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISecureBank extends Remote {
    boolean muteer(int nr, Money money) throws RemoteException;
}
