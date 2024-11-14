package com.server;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.Objects.ClientFX;
import com.Objects.Comanda;
import com.Objects.CommandProduct;
import com.Objects.Product;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main extends WebSocketServer {

    private List<WebSocket> webSockets;
    private List<ClientFX> currentClients;
    private List<ClientFX> clients;
    private List<Comanda> comands;

    private static JSONArray productList;

    public Main(InetSocketAddress address) {
        super(address);
        webSockets = new ArrayList<>();
        currentClients = new ArrayList<>();
        clients = new ArrayList<>();
        comands = new ArrayList<>();

        productList = loadProducts();

        ClientFX client1 = new ClientFX("Alice", "001", "password123", null);
        ClientFX client2 = new ClientFX("Bob", "002", "password456", null);

        Product product1 = new Product("Pizza Margherita", "8.50", "Tomato, mozzarella, and basil", "imageURL1");
        product1.addTags(List.of("Italian", "Vegetarian"));
        Product product2 = new Product("Spaghetti Carbonara", "10.00", "Pasta with eggs, cheese, pancetta", "imageURL2");
        product2.addTags(List.of("Italian", "Pasta"));
        Product product3 = new Product("Caesar Salad", "5.50", "Lettuce, croutons, Caesar dressing", "imageURL3");
        product3.addTags(List.of("Salad", "Vegetarian"));

        CommandProduct commandProduct1 = new CommandProduct(product1);
        commandProduct1.setEstado("listo");
        commandProduct1.setComentario("Extra cheese");

        CommandProduct commandProduct2 = new CommandProduct(product2);
        commandProduct2.setEstado("pendiente");
        commandProduct2.setComentario("No bacon");

        CommandProduct commandProduct3 = new CommandProduct(product3);
        commandProduct3.setEstado("pagado");
        commandProduct3.setComentario("Add grilled chicken");

        Comanda comanda1 = new Comanda(1, 2, client1);
        comanda1.addProducts(List.of(commandProduct1, commandProduct2, commandProduct1, commandProduct1, commandProduct2, commandProduct3));

        Comanda comanda2 = new Comanda(2, 1, client2);
        comanda2.addProducts(List.of(commandProduct3, commandProduct3, commandProduct3, commandProduct1));

        comands.add(comanda1);
        comands.add(comanda2);

        clients.add(new ClientFX("Admin", "1", "1", null));
        clients.add(new ClientFX("Responsable", "2", "2", null));
        clients.add(new ClientFX("Cliente", "3", "3", null));

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("WebSocket client connected: " + conn);

        //Añadir conexión a la lista de conexiones
        webSockets.add(conn);

        conn.send(loadClientsData());
        sendProductsList(conn);
        conn.send(loadCommandsData());
        
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Buscar y eliminar al cliente que se desconectó
        Iterator<WebSocket> iterator = webSockets.iterator();
        while (iterator.hasNext()) {
            WebSocket webSocket = iterator.next();
            if (webSocket.equals(conn)) {
                System.out.println("WebSocket client disconnected: " + webSocket);
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject obj = new JSONObject(message); // Convertir el mensaje en un JSON

        // Comprobar si el mensaje tiene un tipo definido
        if (obj.has("type")) {
            String type = obj.getString("type"); // Obtener el tipo de mensaje

            switch (type) {
                case "reload":
                    // Implementación para manejar el mensaje "reload"
                    System.out.println("Mensaje recibido: reload");

                    conn.send(loadClientsData());
                    sendProductsList(conn);
                    conn.send(loadCommandsData());

                    break;

                case "enterClient":
                    System.out.println("Mensaje recibido: enterClient");
                    
                    ClientFX clienteEnter = getClientById(obj.getString("userID"));
                    String passwordEnter = obj.getString("password");

                    if (clienteEnter == null) {
                        break;
                    }

                    if (!clienteEnter.getPassword().equals(passwordEnter)) {
                        break;
                    }

                    if (currentClients.contains(clienteEnter)) {
                        break;
                    }

                    clienteEnter.setClienteWebSocket(conn);

                    currentClients.add(clienteEnter);

                    broadcast(loadClientsData());

                    break;

                case "exitClient":
                    // Implementación para manejar el mensaje "exitClient"
                    System.out.println("Mensaje recibido: exitClient");
                    
                    ClientFX clienteExit = getClientById(obj.getString("userID"));
                    String passwordExit = obj.getString("password");

                    if (clienteExit == null) {
                        break;
                    }

                    if (!clienteExit.getPassword().equals(passwordExit)) {
                        break;
                    }

                    if (!currentClients.contains(clienteExit)) {
                        break;
                    }

                    currentClients.remove(clienteExit);

                    broadcast(loadClientsData());

                    break;

                default:
                    System.out.println("Mensaje no reconocido: " + type);
                    break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started on port: " + getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public static void main(String[] args) {
        Main server = new Main(new InetSocketAddress(3000));
        server.start();
        LineReader reader = LineReaderBuilder.builder().build();
        System.out.println("Server running. Type 'exit' to gracefully stop it.");

        try {
            while (true) {
                String line = reader.readLine("> ");
                if ("exit".equalsIgnoreCase(line.trim())) {
                    System.out.println("Stopping server...");
                    server.stop(1000);
                    break;
                } else if ("update".equalsIgnoreCase(line.trim())){
                    productList = loadProducts(); 
                } else {
                    System.out.println("Unknown command. Type 'exit' to stop server gracefully.");
                }
            }
        } catch (UserInterruptException | EndOfFileException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped.");
        }
    }

    private String loadClientsData() {
        // JSON para la lista de currentClients
        JSONArray currentClientsList = new JSONArray();
        for (ClientFX client : currentClients) {
            JSONObject clientObj = new JSONObject();
            clientObj.put("nombre", client.getNombre());
            clientObj.put("id", client.getId());
            clientObj.put("password", client.getPassword());
    
            currentClientsList.put(clientObj);
        }
    
        // JSON para la lista de clients
        JSONArray clientsList = new JSONArray();
        for (ClientFX client : clients) {
            JSONObject clientObj = new JSONObject();
            clientObj.put("nombre", client.getNombre());
            clientObj.put("id", client.getId());
            clientObj.put("password", client.getPassword());
    
            clientsList.put(clientObj);
        }
    
        // Armar el mensaje para enviar ambas listas
        JSONObject response = new JSONObject();
        response.put("type", "clientsData");
        response.put("currentClients", currentClientsList);
        response.put("clients", clientsList);
    
        System.out.println(response.toString());
    
        return response.toString();
    }    

    @Override
    public void broadcast(String text) {
        super.broadcast(text);
    }

    private void sendProductsList(WebSocket conn) {
        JSONObject response = new JSONObject();
        response.put("type", "productsList");
        response.put("list", productList);
    
        try {
            conn.send(response.toString());
        } catch (WebsocketNotConnectedException e) {
            System.out.println("Cliente no conectado: " + conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray loadProducts() {
        try {

            String userDir = System.getProperty("user.dir");
            File archivoXML = new File(userDir + "/src/main/resources/data/products.xml");
            System.out.println(archivoXML.toPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivoXML);
            doc.getDocumentElement().normalize();

            JSONArray productList = new JSONArray();

            System.out.println("Contenido del archivo XML:");

            NodeList listaProductos = doc.getElementsByTagName("producto");
            for (int i = 0; i < listaProductos.getLength(); i++) {
                Node nodo = listaProductos.item(i);
                
                JSONObject product = new JSONObject();
                
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    product.put("id", elemento.getAttribute("id"));
                    product.put("tags", elemento.getAttribute("tags"));
                    product.put("nom", elemento.getElementsByTagName("nom").item(0).getTextContent());
                    product.put("preu", elemento.getElementsByTagName("preu").item(0).getTextContent());
                    product.put("descripcio", elemento.getElementsByTagName("descripcio").item(0).getTextContent());
                    System.out.println(System.getProperty("user.dir") + elemento.getElementsByTagName("imatge").item(0).getTextContent());
                    product.put("imatge", elemento.getElementsByTagName("imatge").item(0).getTextContent());

                    productList.put(product);
                }
            }

            return productList;

        } catch (Exception e) {
            return null;
        }        
    } 
    
    private String loadCommandsData() {
        JSONArray comandsJsonArray = new JSONArray();
        
        if (comands != null) {
            for (Comanda comanda : comands) {
                JSONObject comandObject = new JSONObject();
                comandObject.put("number", comanda.getNumber());
                comandObject.put("clientsNumber", comanda.getClientsNumber());
    
                JSONArray productsList = new JSONArray();
                if (comanda.getProducts() != null) {
                    for (CommandProduct commandProduct : comanda.getProducts()) {
                        JSONObject productObject = new JSONObject();
                        productObject.put("nombre", commandProduct.getProducte().getNombre());
                        productObject.put("preu", commandProduct.getProducte().getPreu());
                        
                        // Verificación para evitar valores nulos
                        String description = commandProduct.getProducte().getDescription();
                        productObject.put("description", description != null ? description : "");
    
                        JSONArray productTags = new JSONArray();
                        List<String> tags = commandProduct.getProducte().getTags();
                        if (tags != null) {
                            for (String tag : tags) {
                                productTags.put(tag);
                            }
                        }
                        productObject.put("tags", productTags);
                        productObject.put("estado", commandProduct.getEstado());
    
                        // Verificación para comentario nulo
                        String comentario = commandProduct.getComentario();
                        productObject.put("comentario", comentario != null ? comentario : "");
    
                        productsList.put(productObject);
                    }
                }
    
                comandObject.put("productsList", productsList);
    
                JSONObject clientInfo = new JSONObject();
                clientInfo.put("nombre", comanda.getClientFX().getNombre());
                clientInfo.put("clientId", comanda.getClientFX().getId());
    
                comandObject.put("clientInfo", clientInfo);
                comandObject.put("estado", comanda.getEstado());
    
                comandsJsonArray.put(comandObject);
            }
        }
    
        JSONObject response = new JSONObject();
        response.put("type", "comandsData");
        response.put("list", comandsJsonArray);
    
        System.out.println(response.toString());
        
        return response.toString();  
    }
    

    private ClientFX getClientById(String id) {
        for (ClientFX clienteFX : clients) {
            if (clienteFX.getId().trim().equals(id)) {
                return clienteFX;
            }
        }
        return null;
    }
}
