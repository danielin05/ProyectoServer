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

    public void addProducts(List<Product> addProducts) {
        Map<Product, Integer> productCountMap = new HashMap<>();

        for (Product product : addProducts) {
            productCountMap.put(product, productCountMap.getOrDefault(product, 0) + 1);
        }

        for (Map.Entry<Product, Integer> entry : productCountMap.entrySet()) {
            Product product = entry.getKey();
            int cantidadItems = entry.getValue();
            productsList.add(new CommandProduct(product, cantidadItems));
        }
    }
}
