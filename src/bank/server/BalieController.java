package bank.server;

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
        cbSelectBank1.valueProperty().addListener((ov, t, t1) -> {
                    bankNaam = ov.getValue();
                    if (application.startBalie(bankNaam)) {
                        taMessage.setText(bankNaam + " bank is online");
                    } else {
                        taMessage.setText("Connection Failed");
                    }
                }
        );
    }


    @FXML
    private void selectBank(ActionEvent event) {
    }
}
   
