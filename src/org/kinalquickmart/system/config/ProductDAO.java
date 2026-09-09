package org.kinalquickmart.system.config;

import org.kinalquickmart.system.model.Product;
import java.sql.*;

public class ProductDAO {
    
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
            System.err.println("Error: El código de barras ya existe en la base de datos.");
            return false; 
        } catch (SQLException e) {
            System.err.println("Error de base de datos al guardar producto: " + e.getMessage());
            return false;
        }
    }
}