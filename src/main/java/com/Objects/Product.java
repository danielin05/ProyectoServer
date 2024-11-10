package com.Objects;

public class Product {
    private String nombre;
    private String preu;
    private String description;
    private String imageURL;

    public Product(String nombre, String preu, String description, String imageURL) {
        this.nombre = nombre;
        this.preu = preu;
        this.description = description;
        this.imageURL = imageURL;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getPreu() {
        return preu;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPreu(String preu) {
        this.preu = preu;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}