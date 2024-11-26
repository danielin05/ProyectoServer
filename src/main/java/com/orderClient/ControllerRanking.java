package com.orderClient;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.Objects.UtilsViews;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ControllerRanking {

    @FXML
    private Button volverButton;

    @FXML
    private TableView<String> tableViewRanking;

    @FXML
    private TableColumn<String, String> NameColumn;

    @FXML
    private TableColumn<String, String> CantidadColumn;

    private ObservableList<String> data = FXCollections.observableArrayList();

    @FXML
    private void volverButton(ActionEvent event) {
        System.out.println("Se pulsó el botón volver");
        Platform.runLater(() -> {
            Main.stage.hide();
            Main.stage.setMaximized(true);
            System.out.println("se cambia la interfaz a comanda detallada");
            UtilsViews.cambiarFrame(Main.stage, "/assets/layout_kitchenClient.fxml");
        });
    }

    @FXML
    public void initialize() {
        JSONObject getRanking = new JSONObject();
        getRanking.put("type", "getRanking");
        Main.clienteWebSocket.send(getRanking.toString());

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Programar la tarea para que se ejecute después de 1 segundo
        scheduler.schedule(() -> {
            // Código que se ejecuta después del retraso
            System.out.println("----------------");
            System.out.println(Main.ranking); // Imprimir el ranking actualizado


        }, 100, TimeUnit.MILLISECONDS);

        loadTableData(Main.ranking);
    }

    private void loadTableData(Map<String, Integer> rankingData) {
        Platform.runLater(() -> {
            // Limpiar cualquier dato anterior de la tabla
            data.clear();

            // Iterar sobre el Map y agregar las filas correspondientes a la lista de datos
            for (Map.Entry<String, Integer> entry : rankingData.entrySet()) {
                // Crear una cadena con los datos del ranking y agregarla a la lista observable
                String row = entry.getKey() + ": " + entry.getValue();
                data.add(row);
            }

            // Asignar los datos a la TableView
            tableViewRanking.setItems(data);

            // Configurar las celdas de la tabla
            NameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(":")[0]));
            CantidadColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(":")[1]));
        });
    }
}
