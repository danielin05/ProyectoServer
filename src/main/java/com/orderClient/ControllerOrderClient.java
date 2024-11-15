package com.orderClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Objects.Comanda;
import com.Objects.CommandProduct;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

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

        // Detecta el clic en cualquier canvas
        calienteCanvas.setOnMouseClicked(event -> handleCanvasClick(event, calienteCanvas, Main.comandsByTag.get("caliente")));
        frioCanvas.setOnMouseClicked(event -> handleCanvasClick(event, frioCanvas, Main.comandsByTag.get("frio")));
        postresCanvas.setOnMouseClicked(event -> handleCanvasClick(event, postresCanvas, Main.comandsByTag.get("postre")));
        generalCanvas.setOnMouseClicked(event -> handleCanvasClick(event, generalCanvas, Main.comandsByTag.get("general")));
    }


    private void startDrawingAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    // Tu código de dibujo aquí
                    drawOnCanvas(calienteCanvas, Main.comandsByTag.get("caliente"));
                    drawOnCanvas(frioCanvas, Main.comandsByTag.get("frio"));
                    drawOnCanvas(postresCanvas, Main.comandsByTag.get("postre"));
                    drawOnCanvas(generalCanvas, Main.comandsByTag.get("general"));
                } catch (Exception e) {
                    e.printStackTrace();  // Esto te ayuda a detectar si algo está fallando
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
    
        // Limpia el lienzo antes de dibujar
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    
        double rectWidth = canvas.getWidth() / 5 - 25; // 5 columnas, con separaciones de 25px
        double padding = 25;  // Espaciado entre rectángulos y bordes
    
        // Coordenadas iniciales
        double x = padding;
        double y = padding;
    
        for (int i = 0; i < comandas.size(); i++) {
            Comanda comanda = comandas.get(i);
    
            // Agrupamos los productos por nombre y estado
            Map<String, Map<String, Integer>> productGroups = new HashMap<>();
            for (CommandProduct commandProduct : comanda.getProducts()) {
                String productName = commandProduct.getProducte().getNombre();  // Nombre del producto
                String productState = commandProduct.getEstado();  // Estado del producto
    
                // Solo agrupar y mostrar productos con estado pendiente o listo
                if (productState.equals("pendiente") || productState.equals("listo")) {
                    // Agrupar por nombre y estado
                    productGroups.putIfAbsent(productName, new HashMap<>());
                    productGroups.get(productName).put(productState, 
                        productGroups.get(productName).getOrDefault(productState, 0) + 1);
                }
            }
    
            // Calculamos la altura total de la comanda, que depende de la cantidad de productos
            double rectHeight = 50 + productGroups.size() * 45;  // Altura base + altura de los productos
    
            // Si la comanda no cabe en la columna actual, distribúyela en varias columnas
            while (y + rectHeight > canvas.getHeight()) {
                // La comanda no cabe en la columna actual, salta a la siguiente columna
                x += rectWidth + padding;
                y = padding;
    
                // Si ya no hay espacio en el ancho del canvas, emite un aviso y detiene el dibujo
                if (x + rectWidth > canvas.getWidth()) {
                    System.out.println("Advertencia: No caben más columnas en el canvas.");
                    return; // Detiene la ejecución si no hay espacio
                }
            }
    
            // Dibuja el rectángulo grande de la comanda
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillRect(x, y, rectWidth, rectHeight);
    
            // Dibuja el texto de la comanda con fondo blanco
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(x + 5, y + 5 , rectWidth - 10, 40);  // Fondo del texto
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText("Comanda " + (i + 1), x + 10, y + 25);  // Texto de la comanda
    
            // Posición inicial para los productos dentro de la comanda
            double productY = y + 50;  // Empieza debajo del texto de la comanda
    
            // Dibuja los productos agrupados dentro de la comanda
            for (Map.Entry<String, Map<String, Integer>> productEntry : productGroups.entrySet()) {
                String productName = productEntry.getKey(); // Nombre del producto
                Map<String, Integer> stateCounts = productEntry.getValue();  // Agrupado por estado
    
                for (Map.Entry<String, Integer> stateEntry : stateCounts.entrySet()) {
                    String state = stateEntry.getKey();  // Estado del producto (pendiente, listo, etc.)
                    int count = stateEntry.getValue();  // Cantidad de productos con este estado
    
                    // Asignar color según el estado
                    javafx.scene.paint.Color color;
                    if (state.equals("pendiente")) {
                        color = javafx.scene.paint.Color.WHITE;  // Productos pendientes en blanco
                    } else if (state.equals("listo")) {
                        color = javafx.scene.paint.Color.GRAY;  // Productos listos en gris
                    } else {
                        color = javafx.scene.paint.Color.LIGHTGREEN;  // Este caso no debería ocurrir debido al filtro
                    }
    
                    // Dibuja un rectángulo con el color correspondiente
                    gc.setFill(color);
                    gc.fillRect(x + 5, productY, rectWidth - 10, 40);  // Un rectángulo para el grupo
    
                    // Dibuja el texto dentro del rectángulo (nombre del producto y cantidad)
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                    gc.fillText(count + "x " + productName + " (" + state + ")", x + 15, productY + 25);  // Muestra cantidad, nombre y estado
     
                    // Incrementa la posición Y para el siguiente grupo de productos
                    productY += 20 + padding;  // Espaciado entre productos
                }
            }
    
            // Incrementa la posición vertical para la siguiente comanda
            y += rectHeight + 10;
        }
    }
    
    private void handleCanvasClick(javafx.scene.input.MouseEvent event, Canvas canvas, List<Comanda> comandas) {
        double clickX = event.getX();
        double clickY = event.getY();
    
        double padding = 25;
        double rectWidth = canvas.getWidth() / 5 - padding;  // 5 columnas, con espaciado
        double x = padding;
        double y = padding;
    
        for (int i = 0; i < comandas.size(); i++) {
            Comanda comanda = comandas.get(i);
    
            // Agrupar productos como antes
            Map<String, Map<String, Integer>> productGroups = new HashMap<>();
            for (CommandProduct commandProduct : comanda.getProducts()) {
                String productName = commandProduct.getProducte().getNombre();
                String productState = commandProduct.getEstado();
    
                if (productState.equals("pendiente") || productState.equals("listo")) {
                    productGroups.putIfAbsent(productName, new HashMap<>());
                    productGroups.get(productName).put(productState,
                            productGroups.get(productName).getOrDefault(productState, 0) + 1);
                }
            }
    
            // Calcular la altura del rectángulo de la comanda
            double rectHeight = 50 + productGroups.size() * 45;  // Altura base + productos
    
            // Detección de clic en el título de la comanda
            if (clickX >= x && clickX <= x + rectWidth && clickY >= y && clickY <= y + 40) { // Solo el área del título (comanda)
                System.out.println("Se hizo clic en el título de la comanda " + (i + 1) + " (Mesa: " + comanda.getNumber() + ")");
    
                // Aquí puedes agregar la lógica que desees para cuando se haga clic en el título de la comanda
                return;  // Clic detectado en el título, salimos
            }
    
            // Si no ha sido clic en el título, verificar si es clic en los productos
            if (clickX >= x && clickX <= x + rectWidth && clickY >= y + 50 && clickY <= y + rectHeight) {
                // Llamada a la detección de clic en los productos
                detectProductClick(clickX, clickY, x, y, rectWidth, productGroups, comanda.getNumber());
                return; // Clic detectado en los productos, salimos
            }
    
            // Si la comanda no cabe en esta columna, salta a la siguiente
            y += rectHeight + 10;
            if (y + rectHeight > canvas.getHeight()) {
                x += rectWidth + padding;  // Salta a la siguiente columna
                y = padding;  // Resetea la posición Y
                if (x + rectWidth > canvas.getWidth()) {
                    return; // Si no hay espacio en el canvas, termina la detección
                }
            }
        }
    }
    
    private void detectProductClick(double clickX, double clickY, double comandaX, double comandaY, double rectWidth, Map<String, Map<String, Integer>> productGroups, int numeroMesa) {
        double productY = comandaY + 50;  // Empieza después del rectángulo del título de la comanda

        // Iterar sobre los grupos de productos
        for (Map.Entry<String, Map<String, Integer>> productEntry : productGroups.entrySet()) {
            String productName = productEntry.getKey();
            Map<String, Integer> stateCounts = productEntry.getValue();

            // Iterar sobre los estados de los productos
            for (Map.Entry<String, Integer> stateEntry : stateCounts.entrySet()) {
                String state = stateEntry.getKey();
                int count = stateEntry.getValue();

                // Verificar si el clic está dentro del área del producto
                double productHeight = 40;  // Altura de cada producto

                if (clickX >= comandaX + 5 && clickX <= comandaX + rectWidth - 10 &&
                    clickY >= productY && clickY <= productY + productHeight) {
                    // Si el clic está dentro de este producto
                    System.out.println("Se hizo clic en el producto " + count + "x " + productName + " (" + state + ") en la mesa " + numeroMesa);
                    return;
                }

                productY += productHeight + 25;  // Espacio entre productos
            }
        }
    }

}
