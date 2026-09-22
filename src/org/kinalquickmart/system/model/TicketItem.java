package org.kinalquickmart.system.model;

import java.math.BigDecimal;

public class TicketItem {

    private Product product;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public TicketItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getSalePrice();
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    // Método para aumentar la cantidad (cuando se agrega el mismo producto otra vez)
    public void increaseQuantity(int amount) {
        this.quantity += amount;
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    // Getters para las columnas del TableView
    public String getProductName() {
        return product.getCommercialName();
    }

    public String getBarCode() {
        return product.getBarCode();
    }

    @Override
    public String toString() {
        return product.getCommercialName();
    }
}