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

    // --- LÓGICA DE VALIDACIÓN ROBUSTA ---
    private void validarCredenciales(String correo, String password) {
        String sql = "{CALL sp_validarLogin(?, ?)}";

        // 1. Obtener la conexión FUERA del try para que NO se cierre automáticamente
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        // 2. Solo el CallableStatement y ResultSet van DENTRO del try
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, correo);
            cs.setString(2, password); // Parámetro necesario para el SP

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
                        "