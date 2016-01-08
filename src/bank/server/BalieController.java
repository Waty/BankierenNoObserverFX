/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bank.server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author frankcoenen
 */
public class BalieController implements Initializable {

    @FXML
    private ComboBox<String> cbSelectBank1;

    @FXML
    private TextArea taMessage;

    private BalieServer application;
    private String bankNaam;

    public void setApp(BalieServer application) {
        this.application = application;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cbSelectBank1.getItems().addAll(FXCollections.observableArrayList("RaboBank", "ING", "SNS", "ABN AMRO", "ASN"));

        cbSelectBank1.valueProperty().addListener(new ChangeListener() {
                                                      @Override
                                                      public void changed(ObservableValue ov, Object t, Object t1) {
                                                          bankNaam = (String) ov.getValue();
                                                          if (application.startBalie(bankNaam)) {
                                                              taMessage.setText(bankNaam + " bank is online");
                                                          } else {
                                                              taMessage.setText("Connection Failed");
                                                          }
                                                      }
                                                  }
        );
    }


    @FXML
    private void selectBank(ActionEvent event) {
    }
}
   
