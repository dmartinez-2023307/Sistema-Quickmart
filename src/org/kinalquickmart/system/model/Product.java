package org.kinalquickmart.system.model;

import java.math.BigDecimal;

public class Product {
    
    private int id;
    private String barCode;
    private String commercialName;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private int currentStock;
    private Category category;

    // Constructor vacío (necesario para JavaFX)
    public Product() {
    }

    // Constructor completo
    public Product(int id, String barCode, String commercialName, 
                   BigDecimal costPrice, BigDecimal salePrice, 
                   int currentStock, Category category) {
        this.id = id;
        this.barCode = barCode;
        this.commercialName = commercialName;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.currentStock = currentStock;
        this.category = category;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}