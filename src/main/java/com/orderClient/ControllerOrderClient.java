package com.orderClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.Objects.Comanda;
import com.Objects.CommandProduct;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

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
                try {
                    drawOnCanvas(calienteCanvas, Main.comandsByTag.get("caliente"));
                    drawOnCanvas(frioCanvas, Main.comandsByTag.get("frio"));
                    drawOnCanvas(postresCanvas, Main.comandsByTag.get("postre"));
                    drawOnCanvas(generalCanvas, Main.comandsByTag.get("general"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    private void drawOnCanvas(Canvas canvas, List<Comanda> comandas) {
        if (comandas == null || comandas.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<CommandArea> commandAreas = new ArrayList<>();
        commandAreasByCanvas.put(canvas, commandAreas);

        double rectWidth = canvas.getWidth() / 5 - 25; // 5 columnas
        double padding = 25;

        double x = padding;
        double y = padding;

        for (int i = 0; i < comandas.size(); i++) {
            Comanda comanda = comandas.get(i);

            // Verifica el estado de la comanda
            String estado = "listo";

            for (CommandProduct commandProduct : comanda.getProducts()) {
                if (commandProduct.getEstado().equals("pendiente")) {
                    estado = "pendiente";
                    break;
                }
            }

            comanda.setEstado(estado);

            if (comanda.getEstado().equals("listo")) {
                continue;
            }

            Map<String, Map<String, Integer>> productGroups = new HashMap<>();
            for (CommandProduct product : comanda.getProducts()) {
                String name = product.getProducte().getNombre();
                String state = product.getEstado();
                productGroups.putIfAbsent(name, new HashMap<>());
                productGroups.get(name).put(state, productGroups.get(name).getOrDefault(state, 0) + 1);
            }

            // Calcula tamaño del rectángulo de comanda
            double rectHeight = 50 + productGroups.size() * 45;

            if (y + rectHeight > canvas.getHeight()) {
                x += rectWidth + padding;
                y = padding;
                if (x + rectWidth > canvas.getWidth()) {
                    System.out.println("Advertencia: No caben más columnas en el canvas.");
                    return;
                }
            }

            // Dibuja la comanda
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, rectWidth, rectHeight);

            gc.setFill(Color.WHITE);
            gc.fillRect(x + 5, y + 5, rectWidth - 10, 40);
            gc.setFill(Color.BLACK);

            int numTaula = comanda.getNumber();
            String nombreCamarero = comanda.getClientFX().getNombre();

            gc.fillText("Mesa " + Integer.toString(numTaula) + "  - Cambrer " + nombreCamarero, x + 10, y + 25);

            CommandArea comandaArea = new CommandArea(x, y, rectWidth, rectHeight, comanda);
            commandAreas.add(comandaArea);

            double productY = y + 50;

            for (Map.Entry<String, Map<String, Integer>> entry : productGroups.entrySet()) {
                String name = entry.getKey();
                Map<String, Integer> stateCounts = entry.getValue();

                for (Map.Entry<String, Integer> stateEntry : stateCounts.entrySet()) {
                    String state = stateEntry.getKey();
                    int count = stateEntry.getValue();

                    Color color = state.equals("pendiente") ? Color.WHITE : Color.GRAY;
                    gc.setFill(color);
                    gc.fillRect(x + 5, productY, rectWidth - 10, 40);

                    gc.setFill(Color.BLACK);
                    gc.fillText(count + "x " + name + " (" + state + ")", x + 15, productY + 25);

                    CommandProductArea productArea = new CommandProductArea(
                        x + 5, productY, rectWidth - 10, 40, state, count, name, comanda);
                    comandaArea.products.add(productArea);

                    productY += 45;
                }
            }
            y += rectHeight + 10;
        }
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
                    sendCommandDone(commandArea.comanda.getNumber());
                } else {
                    for (CommandProductArea productArea : commandArea.products) {
                        if (productArea.contains(clickX, clickY)) {
                            System.out.println("Se hizo clic en el producto: " + productArea.name + " (" + productArea.state + ")");
                            if (productArea.state.equals("pendiente")) {
                                sendCommandProductDone(commandArea.comanda.getNumber(), productArea.name);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private void sendCommandProductDone(int commandTable, String productName) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "product_done");
            message.put("table", commandTable);
            message.put("product", productName);

            sendToServer(message.toString());
            System.out.println("Mensaje enviado al servidor: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommandDone(int commandTable) {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "command_done");
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
