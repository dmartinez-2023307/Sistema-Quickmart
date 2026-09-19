package org.kinalquickmart.system.config;

import org.kinalquickmart.system.model.Employee;
import java.sql.*;

public class EmployeeDAO {
    
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
            System.err.println("Error de base de datos al guardar empleado: " + e.getMessage());
            return false;
        }
    }
}