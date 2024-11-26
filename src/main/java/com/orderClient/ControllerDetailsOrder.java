package com.orderClient;

import com.Objects.CommandProduct;
import com.Objects.Product;
import com.Objects.UtilsViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import org.json.JSONObject;

public class ControllerDetailsOrder {

    @FXML
    private TableView<CommandProduct> commandProductsTable;

    @FXML
    private TableColumn<CommandProduct, String> productColumn;

    @FXML
    private TableColumn<CommandProduct, Boolean> checkColumn;

    @FXML
    private Text numTaula;

    @FXML
    private Text cambrerTaula;

    @FXML
    private Text productosTotales;

    @FXML
    private Text totalComanda;

    @FXML
    private Text subtotalComanda;

    @FXML
    private Text subtotalProductossNoPagados;

    @FXML
    private Text personasMesa;

    @FXML
    private Text subtotalPorComensaal;

    private ObservableList<CommandProduct> productsList = FXCollections.observableArrayList();

    // Método para manejar el botón "Marcar pedido"
    @FXML
    private void marcarPedido(ActionEvent event) {
        System.out.println("Se pulsó el botón Marcar pedido");
        for (CommandProduct product : productsList) {
            if (product.isSelected()) { // Solo cambia los productos que están seleccionados
                product.setEstado("pedido"); // Actualiza el estado a "pedido"
                    JSONObject obj = new JSONObject();
                    obj.put("type", "changeStatus");
                    obj.put("newStatus", "pedido");
                    obj.put("nombre", product.getProducte().getNombre());
                    obj.put("preu", product.getProducte().getPreu());
                    obj.put("descripción", product.getProducte().getDescription());
                    obj.put("actualStatus", product.getEstado());
                    obj.put("comand", ControllerOrderClient.selectedComanda.getNumber());
                    Main.clienteWebSocket.send(obj.toString());
            }
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();
    }

    // Método para manejar el botón "Marcar Pendent"
    @FXML
    private void marcarPendent(ActionEvent event) {
        System.out.println("Se pulsó el botón Marcar Pendent");
        for (CommandProduct product : productsList) {
            if (product.isSelected()) {
                product.setEstado("pendiente"); // Actualiza el estado a "Pendent" 
                JSONObject obj = new JSONObject();
                obj.put("type", "changeStatus");
                obj.put("newStatus", "pendiente");
                obj.put("nombre", product.getProducte().getNombre());
                obj.put("preu", product.getProducte().getPreu());
                obj.put("descripción", product.getProducte().getDescription());
                obj.put("actualStatus", product.getEstado());
                obj.put("comand", ControllerOrderClient.selectedComanda.getNumber());
                Main.clienteWebSocket.send(obj.toString());
            }
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();
    }

    @FXML
    private void marcarLlest(ActionEvent event) {
        System.out.println("Se pulsó el botón Marcar Llest");
        for (CommandProduct product : productsList) {
            if (product.isSelected()) {
                product.setEstado("listo"); // Actualiza el estado a "Pendent" 
                JSONObject obj = new JSONObject();
                obj.put("type", "changeStatus");
                obj.put("newStatus", "listo");
                obj.put("nombre", product.getProducte().getNombre());
                obj.put("preu", product.getProducte().getPreu());
                obj.put("descripción", product.getProducte().getDescription());
                obj.put("actualStatus", product.getEstado());
                obj.put("comand", ControllerOrderClient.selectedComanda.getNumber());
                Main.clienteWebSocket.send(obj.toString());
            }
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();
    }

    @FXML
    private void seleccionarTots(ActionEvent event) {
        System.out.println("Se pulsó el botón Seleccionar Tots");
        for (CommandProduct product : productsList) {
            product.setSelected(true); // Marca todos los productos
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios
    }

    // Método para manejar el botón "Desmarcar Tots"
    @FXML
    private void desmarcarTots(ActionEvent event) {
        System.out.println("Se pulsó el botón Desmarcar Tots");
        for (CommandProduct product : productsList) {
            product.setSelected(false); // Desmarca todos los productos
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios
    }

    @FXML
    private void volverButton(ActionEvent event) {
        System.out.println("Se pulsó el botón Volver");
        Platform.runLater(() -> {
            Main.stage.hide();
            Main.stage.setMaximized(true);
            UtilsViews.cambiarFrame(Main.stage, "/assets/layout_kitchenClient.fxml");
        });
        // Lógica para volver a la pantalla anterior
    }

    @FXML
    private void pagaFraccionada(ActionEvent event) {
        System.out.println("Se pulsó el botón Paga Fraccionada");
    
        // Recorremos solo los productos seleccionados y los marcamos como pagados
        for (CommandProduct product : productsList) {
            if (product.isSelected()) { // Solo los productos seleccionados
                product.setEstado("pagado"); // Cambia el estado a "Pagado"
                JSONObject obj = new JSONObject();
                obj.put("type", "changeStatus");
                obj.put("newStatus", "pagado");
                obj.put("nombre", product.getProducte().getNombre());
                obj.put("preu", product.getProducte().getPreu());
                obj.put("descripción", product.getProducte().getDescription());
                obj.put("actualStatus", product.getEstado());
                obj.put("comand", ControllerOrderClient.selectedComanda.getNumber());
                Main.clienteWebSocket.send(obj.toString());
            }
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();
    }
    

    @FXML
    private void pagarTot(ActionEvent event) {
        System.out.println("Se pulsó el botón Pagar Tot");

        // Recorremos todos los productos y los marcamos como pagados
        for (CommandProduct product : productsList) {
            product.setEstado("pagado"); // Cambia el estado a "Pagado"
            product.setSelected(false);  // Opcionalmente, desmarcar los productos
            JSONObject obj = new JSONObject();
                obj.put("type", "changeStatus");
                obj.put("newStatus", "pagado");
                obj.put("nombre", product.getProducte().getNombre());
                obj.put("preu", product.getProducte().getPreu());
                obj.put("descripción", product.getProducte().getDescription());
                obj.put("actualStatus", product.getEstado());
                obj.put("comand", ControllerOrderClient.selectedComanda.getNumber());
                Main.clienteWebSocket.send(obj.toString());
        }
        commandProductsTable.refresh(); // Refresca la tabla para mostrar los cambios

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();
    }


    @FXML
    public void initialize() {

        numTaula.setText("Número de la taula: " + ControllerOrderClient.selectedComanda.getNumber());

        cambrerTaula.setText("Cambrer asignat: " + ControllerOrderClient.selectedComanda.getClientFX().getNombre());

        productosTotales.setText("Número de productes totals: " + ControllerOrderClient.selectedComanda.getProducts().size());

        actualizarTotalComanda();

        actualizarSubtotalComanda();

        actualizarSubtotalSinPagar();

        actualizarSubtotalPorComensal();

        personasMesa.setText("Número de comensales: " + ControllerOrderClient.selectedComanda.getClientsNumber());
        

        // Suponiendo que tienes una lista de productos en ControllerOrderClient.selectedComanda
        // Aquí agregamos los productos al ObservableList
        productsList = FXCollections.observableArrayList(ControllerOrderClient.selectedComanda.getProducts());

        // Configuración de la columna del producto (nombre)
        productColumn.setCellValueFactory(cellData -> {
            CommandProduct product = cellData.getValue();
            return new SimpleStringProperty(product.getProducte().getNombre()); // Asume que CommandProduct tiene un método getProductName()
        });

        // Configuración de la columna CheckBox
        checkColumn.setCellValueFactory(cellData -> {
            CommandProduct product = cellData.getValue();
            BooleanProperty selectedProperty = product.selectedProperty(); // Asume que CommandProduct tiene un método selectedProperty()
            return selectedProperty.asObject();
        });

        // Usamos un TableCell personalizado para mostrar un CheckBox y aplicar el color de fondo de la celda de acuerdo al estado del producto
        productColumn.setCellFactory(param -> new TableCell<CommandProduct, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setStyle(""); // Limpiar estilo
                } else {
                    CommandProduct product = getTableRow().getItem();
                    if (product != null) {
                        // Asignamos un color de fondo según el estado del producto
                        switch (product.getEstado().toLowerCase()) {
                            case "pedido":
                                setStyle("-fx-background-color: F6FAEB;"); // Gris para pedido
                                break;
                            case "pendiente":
                                setStyle("-fx-background-color: yellow;"); // Amarillo para pendiente
                                break;
                            case "listo":
                                setStyle("-fx-background-color: lightgreen;"); // Verde para listo
                                break;
                            case "pagado":
                                setStyle("-fx-background-color: A0A0A0; -fx-text-fill: white;"); // Negro para pagado (con texto blanco)
                                break;
                            default:
                                setStyle(""); // Sin color si no se encuentra el estado
                                break;
                        }
                    }
                    setText(item);
                }
            }
        });

        // Usamos un TableCell personalizado para la columna CheckBox y cambiar su fondo según el estado
        checkColumn.setCellFactory(param -> new TableCell<CommandProduct, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setStyle(""); // Limpiar estilo
                } else {
                    CommandProduct product = getTableRow().getItem();
                    if (product != null) {
                        // Asignamos un color de fondo según el estado del producto en la columna del CheckBox
                        switch (product.getEstado().toLowerCase()) {
                            case "pedido":
                                setStyle("-fx-background-color: F6FAEB;"); // Gris para pedido
                                break;
                            case "pendiente":
                                setStyle("-fx-background-color: yellow;"); // Amarillo para pendiente
                                break;
                            case "listo":
                                setStyle("-fx-background-color: lightgreen;"); // Verde para listo
                                break;
                            case "pagado":
                                setStyle("-fx-background-color: A0A0A0; -fx-text-fill: white;"); // Negro para pagado (con texto blanco)
                                break;
                            default:
                                setStyle(""); // Sin color si no se encuentra el estado
                                break;
                        }
                    }
                    // Crear un CheckBox y asignarlo
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item != null && item); // Establece si está seleccionado
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (!empty) {
                            CommandProduct product2 = getTableRow().getItem();
                            if (product2 != null) {
                                product2.setSelected(newValue); // Actualiza el estado de selección del producto
                            }
                        }
                    });
                    setGraphic(checkBox);
                }
                actualizarSubtotalComanda();

                actualizarSubtotalPorComensal();
            }
        });

        // Asignar los datos a la TableView
        commandProductsTable.setItems(productsList);
    }

        // Método para calcular el precio total de la comanda
    private double calcularTotalComanda() {
        double total = 0.0;
        
        // Iterar sobre la lista de productos de la comanda
        for (CommandProduct product : ControllerOrderClient.selectedComanda.getProducts()) {
            total += Double.valueOf(product.getProducte().getPreu());  // Sumar el precio de cada producto
        }
        
        return total;
    }

    // Método para actualizar el total de la comanda en la interfaz
    private void actualizarTotalComanda() {
        double total = calcularTotalComanda();
        totalComanda.setText("Total de la comanda: " + String.format("%.2f", total));
    }

    // Método para calcular el precio de los productos seleccionados
    private double calcularTotalSeleccionados() {
        double subtotal = 0.0;
        
        // Iterar sobre los productos y sumar solo los seleccionados
        for (CommandProduct product : productsList) {
            if (product.isSelected()) {
                subtotal += Double.valueOf(product.getProducte().getPreu());  // Sumar el precio de los productos seleccionados
            }
        }
        
        return subtotal;
    }

    // Método para actualizar el subtotal de los productos seleccionados en la interfaz
    private void actualizarSubtotalComanda() {
        double subtotal = calcularTotalSeleccionados();
        subtotalComanda.setText("Subtotal de los productos seleccionados: " + String.format("%.2f", subtotal));
    }

    private double calcularTotalComandaSinPagar() {
        double total = 0.0;
        
        // Iterar sobre la lista de productos de la comanda
        for (CommandProduct product : ControllerOrderClient.selectedComanda.getProducts()) {
            if (!product.getEstado().equals("pagado")) {
                total += Double.valueOf(product.getProducte().getPreu());  // Sumar el precio de cada producto
            }
        }
        
        return total;
    }

    private void actualizarSubtotalSinPagar() {
        double total = calcularTotalComandaSinPagar();
        subtotalProductossNoPagados.setText("Subtotal de no pagados: " + String.format("%.2f", total));
    }

    private void actualizarSubtotalPorComensal() {
        double total = calcularTotalComandaSinPagar() / ControllerOrderClient.selectedComanda.getClientsNumber();
        subtotalPorComensaal.setText("Subtotal por comensal: " + String.format("%.2f", total));
    }
}
