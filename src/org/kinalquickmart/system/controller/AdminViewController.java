package org.kinalquickmart.system.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
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
import org.kinalquickmart.system.model.Category;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.utils.AlertInformation;

public class AdminViewController implements Initializable {

    private final AlertInformation alertInfo = new AlertInformation();
    private ObservableList<Product> listaProductos;

    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnLogOut;
    @FXML private Button btnManagementUser;
    @FXML private Button btnSearch;
    
    @FXML private TextField txtSearch;
    
    @FXML private TableView<Product> inventoryTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, BigDecimal> colPrice;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ AdminViewController inicializado correctamente");
        configurarTabla();
        readProduct();
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
    private void handleEditProduct() {
        alertInfo.viewAlert("INFORMATION", "Editar Producto", "Función en desarrollo", "Aviso");
    }

    @FXML
    private void handleDeleteProduct() {
        alertInfo.viewAlert("WARNING", "Eliminar Producto", "¿Estás seguro de eliminar el producto seleccionado?", "Confirmación");
    }

    @FXML
    private void searchProduct() {
        String busqueda = txtSearch.getText().trim();
        if (busqueda.isEmpty()) {
            alertInfo.viewAlert("WARNING", "Campo vacío", "Ingresa un término de búsqueda", "Validación");
            return;
        }
        System.out.println("🔍 Buscando en base de datos: " + busqueda);
        // TODO: Implementar filtro llamando a sp_buscarProducto(busqueda)
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
        // Los strings deben coincidir EXACTAMENTE con los nombres de los atributos en Product.java
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("commercialName"));
        
        // Como 'category' es un objeto, extraemos su nombre para mostrarlo como texto en la columna
        colCategory.setCellValueFactory(cellData -> {
            Category cat = cellData.getValue().getCategory();
            String nombreCat = (cat != null && cat.getName() != null) ? cat.getName() : "Sin categoría";
            return new javafx.beans.property.SimpleStringProperty(nombreCat);
        });
        
        colStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
    }

    private void readProduct() {
        // Nombre exacto del procedimiento almacenado definido en tu DDL
        String sql = "{CALL sp_listarProductos()}";

        // 1. OBTENER LA CONEXIÓN FUERA DEL TRY
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        // 2. SOLO CallableStatement y ResultSet van DENTRO del try-with-resources
        try (CallableStatement cs = conn.prepareCall(sql); 
             ResultSet rs = cs.executeQuery()) {

            listaProductos = FXCollections.observableArrayList();
            
            while (rs.next()) {
                Product producto = new Product();
                
                // Usamos los setters EXACTOS de tu clase Product.java
                producto.setId(rs.getInt("id_producto"));
                producto.setBarCode(rs.getString("codigo_barras"));
                producto.setCommercialName(rs.getString("nombre_comercial"));
                producto.setCostPrice(rs.getBigDecimal("precio_costo"));
                producto.setSalePrice(rs.getBigDecimal("precio_venta"));
                producto.setCurrentStock(rs.getInt("stock_actual"));
                
                // Creamos un objeto Category temporal con el nombre que nos devuelve el JOIN del SP
                Category cat = new Category();
                cat.setName(rs.getString("nombre_categoria")); 
                producto.setCategory(cat);
                
                listaProductos.add(producto);
            }
            
            // Asignamos la lista a la tabla
            inventoryTable.setItems(listaProductos);
            System.out.println("✅ Total de productos cargados en tabla: " + listaProductos.size());

        } catch (Exception e) {
            alertInfo.viewAlert("ERROR", "Error de Base de Datos", "No se pudieron cargar los productos: " + e.getMessage(), "Error");
            e.printStackTrace();
        }
    }
}