package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.model.Category;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.utils.AlertInformation;

public class AdminViewController implements Initializable {

    private final AlertInformation alertInfo = new AlertInformation();
    private ObservableList<Product> listaProductos = FXCollections.observableArrayList();

    @FXML
    private Button btnCreate;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnManagementUser;
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Product> inventoryTable;
    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colNombre;
    @FXML
    private TableColumn<Product, String> colCategoria;
    @FXML
    private TableColumn<Product, Integer> colStock;
    @FXML
    private TableColumn<Product, BigDecimal> colPrecio;
    @FXML
    private Button btnReporteInventario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        readProduct();
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> searchProduct(null));
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
            alertInfo.viewAlert("ERROR", "Error al abrir ventana", "No se pudo abrir la ventana de registro.", "Error de navegación");
            e.printStackTrace();
        }
    }

@FXML
private void editProduct() {
    Product product = getSelectedProduct();
    if (product == null) {
        alertInfo.viewAlert("WARNING", "Advertencia", "Por favor, selecciona un producto de la tabla.", "Sin selección");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/EditProduct.fxml"));
        Parent root = loader.load();

        EditProductController controller = loader.getController();
        controller.setProduct(product);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Editar Producto: " + product.getCommercialName());
        stage.setScene(new Scene(root));
        stage.showAndWait();

        readProduct();  

    } catch (Exception e) {
        e.printStackTrace();
        alertInfo.viewAlert("ERROR", "Error", "No se pudo abrir el formulario de edición.\nDetalle: " + e.getMessage(), "Error");
    }
}

    @FXML
    private void deleteProduct() {
        Product product = getSelectedProduct();
        if (product == null) {
            alertInfo.viewAlert("WARNING", "Selección requerida", "Por favor, selecciona un producto de la tabla.", "Advertencia");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este producto?");
        alert.setContentText("Producto: " + product.getCommercialName());

        ButtonType botonEliminar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonEliminar, botonCancelar);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isEmpty() || resultado.get() != botonEliminar) {
            System.out.println("Eliminación cancelada por el usuario.");
            return;
        }

        String sql = "{CALL sp_eliminarProducto(?)}";

        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, product.getId());
                cs.executeUpdate();

                alertInfo.viewAlert("INFORMATION", "Éxito", "Producto eliminado correctamente.", "Eliminación");
                readProduct();
            }
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudo eliminar: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchProduct(ActionEvent event) {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            readProduct();
            return;
        }

        String sql = "{CALL sp_buscarProducto(?)}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();

        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchText);
            try (ResultSet rs = ps.executeQuery()) {
                listaProductos.clear();
                while (rs.next()) {
                    Product producto = new Product();
                    producto.setId(rs.getInt("id_producto"));
                    producto.setBarCode(rs.getString("codigo_barras"));
                    producto.setCommercialName(rs.getString("nombre_comercial"));
                    producto.setSalePrice(rs.getBigDecimal("precio_venta"));
                    producto.setCurrentStock(rs.getInt("stock_actual"));
                    producto.setCostPrice(BigDecimal.ZERO);

                    Category cat = new Category();
                    cat.setName(rs.getString("nombre_categoria"));
                    producto.setCategory(cat);
                    listaProductos.add(producto);
                }
                inventoryTable.setItems(listaProductos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Búsqueda", "No se pudo buscar: " + e.getMessage(), "Error");
        }
    }

    @FXML
    private void logOutUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("QuickMart - Login");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirReporteInventario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/InventoryReportView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("QuickMart - Reporte de Inventario");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error de navegación", "No se pudo abrir el reporte de inventario: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void managementUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/EmployeeManagementView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("QuickMart - Gestión de Empleados");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("commercialName"));
        colCategoria.setCellValueFactory(cellData -> {
            Category cat = cellData.getValue().getCategory();
            String nombreCat = (cat != null && cat.getName() != null) ? cat.getName() : "Sin categoría";
            return new javafx.beans.property.SimpleStringProperty(nombreCat);
        });
        colStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
    }

    private void readProduct() {
        String sql = "{CALL sp_listarProductos()}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();

        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql); ResultSet rs = cs.executeQuery()) {
            listaProductos.clear();
            while (rs.next()) {
                Product producto = new Product();
                producto.setId(rs.getInt("id_producto"));
                producto.setBarCode(rs.getString("codigo_barras"));
                producto.setCommercialName(rs.getString("nombre_comercial"));
                producto.setCostPrice(rs.getBigDecimal("precio_costo"));
                producto.setSalePrice(rs.getBigDecimal("precio_venta"));
                producto.setCurrentStock(rs.getInt("stock_actual"));

                Category cat = new Category();
                cat.setId(rs.getInt("id_categoria"));
                cat.setName(rs.getString("nombre_categoria"));
                producto.setCategory(cat);

                listaProductos.add(producto);
            }
            inventoryTable.setItems(listaProductos);
            System.out.println(" Total de productos cargados en tabla: " + listaProductos.size());
        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudieron cargar los productos: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    private Product getSelectedProduct() {
        return inventoryTable.getSelectionModel().getSelectedItem();
    }

    public void configurarPermisos(String rol) {
        if (rol == null) {
            return;
        }

        switch (rol) {
            case "Bodeguero":
                btnReporteInventario.setVisible(false);
                btnReporteInventario.setManaged(false);

                btnDelete.setVisible(false);
                btnDelete.setManaged(false);

                btnManagementUser.setVisible(false);
                btnManagementUser.setManaged(false);
                break;

            case "Administrador":
            default:
                break;
        }
    }
}
