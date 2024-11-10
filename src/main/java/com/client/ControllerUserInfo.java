package com.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

        // Actualiza el icono cada vez que el estado del ToggleButton cambia
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
