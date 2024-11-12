package com.orderClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.Objects.Comanda;
import com.Objects.Product;
import com.client.UtilsViews;

public class Main extends Application {

    private static WebSocketClient clienteWebSocket;
    private static List<Comanda> comands;
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        comands = new ArrayList<>();

        Main.stage = stage;
        
        // Carga la vista inicial desde el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout_connect_Order.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Barretina");
        stage.getIcons().add(new Image("/images/logo.png"));
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

        System.out.println(uri);
        
        // Crear el cliente WebSocket
        try {
            clienteWebSocket = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Conexión establecida con el servidor: " + uri);

                    Platform.runLater(() -> {
                        stage.hide();
                        stage.setMaximized(true);
                        UtilsViews.cambiarFrame(stage, "/assets/layout_kitchenClient.fxml");
                    });
                }

                @Override
                public void onMessage(String message) {
                    JSONObject obj = new JSONObject(message);
                    if (obj.has("type")) {
                        
                        String type = obj.getString("type");

                        if ("comandsData".equals(type)) {

                            JSONArray comandsArray = obj.getJSONArray("list");

                            System.out.println(comandsArray.toString());

                            for (int i = 0; i < comandsArray.length(); i++) {
                                JSONObject comandObject = comandsArray.getJSONObject(i);
                                
                                Comanda comanda = new Comanda(comandObject.getInt("number"), comandObject.getInt("clientsNumber"));

                                // Obtener la lista de productos de cada comanda
                                JSONArray productsArray = comandObject.getJSONArray("productsList");
                                List<Product> productsList = new ArrayList<>();

                                for (int j = 0; j < productsArray.length(); j++) {
                                    JSONObject productObject = productsArray.getJSONObject(j);
                                
                                    // Crear nueva instancia de Product
                                    Product product = new Product(productObject.getString("nombre"), productObject.getString("preu"));
                                
                                    List<String> tags = new ArrayList<>();
                                    JSONArray tagsArray = productObject.getJSONArray("tags");
                                
                                    for (int e = 0; e < tagsArray.length(); e++) {
                                        tags.add(tagsArray.getString(e));
                                    }
                                
                                    product.addTags(tags);
                                    productsList.add(product);
                                }                                

                                // Asignar la lista de productos a la comanda
                                comanda.addProducts(productsList);
                                
                                comands.add(comanda);
                            }

                            printearComandas();
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

    private static void printearComandas() {
        for (Comanda comanda : comands) {
            System.out.println(comanda);
        }
    }
}