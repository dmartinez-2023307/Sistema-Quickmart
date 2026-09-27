package org.kinalquickmart.system.dao;

import org.kinalquickmart.system.model.Product;
import org.kinalquickmart.system.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.kinalquickmart.system.config.ConexionDB;

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
            System.err.println("Error: El código de barras ya existe.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

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

    // ✅ MÉTODO NUEVO: Listar todos
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "CALL sp_listarProductos()";
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category cat = new Category(rs.getInt("id_categoria"), rs.getString("nombre_categoria"), "");
                Product p = new Product(
                    rs.getInt("id_producto"),
                    rs.getString("codigo_barras"),
                    rs.getString("nombre_comercial"),
                    rs.getBigDecimal("precio_costo"),
                    rs.getBigDecimal("precio_venta"),
                    rs.getInt("stock_actual"),
                    cat
                );
                list.add(p);
            }
            rs.close(); stmt.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ✅ MÉTODO NUEVO: Eliminar
    public boolean deleteProduct(int id) {
        String sql = "CALL sp_eliminarProducto(?)";
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, id);
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}