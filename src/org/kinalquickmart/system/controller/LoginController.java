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

    private static final String RUTA_VISTAS = "/org/kinalquickmart/system/view/";
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
        validarCredenciales(correo, password);
    }

    private void validarCredenciales(String correo, String password) {
        String sql = "{CALL sp_validarLogin(?, ?)}";

        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, correo);
            cs.setString(2, password); 

            try (ResultSet rs = cs.executeQuery()) {
                // El Stored Procedure 'sp_validarLogin' ya valida la contraseña y que esté activo.
                // Si rs.next() es verdadero, el login es exitoso.
                
                if (!rs.next()) {
                    alertInfo.viewAlert(
                        "ERROR",
                        "Credenciales Incorrectas",
                        "El correo o la contraseña no son válidos, o la cuenta está inactiva.",
                        "Error de autenticación"
                    );
                    txtPassword.clear(); 
                    return;
                }

                // ¡LOGIN EXITOSO!
                String nombreUsuario = rs.getString("nombre_completo");
                System.out.println("✅ Login exitoso para: " + nombreUsuario);
                
                // Cambiar a la vista AdminView
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + "AdminView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("QuickMart - Panel Principal");
                    stage.show();
                } catch (IOException e) {
                    alertInfo.viewAlert("ERROR", "Error", "No se pudo cargar la vista principal.", "Error");
                    e.printStackTrace();
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudo conectar: " + e.getMessage(), "Error");
        }
    }
}