package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.Objects.ClientFX;

public class ControllerCurrentClients implements Initializable {

    @FXML
    private Canvas canvasCurrentUsers;

    @FXML
    private Button enterButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button consuptionButton;

    private static final int RECTANGLE_WIDTH = 135;  // Ancho del rectángulo
    private static final int RECTANGLE_HEIGHT = 180;  // Altura del rectángulo
    private static final int RECTANGLE_SPACING = 20;  // Espacio entre los rectángulos
    private static final int CORNER_RADIUS = 15; // Radio de las esquinas redondeadas

    private final List<ClientFX> currentClients = Main.currentClients; // Lista de clientes

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawRectanglesOnCanvas();
        
        // Agregar un manejador de eventos para detectar clics
        canvasCurrentUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);
    }

    @FXML
    private void enterButton(ActionEvent event) {
        System.out.println("Se pulsó el botón entrar");
        // Lógica para el botón "entrar"
    }

    @FXML
    private void exitButton(ActionEvent event) {
        System.out.println("Se pulsó el botón salir");
        // Lógica para el botón "salir"
    }

    @FXML
    private void consuptionButton(ActionEvent event) {
        System.out.println("Se pulsó el botón consumo interno");
        // Lógica para el botón "entrar"
    }

    private void drawRectanglesOnCanvas() {
        GraphicsContext gc = canvasCurrentUsers.getGraphicsContext2D();
        
        // Configura una sombra para los rectángulos (solo para el rectángulo grande)
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        shadow.setColor(Color.GRAY);
        
        // Calcula las filas y columnas según el ancho del canvas
        double canvasWidth = canvasCurrentUsers.getWidth();
        int rectanglesPerRow = (int) ((canvasWidth + RECTANGLE_SPACING) / (RECTANGLE_WIDTH + RECTANGLE_SPACING));
    
        // Dibuja los rectángulos
        for (int i = 0; i < currentClients.size(); i++) {
            ClientFX client = currentClients.get(i);  // Obtener el cliente

            // Calcula la posición de cada rectángulo
            int row = i / rectanglesPerRow;
            int col = i % rectanglesPerRow;
            
            double x = col * (RECTANGLE_WIDTH + RECTANGLE_SPACING) + 5;
            double y = row * (RECTANGLE_HEIGHT + RECTANGLE_SPACING) + 5; // Ajuste aquí para mover los rectángulos hacia arriba
            
            // Aplica la sombra solo al rectángulo exterior
            gc.setEffect(shadow);
            
            // Dibuja un rectángulo redondeado exterior (primer color)
            gc.setFill(Color.web("#D9CAC1")); // Primer color
            gc.fillRoundRect(x, y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
            
            // Dibujar el borde del rectángulo grande
            gc.setStroke(Color.web("#26170F")); // Borde de color #26170F
            gc.setLineWidth(2); // Grosor del borde más fino (ajustado a 2)
            gc.strokeRoundRect(x, y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
            
            // Restablece el efecto para no afectar el siguiente rectángulo
            gc.setEffect(null);
    
            // Dibuja un rectángulo redondeado interior (segundo color) sin sombra
            gc.setFill(Color.web("#A69286")); // Segundo color
            gc.fillRoundRect(x + 5, y + 5, RECTANGLE_WIDTH - 10, RECTANGLE_HEIGHT - 60, CORNER_RADIUS, CORNER_RADIUS);
            
            // Añadir el nombre del cliente sobre el rectángulo
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", 18)); // Usamos una fuente estándar para el nombre
            String clientName = client.getNombre();
            double nameWidth = getTextWidth(gc, clientName); // Obtener el ancho del nombre
            gc.fillText(clientName, x + RECTANGLE_WIDTH / 2 - nameWidth / 2, y + RECTANGLE_HEIGHT - 30); // Nombre centrado
            
            // Formatear el ID a tres dígitos
            String formattedId = String.format("%03d", Integer.parseInt(client.getId())); // Formatea la ID a 3 cifras
            gc.setFill(Color.WHITE);  // El texto de la ID será blanco
            gc.setFont(Font.font("Calibri", 50)); // Fuente Calibri para la ID

            // Calcular el ancho de la ID
            double idWidth = getTextWidth(gc, formattedId);
            gc.fillText(formattedId, x + RECTANGLE_WIDTH / 2 - idWidth / 2, y + 70); // ID centrado
        }
    }

    // Método para calcular el ancho de un texto
    private double getTextWidth(GraphicsContext gc, String text) {
        Text tempText = new Text(text);
        tempText.setFont(gc.getFont());
        return tempText.getBoundsInLocal().getWidth();
    }

    private void handleCanvasClick(MouseEvent event) {
        // Obtener la posición del clic
        double x = event.getX();
        double y = event.getY();

        // Verificar si el clic está dentro de un rectángulo
        int row = (int) (y / (RECTANGLE_HEIGHT + RECTANGLE_SPACING));
        int col = (int) (x / (RECTANGLE_WIDTH + RECTANGLE_SPACING));
        int index = row * (int) ((canvasCurrentUsers.getWidth() + RECTANGLE_SPACING) / (RECTANGLE_WIDTH + RECTANGLE_SPACING)) + col;

        if (index >= 0 && index < currentClients.size()) {
            ClientFX selectedClient = currentClients.get(index);
            System.out.println("Cliente seleccionado:");
            System.out.println("ID: " + selectedClient.getId());
            System.out.println("Nombre: " + selectedClient.getNombre());
            System.out.println("Contraseña: " + selectedClient.getPassword());

            openUserLayout();
        }
    }

    private void openUserLayout() {
        try {

            Parent loadedPane = FXMLLoader.load(getClass().getResource("/assets/layout_userInfo.fxml"));
            GridPane.setColumnIndex(loadedPane, 1);
            GridPane.setRowIndex(loadedPane, 0);
            ControllerClients.instance.getRootGridPane().getChildren().removeLast();
            ControllerClients.instance.getRootGridPane().getChildren().add(loadedPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
