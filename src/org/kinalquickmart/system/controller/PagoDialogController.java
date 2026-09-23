package org.kinalquickmart.system.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PagoDialogController implements Initializable {

    @FXML private TextField txtNit;
    @FXML private ComboBox<String> cmbFormaPago;
    @FXML private TextField txtMontoRecibido;
    @FXML private Label lblCambio;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    
    private Stage dialog;
    private boolean confirmado = false;
    private double totalVenta = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbFormaPago.getItems().addAll("Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito");
        cmbFormaPago.setValue("Efectivo");
        cmbFormaPago.setOnAction(e -> manejarCambioFormaPago());
        
        txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtMontoRecibido.setText(oldVal);
            } else {
                calcularCambio();
            }
        });
        
        btnConfirmar.setOnAction(e -> confirmarPago());
        btnCancelar.setOnAction(e -> cancelarPago());
    }
    
    // ===== VALIDACIÓN SIMPLE Y ROBUSTA DEL NIT =====
    private boolean validarNit(String nit) {
        // Si está vacío, es válido (se usará C/F por defecto)
        if (nit == null || nit.trim().isEmpty()) {
            return true;
        }
        
        // Limpiar: quitar espacios y convertir a mayúsculas
        nit = nit.trim().toUpperCase();
        
        // Si es exactamente "C/F", es válido
        if (nit.equals("C/F")) {
            return true;
        }
        
        // Validar formato NIT: NNNNNNN-N (7 dígitos + guión + 1 dígito)
        // Ejemplo válido: 1583083-7, 1234567-8
        return nit.matches("\\d{7}-\\d{1}");
    }
    
    private void manejarCambioFormaPago() {
        String formaPago = cmbFormaPago.getValue();
        if (formaPago != null && !formaPago.equals("Efectivo")) {
            txtMontoRecibido.setDisable(true);
            txtMontoRecibido.setText("0");
            lblCambio.setText("Q0.00");
        } else {
            txtMontoRecibido.setDisable(false);
            txtMontoRecibido.clear();
        }
    }
    
    private void calcularCambio() {
        try {
            double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
            double cambio = montoRecibido - totalVenta;
            
            if (cambio >= 0) {
                lblCambio.setText("Q" + String.format("%.2f", cambio));
                lblCambio.setTextFill(Color.web("#27ae60"));
            } else {
                lblCambio.setText("Falta: Q" + String.format("%.2f", Math.abs(cambio)));
                lblCambio.setTextFill(Color.web("#e74c3c"));
            }
        } catch (NumberFormatException e) {
            lblCambio.setText("Q0.00");
        }
    }
    
    private void confirmarPago() {
        // Obtener y limpiar el NIT
        String nit = txtNit.getText().trim().toUpperCase();
        
        // Validar NIT
        if (!validarNit(nit)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("NIT inválido");
            alert.setHeaderText(null);
            alert.setContentText("El NIT debe tener el formato: NNNNNNN-N\nEjemplo: 1234567-8\n\nO use 'C/F' para consumidor final.");
            alert.showAndWait();
            return;
        }
        
        // Si está vacío, asignar C/F por defecto
        if (nit.isEmpty()) {
            nit = "C/F";
            txtNit.setText("C/F");
        }
        
        String formaPago = cmbFormaPago.getValue();
        
        if (formaPago.equals("Efectivo")) {
            try {
                double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
                
                if (montoRecibido < totalVenta) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Monto insuficiente");
                    alert.setHeaderText(null);
                    alert.setContentText("El monto recibido es menor al total de la venta.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Ingrese un monto válido.");
                alert.showAndWait();
                return;
            }
        }
        
        confirmado = true;
        dialog.close();
    }
    
    private void cancelarPago() {
        confirmado = false;
        dialog.close();
    }
    
    public void setDialog(Stage dialog) {
        this.dialog = dialog;
    }
    
    public void setTotalVenta(double total) {
        this.totalVenta = total;
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public String getNit() {
        String nit = txtNit.getText().trim().toUpperCase();
        if (nit.isEmpty()) {
            return "C/F";
        }
        return nit;
    }
    
    public String getFormaPago() {
        return cmbFormaPago.getValue();
    }
    
    public double getMontoRecibido() {
        try {
            return Double.parseDouble(txtMontoRecibido.getText());
        } catch (NumberFormatException e) {
            return totalVenta;
        }
    }
    
    public double getCambio() {
        double montoRecibido = getMontoRecibido();
        return montoRecibido - totalVenta;
    }
}