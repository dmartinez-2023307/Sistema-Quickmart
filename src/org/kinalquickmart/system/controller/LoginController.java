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
        String sql = "{CALL sp_validarLogin(?)}";

        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, correo);

            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) {
                    alertInfo.viewAlert("ERROR", "Usuario no encontrado", "El correo no está registrado o la cuenta está inactiva.", "Error de autenticación");
                    return;
                }

                // 1️⃣ OBTENER EL ID DEL USUARIO 
                // (Asegúrate de que tu procedimiento almacenado sp_validarLogin devuelva la columna "id_usuario")
                int idUsuario = rs.getInt("id_usuario"); 
                String nombreUsuario = rs.getString("nombre_completo");
                String rolUsuario = rs.getString("rol");
                
                System.out.println("✅ Login exitoso para: " + nombreUsuario + " | Rol: " + rolUsuario);

                String vistaDestino = "Cajero".equals(rolUsuario) ? "CashierView.fxml" : "AdminView.fxml";

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + vistaDestino));
                    Parent root = loader.load();

                    // 2️⃣ PASAR LOS DATOS AL CONTROLADOR CORRESPONDIENTE
                    if ("Cajero".equals(rolUsuario)) {
                        // Si es cajero, le pasamos el ID y el nombre para la factura
                        CashierController cajeroController = loader.getController();
                        cajeroController.setCajeroData(idUsuario, nombreUsuario);
                    } else {
                        // Si es admin, configuramos los permisos como ya lo tenías
                        AdminViewController adminController = loader.getController();
                        adminController.configurarPermisos(rolUsuario);
                    }

                    Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("QuickMart - " + rolUsuario);
                    stage.show();
                    
                } catch (IOException e) {
                    alertInfo.viewAlert("ERROR", "Error", "No se pudo cargar la vista: " + vistaDestino, "Error");
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudo conectar: " + e.getMessage(), "Error");
        }
    }

    private void navegarAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + "AdminView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + "CashierView.fxml"));
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