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
import org.kinalquickmart.system.config.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
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

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AlertInformation alertInfo = new AlertInformation();

    private Employee employeeToEdit;
    private Runnable tableViewUpdater;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarRoles();
    }

    private void cargarRoles() {
        List<String> roles = Arrays.asList("Cajero", "Bodeguero");  // Sin Administrador
        cmbRole.getItems().setAll(roles);
    }

    public void setTableViewUpdater(Runnable updater) {
        this.tableViewUpdater = updater;
    }

    public void loadEmployeeData(Employee employee) {
        this.employeeToEdit = employee;
        txtFullName.setText(employee.getFullName());
        txtEmail.setText(employee.getEmail());
        pwdPassword.clear(); // nunca se muestra el hash; se escribe una contraseña nueva
        cmbRole.setValue(employee.getRole());
        btnRegisterUser.setText("ACTUALIZAR");
    }

    @FXML
    private void handleRegister() {
        if (txtFullName.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || pwdPassword.getText().trim().isEmpty()
                || cmbRole.getValue() == null) {

            alertInfo.viewAlert("WARNING", "Campos incompletos", "Por favor, complete todos los campos.", "Validación");
            return;
        }

        // Validar formato de correo (opcional pero recomendado)
        String email = txtEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            alertInfo.viewAlert("WARNING", "Correo inválido", "El correo electrónico no tiene un formato válido.", "Validación");
            return;
        }

        // Verificar si el correo ya existe ANTES de intentar guardar
        boolean correoCambio = employeeToEdit == null
                || !email.equalsIgnoreCase(employeeToEdit.getEmail());
        if (correoCambio && employeeDAO.emailExists(email)) {
            alertInfo.viewAlert("WARNING", "Correo duplicado",
                    "El correo '" + email + "' ya está registrado en el sistema.\nPor favor, use un correo diferente.",
                    "Correo existente");
            txtEmail.clear();  // Limpia el campo para que el usuario lo corrija
            txtEmail.requestFocus();  // Pone el cursor en el campo
            return;
        }

        if (employeeToEdit != null) {
            // MODO EDICIÓN
            employeeToEdit.setFullName(txtFullName.getText().trim());
            employeeToEdit.setEmail(email);
            employeeToEdit.setPassword(PasswordUtil.hash(pwdPassword.getText().trim()));
            employeeToEdit.setRole(cmbRole.getValue());

            if (employeeDAO.updateEmployee(employeeToEdit)) {
                alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado actualizado correctamente.", "Actualización");
                limpiarCampos();

                if (tableViewUpdater != null) {
                    javafx.application.Platform.runLater(() -> tableViewUpdater.run());
                }

                Stage stage = (Stage) btnRegisterUser.getScene().getWindow();
                stage.close();
            } else {
                alertInfo.viewAlert("ERROR", "Error", "No se pudo actualizar el empleado.", "Error");
            }
        } else {
            // MODO CREAR
            Employee newEmployee = new Employee();
            newEmployee.setFullName(txtFullName.getText().trim());
            newEmployee.setEmail(email);
            newEmployee.setPassword(PasswordUtil.hash(pwdPassword.getText().trim()));
            newEmployee.setRole(cmbRole.getValue());
            newEmployee.setActive(true);

            if (employeeDAO.saveEmployee(newEmployee)) {
                alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado registrado correctamente.", "Registro");
                limpiarCampos();

                if (tableViewUpdater != null) {
                    javafx.application.Platform.runLater(() -> tableViewUpdater.run());
                }

                Stage stage = (Stage) btnRegisterUser.getScene().getWindow();
                stage.close();
            } else {
                alertInfo.viewAlert("ERROR", "Error", "No se pudo registrar el empleado.", "Error");
            }
        }
    }

    private void limpiarCampos() {
        txtFullName.clear();
        txtEmail.clear();
        pwdPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        txtFullName.requestFocus();
    }
}
