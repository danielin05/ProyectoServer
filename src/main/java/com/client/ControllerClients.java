package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
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
    private Button logInButton;

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
    private void logInUser(ActionEvent event) {
        System.out.println("Se pulsó el botón iniciarSesión");
    }

    @FXML
    private void addUsersButton(ActionEvent event) {
        System.out.println("Se pulsó el botón addUsers");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Parent loadedPane = FXMLLoader.load(getClass().getResource("/assets/layout_currentClients.fxml"));
            GridPane.setColumnIndex(loadedPane, 1);
            GridPane.setRowIndex(loadedPane, 0);
            rootGridPane.getChildren().add(loadedPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Cargó");
    }
}
