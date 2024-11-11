package com.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.Objects.ClientFX;
import com.Objects.Comanda;
import com.Objects.Product;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Date;

public class Main extends Application {

    public static WebSocketClient clienteWebSocket;
    private static Stage stage;

    // Listas para almacenar los datos de clientes y productos
    public static List<ClientFX> clients = new ArrayList<>();
    public static List<ClientFX> currentClients = new ArrayList<>();
    public static List<Product> productsList = new ArrayList<>();
    private static List<Comanda> comands = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Carga la vista inicial desde el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout_connect.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(true);
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

        // Crear la URI del WebSocket
        String uri = choiceConnect + ip + ":" + port;
        
        // Crear el cliente WebSocket
        try {
            clienteWebSocket = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Conexión establecida con el servidor: " + uri);
                    
                    Platform.runLater(() -> {
                        stage.hide();
                        stage.setMaximized(true);
                        UtilsViews.cambiarFrame(stage, "/assets/layout_clients.fxml");
                    });
                }

                @Override
                public void onMessage(String message) {
                    JSONObject obj = new JSONObject(message);
                    if (obj.has("type")) {
                        String type = obj.getString("type");
                        
                        if ("clientsData".equals(type)) {
                            clients.clear();
                            currentClients.clear();

                            System.out.println(message);


                            // Parsear y cargar los clientes en las listas
                            JSONArray clientsArray = obj.getJSONArray("clients");
                            JSONArray currentClientsArray = obj.getJSONArray("currentClients");

                            for (int i = 0; i < clientsArray.length(); i++) {
                                JSONObject clientObj = clientsArray.getJSONObject(i);
                                String id = clientObj.getString("id");
                                String nombre = clientObj.getString("nombre");
                                String contraseña = clientObj.getString("password");
                                
                                String lastAccesStr = clientObj.getString("lastAcces");
                                
                                Date lastAcces = null;
                            
                                // Comprobar si la fecha es "No disponible" o un valor válido
                                if (!"No disponible".equals(lastAccesStr)) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        lastAcces = dateFormat.parse(lastAccesStr);  // Parsear solo si la fecha es válida
                                    } catch (ParseException e) {
                                        e.printStackTrace(); // Manejar la excepción si es necesario
                                    }
                                }
                            
                                clients.add(new ClientFX(nombre, id, contraseña, lastAcces, null));
                            }
                            
                            for (int i = 0; i < currentClientsArray.length(); i++) {
                                JSONObject currentClientObj = currentClientsArray.getJSONObject(i);
                                String id = currentClientObj.getString("id");
                                String nombre = currentClientObj.getString("nombre");
                                String contraseña = currentClientObj.getString("password");
                            
                                String lastAccesStr = currentClientObj.getString("lastAcces");
                                Date lastAcces = null;
                            
                                // Comprobar si la fecha es "No disponible" o un valor válido
                                if (!"No disponible".equals(lastAccesStr)) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        lastAcces = dateFormat.parse(lastAccesStr);  // Parsear solo si la fecha es válida
                                    } catch (ParseException e) {
                                        e.printStackTrace(); // Manejar la excepción si es necesario
                                    }
                                }
                            
                                currentClients.add(new ClientFX(nombre, id, contraseña, lastAcces, null));
                            }        
                                
                            System.out.println("Clients loaded: " + clients.size());
                            System.out.println("Current clients loaded: " + currentClients.size());

                        } else if ("productsList".equals(type)) {
                            productsList.clear();

                            // Parsear y cargar los productos en la lista
                            JSONArray productsArray = obj.getJSONArray("list");

                            for (int i = 0; i < productsArray.length(); i++) {
                                JSONObject productObj = productsArray.getJSONObject(i);
                                String nombre = productObj.getString("nom").trim();
                                String preu = productObj.getString("preu").trim();
                                String description = productObj.getString("descripcio").trim();
                                String imageURL = productObj.getString("imatge").trim();

                                productsList.add(new Product(nombre, preu, description, imageURL));
                            }

                            System.out.println("Products loaded: " + productsList.size());

                        } else if ("comandsData".equals(type)) {
                            
                            System.out.println(message);

                            JSONArray comandsArray = obj.getJSONArray("list");

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
                                    
                                    productsList.add(product);
                                }

                                // Asignar la lista de productos a la comanda
                                comanda.addProducts(productsList);
                                
                                comands.add(comanda);
                            }
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