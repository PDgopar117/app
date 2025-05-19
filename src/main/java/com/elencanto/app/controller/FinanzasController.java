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
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class FinanzasController {
    private static final Logger logger = Logger.getLogger(FinanzasController.class.getName());

    @FXML
    private DatePicker fechaPicker;
    
    @FXML
    private Label totalIngresosLabel;
    
    @FXML
    private Label totalEgresosLabel;
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private TableView<Transaccion> ingresosTable;
    
    @FXML
    private TableView<Transaccion> egresosTable;
    
    @FXML
    private TextField conceptoField;
    
    @FXML
    private TextField montoField;
    
    @FXML
    private ComboBox<TipoTransaccion> tipoComboBox;

    private TransaccionDAO transaccionDAO;

    @FXML
    public void initialize() {
        transaccionDAO = new TransaccionDAO();
        fechaPicker.setValue(LocalDate.now());
        configurarComboBox();
        configurarTablas();
        
        // Agregar listener para actualizar datos cuando cambie la fecha
        fechaPicker.valueProperty().addListener((obs, oldVal, newVal) -> actualizarDatos());
        
        actualizarDatos();
    }

    private void actualizarDatos() {
        try {
            LocalDate fecha = fechaPicker.getValue();
            
            // Calcular totales
            BigDecimal totalIngresos = transaccionDAO.calcularTotalPorTipoYFecha(TipoTransaccion.INGRESO, fecha);
            BigDecimal totalEgresos = transaccionDAO.calcularTotalPorTipoYFecha(TipoTransaccion.EGRESO, fecha);
            BigDecimal balance = totalIngresos.subtract(totalEgresos);
            
            // Actualizar labels
            totalIngresosLabel.setText(String.format("Ingresos: $%.2f", totalIngresos));
            totalEgresosLabel.setText(String.format("Egresos: $%.2f", totalEgresos));
            balanceLabel.setText(String.format("Balance: $%.2f", balance));
            
            // Cargar transacciones
            cargarTransacciones();
            
        } catch (SQLException e) {
            logger.severe("Error al actualizar datos financieros: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los datos financieros");
        }
    }

    private void cargarTransacciones() {
        try {
            LocalDate fecha = fechaPicker.getValue();
            
            // Cargar ingresos
            List<Transaccion> ingresos = transaccionDAO.listarPorTipoYFecha(TipoTransaccion.INGRESO, fecha);
            ingresosTable.setItems(FXCollections.observableArrayList(ingresos));
            
            // Cargar egresos
            List<Transaccion> egresos = transaccionDAO.listarPorTipoYFecha(TipoTransaccion.EGRESO, fecha);
            egresosTable.setItems(FXCollections.observableArrayList(egresos));
            
        } catch (SQLException e) {
            logger.severe("Error al cargar transacciones: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las transacciones");
        }
    }

    @FXML
    private void registrarTransaccion() {
        try {
            if (!validarCampos()) {
                return;
            }

            Transaccion transaccion = new Transaccion();
            transaccion.setTipo(tipoComboBox.getValue());
            transaccion.setConcepto(conceptoField.getText());
            transaccion.setMonto(new BigDecimal(montoField.getText()));
            transaccion.setFecha(LocalDateTime.now());
            transaccion.setUsuarioId(SessionManager.getInstance().getUsuarioActual().getId());

            if (transaccionDAO.agregarTransaccion(transaccion)) {
                mostrarAlerta("Éxito", "Transacción registrada correctamente");
                limpiarCampos();
                actualizarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la transacción");
            }
        } catch (SQLException e) {
            logger.severe("Error al registrar transacción: " + e.getMessage());
            mostrarAlerta("Error", "Error al registrar la transacción");
        }
    }

    private boolean validarCampos() {
        if (tipoComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un tipo de transacción");
            return false;
        }

        if (conceptoField.getText().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar un concepto");
            return false;
        }

        if (montoField.getText().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar un monto");
            return false;
        }

        try {
            BigDecimal monto = new BigDecimal(montoField.getText());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("Error", "El monto debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto debe ser un número válido");
            return false;
        }

        return true;
    }

    private void configurarComboBox() {
        tipoComboBox.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
    }

    private void configurarTablas() {
        // Configurar columnas de la tabla de ingresos
        // ... (código de configuración de columnas)
    }

    private void limpiarCampos() {
        tipoComboBox.setValue(null);
        conceptoField.clear();
        montoField.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}