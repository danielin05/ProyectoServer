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

    private static JSONArray productList;

    public Main(InetSocketAddress address) {
        super(address);
        webSockets = new ArrayList<>();
        currentClients = new ArrayList<>();
        clients = new ArrayList<>();

        productList = loadProducts();

        currentClients.add(new ClientFX("Admin", "1", "1", null,null));
        currentClients.add(new ClientFX("Responsable", "2", "2", null, null));
        currentClients.add(new ClientFX("Cliente", "3", "3", null, null));

        clients.add(new ClientFX("Admin", "1", "1", null, null));
        clients.add(new ClientFX("Responsable", "2", "2", null, null));
        clients.add(new ClientFX("Cliente", "3", "3", null, null));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("WebSocket client connected: " + conn);

        //Añadir conexión a la lista de conexiones
        webSockets.add(conn);

        conn.send(loadClientsData());
        sendProductsList(conn);
        
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

                    break;

                case "addClient":
                    // Implementación para manejar el mensaje "addClient"
                    System.out.println("Mensaje recibido: addClient");
                    //handleAddClient(clientId, obj);
                    break;

                case "logInClient":
                    // Implementación para manejar el mensaje "logInClient"
                    System.out.println("Mensaje recibido: logInClient");
                    //handleLogInClient(clientId, obj);
                    break;

                case "exitClient":
                    // Implementación para manejar el mensaje "exitClient"
                    System.out.println("Mensaje recibido: exitClient");
                    //handleExitClient(clientId);
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
    
            // Verificar si lastAcces es null antes de llamar a toString
            if (client.getLastAcces() != null) {
                clientObj.put("lastAcces", client.getLastAcces().toString());
            } else {
                clientObj.put("lastAcces", "No disponible");  // O cualquier otro valor representativo
            }
    
            currentClientsList.put(clientObj);
        }
    
        // JSON para la lista de clients
        JSONArray clientsList = new JSONArray();
        for (ClientFX client : clients) {
            JSONObject clientObj = new JSONObject();
            clientObj.put("nombre", client.getNombre());
            clientObj.put("id", client.getId());
            clientObj.put("password", client.getPassword());
    
            // Verificar si lastAcces es null antes de llamar a toString
            if (client.getLastAcces() != null) {
                clientObj.put("lastAcces", client.getLastAcces().toString());
            } else {
                clientObj.put("lastAcces", "No disponible");  // O cualquier otro valor representativo
            }
    
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
                    product.put("imatge", elemento.getElementsByTagName("imatge").item(0).getTextContent());

                    productList.put(product);
                }
            }

            return productList;

        } catch (Exception e) {
            return null;
        }        
    }   
}
