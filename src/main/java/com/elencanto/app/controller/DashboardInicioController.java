/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DashboardInicioController {
    private static final Logger logger = Logger.getLogger(DashboardInicioController.class.getName());
    private static long lastClickTime = 0; // Para evitar múltiples clicks

    @FXML
    private Label ocupacionLabel;
    
    @FXML
    private Label habitacionesLabel;
    
    @FXML
    private Label ingresosLabel;
    
    @FXML
    private Label egresosLabel;
    
    @FXML
    private GridPane habitacionesGrid;

    @FXML
    public void initialize() {
        cargarEstadisticas();
        cargarHabitaciones();
    }

    @FXML
    private void handleNuevaReserva() {
        // Evitar múltiples llamadas en corto tiempo
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 500) { // 500ms debounce
            return;
        }
        lastClickTime = currentTime;
        
        logger.info("Abriendo formulario de nueva reserva...");
        
        try {
            // Intentar cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevaReserva.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Nueva Reserva");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            stage.showAndWait();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir formulario de nueva reserva", e);
            
            // Si falla, mostrar un formulario simplificado
            crearFormularioReservaSencillo();
        }
    }

    private void crearFormularioReservaSencillo() {
        Stage stage = new Stage();
        stage.setTitle("Nueva Reserva");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);
        
        Label titulo = new Label("Nueva Reserva");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label habitacionLabel = new Label("Habitación:");
        ComboBox<String> habitacionCombo = new ComboBox<>(
            FXCollections.observableArrayList("Habitación 101", "Habitación 102", "Habitación 201")
        );
        habitacionCombo.setPromptText("Seleccione una habitación");
        
        Label fechaLabel = new Label("Fecha:");
        DatePicker fechaPicker = new DatePicker(LocalDate.now());
        
        Label horasLabel = new Label("Horas:");
        TextField horasField = new TextField();
        horasField.setPromptText("Número de horas");
        
        Label totalLabel = new Label("Total: 0.00");
        totalLabel.setStyle("-fx-font-weight: bold;");
        
        // Cálculo del total
        horasField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.matches("\\d*")) {
                try {
                    int horas = Integer.parseInt(newValue.isEmpty() ? "0" : newValue);
                    double tarifa = 150.0; // Tarifa base ejemplo
                    totalLabel.setText(String.format("Total: $%.2f", horas * tarifa));
                } catch (NumberFormatException e) {
                    totalLabel.setText("Total: 0.00");
                }
            } else {
                horasField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        HBox buttons = new HBox(10);
        Button confirmarBtn = new Button("Confirmar Reserva");
        confirmarBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        Button cancelarBtn = new Button("Cancelar");
        
        confirmarBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reserva Creada");
            alert.setHeaderText(null);
            alert.setContentText("Reserva creada exitosamente");
            alert.showAndWait();
            stage.close();
        });
        
        cancelarBtn.setOnAction(e -> stage.close());
        
        buttons.getChildren().addAll(confirmarBtn, cancelarBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        root.getChildren().addAll(
            titulo,
            habitacionLabel, habitacionCombo,
            fechaLabel, fechaPicker,
            horasLabel, horasField,
            totalLabel,
            new Separator(),
            buttons
        );
        
        stage.setScene(new Scene(root, 400, 350));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void cargarEstadisticas() {
        // Aquí cargarías los datos reales desde tu base de datos
        ocupacionLabel.setText("75%");
        habitacionesLabel.setText("8");
        ingresosLabel.setText("458.900");
        egresosLabel.setText("156.700");
    }

    private void cargarHabitaciones() {
        // Ejemplo de cómo agregar una habitación
        for (int i = 0; i < 5; i++) {
            VBox habitacionCard = crearTarjetaHabitacion(i + 1);
            habitacionesGrid.add(habitacionCard, i % 4, i / 4);
        }
    }

    private VBox crearTarjetaHabitacion(int numero) {
        VBox card = new VBox();
        card.getStyleClass().add("room-card");
        
        HBox header = new HBox();
        Label numeroLabel = new Label("Hab. " + String.format("%02d", numero));
        numeroLabel.getStyleClass().add("room-number");
        
        Circle statusCircle = new Circle(4);
        statusCircle.getStyleClass().addAll("status-circle", "occupied");
        
        header.getChildren().addAll(numeroLabel, statusCircle);
        
        Label tipoLabel = new Label("standard");
        tipoLabel.getStyleClass().add("room-type");
        
        Label horaLabel = new Label("14:30");
        horaLabel.getStyleClass().add("room-time");
        
        card.getChildren().addAll(header, tipoLabel, horaLabel);
        return card;
    }
}