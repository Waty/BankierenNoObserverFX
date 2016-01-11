package bank.bankieren;

class Klant implements IKlant {

    private String naam;

    private String plaats;

    public Klant(String naam, String plaats) {
        this.naam = naam;
        this.plaats = plaats;
    }

    public String getNaam() {
        return naam;
    }

    public String getPlaats() {
        return plaats;
    }

    public int compareTo(IKlant klant) {
        int comp = naam.compareTo(klant.getNaam());
        if (comp != 0) return comp;
        return plaats.compareTo(klant.getPlaats());
    }

    public boolean equals(IKlant o) {
        return this.compareTo(o) == 0;
    }

    public String toString() {
        return naam + " te " + "plaats";
    }

}
