package com.orderClient;

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
        // Lógica para el botón "entrar"
    }

    @FXML
    private void configButton(ActionEvent event) {
        System.out.println("Se pulsó el botón configuración");
        // Lógica para el botón "entrar"
    }

    @FXML
    private void deshacerButton(ActionEvent event) {
        System.out.println("Se pulsó el botón deshacer");
        // Lógica para el botón "entrar"
    }

    @FXML
    private void delanteButton(ActionEvent event) {
        System.out.println("Se pulsó el botón deshacer");
        // Lógica para el botón "entrar"
    }

    @FXML
    private void atrasButton(ActionEvent event) {
        System.out.println("Se pulsó el botón deshacer");
        // Lógica para el botón "entrar"
    }

    @FXML
    public void initialize() {
        drawOnCanvas(calienteCanvas, "Preparar Caliente");
        drawOnCanvas(frioCanvas, "Preparar Frío");
        drawOnCanvas(postresCanvas, "Preparar Postres");
        drawOnCanvas(generalCanvas, "Vista General");
    }

    private void drawOnCanvas(Canvas canvas, String text) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.fillText(text, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
}
