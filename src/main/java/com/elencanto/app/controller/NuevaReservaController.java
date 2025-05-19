/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.Reserva;
import com.elencanto.app.model.EstadoHabitacion;
import com.elencanto.app.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import javafx.util.StringConverter;

public class NuevaReservaController {
    private static final Logger logger = Logger.getLogger(NuevaReservaController.class.getName());

    @FXML
    private ComboBox<Habitacion> habitacionComboBox;
    
    @FXML
    private DatePicker fechaInicioPicker;
    
    @FXML
    private TextField horasField;
    
    @FXML
    private Label totalLabel;
    
    private HabitacionDAO habitacionDAO;
    private ReservaDAO reservaDAO;

    @FXML
    public void initialize() {
        habitacionDAO = new HabitacionDAO();
        reservaDAO = new ReservaDAO();
        
        configurarComboBox();
        cargarHabitacionesDisponibles();
        
        // Agregar listeners para cálculo automático
        habitacionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calcularTotal());
        horasField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                horasField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            calcularTotal();
        });
    }

    private void configurarComboBox() {
        habitacionComboBox.setConverter(new StringConverter<Habitacion>() {
            @Override
            public String toString(Habitacion habitacion) {
                if (habitacion != null) {
                    return String.format("Habitación %s - %s ($%.2f/hora)", 
                        habitacion.getNumero(), 
                        habitacion.getTipo(), 
                        habitacion.getTarifa());
                }
                return null;
            }

            @Override
            public Habitacion fromString(String string) {
                return null;
            }
        });
    }

    private void cargarHabitacionesDisponibles() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas().stream()
                .filter(h -> EstadoHabitacion.DISPONIBLE.equals(h.getEstado()))
                .toList();
            habitacionComboBox.setItems(FXCollections.observableArrayList(habitaciones));
        } catch (SQLException e) {
            logger.severe("Error al cargar habitaciones: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las habitaciones disponibles");
        }
    }

    private void calcularTotal() {
        try {
            Habitacion habitacion = habitacionComboBox.getValue();
            String horasTexto = horasField.getText();
            
            if (habitacion != null && !horasTexto.isEmpty()) {
                int horas = Integer.parseInt(horasTexto);
                BigDecimal tarifa = BigDecimal.valueOf(habitacion.getTarifa());
                BigDecimal total = tarifa.multiply(BigDecimal.valueOf(horas));
                totalLabel.setText(String.format("Total: $%.2f", total.doubleValue()));
            } else {
                totalLabel.setText("Total: $0.00");
            }
        } catch (NumberFormatException e) {
            logger.warning("Error al calcular total: " + e.getMessage());
            totalLabel.setText("Total: $0.00");
        }
    }

    @FXML
    private void confirmarReserva() {
        try {
            if (!validarCampos()) {
                return;
            }

            Habitacion habitacion = habitacionComboBox.getValue();
            LocalDateTime fechaInicio = LocalDateTime.now();
            int horas = Integer.parseInt(horasField.getText());
            LocalDateTime fechaFin = fechaInicio.plusHours(horas);
            
            Reserva reserva = new Reserva();
            reserva.setHabitacionId(habitacion.getId());
            reserva.setCheckIn(fechaInicio);
            reserva.setCheckOut(fechaFin);
            reserva.setTotalPagado(new BigDecimal(totalLabel.getText().replace("Total: $", "")));
            reserva.setEstado(Reserva.EstadoReserva.ACTIVA);

            if (reservaDAO.crear(reserva)) {
                habitacion.setEstado(EstadoHabitacion.OCUPADA);
                habitacionDAO.actualizarHabitacion(habitacion);
                
                mostrarAlerta("Éxito", "Reserva creada correctamente");
                limpiarFormulario();
                cargarHabitacionesDisponibles();
            }
        } catch (SQLException e) {
            logger.severe("Error al crear reserva: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo crear la reserva");
        }
    }

    private boolean validarCampos() {
        if (habitacionComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una habitación");
            return false;
        }

        if (horasField.getText().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar la cantidad de horas");
            return false;
        }

        try {
            int horas = Integer.parseInt(horasField.getText());
            if (horas <= 0) {
                mostrarAlerta("Error", "La cantidad de horas debe ser mayor a 0");
                return false;
            }
            if (horas > 24) {
                mostrarAlerta("Error", "No se pueden reservar más de 24 horas");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La cantidad de horas debe ser un número válido");
            return false;
        }

        return true;
    }

    @FXML
    private void limpiarFormulario() {
        habitacionComboBox.setValue(null);
        horasField.clear();
        totalLabel.setText("Total: $0.00");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}