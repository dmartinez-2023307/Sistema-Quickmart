package org.kinalquickmart.system.model;

public class ProductoTicket {
    private int idProducto;
    private String nombre;
    private int cantidad;
    private double precioUnitario;
    private double total;

    public ProductoTicket(int idProducto, String nombre, int cantidad, double precioUnitario, double total) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    // Getters
    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getTotal() { return total; }
}