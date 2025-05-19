package com.elencanto.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class DatabaseUtil {
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/elencanto_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Cambia esto por tu contraseña

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean crearBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupPath = "backup/database_" + timestamp + ".sql";
        
        try {
            // Asegurar que el directorio de backup existe
            Files.createDirectories(Paths.get("backup"));
            
            // Comando para crear el backup
            ProcessBuilder processBuilder = new ProcessBuilder(
                "mysqldump",
                "-u" + USER,
                "-p" + PASSWORD,
                "elencanto_db"
            );
            
            File backupFile = new File(backupPath);
            processBuilder.redirectOutput(backupFile);
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            return exitCode == 0;
        } catch (Exception e) {
            logger.severe("Error al crear backup: " + e.getMessage());
            return false;
        }
    }
}
                    
