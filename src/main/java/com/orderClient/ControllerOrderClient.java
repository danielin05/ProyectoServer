package com.orderClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.objects.Comanda;
import com.objects.CommandProduct;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ControllerOrderClient {

    @FXML
    private Canvas calienteCanvas;

    @FXML
    private Canvas frioCanvas;

    @FXML
    private Canvas postresCanvas;

    @FXML
    private Canvas generalCanvas;

    @FXML
    private Button apagarButton;

    @FXML
    private Button configButton;

    @FXML
    private Button deshacerButton;

    @FXML
    private Button delanteButton;

    @FXML
    private Button atrasButton;

    private Map<Canvas, List<CommandArea>> commandAreasByCanvas = new HashMap<>();

    @FXML
    private void apagarButton(ActionEvent event) {
        System.out.println("Se pulsó el botón apagar");
        Main.stage.close();
    }

    @FXML
    private void configButton(ActionEvent event) {
        System.out.println("Se pulsó el botón configuración");
    }

    @FXML
    private void deshacerButton(ActionEvent event) {
        System.out.println("Se pulsó el botón deshacer");
    }

    @FXML
    private void delanteButton(ActionEvent event) {
        System.out.println("Se pulsó el botón delante");
    }

    @FXML
    private void atrasButton(ActionEvent event) {
        System.out.println("Se pulsó el botón atrás");
    }

    @FXML
    public void initialize() {
        startDrawingAnimation();

        // Detecta clics en los canvas
        calienteCanvas.setOnMouseClicked(event -> handleCanvasClick(event.getX(), event.getY(), calienteCanvas));
        frioCanvas.setOnMouseClicked(event -> handleCanvasClick(event.getX(), event.getY(), frioCanvas));
        postresCanvas.setOnMouseClicked(event -> handleCanvasClick(event.getX(), event.getY(), postresCanvas));
        generalCanvas.setOnMouseClicked(event -> handleCanvasClick(event.getX(), event.getY(), generalCanvas));
    }

    private void startDrawingAnimation() {
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            // Realizamos el dibujo aquí con un retraso controlado
            drawOnCanvasWithDelay();
        }
    };
    timer.start();
    }

    private void drawOnCanvasWithDelay() {
        // Realizamos una actualización solo después de un pequeño retraso
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            try {
                drawOnCanvas(calienteCanvas, Main.comandsByTag.get("caliente"));
                drawOnCanvas(frioCanvas, Main.comandsByTag.get("frio"));
                drawOnCanvas(postresCanvas, Main.comandsByTag.get("postre"));
                drawOnCanvas(generalCanvas, Main.comandsByTag.get("general"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        
        // Este timeline se repetirá cada 100ms
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();  // Inicia el timeline con el intervalo
    }

    private void drawOnCanvas(Canvas canvas, List<Comanda> comandas) {
        if (comandas == null || comandas.isEmpty()) {
            return;
        }
    
        GraphicsContext gc = canvas.getGraphicsContext2D();
    
        // Limpiamos todo el canvas, ya que las posiciones pueden cambiar
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    
        List<CommandArea> commandAreas = new ArrayList<>();
        commandAreasByCanvas.put(canvas, commandAreas);
    
        double rectWidth = canvas.getWidth() / 5 - 25; // 5 columnas
        double padding = 25;
    
        double x = padding;
        double y = padding;
    
        // Ahora dibujamos cada comanda, asegurándonos de recalcular las posiciones
        for (int i = 0; i < comandas.size(); i++) {
            Comanda comanda = comandas.get(i);
    
            if (comanda.getEstado().equals("pagado")) {
                continue;
            }
    
            // Verifica el estado de la comanda
            String estadoComanda = determinarEstadoComanda(comanda);
            comanda.setEstado(estadoComanda);
    
            Map<String, Map<String, Integer>> productGroups = agruparProductosPorEstado(comanda);
    
            // Calcula tamaño del rectángulo de comanda
            double rectHeight = 50 + productGroups.size() * 45;
    
            // Asegúrate de que no se solapen las comandas, actualizando las posiciones
            if (y + rectHeight > canvas.getHeight()) {
                x += rectWidth + padding;  // Mover a la siguiente columna
                y = padding;  // Volver a la parte superior
                if (x + rectWidth > canvas.getWidth()) {
                    System.out.println("Advertencia: No caben más columnas en el canvas.");
                    return;
                }
            }
    
            // Dibuja el fondo de la comanda
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, rectWidth, rectHeight);
    
            gc.setFill(Color.WHITE);
            gc.fillRect(x + 5, y + 5, rectWidth - 10, 40);
            gc.setFill(Color.BLACK);
    
            int numTaula = comanda.getNumber();
            String nombreCamarero = comanda.getClientFX().getNombre();
    
            gc.fillText("Mesa " + Integer.toString(numTaula) + " - Cambrer " + nombreCamarero, x + 10, y + 25);
    
            CommandArea comandaArea = new CommandArea(x, y, rectWidth, rectHeight, comanda);
            commandAreas.add(comandaArea);
    
            double productY = y + 50;
    
            // Dibuja los productos de la comanda
            for (Map.Entry<String, Map<String, Integer>> entry : productGroups.entrySet()) {
                String productName = entry.getKey();
                Map<String, Integer> stateCounts = entry.getValue();
    
                for (Map.Entry<String, Integer> stateEntry : stateCounts.entrySet()) {
                    String state = stateEntry.getKey();
                    int count = stateEntry.getValue();
    
                    // Determina el color según el estado
                    Color color = getColorByState(state);
                    gc.setFill(color);
                    gc.fillRect(x + 5, productY, rectWidth - 10, 40);
    
                    gc.setFill(Color.BLACK);
                    gc.fillText(count + "x " + productName + " (" + state + ")", x + 15, productY + 30);
    
                    CommandProductArea productArea = new CommandProductArea(
                            x + 5, productY, rectWidth - 10, 40, state, count, productName, comanda);
                    comandaArea.products.add(productArea);
    
                    productY += 45;
                }
            }
    
            // Actualizamos la posición para la siguiente comanda
            y += rectHeight + 10;
        }
    }
    

    private Color getColorByState(String state) {
        switch (state) {
            case "demanat":
                return Color.LIGHTBLUE;
            case "pendiente":
                return Color.YELLOW;
            case "listo":
                return Color.LIGHTGREEN;
            case "pagado":
                return Color.GRAY;
            default:
                return Color.WHITE; // Color por defecto
        }
    }
    
    private String determinarEstadoComanda(Comanda comanda) {
        boolean allPaid = true; // Para verificar si todos están pagados
        String estado = "listo"; // Estado predeterminado
    
        for (CommandProduct product : comanda.getProducts()) {
            String productEstado = product.getEstado();
    
            if (!productEstado.equals("pagado")) {
                allPaid = false; // Si hay productos que no están pagados, no todos están en "pagado"
            }
    
            if (productEstado.equals("pendiente")) {
                return "pendiente"; // Prioridad máxima: si hay un producto pendiente, el estado de la comanda es "pendiente"
            }
    
            if (productEstado.equals("demanat")) {
                estado = "demanat"; // Si hay un producto "demanat" y no hay "pendiente", será "demanat"
            }
        }
    
        // Si todos los productos están pagados
        if (allPaid) {
            return "pagado";
        }
    
        return estado; // Devuelve el estado calculado
    }
    
    private Map<String, Map<String, Integer>> agruparProductosPorEstado(Comanda comanda) {
        Map<String, Map<String, Integer>> productGroups = new HashMap<>();
        for (CommandProduct product : comanda.getProducts()) {
            String name = product.getProducte().getNombre();
            String state = product.getEstado();
            productGroups.putIfAbsent(name, new HashMap<>());
            productGroups.get(name).put(state, productGroups.get(name).getOrDefault(state, 0) + 1);
        }
        return productGroups;
    }

    private void handleCanvasClick(double clickX, double clickY, Canvas canvas) {
        List<CommandArea> commandAreas = commandAreasByCanvas.get(canvas);
        if (commandAreas == null) {
            return;
        }

        for (CommandArea commandArea : commandAreas) {
            if (commandArea.contains(clickX, clickY)) {
                if (commandArea.containsTitle(clickX, clickY)) {
                    System.out.println("Se hizo clic en el título de la comanda: " + commandArea.comanda.getNumber());
                    sendCommandSelect(commandArea.comanda.getNumber());
                } else {
                    for (CommandProductArea productArea : commandArea.products) {
                        if (productArea.contains(clickX, clickY)) {
                            System.out.println("Se hizo clic en el producto: " + productArea.name + " (" + productArea.state + ")");
                            sendCommandProductSelect(commandArea.comanda.getNumber(), productArea.name);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void sendCommandProductSelect(int commandTable, String productName) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "product_select");
            message.put("table", commandTable);
            message.put("product", productName);

            sendToServer(message.toString());
            System.out.println("Mensaje enviado al servidor: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommandSelect(int commandTable) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "command_select");
            message.put("table", commandTable);

            sendToServer(message.toString());
            System.out.println("Mensaje enviado al servidor: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String message) {
        if (Main.clienteWebSocket != null && Main.clienteWebSocket.isOpen()) {
            Main.clienteWebSocket.send(message);
        } else {
            System.err.println("WebSocket no está conectado.");
        }
    }

    static class CommandArea {
        double x, y, width, height;
        Comanda comanda;
        List<CommandProductArea> products = new ArrayList<>();

        CommandArea(double x, double y, double width, double height, Comanda comanda) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.comanda = comanda;
        }

        boolean contains(double clickX, double clickY) {
            return clickX >= x && clickX <= x + width && clickY >= y && clickY <= y + height;
        }

        boolean containsTitle(double clickX, double clickY) {
            return contains(clickX, clickY) && clickY <= y + 40;
        }
    }

    static class CommandProductArea {
        double x, y, width, height;
        String state, name;
        int count;
        Comanda comanda;

        CommandProductArea(double x, double y, double width, double height, String state, int count, String name, Comanda comanda) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.state = state;
            this.name = name;
            this.count = count;
            this.comanda = comanda;
        }

        boolean contains(double clickX, double clickY) {
            return clickX >= x && clickX <= x + width && clickY >= y && clickY <= y + height;
        }
    }
}
