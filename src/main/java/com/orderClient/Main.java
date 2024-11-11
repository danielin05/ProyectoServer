package com.orderClient;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static WebSocketClient clienteWebSocket;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(1);
        
        // Carga la vista inicial desde el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout_kitchenClient.fxml"));
        System.out.println(1);
        Scene scene = new Scene(root);
        System.out.println(1);

        stage.setScene(scene);
        System.out.println(1);
        stage.setResizable(false);
        System.out.println(1);
        stage.setMaximized(true);
        stage.setTitle("Barretina");
        System.out.println(1);
        stage.getIcons().add(new Image("/images/logo.png"));
        System.out.println(1);
        stage.show();

        // Agrega un icono solo si no es un Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            stage.getIcons().add(new Image("/images/logo.png"));
        }
    }

    @Override
    public void stop() {
        System.exit(1); // Termina todos los servicios
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void establecerConexion(String portString, String ip, String choiceConnect) {

        if (portString.isEmpty() || ip.isEmpty()) {
            System.out.println("Por favor, completa todos los campos.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            System.out.println("El puerto debe ser un número válido.");
            return;
        }

        String uri = choiceConnect + ip + ":" + port;
        
        // Crear el cliente WebSocket
        try {
            clienteWebSocket = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Conexión establecida con el servidor: " + uri);
                }

                @Override
                public void onMessage(String message) {
                    JSONObject obj = new JSONObject(message);
                    if (obj.has("type")) {
                        String type = obj.getString("type");
                        
                        
                    }
                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Conexión cerrada: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error en la conexión: " + ex.getMessage());
                }
            };

            // Intentar conectar al servidor
            clienteWebSocket.connect();
        } catch (URISyntaxException e) {
            System.out.println("URI no válida: " + e.getMessage());
        }
    }
}