package bank.centrale;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CentraleBankServer {
    public static void main(String[] arg) {
        try (CentraleBank centraleBank = new CentraleBank()) {

            System.out.println("CB has started");
            Registry registry = LocateRegistry.createRegistry(12345);
            registry.rebind("centralbank", centraleBank);
            System.out.println("CB is bound in the registry");
            new Scanner(System.in).next();
            System.out.println("CB is closing");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
