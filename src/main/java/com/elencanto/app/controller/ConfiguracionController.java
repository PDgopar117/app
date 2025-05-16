/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.util.DatabaseUtil;
import com.elencanto.app.util.PasswordHash;
import com.elencanto.app.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.logging.Logger;

public class ConfiguracionController {
    private static final Logger logger = Logger.getLogger(ConfiguracionController.class.getName());
    
    @FXML
    private TextField nombreNegocioField;
    
    @FXML
    private TextField direccionField;
    
    @FXML
    private TextField telefonoField;
    
    @FXML
    private PasswordField passwordActualField;
    
    @FXML
    private PasswordField nuevaPasswordField;
    
    @FXML
    private PasswordField confirmarPasswordField;
    
    @FXML
    public void initialize() {
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        // Cargar configuración desde la base de datos
        try {
            nombreNegocioField.setText("El Encanto");
            direccionField.setText("Dirección actual");
            telefonoField.setText("Teléfono actual");
        } catch (Exception e) {
            logger.severe("Error al cargar la configuración: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la configuración", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void guardarConfiguracion() {
        try {
            // Implementar lógica para guardar en base de datos
            mostrarAlerta("Éxito", "Configuración guardada correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            logger.severe("Error al guardar la configuración: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo guardar la configuración", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void cambiarPassword() {
        if (passwordActualField.getText().isEmpty() || 
            nuevaPasswordField.getText().isEmpty() || 
            confirmarPasswordField.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return;
        }
        
        if (!nuevaPasswordField.getText().equals(confirmarPasswordField.getText())) {
            mostrarAlerta("Error", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Implementar lógica de cambio de contraseña
            mostrarAlerta("Éxito", "Contraseña actualizada correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            logger.severe("Error al cambiar la contraseña: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cambiar la contraseña", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void crearRespaldo() {
        try {
            if (DatabaseUtil.crearBackup()) {
                mostrarAlerta("Éxito", "Respaldo creado correctamente", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo crear el respaldo", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            logger.severe("Error al crear respaldo: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear respaldo", Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}