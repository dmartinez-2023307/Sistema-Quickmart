package org.kinalquickmart.system.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.kinalquickmart.system.config.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.utils.AlertInformation;

public class RegisterEmployeeController implements Initializable {

    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Button btnRegister;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AlertInformation alertInfo = new AlertInformation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarRoles();
    }

    private void cargarRoles() {
        List<String> roles = Arrays.asList("Administrador", "Cajero", "Bodeguero");
        cmbRole.getItems().setAll(roles);
    }

    @FXML
    private void handleRegister() {
        if (txtFullName.getText().trim().isEmpty() || 
            txtEmail.getText().trim().isEmpty() || 
            txtPassword.getText().trim().isEmpty() || 
            cmbRole.getValue() == null) {
            
            alertInfo.viewAlert("WARNING", "Campos incompletos", "Por favor, complete todos los campos.", "Validación");
            return;
        }

        Employee newEmployee = new Employee();
        newEmployee.setFullName(txtFullName.getText().trim());
        newEmployee.setEmail(txtEmail.getText().trim());
        newEmployee.setPassword(txtPassword.getText().trim());
        newEmployee.setRole(cmbRole.getValue());
        newEmployee.setActive(true);

        if (employeeDAO.saveEmployee(newEmployee)) {
            alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado registrado correctamente.", "Registro");
            limpiarCampos();
        } else {
            alertInfo.viewAlert("ERROR", "Error", "No se pudo registrar. El correo ya existe.", "Error");
        }
    }

    private void limpiarCampos() {
        txtFullName.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        txtFullName.requestFocus();
    }
}