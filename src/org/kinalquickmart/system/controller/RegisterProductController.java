package org.kinalquickmart.system.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.kinalquickmart.system.config.CategoryDAO;
import org.kinalquickmart.system.config.ProductDAO;
import org.kinalquickmart.system.model.Category;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.utils.AlertInformation;

public class RegisterProductController implements Initializable {

    @FXML
    private TextField txtBarCode;
    @FXML
    private TextField txtProductName;
    @FXML
    private TextField txtCostPrice;
    @FXML
    private TextField txtSalePrice;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<Category> cmbCategory;
    @FXML
    private Button btnRegister;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final AlertInformation alertInfo = new AlertInformation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCategorias();
        configurarValidaciones();
    }

    private void cargarCategorias() {
        cmbCategory.getItems().setAll(categoryDAO.getAllCategories());
    }

    private void configurarValidaciones() {
        // Solo permitir números en código de barras
        txtBarCode.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtBarCode.setText(oldValue);
            }
        });

        // Solo permitir números y punto decimal en precios
        String decimalRegex = "\\d*\\.?\\d{0,2}";
        txtCostPrice.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches(decimalRegex)) {
                txtCostPrice.setText(oldValue);
            }
        });

        txtSalePrice.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches(decimalRegex)) {
                txtSalePrice.setText(oldValue);
            }
        });

        // Solo permitir números enteros en stock
        txtStock.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtStock.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleRegister() {
        if (camposVacios()) {
            alertInfo.viewAlert(
                    "WARNING",
                    "Campos Incompletos",
                    "Por favor, complete todos los campos obligatorios.",
                    "Validación de campos"
            );
            return;
        }

        try {
            BigDecimal costPrice = new BigDecimal(txtCostPrice.getText());
            BigDecimal salePrice = new BigDecimal(txtSalePrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                alertInfo.viewAlert(
                        "ERROR",
                        "Precio Inválido",
                        "El precio de costo debe ser mayor a 0.",
                        "Validación de precio"
                );
                return;
            }

            if (salePrice.compareTo(costPrice) <= 0) {
                alertInfo.viewAlert(
                        "ERROR",
                        "Precio Inválido",
                        "El precio de venta debe ser MAYOR que el precio de costo.",
                        "Validación de precio"
                );
                return;
            }

            if (stock < 0) {
                alertInfo.viewAlert(
                        "ERROR",
                        "Stock Inválido",
                        "El stock no puede ser un número negativo.",
                        "Validación de stock"
                );
                return;
            }

            Product newProduct = new Product();
            newProduct.setBarCode(txtBarCode.getText().trim());
            newProduct.setCommercialName(txtProductName.getText().trim());
            newProduct.setCostPrice(costPrice);
            newProduct.setSalePrice(salePrice);
            newProduct.setCurrentStock(stock);
            newProduct.setCategory(cmbCategory.getValue());

            if (productDAO.saveProduct(newProduct)) {
                String mensajeConfirmacion = String.format(
                        "Producto: %s\nCódigo: %s\nStock registrado: %d unidades\nPrecio venta: Q%.2f",
                        newProduct.getCommercialName(),
                        newProduct.getBarCode(),
                        newProduct.getCurrentStock(),
                        newProduct.getSalePrice()
                );

                alertInfo.viewAlert(
                        "INFORMATION",
                        "Registro Exitoso",
                        mensajeConfirmacion,
                        "Producto guardado correctamente"
                );
                limpiarCampos();
            } else {
                alertInfo.viewAlert(
                        "ERROR",
                        "Error de Registro",
                        "El código de barras ya existe. Por favor, utilice un código único.",
                        "Error de base de datos"
                );
                txtBarCode.requestFocus();
                txtBarCode.selectAll();
            }

        } catch (NumberFormatException e) {
            alertInfo.viewAlert(
                    "ERROR",
                    "Formato Inválido",
                    "Verifique que los precios y el stock sean números válidos.",
                    "Error de formato"
            );
        }
    }

    @FXML
    private void limpiarCampos() {
        txtBarCode.clear();
        txtProductName.clear();
        txtCostPrice.clear();
        txtSalePrice.clear();
        txtStock.clear();
        cmbCategory.getSelectionModel().clearSelection();
        txtBarCode.requestFocus();
    }

    private boolean camposVacios() {
        return txtBarCode.getText().isEmpty()
                || txtProductName.getText().isEmpty()
                || txtCostPrice.getText().isEmpty()
                || txtSalePrice.getText().isEmpty()
                || txtStock.getText().isEmpty()
                || cmbCategory.getValue() == null;
    }
}
