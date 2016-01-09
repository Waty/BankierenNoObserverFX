package bank.internettoegang;

import bank.bankieren.IBank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

public class Balie extends UnicastRemoteObject implements IBalie {

    private static final long serialVersionUID = -4194975069137290780L;
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private IBank bank;
    private HashMap<String, ILoginAccount> loginaccounts;
    //private Collection<IBankiersessie> sessions;
    private java.util.Random random;

    public Balie(IBank bank) throws RemoteException {
        this.bank = bank;
        loginaccounts = new HashMap<>();
        //sessions = new HashSet<IBankiersessie>();
        random = new Random();
    }

    public String openRekening(String naam, String plaats, String wachtwoord) {
        if (naam.equals("") || plaats.equals("")) return null;

        if (wachtwoord.length() < 4 || wachtwoord.length() > 8) return null;

        int nr = bank.openRekening(naam, plaats);
        if (nr == -1) return null;

        String accountname = generateId(8);
        while (loginaccounts.containsKey(accountname))
            accountname = generateId(8);
        loginaccounts.put(accountname, new LoginAccount(accountname,
                wachtwoord, nr));

        return accountname;
    }

    public IBankiersessie logIn(String accountnaam, String wachtwoord) throws RemoteException {
        ILoginAccount account = loginaccounts.get(accountnaam);
        if (account != null && account.checkWachtwoord(wachtwoord)) return new Bankiersessie(account.getReknr(), bank);
        return null;
    }

    private String generateId(int x) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < x; i++) {
            int rand = random.nextInt(36);
            s.append(CHARS.charAt(rand));
        }
        return s.toString();
    }


}
