/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.EstadoHabitacion;
import com.elencanto.app.model.TipoHabitacion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HabitacionesController {
    private static final Logger logger = Logger.getLogger(HabitacionesController.class.getName());
    private static long lastClickTime = 0;

    @FXML
    private Button agregarHabitacionButton;

    @FXML
    private Label totalHabitacionesLabel;

    @FXML
    private Label habitacionesDisponiblesLabel;

    @FXML
    private Label habitacionesOcupadasLabel;

    @FXML
    private Label habitacionesLimpiezaLabel;

    @FXML
    private TextField buscarField;

    @FXML
    private ComboBox<String> filtroTipoComboBox;

    @FXML
    private ComboBox<String> filtroEstadoComboBox;

    @FXML
    private TableView<Habitacion> habitacionesTableView;

    @FXML
    private TableColumn<Habitacion, String> numeroColumn;

    @FXML
    private TableColumn<Habitacion, String> tipoColumn;

    @FXML
    private TableColumn<Habitacion, String> estadoColumn;

    @FXML
    private TableColumn<Habitacion, Double> tarifaColumn;

    @FXML
    private TableColumn<Habitacion, Void> accionesColumn;

    private HabitacionDAO habitacionDAO;

    @FXML
    public void initialize() {
        try {
            // Inicializar DAO
            habitacionDAO = new HabitacionDAO();
            
            // Conectar el botón programáticamente (alternativa a modificar el FXML)
            agregarHabitacionButton.setOnAction(event -> agregarHabitacion());
            
            // Configurar columnas
            numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));
            tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            tarifaColumn.setCellValueFactory(new PropertyValueFactory<>("tarifa"));
            
            // Configurar filtros
            filtroTipoComboBox.getItems().add("Todos");
            for (TipoHabitacion tipo : TipoHabitacion.values()) {
                filtroTipoComboBox.getItems().add(tipo.name());
            }
            
            filtroEstadoComboBox.getItems().add("Todos");
            for (EstadoHabitacion estado : EstadoHabitacion.values()) {
                filtroEstadoComboBox.getItems().add(estado.name());
            }
            
            // Cargar datos
            cargarHabitaciones();
            
        } catch (Exception e) {
            logger.severe("Error al inicializar controlador: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo inicializar el controlador de habitaciones");
        }
    }

    private void cargarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas();
            habitacionesTableView.setItems(FXCollections.observableArrayList(habitaciones));
            
            // Actualizar contadores
            actualizarContadores(habitaciones);
            
        } catch (SQLException e) {
            logger.severe("Error al cargar habitaciones: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las habitaciones");
        }
    }

    private void actualizarContadores(List<Habitacion> habitaciones) {
        int total = habitaciones.size();
        int disponibles = 0;
        int ocupadas = 0;
        int limpieza = 0;
        
        for (Habitacion h : habitaciones) {
            switch (h.getEstado()) {
                case DISPONIBLE:
                    disponibles++;
                    break;
                case OCUPADA:
                    ocupadas++;
                    break;
                case LIMPIEZA:
                    limpieza++;
                    break;
            }
        }
        
        totalHabitacionesLabel.setText(String.valueOf(total));
        habitacionesDisponiblesLabel.setText(String.valueOf(disponibles));
        habitacionesOcupadasLabel.setText(String.valueOf(ocupadas));
        habitacionesLimpiezaLabel.setText(String.valueOf(limpieza));
    }

    @FXML
    private void agregarHabitacion() {
        // Implementar lógica para agregar habitación
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastClickTime < 500){
            return;
        }
        lastClickTime = currentTime;
        
        logger.info("Abriendo formulario de nueva habitacion...");
        
        try {
            // Intentar cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NuevaHabitacion.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Nueva Habitación");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            stage.showAndWait();
            
            // Después de cerrar el diálogo, recargar las habitaciones
            cargarHabitaciones();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir formulario de nueva habitación", e);
            
            // Si falla, mostrar un formulario simplificado
            crearFormularioHabitacionSencillo();
        }
    }

    private void crearFormularioHabitacionSencillo() {
        Stage stage = new Stage();
        stage.setTitle("Nueva Habitación");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);
        
        Label titulo = new Label("Crear Nueva Habitación");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label numeroLabel = new Label("Número de Habitación:");
        TextField numeroField = new TextField();
        numeroField.setPromptText("Ejemplo: 101");
        
        Label tipoLabel = new Label("Tipo de Habitación:");
        ComboBox<TipoHabitacion> tipoCombo = new ComboBox<>(
            FXCollections.observableArrayList(TipoHabitacion.values())
        );
        
        
        Label tarifaLabel = new Label("Tarifa por Hora:");
        TextField tarifaField = new TextField();
        tarifaField.setPromptText("Ejemplo: 150.00");
        
        Label caracteristicasLabel = new Label("Características:");
        TextArea caracteristicasArea = new TextArea();
        caracteristicasArea.setPrefRowCount(4);
        caracteristicasArea.setPromptText("Ingrese características separadas por comas");
        
        HBox buttons = new HBox(10);
        Button guardarBtn = new Button("Guardar Habitación");
        guardarBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        Button cancelarBtn = new Button("Cancelar");
        
        guardarBtn.setOnAction(e -> {
            // Validar campos
            if (numeroField.getText().isEmpty() || tarifaField.getText().isEmpty()) {
                mostrarAlerta("Error", "Debe completar los campos obligatorios");
                return;
            }
            
            try {
                double tarifa = Double.parseDouble(tarifaField.getText());
                
                // Crear objeto habitación
                Habitacion nuevaHabitacion = new Habitacion();
                nuevaHabitacion.setNumero(numeroField.getText());
                nuevaHabitacion.setTipo(tipoCombo.getValue());
                nuevaHabitacion.setEstado(EstadoHabitacion.DISPONIBLE);
                nuevaHabitacion.setTarifa(tarifa);
                nuevaHabitacion.setCaracteristicas(caracteristicasArea.getText());
                
                // Guardar en la base de datos
                try {
                    if (habitacionDAO.crearHabitacion(nuevaHabitacion)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setHeaderText(null);
                        alert.setContentText("Habitación creada exitosamente");
                        alert.showAndWait();
                        
                        // Recargar habitaciones
                        cargarHabitaciones();
                        
                        stage.close();
                    } else {
                        mostrarAlerta("Error", "No se pudo crear la habitación");
                    }
                } catch (SQLException ex) {
                    logger.severe("Error al crear habitación: " + ex.getMessage());
                    mostrarAlerta("Error", "Error al crear habitación: " + ex.getMessage());
                }
                
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "La tarifa debe ser un número válido");
            }
        });
        
        cancelarBtn.setOnAction(e -> stage.close());
        
        buttons.getChildren().addAll(guardarBtn, cancelarBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        root.getChildren().addAll(
            titulo,
            numeroLabel, numeroField,
            tipoLabel, tipoCombo,
            tarifaLabel, tarifaField,
            caracteristicasLabel, caracteristicasArea,
            new Separator(),
            buttons
        );
        
        stage.setScene(new Scene(root, 450, 450));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnHidden(e -> cargarHabitaciones()); // Recargar al cerrar
        stage.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}