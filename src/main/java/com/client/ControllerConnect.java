package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import org.java_websocket.client.WebSocketClient;
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

    public static WebSocketClient clienteWebSocket;

    public static ControllerConnect instance;

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
        instance = this;
        ipField.setText("barretina3.ieti.site");
        portField.setText("443");
        choiceConnect.getItems().addAll("local", "proxmox");
        choiceConnect.setValue("local");
    }

    public void sendMessage(String message) {
        if (clienteWebSocket != null && clienteWebSocket.isOpen()) {
            clienteWebSocket.send(message);
        } else {
            System.out.println("No se puede enviar el mensaje. Conexión no está abierta.");
        }
    }
}