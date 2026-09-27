package org.kinalquickmart.system.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.utils.AlertInformation;

public class InventoryReportController implements Initializable {

    private final AlertInformation alertInfo = new AlertInformation();

    @FXML private Label lblValorTotal;
    @FXML private Label lblFecha;
    @FXML private Button btnActualizar;
    @FXML private Button btnCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        calcularValorInventario();
    }

    @FXML
    private void actualizarReporte(ActionEvent event) {
        calcularValorInventario();
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void calcularValorInventario() {
        String sql = "{CALL sp_valorInventario()}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();

        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error de conexión");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql); ResultSet rs = cs.executeQuery()) {
            BigDecimal valorTotal = BigDecimal.ZERO;
            if (rs.next()) {
                BigDecimal resultado = rs.getBigDecimal("valor_total");
                if (resultado != null) {
                    valorTotal = resultado;
                }
            }

            lblValorTotal.setText("Q" + String.format("%.2f", valorTotal));

            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            lblFecha.setText("Calculado el " + fechaHora);

        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudo calcular el valor del inventario: " + e.getMessage(), "Error");
        }
    }
}