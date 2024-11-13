package com.Objects;

import java.util.List;

public class CommandProduct {

    public Product producte;
    public int cantidad;
    public String estado;

    private static final List<String> ESTADOS = List.of("pendiente","listo","pagado");

    public CommandProduct(Product producte, int cantidad) {
        this.producte = producte;
        this.cantidad = cantidad;
        this.estado = ESTADOS.get(0);
    }

    public Product getProducte() {
        return producte;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(int i) {
        this.estado = ESTADOS.get(i);
    }
}
