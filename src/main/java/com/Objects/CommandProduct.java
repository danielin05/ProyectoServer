package com.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;

public class CommandProduct {

    private Product producte;
    private String estado;
    private String comentario;
    private BooleanProperty selected;  // Cambiado de boolean a BooleanProperty

    private static final List<String> ESTADOS = List.of("pedido", "pendiente", "listo", "pagado");

    // Constructor
    public CommandProduct(Product producte) {
        this.producte = producte;
        this.estado = ESTADOS.get(0);  // Estado inicial: "pedido"
        this.comentario = null;
        this.selected = new SimpleBooleanProperty(false);  // Inicializa la propiedad selected como false
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

    public BooleanProperty selectedProperty() {
        return selected;  // Devuelve la propiedad booleana observada
    }

    public boolean isSelected() {
        return selected.get();  // Obtiene el valor de la propiedad selected
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);  // Establece el valor de la propiedad selected
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "CommandProduct{" +
                "producte=" + producte +
                ", estado='" + estado + '\'' +
                ", comentario='" + comentario + '\'' +
                ", selected=" + selected.get() +
                '}';
    }
}
