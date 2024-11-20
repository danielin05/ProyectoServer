package com.objects;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar.ButtonData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginPopup extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Ejemplo de uso
        Map<String, String> credentials = showLoginPopup("Entrada", "Introduce tus credenciales de inicio de sesión");
        
        // Validación y uso de credenciales
        if (credentials != null) {
            String userId = credentials.get("userID");
            String password = credentials.get("password");
            System.out.println("ID de usuario: " + userId);
            System.out.println("Contraseña: " + password);
            // Agrega la lógica de autenticación o lo que necesites hacer con las credenciales
        }
    }

    /**
     * Método para mostrar un cuadro de diálogo para ingresar ID de usuario y contraseña.
     *
     * @param title   Título de la ventana.
     * @param message Mensaje o instrucción detallada.
     * @return Un `Map` con el ID de usuario y contraseña, o `null` si se cancela.
     */
    public static Map<String, String> showLoginPopup(String title, String message) {
        // Crear una alerta de tipo personalizado
        Alert loginAlert = new Alert(Alert.AlertType.NONE);
        loginAlert.setTitle(title);
        loginAlert.setHeaderText(message);

        // Crear el GridPane para colocar los campos de ID y Contraseña
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Campos de entrada para ID de usuario y contraseña
        TextField idField = new TextField();
        idField.setPromptText("ID de usuario");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        Text idText = new Text();
        Text passwordText = new Text();

        idText.setText("Id campo: ");
        passwordText.setText("Password campo: ");

        grid.add(idText, 1, 0);
        grid.add(passwordText, 1, 1);

        grid.add(idField, 2, 0);
        grid.add(passwordField, 2, 1);

        // Estilo
        DialogPane dialogPane = loginAlert.getDialogPane();
        dialogPane.setContent(grid);

        // Botones de "Aceptar" y "Cancelar"
        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        loginAlert.getButtonTypes().addAll(loginButtonType, cancelButtonType);

        // Mostrar la alerta y capturar la respuesta
        Optional<ButtonType> result = loginAlert.showAndWait();

        // Verificar si se presionó "Aceptar"
        if (result.isPresent() && result.get() == loginButtonType) {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("userID", idField.getText());
            credentials.put("password", passwordField.getText());
            return credentials;  // Retornar credenciales ingresadas
        } else {
            return null;  // Usuario presionó "Cancelar"
        }
    }
}
