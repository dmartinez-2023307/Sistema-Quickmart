package org.kinalquickmart.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.utils.AlertInformation;
import org.kinalquickmart.system.utils.PasswordUtil;

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

        // 2. Validar credenciales contra la base de datos
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall("{CALL sp_validarLogin(?)}")) {
            cs.setString(1, correo);

            try (ResultSet rs = cs.executeQuery()) {
                // Correo inexistente o contraseña que no coincide con el hash guardado
                if (!rs.next() || !PasswordUtil.verificar(password, rs.getString("password"))) {
                    alertInfo.viewAlert(
                        "ERROR",
                        "Credenciales Incorrectas",
                        "El correo o la contraseña no son válidos.",
                        "Error de autenticación"
                    );
                    txtPassword.clear(); // Limpiar solo la contraseña para reintentar
                    return;
                }

                // Credenciales correctas, pero la cuenta fue desactivada
                if (!rs.getBoolean("activo")) {
                    alertInfo.viewAlert(
                        "ERROR",
                        "Usuario Inactivo",
                        "Tu cuenta está inactiva. Contacta al administrador.",
                        "Error de autenticación"
                    );
                    txtPassword.clear();
                    return;
                }

                String nombre = rs.getString("nombre_completo");
                String rol = rs.getString("rol");

                // 3. Redirigir a la vista que corresponde al rol obtenido de la BD
                navegarSegunRol(rol, nombre);
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Base de Datos",
                "No se pudo validar el usuario: " + e.getMessage(),
                "Error de conexión"
            );
        }
    }

    /** Elige la vista según el rol (ENUM de la tabla Usuario) y cambia la escena. */
    private void navegarSegunRol(String rol, String nombre) {
        String vista;
        String titulo;

        switch (rol) {
            case "Administrador" -> {
                vista = "AdminView.fxml";
                titulo = "QuickMart - Panel de Administrador";
            }
            case "Bodeguero" -> {
                vista = "WineryView.fxml";
                titulo = "QuickMart - Bodega";
            }
            case "Cajero" -> {
                vista = "CashierView.fxml";
                titulo = "QuickMart - Caja";
            }
            default -> {
                alertInfo.viewAlert(
                    "ERROR",
                    "Rol no reconocido",
                    "El rol \"" + rol + "\" no tiene una vista asignada.",
                    "Error de autenticación"
                );
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + vista));
            Parent root = loader.load();

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alertInfo.viewAlert(
                "ERROR",
                "Error de Navegación",
                "No se pudo cargar la vista de " + rol + ": " + e.getMessage(),
                "Error del sistema"
            );
        }
    }
}
 