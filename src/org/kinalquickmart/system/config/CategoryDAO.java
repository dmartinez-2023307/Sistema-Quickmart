package org.kinalquickmart.system.config;

import org.kinalquickmart.system.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String sql = "CALL sp_listarCategorias()";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                categoryList.add(new Category(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre_categoria"),
                    rs.getString("descripcion")
                ));
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error de base de datos al obtener categorías: " + e.getMessage());
        }
        return categoryList;
    }
}