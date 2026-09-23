package org.kinalquickmart.system.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kinalquickmart.system.config.ConexionDB;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.model.ProductoTicket;
import org.kinalquickmart.system.model.TicketItem;
import org.kinalquickmart.system.utils.AlertInformation;
import org.kinalquickmart.system.utils.PDFGenerator;

public class CashierController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnLogOut;
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
    
    // Datos del cajero para la factura
    private Integer currentUserId = 1; 
    private String nombreCajero = "Cajero"; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnasCatalogo();
        configurarColumnasTicket();
        tblTicket.setItems(ticketItems);
        tblCatalog.setItems(catalogProducts);
        
        cargarProductosCatalogo();
        tblCatalog.setOnMouseClicked(this::handleTableClick);
        
        // Evento de doble clic en tblTicket para eliminar producto
        tblTicket.setOnMouseClicked(this::handleEliminarDelTicket);
        
        txtSearch.requestFocus();
    }

    public void setCajeroData(Integer userId, String nombre) {
        this.currentUserId = userId;
        this.nombreCajero = nombre;
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
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String texto = txtSearch.getText().trim();
        if (texto.isEmpty()) {
            alertInfo.viewAlert("WARNING", "Campo vacío", "Por favor, ingresa un código o nombre de producto.", "Validación");
            return;
        }
        buscarProductos(texto);
        txtSearch.clear();
        txtSearch.requestFocus();
    }

    private void buscarProductos(String texto) {
        String sql = "{CALL sp_buscarProducto(?)}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) return;

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
                    alertInfo.viewAlert("WARNING", "Producto no encontrado", "No se encontraron productos que coincidan con: " + texto, "Búsqueda");
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al buscar producto: " + e.getMessage());
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Product selectedProduct = tblCatalog.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                agregarAlTicket(selectedProduct);
            }
        }
    }

    // Eliminar producto del ticket con doble clic
    @FXML
    private void handleEliminarDelTicket(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TicketItem selectedItem = tblTicket.getSelectionModel().getSelectedItem();
            
            if (selectedItem != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Eliminar producto");
                confirmAlert.setHeaderText("¿Estás seguro de eliminar este producto del ticket?");
                confirmAlert.setContentText("Producto: " + selectedItem.getProductName() + 
                                           "\nCantidad: " + selectedItem.getQuantity() +
                                           "\n\nEl stock será devuelto al inventario.");

                java.util.Optional<ButtonType> result = confirmAlert.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    devolverStock(selectedItem.getProduct().getId(), selectedItem.getQuantity());
                    ticketItems.remove(selectedItem);
                    actualizarTotal();
                    cargarProductosCatalogo();
                    
                    alertInfo.viewAlert("INFORMATION", "Producto eliminado", 
                        "El producto fue eliminado del ticket y el stock fue devuelto al inventario.", "Éxito");
                }
            }
        }
    }

    // Devolver stock al inventario
    private void devolverStock(Integer idProducto, Integer cantidad) {
        String sql = "{CALL sp_devolverStock(?, ?)}";
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        
        if (conn == null) {
            alertInfo.viewAlert("ERROR", "Error de Sistema", "No hay conexión a la base de datos.", "Error");
            return;
        }

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idProducto);
            cs.setInt(2, cantidad);
            cs.executeUpdate();
            System.out.println("✅ Stock devuelto: Producto ID " + idProducto + ", Cantidad: " + cantidad);
        } catch (SQLException e) {
            System.err.println("❌ Error al devolver stock: " + e.getMessage());
            e.printStackTrace();
            alertInfo.viewAlert("ERROR", "Error", "No se pudo devolver el stock al inventario: " + e.getMessage(), "Error");
        }
    }

    @FXML
    private void handleAddToTicket() {
        Product selectedProduct = tblCatalog.getSelectionModel().getSelectedItem();
        
        if (selectedProduct == null) {
            alertInfo.viewAlert("WARNING", "Selección requerida", "Por favor, selecciona un producto del catálogo.", "Validación");
            return;
        }

        agregarAlTicket(selectedProduct);
    }

    private void agregarAlTicket(Product selectedProduct) {
        if (selectedProduct.getCurrentStock() == 0) {
            alertInfo.viewAlert("ERROR", "Producto agotado", "El producto '" + selectedProduct.getCommercialName() + "' está agotado.", "Error de stock");
            return;
        }

        TicketItem existingItem = ticketItems.stream()
            .filter(item -> item.getProduct().getId() == selectedProduct.getId())
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            if (existingItem.getQuantity() >= selectedProduct.getCurrentStock()) {
                alertInfo.viewAlert("WARNING", "Stock insuficiente", "No hay suficiente stock disponible.", "Validación de stock");
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
        lblTotal.setText("Q" + String.format("%.2f", total));
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
    private void handleFinalize() {
        if (ticketItems.isEmpty()) {
            alertInfo.viewAlert("WARNING", "Ticket vacío", "No hay productos en el ticket.", "Validación");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kinalquickmart/system/view/PagoDialogView.fxml"));
            Parent root = loader.load();
            PagoDialogController controller = loader.getController();
            
            controller.setTotalVenta(total.doubleValue());
            
            Stage dialog = new Stage();
            controller.setDialog(dialog);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Finalizar Venta - QuickMart");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            
            dialog.showAndWait();
            
            if (controller.isConfirmado()) {
                String nit = controller.getNit();
                String formaPago = controller.getFormaPago();
                double montoRecibido = controller.getMontoRecibido();
                double cambio = controller.getCambio();
                
                Integer idVenta = procesarVentaEnBD();
                
                if (idVenta != null) {
                    generarFacturaPDF(idVenta, nit, formaPago, montoRecibido, cambio);
                    
                    ticketItems.clear();
                    total = BigDecimal.ZERO;
                    lblTotal.setText("Q0.00");
                    cargarProductosCatalogo();
                    txtSearch.requestFocus();
                } else {
                    alertInfo.viewAlert("ERROR", "Error de Venta", "No se pudo completar la venta. Revisa la consola.", "Error");
                }
            }
            
        } catch (IOException e) {
            alertInfo.viewAlert("ERROR", "Error", "No se pudo cargar el diálogo de pago.", "Error");
            e.printStackTrace();
        }
    }

    // ===== GENERAR PDF Y ABRIRLO AUTOMÁTICAMENTE =====
    private void generarFacturaPDF(Integer idVenta, String nit, String formaPago, double montoRecibido, double cambio) {
        List<ProductoTicket> productos = new ArrayList<>();
        double subtotal = 0.0;

        for (TicketItem item : ticketItems) {
            int idProducto = item.getProduct().getId();
            String nombre = item.getProductName();
            int cantidad = item.getQuantity();
            double precioUnitario = item.getUnitPrice().doubleValue();
            double totalItem = item.getSubtotal().doubleValue();

            productos.add(new ProductoTicket(idProducto, nombre, cantidad, precioUnitario, totalItem));
            subtotal += totalItem;
        }

        double iva = subtotal * 0.15;
        double totalVenta = subtotal + iva;
        
        String numeroFactura = String.format("%09d", idVenta);
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
        String cajero = (this.nombreCajero != null) ? this.nombreCajero : "Cajero ID: " + currentUserId;

        String rutaPDF = PDFGenerator.generarFacturaConPago(
            numeroFactura, fecha, cajero, nit,
            productos, subtotal, iva, totalVenta,
            formaPago, montoRecibido, cambio
        );

        if (rutaPDF != null) {
            // ⭐ ABRIR EL PDF AUTOMÁTICAMENTE
            try {
                File archivoPDF = new File(rutaPDF);
                if (archivoPDF.exists()) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(archivoPDF);
                        System.out.println("✅ PDF abierto automáticamente: " + rutaPDF);
                    } else {
                        System.out.println("⚠️ No se puede abrir el PDF automáticamente en este sistema.");
                    }
                } else {
                    System.err.println(" El archivo PDF no existe en: " + rutaPDF);
                }
            } catch (Exception e) {
                System.err.println("❌ Error al abrir el PDF: " + e.getMessage());
                e.printStackTrace();
            }
            
            alertInfo.viewAlert("INFORMATION", "Venta y Factura generada", 
                "Venta #" + idVenta + " completada exitosamente.\nLa factura se ha abierto automáticamente.", "Éxito");
        } else {
            alertInfo.viewAlert("WARNING", "Venta completada, error en PDF", 
                "La venta se registró en la BD, pero hubo un error al generar el PDF.", "Advertencia");
        }
    }

    // ===== LÓGICA DE BASE DE DATOS =====
    private Integer procesarVentaEnBD() {
        Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
        if (conn == null) return null;

        try {
            conn.setAutoCommit(false);

            Integer idVenta = crearVenta(conn);
            if (idVenta == null) {
                throw new Exception("No se pudo crear la venta");
            }

            for (TicketItem item : ticketItems) {
                agregarDetalleVenta(conn, idVenta, item);
                descontarStock(conn, item.getProduct().getId(), item.getQuantity());
            }

            conn.commit();
            return idVenta;

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
            e.printStackTrace();
            return null;
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