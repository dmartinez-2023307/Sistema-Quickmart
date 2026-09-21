package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.utils.AlertInformation;

public class LoginController {

    private final AlertInformation alertInfo = new AlertInformation();

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private void initialize() {
    }

    @FXML
    private void login() {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            alertInfo.viewAlert(
                "WARNING",
                "Campos vacíos",
                "Por favor, ingresa tu correo y contraseña.",
                "Validación de campos"
            );
            return;
        }

        validarCredenciales(correo, password);
    }

    private void validarCredenciales(String correo, String password) {
        String sql = "{CALL sp_validarLogin(?, ?)}";
        
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) {
            alertInfo.viewAlert(
                "ERROR", 
                "Error de Sistema", 
                "No hay conexión a la base de datos.", 
                "Error de conexión"
            );
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, correo);
            cs.setString(2, password);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String rol = rs.getString("rol");
                    String nombre = rs.getString("nombre_completo");
                    
                    alertInfo.viewAlert(
                        "INFORMATION",
                        "Inicio de Sesión Exitoso",
                        "¡Bienvenido al sistema, " + nombre + "!",
                        "Éxito"
                    );
                    
                    if ("Administrador".equalsIgnoreCase(rol)) {
                        navegarAdminView();
                    } else if ("Cajero".equalsIgnoreCase(rol)) {
                        navegarCashierView();
                    } else {
                        alertInfo.viewAlert(
                            "ERROR",
                            "Rol no permitido",
                            "Tu rol de usuario no tiene permisos para acceder al sistema.",
                            "Acceso denegado"
                        );
                    }
                } else {
                    alertInfo.viewAlert(
                        "ERROR",
                        "Credenciales Incorrectas",
                        "El correo o la contraseña no son válidos.",
                        "Error de autenticación"
                    );
                    txtPassword.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Base de Datos",
                "No se pudo validar el usuario: " + e.getMessage(),
                "Error del sistema"
            );
        }
    }

    private void navegarAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/AdminView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setTitle("QuickMart - Panel de Administrador");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Navegación",
                "No se pudo cargar el panel de administrador.",
                "Error del sistema"
            );
        }
    }

    private void navegarCashierView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/CashierView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setTitle("QuickMart - Punto de Venta");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Navegación",
                "No se pudo cargar la vista de cajero.",
                "Error del sistema"
            );
        }
    }
}