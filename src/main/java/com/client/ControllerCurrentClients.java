package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCurrentClients implements Initializable {

    @FXML
    private Canvas canvasCurrentUsers;

    @FXML
    private Button enterButton;

    @FXML
    private Button exitButton;

    private static final int RECTANGLE_WIDTH = 135;  // Ancho del rectángulo
    private static final int RECTANGLE_HEIGHT = 180;  // Altura del rectángulo
    private static final int RECTANGLE_SPACING = 20;  // Espacio entre los rectángulos
    private static final int NUM_RECTANGLES = 14;  // Número de rectángulos
    private static final int CORNER_RADIUS = 15;
    
    // Radio de las esquinas redondeadas

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawRectanglesOnCanvas();
    }

    @FXML
    private void enterButton(ActionEvent event) {
        System.out.println("Se pulsó el botón entrar");
        // Cierra la ventana o regresa a la pantalla anterior
    }

    @FXML
    private void exitButton(ActionEvent event) {
        System.out.println("Se pulsó el botón salir");
        // Cierra la ventana o regresa a la pantalla anterior
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
        for (int i = 0; i < NUM_RECTANGLES; i++) {
            // Calcula la posición de cada rectángulo
            int row = i / rectanglesPerRow;
            int col = i % rectanglesPerRow;
            
            double x = col * (RECTANGLE_WIDTH + RECTANGLE_SPACING);
            double y = row * (RECTANGLE_HEIGHT + RECTANGLE_SPACING);
            
            // Aplica la sombra solo al rectángulo exterior
            gc.setEffect(shadow);
            
            // Dibuja un rectángulo redondeado exterior (primer color)
            gc.setFill(Color.web("#A69286")); // Primer color
            gc.fillRoundRect(x, y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
            
            // Restablece el efecto para no afectar el siguiente rectángulo
            gc.setEffect(null);
    
            // Dibuja un rectángulo redondeado interior (segundo color) sin sombra
            gc.setFill(Color.web("#D9CAC1")); // Segundo color
            gc.fillRoundRect(x + 5, y + 5, RECTANGLE_WIDTH - 10, RECTANGLE_HEIGHT - 60, CORNER_RADIUS, CORNER_RADIUS);
        }
    }
    
}
