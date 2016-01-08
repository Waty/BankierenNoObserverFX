package bank.bankieren;

import java.io.Serializable;

public interface IRekening extends Serializable {
  int getNr();
  Money getSaldo();
  IKlant getEigenaar();
  int getKredietLimietInCenten();
}

