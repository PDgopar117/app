/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Transaccion.TipoTransaccion;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FinanzasController {
    private static final Logger logger = Logger.getLogger(FinanzasController.class.getName());

    @FXML
    private DatePicker fechaSelector;
    
    @FXML
    private Label ingresosDiaLabel;
    
    @FXML
    private Label egresosDiaLabel;
    
    @FXML
    private Label balanceDiaLabel;
    
    @FXML
    private Button agregarIngresoButton;
    
    @FXML
    private Button agregarEgresoButton;
    
    @FXML
    private VBox ingresosContainer;
    
    @FXML
    private VBox egresosContainer;
    
    @FXML
    private LineChart<String, Number> lineChart; // Deberías agregar fx:id al LineChart en el FXML

    private TransaccionDAO transaccionDAO;

    @FXML
    public void initialize() {
        try {
            // Inicializar DAO
            transaccionDAO = new TransaccionDAO();
            
            // Configurar fecha actual
            fechaSelector.setValue(LocalDate.now());
            
            // Agregar listener para cambios de fecha
            fechaSelector.valueProperty().addListener((obs, oldVal, newVal) -> cargarDatos(newVal));
            
            // Configurar botones
            agregarIngresoButton.setOnAction(e -> mostrarDialogoNuevaTransaccion(TipoTransaccion.INGRESO));
            agregarEgresoButton.setOnAction(e -> mostrarDialogoNuevaTransaccion(TipoTransaccion.EGRESO));
            
            // Cargar datos iniciales
            cargarDatos(fechaSelector.getValue());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar controlador de finanzas", e);
            mostrarAlerta("Error", "No se pudo inicializar el módulo de finanzas");
        }
    }
    
    private void cargarDatos(LocalDate fecha) {
        try {
            // Calcular totales
            BigDecimal ingresos = transaccionDAO.calcularTotalPorTipoYFecha(TipoTransaccion.INGRESO, fecha);
            BigDecimal egresos = transaccionDAO.calcularTotalPorTipoYFecha(TipoTransaccion.EGRESO, fecha);
            BigDecimal balance = ingresos.subtract(egresos);
            
            // Actualizar etiquetas
            ingresosDiaLabel.setText("$" + ingresos.toString());
            egresosDiaLabel.setText("$" + egresos.toString());
            balanceDiaLabel.setText("$" + balance.toString());
            
            // Cargar transacciones
            cargarTransacciones(fecha);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cargar datos financieros", e);
            mostrarAlerta("Error", "No se pudieron cargar los datos financieros");
        }
    }
    
    private void cargarTransacciones(LocalDate fecha) throws SQLException {
        // Limpiar contenedores
        ingresosContainer.getChildren().clear();
        egresosContainer.getChildren().clear();
        
        // Cargar ingresos
        for (Transaccion t : transaccionDAO.listarPorTipoYFecha(TipoTransaccion.INGRESO, fecha)) {
            Label label = new Label(t.getConcepto() + ": $" + t.getMonto());
            ingresosContainer.getChildren().add(label);
        }
        
        // Cargar egresos
        for (Transaccion t : transaccionDAO.listarPorTipoYFecha(TipoTransaccion.EGRESO, fecha)) {
            Label label = new Label(t.getConcepto() + ": $" + t.getMonto());
            egresosContainer.getChildren().add(label);
        }
    }
    
    private void mostrarDialogoNuevaTransaccion(TipoTransaccion tipo) {
        // Implementación básica para mostrar diálogo de nueva transacción
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nueva " + (tipo == TipoTransaccion.INGRESO ? "Ingreso" : "Egreso"));
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad en desarrollo");
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}