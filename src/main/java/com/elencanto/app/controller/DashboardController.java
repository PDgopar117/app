/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
/**
 *
 * @author Gopar117
 */
public class DashboardController {
    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());
    
    @FXML
    private Label ocupacionLabel;
    
    @FXML
    private Label habitacionesDisponiblesLabel;
    
    @FXML
    private Label ingresosDiaLabel;
    
    @FXML
    private Label egresosDiaLabel;
    
    @FXML
    private Button nuevaReservaButton;
    
    @FXML
private javafx.scene.layout.FlowPane habitacionesContainer;
    
    private HabitacionDAO habitacionDAO = new HabitacionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    
    @FXML
    public void initialize() {
        cargarDatosDashboard();
        configurarBotones();
    }
    
    private void cargarDatosDashboard() {
        // Cargar ocupación
        int totalHabitaciones = habitacionDAO.contarTotalHabitaciones();
        int habitacionesOcupadas = habitacionDAO.contarHabitacionesPorEstado(Habitacion.EstadoHabitacion.OCUPADA);
        
        if (totalHabitaciones > 0) {
            double porcentaje = (double) habitacionesOcupadas / totalHabitaciones * 100;
            ocupacionLabel.setText(String.format("%.0f%%", porcentaje));
        } else {
            ocupacionLabel.setText("0%");
        }
        
        // Cargar habitaciones disponibles
        int disponibles = habitacionDAO.contarHabitacionesPorEstado(Habitacion.EstadoHabitacion.DISPONIBLE);
        habitacionesDisponiblesLabel.setText(String.valueOf(disponibles));
        
        // Cargar ingresos del día
        BigDecimal ingresosDia = transaccionDAO.calcularTotalPorTipoYFecha(
                Transaccion.TipoTransaccion.INGRESO, LocalDate.now());
        ingresosDiaLabel.setText(String.format("$%,.0f", ingresosDia));
        
        // Cargar egresos del día
        BigDecimal egresosDia = transaccionDAO.calcularTotalPorTipoYFecha(
                Transaccion.TipoTransaccion.EGRESO, LocalDate.now());
        egresosDiaLabel.setText(String.format("$%,.0f", egresosDia));
        
        // Cargar estado de habitaciones en contenedor
        cargarEstadoHabitaciones();
    }
    
    private void cargarEstadoHabitaciones() {
        habitacionesContainer.getChildren().clear();
        
        List<Habitacion> habitaciones = habitacionDAO.listarTodas();
        
        for (Habitacion habitacion : habitaciones) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HabitacionItem.fxml"));
                Parent item = loader.load();
                
                HabitacionItemController controller = loader.getController();
                controller.configurarHabitacion(habitacion);
                
                habitacionesContainer.getChildren().add(item);
            } catch (IOException e) {
                logger.severe("Error al cargar ítem de habitación: " + e.getMessage());
            }
        }
    }
    
    private void configurarBotones() {
        nuevaReservaButton.setOnAction(event -> abrirNuevaReserva());
    }
    
    private void abrirNuevaReserva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevaReserva.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nueva Reserva");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Recargar datos al cerrar la ventana
            cargarDatosDashboard();
        } catch (IOException e) {
            logger.severe("Error al abrir ventana de nueva reserva: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDashboardClick(ActionEvent event) {
        // Ya estamos en Dashboard, no hacer nada
    }
    
    @FXML
    private void handleHabitacionesClick(ActionEvent event) {
        cargarVista("/fxml/Habitaciones.fxml", "Habitaciones");
    }
    
    @FXML
    private void handleReservasClick(ActionEvent event) {
        cargarVista("/fxml/Reservas.fxml", "Reservas");
    }
    
    @FXML
    private void handleFinanzasClick(ActionEvent event) {
        cargarVista("/fxml/Finanzas.fxml", "Finanzas");
    }
    
    @FXML
    private void handleReportesClick(ActionEvent event) {
        cargarVista("/fxml/Reportes.fxml", "Reportes");
    }
    
    @FXML
    private void handleConfiguracionClick(ActionEvent event) {
        cargarVista("/fxml/Configuracion.fxml", "Configuración");
    }
    
    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            // Limpiar sesión
            SessionManager.getInstance().cerrarSesion();
            
            // Cargar pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent loginRoot = loader.load();
            
            Scene scene = new Scene(loginRoot);
            Stage stage = (Stage) ocupacionLabel.getScene().getWindow();
            
            stage.setTitle("El Encanto - Iniciar Sesión");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
            logger.info("Sesión cerrada correctamente");
        } catch (IOException e) {
            logger.severe("Error al cerrar sesión: " + e.getMessage());
        }
    }
    
    private void cargarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ocupacionLabel.getScene().getWindow();
            
            stage.setTitle("El Encanto - " + titulo);
            stage.setScene(scene);
            
            logger.info("Vista " + titulo + " cargada correctamente");
        } catch (IOException e) {
            logger.severe("Error al cargar vista " + titulo + ": " + e.getMessage());
        }
    }
}
