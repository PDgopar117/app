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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class HabitacionesOcupadasController {
    private static final Logger logger = Logger.getLogger(HabitacionesOcupadasController.class.getName());
    
    @FXML
    private VBox habitacionesContainer;
    
    private ReservaDAO reservaDAO = new ReservaDAO();
    private HabitacionDAO habitacionDAO = new HabitacionDAO();
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML
    public void initialize() {
        cargarHabitacionesOcupadas();
    }
    
    private void cargarHabitacionesOcupadas() {
        habitacionesContainer.getChildren().clear();
        
        List<Reserva> reservasActivas = reservaDAO.listarPorEstado(Reserva.EstadoReserva.ACTIVA);
        
        for (Reserva reserva : reservasActivas) {
            VBox reservaItem = crearReservaItem(reserva);
            habitacionesContainer.getChildren().add(reservaItem);
        }
    }
    
    private VBox crearReservaItem(Reserva reserva) {
        VBox item = new VBox(5);
        item.getStyleClass().add("reserva-item");
        
        Habitacion habitacion = reserva.getHabitacion();
        
        // Crear elementos de la reserva
        Button habitacionBtn = new Button("Habitación " + habitacion.getNumero());
        habitacionBtn.getStyleClass().add("titulo-habitacion");
        
        String tipoHabitacion = habitacion.getTipo() == Habitacion.TipoHabitacion.STANDARD ? 
                                "Standard" : "Suite";
        
        Button tipoBtn = new Button(tipoHabitacion);
        tipoBtn.getStyleClass().add("tipo-habitacion");
        
        String checkInTexto = "Check-in: " + reserva.getCheckIn().format(formatter);
        String checkOutTexto = "Check-out: " + reserva.getCheckOut().format(formatter);
        
        Button checkInBtn = new Button(checkInTexto);
        Button checkOutBtn = new Button(checkOutTexto);
        
        Duration tiempoRestante = reserva.getTiempoRestante();
        String tiempoTexto = "";
        
        if (tiempoRestante.isNegative()) {
            tiempoTexto = "Tiempo excedido";
        } else {
            long horas = tiempoRestante.toHours();
            long minutos = tiempoRestante.toMinutesPart();
            tiempoTexto = String.format("%dh %02dm", horas, minutos);
        }
        
        Button tiempoBtn = new Button("Restante: " + tiempoTexto);
        
        Button finalizarBtn = new Button("Finalizar");
        finalizarBtn.getStyleClass().add("btn-finalizar");
        finalizarBtn.setOnAction(event -> finalizarReserva(reserva));
        
        // Añadir elementos al contenedor
        item.getChildren().addAll(habitacionBtn, tipoBtn, checkInBtn, 
                                 checkOutBtn, tiempoBtn, finalizarBtn);
        
        return item;
    }
    
    private void finalizarReserva(Reserva reserva) {
        try {
            // Cambiar estado de la reserva
            reserva.setEstado(Reserva.EstadoReserva.FINALIZADA);
            reservaDAO.actualizarReserva(reserva);
            
            // Cambiar estado de la habitación a limpieza
            Habitacion habitacion = reserva.getHabitacion();
            habitacion.setEstado(Habitacion.EstadoHabitacion.LIMPIEZA);
            habitacionDAO.actualizarHabitacion(habitacion);
            
            mostrarAlerta("Éxito", "Reserva finalizada correctamente.");
            
            // Recargar la lista
            cargarHabitacionesOcupadas();
        } catch (Exception e) {
            logger.severe("Error al finalizar reserva: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo finalizar la reserva.");
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
