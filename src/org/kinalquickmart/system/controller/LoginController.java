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
        // Código de inicialización si es necesario
    }

    @FXML
    private void login() {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Validar que los campos no estén vacíos
        if (correo.isEmpty() || password.isEmpty()) {
            alertInfo.viewAlert(
                "WARNING",
                "Campos vacíos",
                "Por favor, ingresa tu correo y contraseña.",
                "Validación de campos"
            );
            return;
        }

        // 2. Validar credenciales en la base de datos
        if (validarCredenciales(correo, password)) {
            alertInfo.viewAlert(
                "INFORMATION",
                "Inicio de Sesión Exitoso",
                "¡Bienvenido al sistema!",
                "Éxito"
            );
            navegarAdminView();
        } else {
            alertInfo.viewAlert(
                "ERROR",
                "Credenciales Incorrectas",
                "El correo o la contraseña no son válidos, o la cuenta está inactiva.",
                "Error de autenticación"
            );
            txtPassword.clear(); 
        }
    }

    // --- AQUÍ ESTÁ LA CORRECCIÓN CLAVE ---
    private boolean validarCredenciales(String correo, String password) {
        String sql = "{CALL sp_validarLogin(?, ?)}";


        // 1. Obtener la conexión FUERA del try para que NO se cierre automáticamente
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return false;
        }


        // 2. Solo el CallableStatement y ResultSet van DENTRO del try
        try (CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, correo);
            cs.setString(2, password);

            try (ResultSet rs = cs.executeQuery()) {
                // Si rs.next() es true, encontró el usuario y la contraseña coincide
                return rs.next(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Base de Datos",
                "No se pudo conectar: " + e.getMessage(),
                "Error de conexión"
            );
            return false;
        }
    }

    private void navegarAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/AdminView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setTitle("QuickMart - Panel de Administrador");
            stage.setScene(scene);
            stage.setResizable(false); // Opcional: evita que el usuario redimensione la ventana
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Navegación",
                "No se pudo cargar el panel de administrador: " + e.getMessage(),
                "Error del sistema"
            );
        }
    }
}