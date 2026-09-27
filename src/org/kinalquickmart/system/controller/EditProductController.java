package org.kinalquickmart.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kinalquickmart.system.model.Product;
import java.sql.CallableStatement;
import java.sql.Connection;
import org.kinalquickmart.system.config.ConexionDB;

public class EditProductController {

    @FXML private TextField txtNombreComercial;
    @FXML private TextField txtPrecioCosto;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStock;
    @FXML private TextField txtCategoria;
    @FXML private javafx.scene.control.Button btnGuardar;

    private Product productoSeleccionado;
    private int idCategoriaParaBD;

    public void setProduct(Product product) {
        this.productoSeleccionado = product;
        if (product != null) {
            txtNombreComercial.setText(product.getCommercialName());
            txtPrecioCosto.setText(String.valueOf(product.getCostPrice()));
            txtPrecioVenta.setText(String.valueOf(product.getSalePrice()));
            txtStock.setText(String.valueOf(product.getCurrentStock()));
            
            if (product.getCategory() != null) {
                txtCategoria.setText(product.getCategory().getName());
                this.idCategoriaParaBD = product.getCategory().getId();
            } else {
                txtCategoria.setText("Sin categoría");
                this.idCategoriaParaBD = 0;
            }
        }
    }

    @FXML
    private void guardarCambios() {
        try {
            // 1. Validar campos vacíos
            if (txtNombreComercial.getText().trim().isEmpty() || 
                txtPrecioCosto.getText().trim().isEmpty() || 
                txtPrecioVenta.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Todos los campos son obligatorios.");
                return;
            }

            double precioCosto = Double.parseDouble(txtPrecioCosto.getText().trim());
            double precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (precioCosto >= precioVenta) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Lógica", 
                    "El precio de costo NO puede ser mayor o igual al precio de venta.");
                return;
            }

            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Stock", 
                    "El stock no puede ser negativo.");
                return;
            }

            String nombre = txtNombreComercial.getText().trim();

            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); 
            String sql = "{CALL sp_actualizarProducto(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, productoSeleccionado.getId());
                cs.setString(2, nombre);
                cs.setDouble(3, precioCosto);
                cs.setDouble(4, precioVenta);
                cs.setInt(5, stock);
                
                // Si el ID de categoría es 0 o menor, lo guardamos como NULL
                if (this.idCategoriaParaBD <= 0) {
                    cs.setNull(6, java.sql.Types.INTEGER);
                } else {
                    cs.setInt(6, this.idCategoriaParaBD);
                }

                cs.execute();
            }

            // 6. Éxito y cierre
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto actualizado correctamente.");
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", "Los precios y el stock deben ser números válidos.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el producto.\nDetalle: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}