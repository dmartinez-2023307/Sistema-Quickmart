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
import javafx.stage.Stage;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.service.EmployeeService;
import org.kinalquickmart.system.utils.AlertInformation;
import org.kinalquickmart.system.utils.PasswordUtil;

public class RegisterEmployeeController implements Initializable {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private Button btnRegisterUser;

    private final EmployeeService empService = new EmployeeService();
    private final AlertInformation alertInfo = new AlertInformation();

    private Employee employeeToEdit;
    private Runnable tableViewUpdater;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarRoles();
    }

    private void cargarRoles() {
        cmbRole.getItems().setAll(Arrays.asList("Cajero", "Bodeguero"));
    }

    public void setTableViewUpdater(Runnable updater) {
        this.tableViewUpdater = updater;
    }

    public void loadEmployeeData(Employee employee) {
        this.employeeToEdit = employee;
        txtFullName.setText(employee.getFullName());
        txtEmail.setText(employee.getEmail());
        pwdPassword.clear();
        cmbRole.setValue(employee.getRole());
        btnRegisterUser.setText("ACTUALIZAR");
    }

    @FXML
    private void handleRegister() {
        if (txtFullName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()
                || pwdPassword.getText().trim().isEmpty() || cmbRole.getValue() == null) {
            alertInfo.viewAlert("WARNING", "Campos incompletos", "Por favor, complete todos los campos.", "Validación");
            return;
        }

        String email = txtEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            alertInfo.viewAlert("WARNING", "Correo inválido", "El correo electrónico no tiene un formato válido.", "Validación");
            return;
        }

        if (employeeToEdit != null) {
            employeeToEdit.setFullName(txtFullName.getText().trim());
            employeeToEdit.setEmail(email);
            employeeToEdit.setPassword(PasswordUtil.hash(pwdPassword.getText().trim()));
            employeeToEdit.setRole(cmbRole.getValue());

            if (empService.updateEmployee(employeeToEdit)) {
                alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado actualizado correctamente.", "Actualización");
                cerrarYActualizar();
            } else {
                alertInfo.viewAlert("ERROR", "Error", "No se pudo actualizar el empleado.", "Error");
            }
        } else {
            Employee newEmployee = new Employee();
            newEmployee.setFullName(txtFullName.getText().trim());
            newEmployee.setEmail(email);
            newEmployee.setPassword(PasswordUtil.hash(pwdPassword.getText().trim()));
            newEmployee.setRole(cmbRole.getValue());
            newEmployee.setActive(true);

            if (empService.registerEmployee(newEmployee)) {
                alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado registrado correctamente.", "Registro");
                cerrarYActualizar();
            } else {
                alertInfo.viewAlert("WARNING", "Correo duplicado", "El correo ya está registrado.", "Error");
                txtEmail.clear();
                txtEmail.requestFocus();
            }
        }
    }

    private void cerrarYActualizar() {
        limpiarCampos();
        if (tableViewUpdater != null) {
            javafx.application.Platform.runLater(() -> tableViewUpdater.run());
        }
        Stage stage = (Stage) btnRegisterUser.getScene().getWindow();
        stage.close();
    }

    private void limpiarCampos() {
        txtFullName.clear();
        txtEmail.clear();
        pwdPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        txtFullName.requestFocus();
    }
}
