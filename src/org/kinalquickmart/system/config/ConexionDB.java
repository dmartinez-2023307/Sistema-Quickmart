package org.kinalquickmart.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    private static ConexionDB instanciaConexionDB;
    private Connection connection;

    public Connection getConnection() {
        return this.connection;
    }
    
    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE;
            connection = DriverManager.getConnection(url, Enviroment.USER, Enviroment.PASSWORD);
            System.out.println("✅ CONEXIÓN EXITOSA a: " + Enviroment.DATA_BASE);
            

        } catch (SQLException e) {
            System.err.println(" ERROR DE CONEXIÓN SQL:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(" Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ConexionDB getInstanciaConexionDB() {
        if (instanciaConexionDB == null) {
            instanciaConexionDB = new ConexionDB();
        }
        return instanciaConexionDB;
    }
}