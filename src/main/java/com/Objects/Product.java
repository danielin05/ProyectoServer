package com.Objects;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private String nombre;
    private String preu;
    private String description;
    private String imageBase64;
    private List<String> tags;

    public Product(String nombre, String preu, String description, String imageBase64) {
        this.nombre = nombre;
        this.preu = preu;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.tags = new ArrayList<>();
    }

    public Product(String nombre, String preu) {
        this.nombre = nombre;
        this.preu = preu;
        this.tags = new ArrayList<>();
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

    public String getImageBase64() {
        return imageBase64;
    }

    public List<String> getTags() {
        return tags;
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

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void addTags(List<String> tagsToAdd) {
        for (String tag : tagsToAdd) {
            tags.add(tag);
        }
    }
}