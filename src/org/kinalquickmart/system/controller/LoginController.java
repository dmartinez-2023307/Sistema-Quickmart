package org.kinalquickmart.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.io.IOException;
import java.sql.ResultSet;
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
                    "",
                    "Campos vacíos",
                    "Por favor, ingresa tu correo y contraseña.",
                    "Validación de campos"
            );
            return;
        } else {
            alertInfo.viewAlert(
                    "ERROR",
                    "Credenciales Incorrectas",
                    "Error, USUARIO NO ENCONTRADO",
                    "Error de autenticación"
            );
            txtPassword.clear();
        }
    }
    
 

    private boolean validarCredenciales(String correo, String password) {
        String sql = "{CALL sp_validarLogin(?, ?)}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, password);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return true; // 
                }
                return false; // 
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
    
}
