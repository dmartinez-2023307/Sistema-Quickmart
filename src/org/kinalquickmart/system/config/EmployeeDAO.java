package org.kinalquickmart.system.config;

import org.kinalquickmart.system.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    // ✅ Listar todos los empleados
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id_usuario, correo, nombre_completo, rol, activo FROM Usuario ORDER BY nombre_completo";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id_usuario"));
                emp.setEmail(rs.getString("correo"));
                emp.setFullName(rs.getString("nombre_completo"));
                emp.setRole(rs.getString("rol"));
                emp.setActive(rs.getBoolean("activo"));
                
                employees.add(emp);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    // ✅ Buscar empleados por nombre o correo
    public List<Employee> searchEmployees(String texto) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id_usuario, correo, nombre_completo, rol, activo FROM Usuario WHERE nombre_completo LIKE ? OR correo LIKE ? ORDER BY nombre_completo";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchParam = "%" + texto + "%";
            stmt.setString(1, searchParam);
            stmt.setString(2, searchParam);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id_usuario"));
                emp.setEmail(rs.getString("correo"));
                emp.setFullName(rs.getString("nombre_completo"));
                emp.setRole(rs.getString("rol"));
                emp.setActive(rs.getBoolean("activo"));
                
                employees.add(emp);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error al buscar empleados: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    // ✅ Guardar empleado (ya lo tenías)
    public boolean saveEmployee(Employee employee) {
        String sql = "CALL sp_crearUsuario(?, ?, ?, ?)";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            
            stmt.setString(1, employee.getEmail());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getFullName());
            stmt.setString(4, employee.getRole());
            
            stmt.execute();
            stmt.close();
            return true;
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: El correo ya está registrado.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error al guardar empleado: " + e.getMessage());
            return false;
        }
    }

    // ✅ Actualizar empleado
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Usuario SET nombre_completo = ?, correo = ?, rol = ? WHERE id_usuario = ?";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getRole());
            stmt.setInt(4, employee.getId());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    // ✅ Despedir/Desactivar empleado (soft delete)
    public boolean fireEmployee(int id) {
        String sql = "UPDATE Usuario SET activo = FALSE WHERE id_usuario = ?";
        
        try {
            Connection conn = ConexionDB.getInstanciaConexionDB().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al despedir empleado: " + e.getMessage());
            return false;
        }
    }
}