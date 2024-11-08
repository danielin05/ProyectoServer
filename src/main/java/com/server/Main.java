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

        currentClients.add(new ClientFX("Admin", 1, 1, null));
        currentClients.add(new ClientFX("Responsable", 2, 2, null));
        currentClients.add(new ClientFX("Cliente", 3, 3, null));

        clients.add(new ClientFX("Admin", 1, 1, null));
        clients.add(new ClientFX("Responsable", 2, 2, null));
        clients.add(new ClientFX("Cliente", 3, 3, null));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("WebSocket client connected: " + conn);

        //Añadir conexión a la lista de conexiones
        webSockets.add(conn);

        sendClientsList();
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
        sendClientsList();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject obj = new JSONObject(message);
        WebSocket clientId = null;
        for (ClientFX cliente : clients) {
            if (cliente.getClienteWebSocket() == conn) {
                clientId = cliente.getClienteWebSocket();
                break;
            }
        }

        if (obj.has("type")) {
            String type = obj.getString("type");
            switch (type) {
                case "hola":
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

    private void sendClientsList() {
        JSONArray clientList = new JSONArray();
        for (ClientFX client : currentClients) {
            JSONObject clientObj = new JSONObject();
            clientObj.put("nombre", client.getNombre());
            clientObj.put("id", client.getId());
            clientList.put(clientObj);
        }

        JSONObject response = new JSONObject();
        response.put("type", "clientList");
        response.put("list", clientList);

        for (WebSocket webSocket : webSockets) {
            try {
                webSocket.send(response.toString());
            } catch (WebsocketNotConnectedException e) {
                System.out.println("Cliente no conectado: " + webSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static JSONArray loadProducts() { //Cargar productos
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

            System.out.println(productList);

            return productList;

        } catch (Exception e) {
            return null;
        }        
    }   
}
