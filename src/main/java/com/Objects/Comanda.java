package com.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Comanda {

    private int number;
    private int clientsNumber;
    private List<CommandProduct> productsList;
    private ClientFX clientFX;
    private String estado;

    private static final List<String> ESTADOS = List.of("pendiente", "listo", "pagado");

    public Comanda(int number, int clientsNumber, ClientFX clientFX) {
        this.number = number;
        this.clientsNumber = clientsNumber;
        this.productsList = new ArrayList<>();
        this.clientFX = clientFX;
        this.estado = ESTADOS.get(0);
    }

    public int getNumber() {
        return number;
    }

    public int getClientsNumber() {
        return clientsNumber;
    }

    public List<CommandProduct> getProducts() {
        return productsList;
    }

    public ClientFX getClientFX() {
        return clientFX;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void addProducts(List<CommandProduct> addCommandProducts) {
        for (CommandProduct product : addCommandProducts) {
            productsList.add(product);
        }
    }

    public Map<String, Integer> getCommandInfo() {
        Map<String, Integer> info = new HashMap<>();
        for (CommandProduct commandProduct : productsList) {
            Product product = commandProduct.getProducte();
            String nombreProducto = product.getNombre();
            
            info.put(nombreProducto, info.getOrDefault(nombreProducto, 0) + 1);
        }
        return info;
    }   
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comanda Número: ").append(number).append("\n");
        sb.append("Número de Clientes: ").append(clientsNumber).append("\n");
        sb.append("Estado de la Comanda: ").append(estado).append("\n");
        sb.append("Productos:\n");

        for (CommandProduct commandProduct : productsList) {
            sb.append(" - Producto: ").append(commandProduct.getProducte().getNombre())
            .append(", Estado: ").append(commandProduct.getEstado()).append("\n");
        }

        return sb.toString();
    }
}    
