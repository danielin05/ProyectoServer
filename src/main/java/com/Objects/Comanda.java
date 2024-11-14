package com.Objects;

import java.util.ArrayList;
import java.util.List;
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
}
