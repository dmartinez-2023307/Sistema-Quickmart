package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;  // ✅ ESTE ES EL QUE FALTA
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.kinalquickmart.system.config.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.utils.AlertInformation;

public class EmployeeManagementController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private TableView<Employee> tblEmployees;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colFullName;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colRole;
  @FXML private TableColumn<Employee, String> colActive;
    @FXML private Button btnEdit;
    @FXML private Button btnFire;
    @FXML private Button btnRegisterNew;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AlertInformation alertInfo = new AlertInformation();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarEmpleados();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        colActive.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().isActive();
            String estado = activo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
        
        tblEmployees.setItems(employeeList);
    }

    private void cargarEmpleados() {
        employeeList.setAll(employeeDAO.getAllEmployees());
    }

    @FXML
    private void handleSearch() {
        String texto = txtSearch.getText().trim();
        
        if (texto.isEmpty()) {
            cargarEmpleados();
            return;
        }

        List<Employee> resultados = employeeDAO.searchEmployees(texto);
        employeeList.setAll(resultados);
    }

    @FXML
    private void handleEdit() {
        Employee selected = tblEmployees.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            alertInfo.viewAlert(
                "WARNING",
                "Selección requerida",
                "Por favor, selecciona un empleado de la tabla.",
                "Advertencia"
            );
            return;
        }

        // Aquí podrías abrir un formulario de edición
        alertInfo.viewAlert(
            "INFORMATION",
            "Editar Empleado",
            "ID: " + selected.getId() + "\n" +
            "Nombre: " + selected.getFullName() + "\n" +
            "Correo: " + selected.getEmail() + "\n" +
            "Rol: " + selected.getRole() + "\n" +
            "Estado: " + (selected.isActive() ? "Activo" : "Inactivo") + "\n\n" +
            "(El formulario de edición se implementará aquí)",
            "Editar Empleado"
        );
    }

    @FXML
    private void handleFire() {
        Employee selected = tblEmployees.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            alertInfo.viewAlert(
                "WARNING",
                "Selección requerida",
                "Por favor, selecciona un empleado de la tabla.",
                "Advertencia"
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Despido");
        alert.setHeaderText("Despedir Empleado");
        alert.setContentText("¿Estás seguro de que deseas despedir a " + selected.getFullName() + "?\n\nEsta acción desactivará su cuenta y no podrá iniciar sesión.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (employeeDAO.fireEmployee(selected.getId())) {
                alertInfo.viewAlert(
                    "INFORMATION",
                    "Éxito",
                    "El empleado ha sido despedido correctamente.",
                    "Despido"
                );
                cargarEmpleados();
            } else {
                alertInfo.viewAlert(
                    "ERROR",
                    "Error",
                    "No se pudo despedir al empleado.",
                    "Error de base de datos"
                );
            }
        }
    }

@FXML
private void handleRegisterNew() {
    try {
        // Verifica la ruta exacta del archivo
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/org/kinalquickmart/system/view/RegisterEmployeeView.fxml")
        );
        
        if (loader.getLocation() == null) {
            alertInfo.viewAlert(
                "ERROR",
                "Archivo no encontrado",
                "No se encuentra RegisterEmployeeView.fxml\n\nRuta buscada: /org/kinalquickmart/system/view/RegisterEmployeeView.fxml",
                "Error"
            );
            return;
        }
        
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("QuickMart - Registrar Nuevo Empleado");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        
    } catch (IOException e) {
        alertInfo.viewAlert(
            "ERROR",
            "Error al abrir ventana",
            "No se pudo abrir: " + e.getMessage(),
            "Error de navegación"
        );
        e.printStackTrace();
    }
}
}