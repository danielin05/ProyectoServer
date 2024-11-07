package com.client;

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

    public static Stage stageFX;

    public static WebSocketClient clienteWebSocket;

    @Override
    public void start(Stage stage) throws Exception {

        stageFX = stage;
        // Carrega la vista inicial des del fitxer FXML
        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout_connect.fxml"));
        Scene scene = new Scene(root);

        stageFX.setScene(scene);
        stageFX.setResizable(true);
        stageFX.setTitle("Barretina");
        stageFX.getIcons().add(new Image("/images/logo.png"));
        stageFX.show();

        // Afegeix una icona només si no és un Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            stageFX.getIcons().add(new Image("/images/logo.png"));
        }
    }

    @Override
    public void stop() {
        System.exit(1); // Kill all executor services
    }

    public static Stage getStage() {
        return stageFX;
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

        // Crear la URI del WebSocket
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
                        if ("clientList".equals(type)) {

                            System.out.println(message);

                        }   
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
