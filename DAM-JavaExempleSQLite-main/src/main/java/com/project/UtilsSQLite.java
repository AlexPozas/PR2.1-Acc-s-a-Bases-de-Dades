package com.project;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UtilsSQLite {

    public static Connection connect (String filePath) {
        Connection conn = null;
        
        try {
            String url = "jdbc:sqlite:" + filePath;
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("BBDD driver: " + meta.getDriverName());
            }
            System.out.println("BBDD SQLite connectada");
        } catch (SQLException e) { e.printStackTrace(); }

        return conn;
    }

    public static void disconnect (Connection conn ) {
        try {
            if (conn != null) { 
                conn.close(); 
                System.out.println("DDBB SQLite desconnectada");
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    public static ArrayList<String> listTables(Connection conn, String tableName) throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
    
        if (tableName == null) {
            // Obtener todas las tablas
            try (ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        } else {
            // Obtener tablas específicas
            try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        }
    
        return tables;
    }

    public static int queryUpdate (Connection conn, String sql) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public static ResultSet querySelect (Connection conn, String sql) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) { e.printStackTrace(); }
        return rs;
    }
    public static ResultSet querySelect(Connection conn, String query, Object... params) throws SQLException {
    PreparedStatement pstmt = conn.prepareStatement(query);

    // Configurar los parámetros dinámicamente
    for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
    }

    // Ejecutar la consulta
    return pstmt.executeQuery();
}
}