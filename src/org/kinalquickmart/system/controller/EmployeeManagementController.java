package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import org.kinalquickmart.system.config.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.utils.AlertInformation;

public class EmployeeManagementController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDespedir;
    @FXML
    private Button btnRegisterNew;
    @FXML
    private TableView<Employee> tblEmployees;
    @FXML
    private TableColumn<Employee, Integer> colId;
    @FXML
    private TableColumn<Employee, String> colFullName;
    @FXML
    private TableColumn<Employee, String> colEmail;
    @FXML
    private TableColumn<Employee, String> colRole;
    @FXML
    private TableColumn<Employee, String> colActive;

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

    public void cargarEmpleados() {
        employeeList.setAll(employeeDAO.getAllEmployees());
    }

    @FXML
    private void handleSearch() {
        String texto = txtSearch.getText().trim();

        if (texto.isEmpty()) {
            cargarEmpleados();
            return;
        }

        employeeList.setAll(employeeDAO.searchEmployees(texto));
    }

    @FXML
    private void handleRegisterNew() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/kinalquickmart/system/view/RegisterEmployeeView.fxml")
            );

            Parent root = loader.load();
            RegisterEmployeeController registerController = loader.getController();

            // Pasar el método para actualizar la tabla
            registerController.setTableViewUpdater(this::cargarEmpleados);

            Stage stage = new Stage();
            stage.setTitle("QuickMart - Registrar Nuevo Empleado");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error al abrir ventana", "No se pudo abrir: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        Employee selected = tblEmployees.getSelectionModel().getSelectedItem();

        if (selected == null) {
            alertInfo.viewAlert("WARNING", "Sin selección", "Seleccione un empleado de la tabla para editar.", "Advertencia");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/kinalquickmart/system/view/RegisterEmployeeView.fxml")
            );

            Parent root = loader.load();
            RegisterEmployeeController controller = loader.getController();

            // Cargar los datos del empleado en los campos
            controller.loadEmployeeData(selected);

            // Cuando se guarde, actualizar la tabla
            controller.setTableViewUpdater(this::cargarEmpleados);

            Stage stage = new Stage();
            stage.setTitle("QuickMart - Editar Empleado");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error", "No se pudo abrir la ventana de edición.", "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDespedir() {
        Employee selected = tblEmployees.getSelectionModel().getSelectedItem();

        if (selected == null) {
            alertInfo.viewAlert("WARNING", "Sin selección", "Seleccione un empleado de la tabla para eliminar.", "Advertencia");
            return;
        }

        boolean confirmar = alertInfo.viewConfirm(
                "CONFIRMAR",
                "Eliminar empleado",
                "¿Está seguro que desea ELIMINAR permanentemente a " + selected.getFullName() + "?",
                "Confirmación"
        );

        if (confirmar) {
            // Eliminar permanentemente
            if (employeeDAO.deleteEmployee(selected.getId())) {
                alertInfo.viewAlert("INFORMATION", "Éxito", "Empleado eliminado correctamente.", "Eliminación");
                cargarEmpleados();
            } else {
                alertInfo.viewAlert("ERROR", "Error", "No se pudo eliminar al empleado.", "Error");
            }
        }
    }
}
