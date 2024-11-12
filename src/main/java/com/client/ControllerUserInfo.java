package com.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import com.Objects.WarningPopup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class ControllerUserInfo implements Initializable {

    @FXML
    private Button acceptButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField lastDateField;

    @FXML
    private ToggleButton rememberCheck;

    // Acción para el botón aceptar
    @FXML
    private void acceptButton(ActionEvent event) {
        System.out.println("Se pulsó el botón aceptar");
        if (!passwordField.getText().trim().isEmpty()) {
                
            JSONObject message = new JSONObject();
            message.put("type", "logInClient"); 
            message.put("userID", ControllerCurrentClients.selectedUser.getId());
            message.put("password", passwordField.getText());
            message.put("rememberCheck", rememberCheck.isSelected());
            Main.clienteWebSocket.send(message.toString());

        } else {
            System.out.println("Introduce la contraseña");
            WarningPopup.showWarning("Contraseña inválida", "Debes introducir una contraseña válida");
        }
    }

    // Acción para el botón cancelar
    @FXML
    private void cancelButton(ActionEvent event) {
        System.out.println("Se pulsó el botón cancelar");
        try {

            Parent loadedPane = FXMLLoader.load(getClass().getResource("/assets/layout_currentClients.fxml"));
            GridPane.setColumnIndex(loadedPane, 1);
            GridPane.setRowIndex(loadedPane, 0);
            ControllerClients.instance.getRootGridPane().getChildren().removeLast();
            ControllerClients.instance.getRootGridPane().getChildren().add(loadedPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa el ToggleButton con el estado adecuado
        updateToggleButtonIcon();

        nameField.setText(ControllerCurrentClients.selectedUser.getNombre());
        if (ControllerCurrentClients.selectedUser.getLastAcces() == null) {
            lastDateField.setText("No data found");
        } else {
            lastDateField.setText(ControllerCurrentClients.selectedUser.getLastAcces().toString());
        }

        if (ControllerCurrentClients.selectedUser.getRememberPassword()) {
            passwordField.setText(ControllerCurrentClients.selectedUser.getPassword());
            rememberCheck.setSelected(true);
        }

        rememberCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateToggleButtonIcon();
        });
    }

    private void updateToggleButtonIcon() {
        if (rememberCheck.isSelected()) {
            rememberCheck.setText("✔"); // Check cuando está seleccionado
        }
    }
}
