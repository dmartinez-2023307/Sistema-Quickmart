package org.kinalquickmart.system.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.service.EmployeeService;
import org.kinalquickmart.system.utils.AlertInformation;

public class LoginController {

    private static final String RUTA_VISTAS = "/org/kinalquickmart/system/view/";
    private final AlertInformation alertInfo = new AlertInformation();
    private final EmployeeService empService = new EmployeeService();

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private void login() {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            alertInfo.viewAlert("WARNING", "Campos vacíos", "Por favor, ingresa tu correo y contraseña.", "Validación");
            return;
        }

        // Delegamos la validación al Service
        Employee usuario = empService.login(correo, password);

        if (usuario != null) {
            System.out.println("✅ Login exitoso para: " + usuario.getFullName() + " | Rol: " + usuario.getRole());
            navegarSegunRol(usuario);
        } else {
            alertInfo.viewAlert("ERROR", "Acceso denegado", "Correo o contraseña incorrectos, o usuario inactivo.", "Error");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    private void navegarSegunRol(Employee usuario) {
        // Declaramos la variable ANTES del try para que el catch también pueda verla
        String vistaDestino = "Cajero".equals(usuario.getRole()) ? "CashierView.fxml" : "AdminView.fxml";
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + vistaDestino));
            Parent root = loader.load();

            // Pasar datos al controller destino si es necesario
            if ("Cajero".equals(usuario.getRole())) {
                CashierController cajeroController = loader.getController();
                cajeroController.setCajeroData(usuario.getId(), usuario.getFullName());
            } else {
                AdminViewController adminController = loader.getController();
                adminController.configurarPermisos(usuario.getRole());
            }

            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("QuickMart - " + usuario.getRole());
            stage.show();

        } catch (IOException e) {
            // Ahora sí funciona, porque vistaDestino existe en este ámbito
            alertInfo.viewAlert("ERROR", "Error", "No se pudo cargar la vista: " + vistaDestino, "Error");
            e.printStackTrace();
        }
    }
}