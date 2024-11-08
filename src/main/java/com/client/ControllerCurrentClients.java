package com.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCurrentClients implements Initializable {

    @FXML
    private Canvas canvasCurrentUsers;

    private static final int RECTANGLE_WIDTH = 135;  // Ancho del rectángulo
    private static final int RECTANGLE_HEIGHT = 180;  // Altura del rectángulo
    private static final int RECTANGLE_SPACING = 20;  // Espacio entre los rectángulos
    private static final int NUM_RECTANGLES = 20;  // Número de rectángulos
    private static final int CORNER_RADIUS = 15;
    
    // Radio de las esquinas redondeadas

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawRectanglesOnCanvas();
    }

    private void drawRectanglesOnCanvas() {
        GraphicsContext gc = canvasCurrentUsers.getGraphicsContext2D();
        
        // Configura una sombra para los rectángulos
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        shadow.setColor(Color.GRAY);
        gc.setEffect(shadow);

        // Establece el color de relleno
        gc.setFill(Color.LIGHTBLUE);

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
            
            // Dibuja un rectángulo redondeado en (x, y)
            gc.fillRoundRect(x, y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
        }
        
        // Restablece el efecto para no afectar otros elementos
        gc.setEffect(null);
    }
}
