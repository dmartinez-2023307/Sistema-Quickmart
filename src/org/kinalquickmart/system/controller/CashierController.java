package org.kinalquickmart.system.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.kinalquickmart.system.config.ProductDAO;
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

    private final ProductDAO productDAO = new ProductDAO();
    private final AlertInformation alertInfo = new AlertInformation();
    private final ObservableList<TicketItem> ticketItems = FXCollections.observableArrayList();
    private BigDecimal total = BigDecimal.ZERO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnasCatalogo();
        configurarColumnasTicket();
        tblTicket.setItems(ticketItems);
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

        List<Product> productos = productDAO.buscarProducto(texto);

        if (productos.isEmpty()) {
            alertInfo.viewAlert(
                "ERROR",
                "Producto no encontrado",
                "El producto no está registrado en el sistema.",
                "Error de búsqueda"
            );
            tblCatalog.getItems().clear();
            return;
        }

        tblCatalog.getItems().setAll(productos);
        txtSearch.clear();
        txtSearch.requestFocus();
    }

    @FXML
    private void handleAddToTicket() {
        Product selectedProduct = tblCatalog.getSelectionModel().getSelectedItem();
        
        if (selectedProduct == null) {
            return;
        }

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

        // Aquí iría la lógica para guardar la venta en la BD
        // Por ahora solo mostramos confirmación
        
        String mensaje = String.format(
            "Venta finalizada exitosamente.\nTotal: Q%.2f\nProductos: %d",
            total,
            ticketItems.size()
        );

        alertInfo.viewAlert(
            "INFORMATION",
            "Venta completada",
            mensaje,
            "Éxito"
        );

        // Limpiar ticket
        ticketItems.clear();
        total = BigDecimal.ZERO;
        lblTotal.setText("TOTAL Q0.00");
        txtSearch.requestFocus();
    }
}