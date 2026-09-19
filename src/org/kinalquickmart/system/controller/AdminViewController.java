/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.utils.AlertInformation;

/**
 *
 * @author informatica
 */
public class AdminViewController {

    private final AlertInformation alertInfo = new AlertInformation();

    private ObservableList<Product> readProductos;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnManagementUser;

    @FXML
    private TextField txtSearchBar;

    @FXML
    private TableView<?> inventoryTable;

    @FXML
    private TableColumn<Product, Integer> colId;

    @FXML
    private TableColumn<Product, String> colNombre;

    @FXML
    private TableColumn<Product, String> colCategoria;

    @FXML
    private TableColumn<Product, Integer> colStock;

    @FXML
    private TableColumn<Product, String> colPrecio;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCreateProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/RegisterProductView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("QuickMart - Registro de Productos");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al abrir ventana de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editProduct() {
        System.out.println("️ Editar producto seleccionado");
        alertInfo.viewAlert(
                "",
                "Editar Producto",
                "",
                "Función en desarrollo"
        );
    }

    @FXML
    private void deleteProduct() {
        System.out.println("️ Eliminar producto seleccionado");
        alertInfo.viewAlert(
                "",
                "Eliminar Producto",
                "¿Estás seguro de eliminar el producto seleccionado?",
                "Confirmación"
        );
    }

    @FXML
    private void searchProduct() {
        String busqueda = txtSearchBar.getText().trim();
        if (busqueda.isEmpty()) {
            alertInfo.viewAlert(
                    "",
                    "Campo vacío",
                    "Ingresa un término de búsqueda",
                    "Validación"
            );
            return;
        }
        System.out.println(" Buscando: " + busqueda);
    }

    @FXML
    private void logOutUser() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/kinalquickmart/system/view/Login.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("QuickMart - Login");
            stage.show();

            System.out.println("Sesión cerrada, regreso al Login");

        } catch (IOException e) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Error de navegación",
                    "No se pudo regresar al Login: " + e.getMessage(),
                    "Error"
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void gestionUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/kinalquickmart/system/view/EmployeeManagementView.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnManagementUser.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("QuickMart - Gestión de Empleados");
            stage.show();

        } catch (IOException e) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Error de navegación",
                    "No se pudo cargar la vista de gestión de empleados: " + e.getMessage(),
                    "Error"
            );
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    private void readProduct() {
        String sql = "{CALL sp_listar_productos()}";

        try (Connection conn = ConexionDB.getInstanciaConexionDB().getConnection(); CallableStatement cs = conn.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            int contador = 0;
            while (rs.next()) {
                contador++;
                System.out.println("Producto " + contador + ": " + rs.getString("nombre"));
            }

            System.out.println("Total de productos cargados: " + contador);

        } catch (Exception e) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Error de Base de Datos",
                    "No se pudieron cargar los productos: " + e.getMessage(),
                    "Error"
            );
            e.printStackTrace();
        }
    }

}
