package org.kinalquickmart.system.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.kinalquickmart.system.config.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.utils.AlertInformation;

public class RegisterEmployeeController implements Initializable {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private Button btnRegister;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AlertInformation alertInfo = new AlertInformation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarRoles();
    }

    private void cargarRoles() {
        // Los roles según tu base de datos
        List<String> roles = Arrays.asList("Administrador", "Bodeguero", "Cajero");
        cmbRole.getItems().setAll(roles);
    }

    @FXML
    private void handleRegister() {
        if (camposVacios()) {
            alertInfo.viewAlert(
                    "WARNING",
                    "Campos Incompletos",
                    "Por favor, complete todos los campos obligatorios.",
                    "Validación de campos"
            );
            return;
        }

        if (!validarCorreo(txtEmail.getText().trim())) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Correo Inválido",
                    "El correo debe tener un formato válido (ejemplo: usuario@dominio.com).",
                    "Validación de correo"
            );
            return;
        }

        if (txtPassword.getText().trim().length() < 5) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Contraseña Inválida",
                    "La contraseña debe tener al menos 5 caracteres.",
                    "Validación de contraseña"
            );
            return;
        }

        Employee newEmployee = new Employee();
        newEmployee.setFullName(txtFullName.getText().trim());
        newEmployee.setEmail(txtEmail.getText().trim());
        newEmployee.setPassword(txtPassword.getText().trim());
        newEmployee.setRole(cmbRole.getValue());

        if (employeeDAO.saveEmployee(newEmployee)) {
            alertInfo.viewAlert(
                    "INFORMATION",
                    "Registro Exitoso",
                    "El empleado ha sido registrado correctamente en el sistema.",
                    "Éxito"
            );
            limpiarCampos();
        } else {
            alertInfo.viewAlert(
                    "ERROR",
                    "Error de Registro",
                    "El correo ya está registrado. Por favor, utilice un correo diferente.",
                    "Error de base de datos"
            );
            txtEmail.requestFocus();
            txtEmail.selectAll();
        }
    }

    @FXML
    private void limpiarCampos() {
        txtFullName.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        txtFullName.requestFocus();
    }

    private boolean camposVacios() {
        return txtFullName.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || txtPassword.getText().trim().isEmpty()
                || cmbRole.getValue() == null;
    }

    private boolean validarCorreo(String correo) {
        // Validación simple de formato de correo
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return correo.matches(regex);
    }
}
