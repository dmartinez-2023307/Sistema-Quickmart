package org.kinalquickmart.system.service;

import org.kinalquickmart.system.dao.ProductDAO;
import org.kinalquickmart.system.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public boolean registerProduct(Product product) {
        if (product.getBarCode() == null || product.getBarCode().trim().isEmpty()) {
            return false; 
        }
        if (product.getCategory() == null || product.getCategory().getId() <= 0) {
            return false; 
        }
        if (product.getCostPrice().compareTo(product.getSalePrice()) > 0) {
            return false; 
        }
        return productDAO.saveProduct(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProduct(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return new ArrayList<>(); 
        }
        return productDAO.buscarProducto(texto);
    }

    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }
}