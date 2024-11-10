package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerConnect implements Initializable {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button acceptButton;

    @FXML
    private ComboBox<String> choiceConnect;

    @FXML
    private void acceptButtonAction(ActionEvent event) {
        System.out.println("Se pulsó el botón aceptar");

        String portText = portField.getText().trim();
        String ip = ipField.getText().trim();
        String connectType = null;
        if (choiceConnect.getValue().equals("local")) {
            connectType = "ws://";
        } else if (choiceConnect.getValue().equals("proxmox")) {
            connectType = "wss://";
        }

        Main.establecerConexion(portText, ip, connectType);
    }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        System.out.println("Se pulsó el botón cancelar");
        // Cierra la ventana o regresa a la pantalla anterior
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ipField.setText("localhost");
        portField.setText("3000");
        choiceConnect.getItems().addAll("local", "proxmox");
        choiceConnect.setValue("local");
    }
}