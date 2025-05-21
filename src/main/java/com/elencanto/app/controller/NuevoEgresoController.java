/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Transaccion.TipoTransaccion;
import com.elencanto.app.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuevoEgresoController {
    private static final Logger logger = Logger.getLogger(NuevoEgresoController.class.getName());
    
    @FXML
    private TextField conceptoField;
    
    @FXML
    private TextField montoField;
    
    @FXML
    private DatePicker fechaPicker;
    
    @FXML
    private ComboBox<String> categoriaComboBox;
    
    @FXML
    private TextArea notasTextArea;
    
    @FXML
    private Button cancelarButton;
    
    @FXML
    private Button guardarButton;
    
    private TransaccionDAO transaccionDAO;
    
    @FXML
    public void initialize() {
        // Inicializar DAO
        transaccionDAO = new TransaccionDAO();
        
        // Configurar fecha actual
        fechaPicker.setValue(LocalDate.now());
        
        // Configurar categorías de egresos
        categoriaComboBox.setItems(FXCollections.observableArrayList(
            "Servicios", "Insumos", "Mantenimiento", "Nómina", "Impuestos", "Otros"
        ));
        
        // Validación de campo de monto (solo números y punto decimal)
        montoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                montoField.setText(oldValue);
            }
        });
    }
    
    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            try {
                // Crear nuevo objeto de transacción
                Transaccion egreso = new Transaccion();
                egreso.setTipo(TipoTransaccion.EGRESO);
                egreso.setConcepto(obtenerConceptoCompleto());
                egreso.setMonto(new BigDecimal(montoField.getText()));
                
                // Obtener fecha y hora
                LocalDateTime fechaHora = LocalDateTime.of(
                    fechaPicker.getValue(), 
                    LocalTime.now()
                );
                egreso.setFecha(fechaHora);
                
                // Establecer usuario actual
                egreso.setUsuarioId(SessionManager.getInstance().getUsuarioActual().getId());
                
                // Guardar en la base de datos
                if (transaccionDAO.crear(egreso)) {
                    mostrarMensaje("Éxito", "Egreso registrado correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo registrar el egreso", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al guardar egreso", e);
                mostrarMensaje("Error", "Error al registrar el egreso: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error inesperado", e);
                mostrarMensaje("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private boolean validarCampos() {
        if (conceptoField.getText().isEmpty()) {
            mostrarMensaje("Error", "El concepto es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        
        if (montoField.getText().isEmpty()) {
            mostrarMensaje("Error", "El monto es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        
        try {
            BigDecimal monto = new BigDecimal(montoField.getText());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje("Error", "El monto debe ser mayor a 0", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "El monto debe ser un número válido", Alert.AlertType.ERROR);
            return false;
        }
        
        if (fechaPicker.getValue() == null) {
            mostrarMensaje("Error", "La fecha es obligatoria", Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    private String obtenerConceptoCompleto() {
        StringBuilder concepto = new StringBuilder(conceptoField.getText().trim());
        
        // Agregar categoría si está seleccionada
        if (categoriaComboBox.getValue() != null) {
            concepto.append(" [").append(categoriaComboBox.getValue()).append("]");
        }
        
        // Agregar notas si existen
        if (notasTextArea.getText() != null && !notasTextArea.getText().trim().isEmpty()) {
            concepto.append(" - ").append(notasTextArea.getText().trim());
        }
        
        return concepto.toString();
    }
    
    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}