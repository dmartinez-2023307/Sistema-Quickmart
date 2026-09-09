package org.kinalquickmart.system.model;

public class Category {
    
    private int id;
    private String name;
    private String description;

    // Constructor vacío (necesario para JavaFX)
    public Category() {
    }

    // Constructor completo
    public Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Este método es CRÍTICO para el ComboBox
    @Override
    public String toString() {
        return name;
    }
}