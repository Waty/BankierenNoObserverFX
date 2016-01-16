/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.server;

import bank.bankieren.Bank;
import bank.centrale.ICentraleBank;
import bank.gui.BankierClient;
import bank.internettoegang.Balie;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author frankcoenen
 */
public class BalieServer extends Application {

    private final double MINIMUM_WINDOW_WIDTH = 600.0;
    private final double MINIMUM_WINDOW_HEIGHT = 200.0;
    private Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        try {
            stage = primaryStage;
            stage.setTitle("Bankieren");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            gotoBankSelect();

            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean startBalie(String nameBank) {
        int port = 1099;

        try (FileOutputStream out = new FileOutputStream(nameBank + ".props")) {
            Properties props = new Properties();
            props.setProperty("balie", String.format("%s:%d/%s", InetAddress.getLocalHost().getHostAddress(), port, nameBank));
            props.store(out, null);

            ICentraleBank centraleBank = (ICentraleBank) Naming.lookup("rmi://localhost:12345/centralbank");
            initOrGetRegistry(port).rebind(nameBank, new Balie(new Bank(centraleBank, nameBank)));
            return true;

        } catch (IOException | NotBoundException ex) {
            Logger.getLogger(BalieServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Registry initOrGetRegistry(int port) throws RemoteException {
        try {
            return LocateRegistry.createRegistry(port);
        } catch (ExportException ignored) {
            return LocateRegistry.getRegistry(port);
        }
    }

    public void gotoBankSelect() {
        try {
            BalieController bankSelect = (BalieController) replaceSceneContent("Balie.fxml");
            bankSelect.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(BankierClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(BalieServer.class.getResource(fxml));
        AnchorPane page;
        try (InputStream in = BalieServer.class.getResourceAsStream(fxml)) {
            page = loader.load(in);
        }
        Scene scene = new Scene(page, 800, 600);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
}
