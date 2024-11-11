package com.Objects;

import java.util.ArrayList;
import java.util.List;

public class Comanda {

    private int number;
    private int clientsNumber;
    private List<Product> productsList;

    public Comanda(int number, int clientsNumber) {
         this.number = number;
        this.clientsNumber = clientsNumber;
        this.productsList = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public int getClientsNumber() {
        return clientsNumber;
    }

    public List<Product> getProducts() {
        return productsList;
    }

    public void setClientsNumber(int clientsNumber) {
        this.clientsNumber = clientsNumber;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addProducts(List<Product> addProducts) {
        for (Product product : addProducts) {
            this.productsList.add(product);
        }
    }

    @Override
    public String toString() {
        return "number: " + number + " clients: " + clientsNumber + " products: " + productsList.toString();
    }
}
