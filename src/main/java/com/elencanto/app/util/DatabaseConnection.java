/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Gopar117
 */
public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    
    private static final String URL = "jdbc:mysql://localhost:3306/elencanto_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root"
            + "";
    
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Conexión a la base de datos establecida");
        } catch (SQLException e) {
            logger.severe("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.connection.close();
                logger.info("Conexión a la base de datos cerrada");
            } catch (SQLException e) {
                logger.severe("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
}
