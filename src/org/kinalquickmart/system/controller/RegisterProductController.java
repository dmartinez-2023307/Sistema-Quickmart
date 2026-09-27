package org.kinalquickmart.system.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;
import org.kinalquickmart.system.model.Category;
import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.service.CategoryService;
import org.kinalquickmart.system.service.ProductService;
import org.kinalquickmart.system.utils.AlertInformation;

public class RegisterProductController implements Initializable {

    @FXML private TextField txtBarCode;
    @FXML private TextField txtProductName;
    @FXML private TextField txtCostPrice;
    @FXML private TextField txtSalePrice;
    @FXML private TextField txtStock;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private Button btnRegister;

    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();
    private final AlertInformation alertInfo = new AlertInformation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCategorias();
        configurarValidaciones();
    }

    private void cargarCategorias() {
        cmbCategory.getItems().setAll(categoryService.getAllCategories());
    }

    private void configurarValidaciones() {
        // Filtro: solo números (para código de barras y stock)
        Pattern soloNumeros = Pattern.compile("\\d*");
        UnaryOperator<TextFormatter.Change> filterNumeros = change -> {
            String newText = change.getControlNewText();
            if (soloNumeros.matcher(newText).matches()) {
                return change;
            }
            return null;
        };

        // Filtro: números con hasta 2 decimales (para precios)
        Pattern decimalPattern = Pattern.compile("\\d*\\.?\\d{0,2}");
        UnaryOperator<TextFormatter.Change> filterDecimal = change -> {
            String newText = change.getControlNewText();
            if (decimalPattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        };

        // Aplicar los filtros a cada TextField
        txtBarCode.setTextFormatter(new TextFormatter<>(filterNumeros));
        txtStock.setTextFormatter(new TextFormatter<>(filterNumeros));
        txtCostPrice.setTextFormatter(new TextFormatter<>(filterDecimal));
        txtSalePrice.setTextFormatter(new TextFormatter<>(filterDecimal));
    }

    @FXML
    private void handleRegisterProduct() {
        if (camposVacios()) {
            alertInfo.viewAlert("WARNING", "Campos Incompletos", "Por favor, complete todos los campos obligatorios.", "Validación");
            return;
        }

        try {
            BigDecimal costPrice = new BigDecimal(txtCostPrice.getText());
            BigDecimal salePrice = new BigDecimal(txtSalePrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            Product newProduct = new Product();
            newProduct.setBarCode(txtBarCode.getText().trim());
            newProduct.setCommercialName(txtProductName.getText().trim());
            newProduct.setCostPrice(costPrice);
            newProduct.setSalePrice(salePrice);
            newProduct.setCurrentStock(stock);
            newProduct.setCategory(cmbCategory.getValue());

            if (productService.registerProduct(newProduct)) {
                String mensaje = String.format("Producto: %s\nCódigo: %s\nStock: %d\nPrecio: Q%.2f",
                        newProduct.getCommercialName(), newProduct.getBarCode(), newProduct.getCurrentStock(), newProduct.getSalePrice());
                alertInfo.viewAlert("INFORMATION", "Registro Exitoso", mensaje, "Producto guardado");
                limpiarCampos();
            } else {
                alertInfo.viewAlert("ERROR", "Error de Registro", "El código de barras ya existe o los datos son inválidos.", "Error");
                txtBarCode.requestFocus();
                txtBarCode.selectAll();
            }
        } catch (NumberFormatException e) {
            alertInfo.viewAlert("ERROR", "Formato Inválido", "Verifique que los precios y el stock sean números válidos.", "Error");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtBarCode.clear(); txtProductName.clear(); txtCostPrice.clear();
        txtSalePrice.clear(); txtStock.clear();
        cmbCategory.getSelectionModel().clearSelection();
        txtBarCode.requestFocus();
    }

    private boolean camposVacios() {
        return txtBarCode.getText().isEmpty() || txtProductName.getText().isEmpty() ||
               txtCostPrice.getText().isEmpty() || txtSalePrice.getText().isEmpty() ||
               txtStock.getText().isEmpty() || cmbCategory.getValue() == null;
    }
}