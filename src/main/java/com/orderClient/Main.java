package com.orderClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.Objects.ClientFX;
import com.Objects.Comanda;
import com.Objects.CommandProduct;
import com.Objects.Product;
import com.Objects.UtilsViews;

public class Main extends Application {

    public static WebSocketClient clienteWebSocket;
    public static Map<String,List<Comanda>> comandsByTag; 
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        comandsByTag = new HashMap<>();

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

                            System.out.println(message);
                    
                            JSONArray comandsArray = obj.getJSONArray("list");
                            System.out.println(comandsArray.toString());
                            List<Comanda> comands = new ArrayList<>();

                            for (int i = 0; i < comandsArray.length(); i++) {
                                JSONObject comandObject = comandsArray.getJSONObject(i);
                    
                                int number = comandObject.getInt("number");
                                int clientsNumber = comandObject.getInt("clientsNumber");

                                JSONObject clientInfo = comandObject.getJSONObject("clientInfo");
                                
                                ClientFX clientFX = new ClientFX(
                                    clientInfo.getString("nombre"),  // nombre del cliente
                                    clientInfo.getString("clientId"),  // clientId
                                    clientInfo.getString("password")  // password
                                );
                                
                                Comanda comanda = new Comanda(number, clientsNumber, clientFX); 
                    
                                JSONArray productsArray = comandObject.getJSONArray("productsList");
                                List<CommandProduct> productsList = new ArrayList<>();
                    
                                for (int j = 0; j < productsArray.length(); j++) {
                                    JSONObject productObject = productsArray.getJSONObject(j);
                    
                                    Product product = new Product(
                                        productObject.getString("nombre"), 
                                        productObject.getString("preu"),
                                        productObject.getString("descripcio"),
                                        productObject.getString("imatge")
                                    );

                                    System.out.println(product.getImageBase64());

                                    product.setDescription(productObject.optString("description", ""));
                    
                                    List<String> tags = new ArrayList<>();
                                    JSONArray tagsArray = productObject.optJSONArray("tags");
                                    if (tagsArray != null) {
                                        for (int e = 0; e < tagsArray.length(); e++) {
                                            tags.add(tagsArray.getString(e));
                                        }
                                    }
                                    product.addTags(tags);

                                    String estado = productObject.optString("estado", "pendiente");
                    
                                    CommandProduct commandProduct = new CommandProduct(product);
                                    commandProduct.setEstado(estado); 
                                    commandProduct.setComentario(productObject.optString("comentario", ""));
                                    
                                    productsList.add(commandProduct);
                                }
                    
                                comanda.addProducts(productsList);
                    
                                comands.add(comanda);

                            }

                            comandsByTag.put("general", comands);
                            comandsByTag.put("caliente", orderCommandsByTag(comands, "caliente"));
                            comandsByTag.put("frio", orderCommandsByTag(comands, "frio"));
                            comandsByTag.put("postre", orderCommandsByTag(comands, "postre"));

                            System.out.println(comandsByTag);
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

    private static List<Comanda> orderCommandsByTag(List<Comanda> comands, String tag) {
        List<Comanda> commandsByTag = new ArrayList<>();
        if (!comands.isEmpty()) {
            for (Comanda comanda : comands) {  
                Comanda commandByTag = new Comanda(comanda.getNumber(), comanda.getClientsNumber(), comanda.getClientFX());
                List<CommandProduct> newProductsByTag = new ArrayList<>();
                for (CommandProduct commandProduct : comanda.getProducts()) {
                    if (commandProduct.getProducte().getTags().contains(tag)) {
                        newProductsByTag.add(commandProduct);
                    }
                }
                if (!newProductsByTag.isEmpty()) {
                    commandByTag.addProducts(newProductsByTag);
                    commandsByTag.add(commandByTag);
                }
            }
            if (!commandsByTag.isEmpty()) {
                return commandsByTag;
            } 
            return null;
        }
        return null;
    }
}