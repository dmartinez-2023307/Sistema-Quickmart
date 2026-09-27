package org.kinalquickmart.system.config;

import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    // Método para guardar un producto (ya lo tenías)
    public boolean saveProduct(Product product) {
        String sql = "CALL sp_crearProducto(?, ?, ?, ?, ?, ?)";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            
            stmt.setString(1, product.getBarCode());
            stmt.setString(2, product.getCommercialName());
            stmt.setBigDecimal(3, product.getCostPrice());
            stmt.setBigDecimal(4, product.getSalePrice());
            stmt.setInt(5, product.getCurrentStock());
            stmt.setInt(6, product.getCategory().getId());
            
            stmt.execute();
            stmt.close();
            
            return true;
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: El código de barras ya existe.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    // ✅ MÉTODO NUEVO: Buscar productos por código o nombre
    public List<Product> buscarProducto(String texto) {
        List<Product> productosEncontrados = new ArrayList<>();
        String sql = "CALL sp_buscarProducto(?)";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            
            stmt.setString(1, texto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setName(rs.getString("nombre_categoria"));
                    
                    Product product = new Product();
                    product.setId(rs.getInt("id_producto"));
                    product.setBarCode(rs.getString("codigo_barras"));
                    product.setCommercialName(rs.getString("nombre_comercial"));
                    product.setSalePrice(rs.getBigDecimal("precio_venta"));
                    product.setCurrentStock(rs.getInt("stock_actual"));
                    product.setCategory(category);
                    
                    productosEncontrados.add(product);
                }
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productosEncontrados;
    }
}