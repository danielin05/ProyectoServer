package com.Objects;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class WarningPopup extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Este es solo un ejemplo de uso, puedes llamar a esta función desde donde necesites
        showWarning("Error de entrada", "La entrada proporcionada no es válida. Por favor, intenta nuevamente.");
    }

    /**
     * Método para mostrar una ventana de advertencia al usuario.
     *
     * @param title Título de la ventana.
     * @param message Mensaje detallado de advertencia.
     */
    public static void showWarning(String title, String message) {
        // Crear una alerta de tipo advertencia
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText("Advertencia");
        alert.setContentText(message);

        // Mostrar la alerta y esperar a que el usuario cierre
        alert.showAndWait();
    }
}
