/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Transaccion.TipoTransaccion;
import com.elencanto.app.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LineChart<String, Number> lineChart;

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
    
    private void mostrarDialogoNuevaTransaccion(final TipoTransaccion tipo) {
        Stage stage = new Stage();
        stage.setTitle("Nueva " + (tipo == TipoTransaccion.INGRESO ? "Ingreso" : "Egreso"));
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Título con estilo diferente según tipo
        Label titulo = new Label("Registrar Nuevo " + (tipo == TipoTransaccion.INGRESO ? "Ingreso" : "Egreso"));
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Cambiar color según tipo (verde para ingresos, rojo para egresos)
        final String colorEstilo = tipo == TipoTransaccion.INGRESO ? 
                "-fx-background-color: #28a745; -fx-text-fill: white;" : 
                "-fx-background-color: #dc3545; -fx-text-fill: white;";
        
        // Crear grid para formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        // Primera columna con ancho fijo
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(100);
        col1.setHalignment(HPos.RIGHT);
        
        // Segunda columna extensible
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPrefWidth(250);
        
        grid.getColumnConstraints().addAll(col1, col2);
        
        // Campos del formulario
        Label conceptoLabel = new Label("Concepto:");
        final TextField conceptoField = new TextField();
        conceptoField.setPromptText("Ej: " + 
                (tipo == TipoTransaccion.INGRESO ? "Pago de cliente" : "Compra de insumos"));
        
        Label montoLabel = new Label("Monto ($):");
        final TextField montoField = new TextField();
        montoField.setPromptText("Ej: 500.00");
        
        // Validación de monto (solo números y punto decimal)
        montoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                montoField.setText(old);
            }
        });
        
        // Si es un egreso, agregamos categorías
        final ComboBox<String> categoriaCombo;
        if (tipo == TipoTransaccion.EGRESO) {
            Label categoriaLabel = new Label("Categoría:");
            categoriaCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Servicios", "Insumos", "Mantenimiento", "Nómina", "Impuestos", "Otros"
            ));
            categoriaCombo.setPromptText("Seleccionar categoría");
            
            grid.add(categoriaLabel, 0, 2);
            grid.add(categoriaCombo, 1, 2);
        } else {
            categoriaCombo = null;
        }
        
        Label notasLabel = new Label("Notas:");
        final TextArea notasArea = new TextArea();
        notasArea.setPromptText("Notas adicionales (opcional)");
        notasArea.setPrefRowCount(3);
        
        // Añadir componentes al grid
        grid.add(conceptoLabel, 0, 0);
        grid.add(conceptoField, 1, 0);
        grid.add(montoLabel, 0, 1);
        grid.add(montoField, 1, 1);
        
        final int notasRow = tipo == TipoTransaccion.EGRESO ? 3 : 2;
        grid.add(notasLabel, 0, notasRow);
        grid.add(notasArea, 1, notasRow);
        
        // Botones
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setOnAction(e -> stage.close());
        
        Button guardarBtn = new Button("Guardar");
        guardarBtn.setStyle(colorEstilo);
        
        // Evento guardar
        guardarBtn.setOnAction(e -> {
            // Validación de campos
            if (conceptoField.getText().isEmpty() || montoField.getText().isEmpty()) {
                mostrarAlerta("Error", "Debe completar los campos obligatorios", Alert.AlertType.ERROR);
                return;
            }
            
            try {
                BigDecimal monto = new BigDecimal(montoField.getText());
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarAlerta("Error", "El monto debe ser mayor a 0", Alert.AlertType.ERROR);
                    return;
                }
                
                // Crear transacción
                Transaccion transaccion = new Transaccion();
                transaccion.setTipo(tipo);
                
                // Construir concepto completo
                StringBuilder concepto = new StringBuilder(conceptoField.getText().trim());
                if (tipo == TipoTransaccion.EGRESO && categoriaCombo != null && categoriaCombo.getValue() != null) {
                    concepto.append(" [").append(categoriaCombo.getValue()).append("]");
                }
                if (!notasArea.getText().isEmpty()) {
                    concepto.append(" - ").append(notasArea.getText().trim());
                }
                transaccion.setConcepto(concepto.toString());
                
                // Establecer monto, fecha y usuario
                transaccion.setMonto(monto);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setUsuarioId(SessionManager.getInstance().getUsuarioActual().getId());
                
                // Guardar en la base de datos
                if (transaccionDAO.crear(transaccion)) {
                    mostrarAlerta("Éxito", 
                        tipo == TipoTransaccion.INGRESO ? "Ingreso registrado correctamente" : "Egreso registrado correctamente", 
                        Alert.AlertType.INFORMATION);
                    
                    // Recargar datos
                    cargarDatos(fechaSelector.getValue());
                    
                    stage.close();
                } else {
                    mostrarAlerta("Error", "No se pudo registrar la transacción", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "El monto debe ser un número válido", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error al guardar transacción", ex);
                mostrarAlerta("Error", "Error al guardar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
        buttons.getChildren().addAll(cancelBtn, guardarBtn);
        
        // Añadir todo al contenedor principal
        root.getChildren().addAll(titulo, grid, new Separator(), buttons);
        
        // Mostrar ventana
        Scene scene = new Scene(root, 450, tipo == TipoTransaccion.EGRESO ? 400 : 350);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
    }
    
    // Método original para alertas de error
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Nuevo método para alertas con diferentes tipos
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}