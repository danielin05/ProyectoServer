package com.objects;

import java.util.List;

public class CommandProduct {

    private Product producte;
    private String estado;
    private String comentario;

    private static final List<String> ESTADOS = List.of("demanat","pendiente","listo","pagado");

    public CommandProduct(Product producte) {
        this.producte = producte;
        this.estado = ESTADOS.get(0);
        this.comentario = null;
    }

    public Product getProducte() {
        return producte;
    }

    public String getEstado() {
        return estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
