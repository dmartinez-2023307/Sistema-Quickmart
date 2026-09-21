package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.kinalquickmart.system.model.Category;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.utils.AlertInformation;

public class AdminViewController implements Initializable {

    private final AlertInformation alertInfo = new AlertInformation();
    private ObservableList<Product> listaProductos = FXCollections.observableArrayList();

    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnLogOut;
    @FXML private Button btnManagementUser;
    @FXML private Button btnSearch;
    
    @FXML private TextField txtSearch;
    
    @FXML private TableView<Product> inventoryTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colNombre;
    @FXML private TableColumn<Product, String> colCategoria;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, BigDecimal> colPrecio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        readProduct(); // Cargar datos al iniciar
        
        // Búsqueda en tiempo real
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchProduct(null);
        });
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
        if (product == null) return;

        String catName = (product.getCategory() != null) ? product.getCategory().getName() : "Sin categoría";
        
        alertInfo.viewAlert("INFORMATION", "Editar Producto",
            "ID: " + product.getId() + "\n" +
            "Nombre: " + product.getCommercialName() + "\n" +
            "Categoría: " + catName + "\n" +
            "Stock: " + product.getCurrentStock() + "\n" +
            "Precio Venta: $" + product.getSalePrice() + "\n\n" +
            "(El formulario de edición se abrirá aquí)",
            "Editar");
    }

    @FXML
    private void deleteProduct() {
        Product product = getSelectedProduct();
        if (product == null) return;

        alertInfo.viewAlert("WARNING", "Confirm Deletion",
            "Are you sure you want to delete the product: " + product.getCommercialName() + "?",
            "Delete Product");

        String sql = "{CALL sp_eliminarProducto(?)}";

        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement cs = conn.prepareCall(sql);
            cs.setInt(1, product.getId());
            cs.executeUpdate();

            alertInfo.viewAlert("INFORMATION", "Success",
                "Product deleted successfully.", "Deletion");

            readProduct(); // Recargar la tabla
            
            cs.close();

        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Database Error",
                "Could not delete: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchProduct(ActionEvent event) {
        String searchText = txtSearch.getText().trim();
        
        if (searchText.isEmpty()) {
            readProduct(); // Si está vacío, recargar todo
            return;
        }
        
        String sql = "{CALL sp_buscarProducto(?)}";
        // 1. Obtener conexión FUERA del try
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        // 2. Solo el PreparedStatement va en el try
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchText);
            
            // 3. El ResultSet en un try anidado
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
                System.out.println("✅ Productos encontrados: " + listaProductos.size());
            }
        } catch (SQLException e) {
            System.err.println(" Error al buscar producto: " + e.getMessage());
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
            stage.show();
        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error de navegación", "No se pudo regresar al Login: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }

    @FXML
    private void managementUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/EmployeeManagementView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnManagementUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("QuickMart - Gestión de Empleados");
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
        // 1. Obtener conexión FUERA del try
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        // 2. Solo CallableStatement y ResultSet van en el try
        try (CallableStatement cs = conn.prepareCall(sql); 
             ResultSet rs = cs.executeQuery()) {

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
}