/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
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
    private VBox ingresosContainer;
    
    @FXML
    private VBox egresosContainer;
    
    @FXML
    private Button agregarIngresoButton;
    
    @FXML
    private Button agregarEgresoButton;
    
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    
    @FXML
    public void initialize() {
        configurarFechaSelector();
        configurarBotones();
        
        // Cargar datos del día actual
        cargarDatosPorFecha(LocalDate.now());
    }
    
    private void configurarFechaSelector() {
        fechaSelector.setValue(LocalDate.now());
        fechaSelector.setOnAction(event -> {
            LocalDate fechaSeleccionada = fechaSelector.getValue();
            cargarDatosPorFecha(fechaSeleccionada);
        });
    }
    
    private void configurarBotones() {
        agregarIngresoButton.setOnAction(event -> abrirDialogoNuevaTransaccion(Transaccion.TipoTransaccion.INGRESO));
        agregarEgresoButton.setOnAction(event -> abrirDialogoNuevaTransaccion(Transaccion.TipoTransaccion.EGRESO));
    }
    
    private void cargarDatosPorFecha(LocalDate fecha) {
        // Cargar ingresos del día
        BigDecimal ingresosDia = transaccionDAO.calcularTotalPorTipoYFecha(
                Transaccion.TipoTransaccion.INGRESO, fecha);
        ingresosDiaLabel.setText(String.format("$%,.0f", ingresosDia));
        
        // Cargar egresos del día
        BigDecimal egresosDia = transaccionDAO.calcularTotalPorTipoYFecha(
                Transaccion.TipoTransaccion.EGRESO, fecha);
        egresosDiaLabel.setText(String.format("$%,.0f", egresosDia));
        
        // Calcular balance
        BigDecimal balance = ingresosDia.subtract(egresosDia);
        balanceDiaLabel.setText(String.format("$%,.0f", balance));
        
        // Si balance es negativo, aplicar estilo
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balanceDiaLabel.getStyleClass().add("texto-negativo");
        } else {
            balanceDiaLabel.getStyleClass().remove("texto-negativo");
        }
        
        // Cargar listados de transacciones
        cargarTransacciones(fecha);
    }
    
    private void cargarTransacciones(LocalDate fecha) {
        // Limpiar containers
        ingresosContainer.getChildren().clear();
        egresosContainer.getChildren().clear();
        
        // Cargar ingresos
        List<Transaccion> ingresos = transaccionDAO.listarPorTipoYFecha(
                Transaccion.TipoTransaccion.INGRESO, fecha);
        
        for (Transaccion ingreso : ingresos) {
            ingresosContainer.getChildren().add(crearTransaccionItem(ingreso));
        }
        
        // Cargar egresos
        List<Transaccion> egresos = transaccionDAO.listarPorTipoYFecha(
                Transaccion.TipoTransaccion.EGRESO, fecha);
        
        for (Transaccion egreso : egresos) {
            egresosContainer.getChildren().add(crearTransaccionItem(egreso));
        }
    }
    
    private VBox crearTransaccionItem(Transaccion transaccion) {
        VBox item = new VBox(5);
        item.getStyleClass().add("transaccion-item");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String hora = transaccion.getFecha().format(formatter);
        
        Label conceptoLabel = new Label(transaccion.getConcepto());
        conceptoLabel.getStyleClass().add("transaccion-concepto");
        
        Label montoLabel = new Label(String.format("$%,.0f", transaccion.getMonto()));
        montoLabel.getStyleClass().add("transaccion-monto");
        
        Label horaLabel = new Label(hora);
        horaLabel.getStyleClass().add("transaccion-hora");
        
        item.getChildren().addAll(conceptoLabel, montoLabel, horaLabel);
        
        return item;
    }
    
    private void abrirDialogoNuevaTransaccion(Transaccion.TipoTransaccion tipo) {
        // Crear diálogo
        Dialog<Transaccion> dialog = new Dialog<>();
        dialog.setTitle("Nueva " + (tipo == Transaccion.TipoTransaccion.INGRESO ? "Ingreso" : "Egreso"));
        
        // Configurar botones
        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);
        
        // Crear campos del formulario
        VBox content = new VBox(10);
        
        Label conceptoLabel = new Label("Concepto:");
        TextField conceptoField = new TextField();
        
        Label montoLabel = new Label("Monto:");
        TextField montoField = new TextField();
        
        content.getChildren().addAll(conceptoLabel, conceptoField, montoLabel, montoField);
        dialog.getDialogPane().setContent(content);
        
        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                try {
                    String concepto = conceptoField.getText();
                    BigDecimal monto = new BigDecimal(montoField.getText().replace(",", ""));
                    
                    Transaccion nuevaTransaccion = new Transaccion();
                    nuevaTransaccion.setTipo(tipo);
                    nuevaTransaccion.setConcepto(concepto);
                    nuevaTransaccion.setMonto(monto);
                    
                    // Corregido: obtener la hora actual correctamente
                    LocalTime horaActual = LocalTime.now();
                    LocalDateTime fechaHora = LocalDateTime.of(LocalDate.now(), horaActual);
                    nuevaTransaccion.setFecha(fechaHora);
                    
                    nuevaTransaccion.setUsuario(SessionManager.getInstance().getUsuarioActual());
                    
                    return nuevaTransaccion;
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "El monto debe ser un número válido.");
                    return null;
                }
            }
            return null;
        });
        
        // Mostrar diálogo y procesar resultado
        dialog.showAndWait().ifPresent(transaccion -> {
            boolean exito = transaccionDAO.agregarTransaccion(transaccion);
            
            if (exito) {
                mostrarAlerta("Éxito", "Transacción guardada correctamente.");
                cargarDatosPorFecha(fechaSelector.getValue());
            } else {
                mostrarAlerta("Error", "No se pudo guardar la transacción.");
            }
        });
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}