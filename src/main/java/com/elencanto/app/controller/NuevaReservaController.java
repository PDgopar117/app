/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;
import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.Reserva;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class NuevaReservaController {
      private static final Logger logger = Logger.getLogger(NuevaReservaController.class.getName());
    
    @FXML
    private VBox habitacionesContainer;
    
    @FXML
    private TextField duracionField;
    
    @FXML
    private Label montoTotalLabel;
    
    @FXML
    private Button reservarButton;
    
    private HabitacionDAO habitacionDAO = new HabitacionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    
    private Habitacion habitacionSeleccionada;
    private BigDecimal tarifaTotal = BigDecimal.ZERO;
    
    @FXML
    public void initialize() {
        cargarHabitacionesDisponibles();
        configurarCampos();
    }
    
    private void cargarHabitacionesDisponibles() {
        List<Habitacion> habitacionesDisponibles = habitacionDAO.listarPorEstado(Habitacion.EstadoHabitacion.DISPONIBLE);
        
        for (Habitacion habitacion : habitacionesDisponibles) {
            HBox habitacionItem = crearHabitacionItem(habitacion);
            habitacionesContainer.getChildren().add(habitacionItem);
        }
    }
    
    private HBox crearHabitacionItem(Habitacion habitacion) {
        HBox item = new HBox(10);
        item.getStyleClass().add("habitacion-item");
        
        Label numeroLabel = new Label("Hab. " + habitacion.getNumero());
        Label tipoLabel = new Label(habitacion.getTipo().toString());
        
        item.getChildren().addAll(numeroLabel, tipoLabel);
        
        // Evento de clic para seleccionar habitación
        item.setOnMouseClicked(event -> seleccionarHabitacion(habitacion, item));
        
        return item;
    }
    
    private void seleccionarHabitacion(Habitacion habitacion, HBox item) {
        // Quitar selección anterior
        habitacionesContainer.getChildren().forEach(node -> {
            node.getStyleClass().remove("habitacion-seleccionada");
        });
        
        // Añadir selección a esta habitación
        item.getStyleClass().add("habitacion-seleccionada");
        
        this.habitacionSeleccionada = habitacion;
        calcularMonto();
    }
    
    private void configurarCampos() {
        // Configurar campo de duración para actualizar monto
        duracionField.textProperty().addListener((obs, oldValue, newValue) -> {
            calcularMonto();
        });
        
        // Configurar botón de reservar
        reservarButton.setOnAction(event -> realizarReserva());
    }
    
    private void calcularMonto() {
        if (habitacionSeleccionada == null) {
            montoTotalLabel.setText("$0");
            tarifaTotal = BigDecimal.ZERO;
            return;
        }
        
        try {
            // Obtener horas de duración (por defecto 1 si está vacío)
            String duracionText = duracionField.getText().trim();
            int horas = duracionText.isEmpty() ? 1 : Integer.parseInt(duracionText);
            
            // Calcular tarifa
            tarifaTotal = habitacionSeleccionada.getTarifa().multiply(BigDecimal.valueOf(horas));
            
            // Mostrar monto formateado
            montoTotalLabel.setText(String.format("$%,.0f", tarifaTotal));
        } catch (NumberFormatException e) {
            montoTotalLabel.setText("$0");
            tarifaTotal = BigDecimal.ZERO;
        }
    }
    
    private void realizarReserva() {
        if (habitacionSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una habitación.");
            return;
        }
        
        try {
            String duracionText = duracionField.getText().trim();
            int horas = duracionText.isEmpty() ? 1 : Integer.parseInt(duracionText);
            
            if (horas <= 0) {
                mostrarAlerta("Error", "La duración debe ser mayor a 0 horas.");
                return;
            }
            
            // Crear nueva reserva
            Reserva reserva = new Reserva();
            reserva.setHabitacion(habitacionSeleccionada);
            reserva.setCheckIn(LocalDateTime.now());
            reserva.setCheckOut(LocalDateTime.now().plusHours(horas));
            reserva.setTotalPagado(tarifaTotal);
            reserva.setEstado(Reserva.EstadoReserva.ACTIVA);
            
            // Guardar reserva en base de datos
            boolean exito = reservaDAO.agregarReserva(reserva);
            
            if (exito) {
                // Actualizar estado de la habitación
                habitacionSeleccionada.setEstado(Habitacion.EstadoHabitacion.OCUPADA);
                habitacionDAO.actualizarHabitacion(habitacionSeleccionada);
                
                // Registrar transacción de ingreso
                Transaccion transaccion = new Transaccion();
                transaccion.setTipo(Transaccion.TipoTransaccion.INGRESO);
                transaccion.setConcepto("Reserva Hab. " + habitacionSeleccionada.getNumero());
                transaccion.setMonto(tarifaTotal);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setUsuario(SessionManager.getInstance().getUsuarioActual());
                transaccion.setReserva(reserva);
                
                transaccionDAO.agregarTransaccion(transaccion);
                
                mostrarAlerta("Éxito", "Reserva realizada correctamente.");
                
                // Cerrar ventana
                Stage stage = (Stage) reservarButton.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la reserva.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La duración debe ser un número válido.");
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
