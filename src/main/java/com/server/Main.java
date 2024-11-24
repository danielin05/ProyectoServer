package com.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
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
import com.Objects.base64Transform;

public class Main extends WebSocketServer {

    private List<WebSocket> webSockets;
    private List<ClientFX> currentClients;
    private List<ClientFX> clients;
    private List<Comanda> comands;

    private static JSONArray productList;
    //private static SSHMySQLConnection dataConnection;

    public Main(InetSocketAddress address) {
        super(address);
        webSockets = new ArrayList<>();
        currentClients = new ArrayList<>();
        clients = new ArrayList<>();
        clients.add(new ClientFX("responsable", "01", "01"));
        comands = new ArrayList<>();

        addCommand();

        productList = loadProducts();

        //dataConnection = new SSHMySQLConnection();

        //dataConnection.connect();
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

                case "command_select":
                    int numTable = obj.getInt("table");

                    for (Comanda comanda : comands) {
                        if (comanda.getNumber() == numTable) {
                            String currentEstado = comanda.getEstado();
                            String nextEstado = getNextEstado(currentEstado);

                            if (nextEstado == null) {
                                System.err.println("No hay un siguiente estado para la comanda " + numTable);
                                break;
                            }

                            // Cambiar el estado de todos los productos al siguiente estado
                            for (CommandProduct commandProduct : comanda.getProducts()) {
                                commandProduct.setEstado(nextEstado);
                                if ("listo".equals(nextEstado)) {
                                    System.out.println("Producto listo: " + commandProduct.getProducte().getNombre());
                                    JSONObject mensaje = new JSONObject();
                                    mensaje.put("type","notifyReady" );
                                    mensaje.put("producto", commandProduct.getProducte().getNombre());
                                    broadcast(mensaje.toString());
                                }
                            }

                            // Cambiar el estado de la comanda al siguiente estado
                            comanda.setEstado(nextEstado);
                            System.out.println(comanda.getEstado());
                            System.out.println("Se cambió el estado de la comanda " + numTable + " a '" + nextEstado + "'");

                            // Enviar actualización a todos los clientes
                            broadcast(loadCommandsData());
                            break;
                        }
                    }
                    break;

                case "product_select":
                    System.out.println(message.toString());

                    // Obtener datos del mensaje
                    int numTableProductDone = obj.getInt("table");
                    String commandProductName = obj.getString("product");

                    boolean comandaFound = false;

                    for (Comanda comanda : comands) {
                        if (comanda.getNumber() == numTableProductDone) {
                            comandaFound = true;

                            boolean productUpdated = false;

                            for (CommandProduct commandProduct : comanda.getProducts()) {
                                if (commandProduct.getProducte().getNombre().equals(commandProductName)) {
                                    String currentProductEstado = commandProduct.getEstado();
                                    String nextProductEstado = getNextEstado(currentProductEstado);

                                    if (nextProductEstado == null) {
                                        System.err.println("El producto '" + commandProductName + "' ya está en el último estado.");
                                        break;
                                    }

                                    // Cambiar el estado del producto al siguiente
                                    commandProduct.setEstado(nextProductEstado);
                                    productUpdated = true;

                                    // Verificar si el estado ahora es "listo"
                                    if ("listo".equals(nextProductEstado)) {
                                        System.out.println("Producto listo: " + commandProductName); 
                                        JSONObject mensaje = new JSONObject();
                                        mensaje.put("type","notifyReady" );
                                        mensaje.put("producto", commandProduct.getProducte().getNombre());
                                        broadcast(mensaje.toString());
                                    }

                                    System.out.println("Se cambió el estado del producto '" + commandProductName + "' a '" + nextProductEstado + "' en la comanda " + numTableProductDone);
                                    break;
                                }
                            }

                            if (!productUpdated) {
                                System.err.println("Producto '" + commandProductName + "' no encontrado o ya estaba en el último estado en la comanda " + numTableProductDone);
                            }

                            // Verificar y actualizar el estado de la comanda
                            updateComandaEstado(comanda);

                            // Enviar actualización a todos los clientes
                            broadcast(loadCommandsData());
                            break;
                        }
                    }

                    if (!comandaFound) {
                        System.err.println("Comanda no encontrada para la mesa " + numTableProductDone);
                    }
                    break;

                case "addCommand":
                    System.out.println("Mensaje recibido: addCommand");

                    // Extraemos la comanda y productos del mensaje
                    JSONObject comandaObj = obj.getJSONObject("comanda");
                    int comandaNumber = comandaObj.getInt("number");

                    // Buscamos si la comanda ya existe
                    Comanda existingComanda = getComandaByNumber(comandaNumber);

                    if (existingComanda != null) {
                        // Si la comanda ya existe, agregamos los productos
                        System.out.println("Comanda ya existe, agregando productos...");
                        JSONArray productsList = comandaObj.getJSONArray("productsList");

                        List<CommandProduct> newProducts = new ArrayList<>();

                        for (int i = 0; i < productsList.length(); i++) {
                            JSONObject productObj = productsList.getJSONObject(i);
                            CommandProduct commandProduct = createCommandProductFromJson(productObj);
                            newProducts.add(commandProduct);
                        }

                        existingComanda.addProducts(newProducts);

                        // Actualizamos el estado de la comanda
                        updateComandaEstado(existingComanda);

                        // Enviar actualización a todos los clientes
                        broadcast(loadCommandsData());
                    } else {
                        // Si la comanda no existe, creamos una nueva
                        System.out.println("Creando nueva comanda...");
                        ClientFX clientFX = getClientById(comandaObj.getJSONObject("clientFX").getString("id"));
                        /*if (clientFX == null) {
                            System.err.println("Cliente no encontrado para la comanda.");
                            break;
                        }*/

                        Comanda newComanda = new Comanda(comandaNumber, comandaObj.getInt("clientsNumber"), clientFX);
                        newComanda.setEstado(comandaObj.getString("estado"));

                        JSONArray productsList = comandaObj.getJSONArray("productsList");

                        List<CommandProduct> newProducts = new ArrayList<>();

                        for (int i = 0; i < productsList.length(); i++) {
                            JSONObject productObj = productsList.getJSONObject(i);
                            CommandProduct commandProduct = createCommandProductFromJson(productObj);
                            newProducts.add(commandProduct);
                        }

                        newComanda.addProducts(newProducts);

                        comands.add(newComanda);

                        //saveComanda(newComanda);

                        // Enviar actualización a todos los clientes
                        broadcast(loadCommandsData());
                    }
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
                    String imageURL = System.getProperty("user.dir") + "\\src\\main\\resources\\productImages\\" + elemento.getElementsByTagName("imatge").item(0).getTextContent();
                    String base64ImageString = base64Transform.convertImageToBase64(imageURL);
                    base64Transform.convertirBase64ToImage(base64ImageString, elemento.getElementsByTagName("imatge").item(0).getTextContent(), "src\\main\\resources\\producImagesDecoded");
                    product.put("imatge", base64ImageString);

                    productList.put(product);
                }
            }

            return productList;

        } catch (Exception e) {
            return null;
        }        
    } 

    private static final List<String> ESTADOS = List.of("demanat", "pendiente", "listo", "pagado");

    private String getNextEstado(String currentEstado) {
        int index = ESTADOS.indexOf(currentEstado);
        if (index == -1 || index == ESTADOS.size() - 1) {
            return null; // No hay siguiente estado
        }
        return ESTADOS.get(index + 1);
    }

    private void updateComandaEstado(Comanda comanda) {
        boolean allPendiente = true;
        boolean allListo = true;
        boolean allPagado = true;
    
        for (CommandProduct product : comanda.getProducts()) {
            String estado = product.getEstado();
            if (!estado.equals("pendiente")) {
                allPendiente = false;
            }
            if (!estado.equals("listo")) {
                allListo = false;
            }
            if (!estado.equals("pagado")) {
                allPagado = false;
            }
        }
    
        if (allPagado) {
            comanda.setEstado("pagado");
        } else if (allListo) {
            comanda.setEstado("listo");
        } else if (allPendiente) {
            comanda.setEstado("pendiente");
        } else {
            comanda.setEstado("demanat");
        }
    
        System.out.println("Estado actualizado de la comanda " + comanda.getNumber() + " a '" + comanda.getEstado() + "'");
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
                clientInfo.put("password", comanda.getClientFX().getPassword());
                clientInfo.put("clientWebsocket", comanda.getClientFX().getClienteWebSocket());
    
                comandObject.put("clientInfo", clientInfo);
                comandObject.put("estado", comanda.getEstado());
    
                comandsJsonArray.put(comandObject);
            }
        }
    
        JSONObject response = new JSONObject();
        response.put("type", "comandsData");
        response.put("list", comandsJsonArray);
        
        return response.toString();  
    }

        // Función para crear un CommandProduct a partir de un objeto JSON
    private CommandProduct createCommandProductFromJson(JSONObject productObj) {
        Product product = new Product(
            productObj.getJSONObject("producte").getString("nombre"),
            productObj.getJSONObject("producte").getString("preu"),
            productObj.getJSONObject("producte").getString("descripcio"),
            productObj.getJSONObject("producte").getString("tags")
        );
        CommandProduct commandProduct = new CommandProduct(product);
        //commandProduct.setComentario(productObj.getString("comentario"));
        commandProduct.setEstado(productObj.getString("estado"));
        return commandProduct;
    }

    // Función para buscar una comanda por número
    private Comanda getComandaByNumber(int number) {
        for (Comanda comanda : comands) {
            if (comanda.getNumber() == number) {
                return comanda;
            }
        }
        return null;
    }
    

    private ClientFX getClientById(String id) {
        for (ClientFX clienteFX : clients) {
            if (clienteFX.getId().trim().equals(id)) {
                return clienteFX;
            }
        }
        return null;
    }

    /*private boolean saveComanda(Comanda comanda) {
        try {
            // Iniciar transacción manualmente
            dataConnection.executeUpdate("START TRANSACTION");

            // Insertar la comanda en la tabla "comanda"
            String insertComandaSQL = String.format(
                "INSERT INTO comanda (id_camarero, num_mesa, clientes, estado, precio_total) " +
                "VALUES (%d, %d, %d, '%s', %.2f)",
                Integer.parseInt(comanda.getClientFX().getId()),  // ID del camarero
                comanda.getNumber(),  // Número de mesa
                comanda.getClientsNumber(),  // Número de clientes
                comanda.getEstado(),  // Estado de la comanda
                calculateTotalPrice(comanda) // Precio total de la comanda
            );

            int comandaId = dataConnection.executeUpdate(insertComandaSQL);
            if (comandaId <= 0) {
                dataConnection.executeUpdate("ROLLBACK");
                throw new SQLException("Fallo al insertar la comanda.");
            }

            // Insertar productos en la tabla "detalle_comanda"
            for (CommandProduct product : comanda.getProducts()) {
                String insertDetalleSQL = String.format(
                    "INSERT INTO detalle_comanda (id_comanda, nombre_producto, descripcion_producto, precio_producto, estado_producto) " +
                    "VALUES (%d, '%s', '%s', %.2f, '%s')",
                    comandaId,  // ID de la comanda
                    product.getProducte().getNombre(),  // Nombre del producto
                    product.getProducte().getDescription(),  // Descripción del producto
                    Double.parseDouble(product.getProducte().getPreu()),  // Precio del producto
                    product.getEstado()  // Estado del producto
                );

                int detalleResult = dataConnection.executeUpdate(insertDetalleSQL);
                if (detalleResult <= 0) {
                    dataConnection.executeUpdate("ROLLBACK");
                    throw new SQLException("Fallo al insertar detalle de la comanda.");
                }
            }

            // Confirmar la transacción
            dataConnection.executeUpdate("COMMIT");
            System.out.println("Comanda guardada exitosamente.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                dataConnection.executeUpdate("ROLLBACK");
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }*/

    /*private double calculateTotalPrice(Comanda comanda) {
        double total = 0.0;
        for (CommandProduct product : comanda.getProducts()) {
            total += Double.parseDouble(product.getProducte().getPreu());
        }
        return total;
    }*/

    public void addCommand() {
        // Crear la primera comanda
        Product pizzaMargherita = new Product("Pizza Margherita", "8.50", "Tomato, mozzarella, and basil", "");
        pizzaMargherita.addTags(List.of("Italian", "Vegetarian", "caliente"));
        
        Product spaghettiCarbonara = new Product("Spaghetti Carbonara", "10.00", "Pasta with eggs, cheese, pancetta", "");
        spaghettiCarbonara.addTags(List.of("Italian", "Pasta", "caliente"));
        
        CommandProduct commandPizza1 = new CommandProduct(pizzaMargherita);
        CommandProduct commandSpaghetti1 = new CommandProduct(spaghettiCarbonara);
        
        Comanda comanda1 = new Comanda(1, 2, new ClientFX("001", "Alice", "001"));
        comanda1.addProducts(List.of(commandPizza1, commandSpaghetti1));
        comands.add(comanda1);
        
        // Crear la segunda comanda
        Product caesarSalad = new Product("Caesar Salad", "5.50", "Lettuce, croutons, Caesar dressing", "");
        caesarSalad.addTags(List.of("Salad", "Vegetarian", "frio"));
        
        Product margheritaPizza = new Product("Margherita Pizza", "9.00", "Tomato, mozzarella, and fresh basil", "");
        margheritaPizza.addTags(List.of("Italian", "Vegetarian", "caliente"));
        
        CommandProduct commandSalad2 = new CommandProduct(caesarSalad);
        CommandProduct commandPizza2 = new CommandProduct(margheritaPizza);
        
        Comanda comanda2 = new Comanda(2, 3, new ClientFX("002", "Bob", "002"));
        comanda2.addProducts(List.of(commandSalad2, commandPizza2));
        comands.add(comanda2);
        
        // Crear la tercera comanda
        Product lasagna = new Product("Lasagna", "12.00", "Layers of pasta with cheese, beef, and tomato sauce", "");
        lasagna.addTags(List.of("Italian", "caliente"));
        
        Product garlicBread = new Product("Garlic Bread", "4.00", "Crispy bread with garlic and butter", "");
        garlicBread.addTags(List.of("Appetizer", "caliente"));
        
        CommandProduct commandLasagna3 = new CommandProduct(lasagna);
        CommandProduct commandGarlicBread3 = new CommandProduct(garlicBread);
        
        Comanda comanda3 = new Comanda(3, 1, new ClientFX("003", "Charlie", "003"));
        comanda3.addProducts(List.of(commandLasagna3, commandGarlicBread3));
        comands.add(comanda3);
        
        // Crear la cuarta comanda
        Product sushiRoll = new Product("Sushi Roll", "15.00", "Sushi with tuna, avocado, and cucumber", "");
        sushiRoll.addTags(List.of("Japanese", "Seafood", "frio"));
        
        Product misoSoup = new Product("Miso Soup", "6.00", "Traditional Japanese soup with tofu and seaweed", "");
        misoSoup.addTags(List.of("Japanese", "Soup", "caliente"));
        
        CommandProduct commandSushi4 = new CommandProduct(sushiRoll);
        CommandProduct commandMisoSoup4 = new CommandProduct(misoSoup);
        
        Comanda comanda4 = new Comanda(4, 4, new ClientFX("004", "David", "004"));
        comanda4.addProducts(List.of(commandSushi4, commandMisoSoup4));
        comands.add(comanda4);
        
        // Crear la quinta comanda
        Product hamburger = new Product("Hamburger", "10.50", "Beef patty, lettuce, tomato, and cheese", "");
        hamburger.addTags(List.of("Fast Food", "Beef", "caliente"));
        
        Product frenchFries = new Product("French Fries", "3.50", "Crispy fried potatoes", "");
        frenchFries.addTags(List.of("Side Dish", "caliente"));
        
        CommandProduct commandBurger5 = new CommandProduct(hamburger);
        CommandProduct commandFries5 = new CommandProduct(frenchFries);
        
        Comanda comanda5 = new Comanda(5, 5, new ClientFX("005", "Eve", "005"));
        comanda5.addProducts(List.of(commandBurger5, commandFries5));
        comands.add(comanda5);
        
        // Imprimir todas las comandas añadidas
        for (Comanda comanda : comands) {
            System.out.println(comanda.toString());
        }
    }    
}
