package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerClients implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button addUsersButton;

    @FXML
    private GridPane rootGridPane;

    @FXML
    private void backButton(ActionEvent event) {
        System.out.println("Se pulsó el back button");
    }

    @FXML
    private void usersButton(ActionEvent event) {
        System.out.println("Se pulsó el botón users");
    }

    @FXML
    private void addUsersButton(ActionEvent event) {
        System.out.println("Se pulsó el botón addUsers");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Cargó");
    }
}
