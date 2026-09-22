package org.kinalquickmart.system.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.model.TicketItem;
import org.kinalquickmart.system.utils.AlertInformation;

public class CashierController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private TableView<Product> tblCatalog;
    @FXML private TableColumn<Product, String> colCatalogCode;
    @FXML private TableColumn<Product, String> colCatalogName;
    @FXML private TableColumn<Product, BigDecimal> colCatalogPrice;
    @FXML private TableColumn<Product, Integer> colCatalogStock;
    @FXML private TableView<TicketItem> tblTicket;
    @FXML private TableColumn<TicketItem, String> colTicketName;
    @FXML private TableColumn<TicketItem, BigDecimal> colTicketPrice;
    @FXML private TableColumn<TicketItem, Integer> colTicketQty;
    @FXML private TableColumn<TicketItem, BigDecimal> colTicketSubtotal;
    @FXML private Label lblTotal;
    @FXML private Button btnFinalize;

    private final AlertInformation alertInfo = new AlertInformation();
    private final ObservableList<TicketItem> ticketItems = FXCollections.observableArrayList();
    private final ObservableList<Product> catalogProducts = FXCollections.observableArrayList();
    private BigDecimal total = BigDecimal.ZERO;
    private Integer currentUserId = 1; // TODO: Obtener del login

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnasCatalogo();
        configurarColumnasTicket();
        tblTicket.setItems(ticketItems);
        tblCatalog.setItems(catalogProducts);
        
        // Cargar todos los productos al iniciar
        cargarProductosCatalogo();
        
        // Agregar evento de doble clic en la tabla
        tblCatalog.setOnMouseClicked(this::handleTableClick);
        
        txtSearch.requestFocus();
    }

    private void configurarColumnasCatalogo() {
        colCatalogCode.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        colCatalogName.setCellValueFactory(new PropertyValueFactory<>("commercialName"));
        colCatalogPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colCatalogStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
    }

    private void configurarColumnasTicket() {
        colTicketName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colTicketPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTicketQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTicketSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void cargarProductosCatalogo() {
        String sql = "{CALL sp_listarProductos()}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            catalogProducts.clear();
            
            while (rs.next()) {
                Product producto = new Product();
                producto.setId(rs.getInt("id_producto"));
                producto.setBarCode(rs.getString("codigo_barras"));
                producto.setCommercialName(rs.getString("nombre_comercial"));
                producto.setSalePrice(rs.getBigDecimal("precio_venta"));
                producto.setCurrentStock(rs.getInt("stock_actual"));
                
                catalogProducts.add(producto);
            }
            
            System.out.println("✅ Productos cargados en catálogo: " + catalogProducts.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Carga", "No se pudieron cargar los productos: " + e.getMessage(), "Error");
        }
    }

    @FXML
    private void handleSearch() {
        String texto = txtSearch.getText().trim();
        
        if (texto.isEmpty()) {
            alertInfo.viewAlert(
                "WARNING",
                "Campo vacío",
                "Por favor, ingresa un código o nombre de producto.",
                "Validación"
            );
            return;
        }

        buscarProductos(texto);
        txtSearch.clear();
        txtSearch.requestFocus();
    }

    private void buscarProductos(String texto) {
        String sql = "{CALL sp_buscarProducto(?)}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, texto);
            
            try (ResultSet rs = cs.executeQuery()) {
                catalogProducts.clear();
                
                while (rs.next()) {
                    Product producto = new Product();
                    producto.setId(rs.getInt("id_producto"));
                    producto.setBarCode(rs.getString("codigo_barras"));
                    producto.setCommercialName(rs.getString("nombre_comercial"));
                    producto.setSalePrice(rs.getBigDecimal("precio_venta"));
                    producto.setCurrentStock(rs.getInt("stock_actual"));
                    
                    catalogProducts.add(producto);
                }
                
                if (catalogProducts.isEmpty()) {
                    alertInfo.viewAlert(
                        "WARNING",
                        "Producto no encontrado",
                        "No se encontraron productos que coincidan con: " + texto,
                        "Búsqueda"
                    );
                } else {
                    System.out.println("✅ Productos encontrados: " + catalogProducts.size());
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar producto: " + e.getMessage());
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Búsqueda", "No se pudo buscar: " + e.getMessage(), "Error");
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Doble clic
            Product selectedProduct = tblCatalog.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                agregarAlTicket(selectedProduct);
            }
        }
    }

    @FXML
    private void handleAddToTicket() {
        Product selectedProduct = tblCatalog.getSelectionModel().getSelectedItem();
        
        if (selectedProduct == null) {
            alertInfo.viewAlert(
                "WARNING",
                "Selección requerida",
                "Por favor, selecciona un producto del catálogo (doble clic).",
                "Validación"
            );
            return;
        }

        agregarAlTicket(selectedProduct);
    }

    private void agregarAlTicket(Product selectedProduct) {
        if (selectedProduct.getCurrentStock() == 0) {
            alertInfo.viewAlert(
                "ERROR",
                "Producto agotado",
                "El producto '" + selectedProduct.getCommercialName() + "' está agotado. No hay stock disponible.",
                "Error de stock"
            );
            return;
        }

        // Verificar si ya está en el ticket
        TicketItem existingItem = ticketItems.stream()
            .filter(item -> item.getProduct().getId() == selectedProduct.getId())
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            // Verificar si hay suficiente stock
            if (existingItem.getQuantity() >= selectedProduct.getCurrentStock()) {
                alertInfo.viewAlert(
                    "WARNING",
                    "Stock insuficiente",
                    "No hay suficiente stock disponible. Solo quedan " + 
                    selectedProduct.getCurrentStock() + " unidades.",
                    "Validación de stock"
                );
                return;
            }
            existingItem.increaseQuantity(1);
            tblTicket.refresh();
        } else {
            ticketItems.add(new TicketItem(selectedProduct, 1));
        }

        actualizarTotal();
        tblCatalog.getSelectionModel().clearSelection();
        txtSearch.requestFocus();
    }

    private void actualizarTotal() {
        total = ticketItems.stream()
            .map(TicketItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        lblTotal.setText("TOTAL Q" + String.format("%.2f", total));
    }

    @FXML
    private void handleFinalize() {
        if (ticketItems.isEmpty()) {
            alertInfo.viewAlert(
                "WARNING",
                "Ticket vacío",
                "No hay productos en el ticket. Agrega productos antes de finalizar.",
                "Validación"
            );
            return;
        }

        // Confirmar la venta
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Venta");
        confirmAlert.setHeaderText("¿Estás seguro de finalizar esta venta?");
        confirmAlert.setContentText(String.format("Total: Q%.2f\nProductos: %d", total, ticketItems.size()));

        java.util.Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // Usuario canceló
        }

        // Procesar la venta
        procesarVenta();
    }

    private void procesarVenta() {
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try {
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Crear la venta
            Integer idVenta = crearVenta(conn);
            
            if (idVenta == null) {
                throw new Exception("No se pudo crear la venta");
            }

            // 2. Agregar detalles y descontar stock
            for (TicketItem item : ticketItems) {
                agregarDetalleVenta(conn, idVenta, item);
                descontarStock(conn, item.getProduct().getId(), item.getQuantity());
            }

            conn.commit(); // Confirmar transacción

            // 3. Mostrar éxito y limpiar
            alertInfo.viewAlert(
                "INFORMATION",
                "Venta completada",
                String.format("Venta #%d finalizada exitosamente.\nTotal: Q%.2f", idVenta, total),
                "Éxito"
            );

            // Limpiar ticket
            ticketItems.clear();
            total = BigDecimal.ZERO;
            lblTotal.setText("TOTAL Q0.00");
            
            // Recargar catálogo con stock actualizado
            cargarProductosCatalogo();
            
            txtSearch.requestFocus();

        } catch (Exception e) {
            try {
                conn.rollback(); // Revertir transacción en caso de error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error de Venta", "No se pudo completar la venta: " + e.getMessage(), "Error");
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer crearVenta(Connection conn) throws SQLException {
        String sql = "{CALL sp_crearVenta(?, ?)}";
        
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, currentUserId);
            cs.setBigDecimal(2, total);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_venta");
                }
            }
        }
        
        return null;
    }

    private void agregarDetalleVenta(Connection conn, Integer idVenta, TicketItem item) throws SQLException {
        String sql = "{CALL sp_agregarDetalle(?, ?, ?, ?, ?)}";
        
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idVenta);
            cs.setInt(2, item.getProduct().getId());
            cs.setInt(3, item.getQuantity());
            cs.setBigDecimal(4, item.getUnitPrice());
            cs.setBigDecimal(5, item.getSubtotal());
            cs.executeUpdate();
        }
    }

    private void descontarStock(Connection conn, Integer idProducto, Integer cantidad) throws SQLException {
        String sql = "{CALL sp_descontarStock(?, ?)}";
        
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idProducto);
            cs.setInt(2, cantidad);
            cs.executeUpdate();
        }
    }
}