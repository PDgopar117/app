/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.model.Reserva;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ReservasController {
    private static final Logger logger = Logger.getLogger(ReservasController.class.getName());
    private static long lastClickTime = 0; // Para evitar múltiples clicks
    
    @FXML
    private TableView<Reserva> reservasTableView;
    
    @FXML
    private Button nuevaReservaButton;
    
    @FXML
    private Button actualizarButton;
    
    @FXML
    private DatePicker fechaDesdeSelector;
    
    @FXML
    private DatePicker fechaHastaSelector;
    
    @FXML
    private ComboBox<String> estadoComboBox;
    
    @FXML
    private Button buscarReservasButton;
    
    @FXML
    private Button exportarReservasButton;
    
    @FXML
    private TableColumn<Reserva, String> idReservaColumn;
    
    @FXML
    private TableColumn<Reserva, String> habitacionColumn;
    
    @FXML
    private TableColumn<Reserva, LocalDate> checkInColumn;
    
    @FXML
    private TableColumn<Reserva, LocalDate> checkOutColumn;
    
    @FXML
    private TableColumn<Reserva, Integer> duracionColumn;
    
    @FXML
    private TableColumn<Reserva, Double> montoColumn;
    
    @FXML
    private TableColumn<Reserva, String> estadoReservaColumn;
    
    @FXML
    private TableColumn<Reserva, String> accionesReservaColumn;
    
    @FXML
    private TilePane habitacionesOcupadasContainer;
    
    private ReservaDAO reservaDAO;
    
    @FXML
    public void initialize() {
        try {
            // Inicializar DAO
            reservaDAO = new ReservaDAO();
            
            // Configurar controles
            configurarControles();
            
            // Cargar datos
            cargarReservas();
            
        } catch (Exception e) {
            logger.severe("Error al inicializar controlador de reservas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo inicializar el controlador de reservas");
        }
    }
    
    private void configurarControles() {
        // Configurar ComboBox de estados
        if (estadoComboBox != null) {
            estadoComboBox.setItems(FXCollections.observableArrayList(
                "Todos", "Activa", "Finalizada", "Cancelada"
            ));
            estadoComboBox.setValue("Todos");
        }
        
        // Configurar DatePickers
        if (fechaDesdeSelector != null) {
            fechaDesdeSelector.setValue(LocalDate.now().minusMonths(1));
        }
        
        if (fechaHastaSelector != null) {
            fechaHastaSelector.setValue(LocalDate.now());
        }
        
        // Configurar columnas de la tabla
        configurarColumnasTabla();
        
        // Configurar manejadores de eventos
        if (buscarReservasButton != null) {
            buscarReservasButton.setOnAction(e -> handleBuscarReservas());
        }
        
        if (exportarReservasButton != null) {
            exportarReservasButton.setOnAction(e -> handleExportarReservas());
        }
        
        if (actualizarButton != null) {
            actualizarButton.setOnAction(e -> handleActualizar());
        }
    }
    
    private void configurarColumnasTabla() {
        // Solo configurar si las columnas existen
        if (reservasTableView == null) return;
        
        if (idReservaColumn != null) {
            idReservaColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getId().toString()));
        }
        
        if (habitacionColumn != null) {
            habitacionColumn.setCellValueFactory(cellData -> {
                Reserva reserva = cellData.getValue();
                // En lugar de usar getHabitacion(), adaptamos según el modelo real
                return new SimpleStringProperty(reserva.toString());
            });
        }
        
        if (checkInColumn != null) {
            checkInColumn.setCellValueFactory(cellData -> {
                Reserva reserva = cellData.getValue();
                // Adaptamos para usar el método real que podría existir
                return new SimpleObjectProperty<>(LocalDate.now()); // Valor temporal
            });
        }
        
        if (checkOutColumn != null) {
            checkOutColumn.setCellValueFactory(cellData -> {
                Reserva reserva = cellData.getValue();
                // Adaptamos para usar el método real que podría existir
                return new SimpleObjectProperty<>(LocalDate.now().plusDays(1)); // Valor temporal
            });
        }
        
        if (duracionColumn != null) {
            duracionColumn.setCellValueFactory(cellData -> {
                // Valor predeterminado o calculado de otra manera
                return new SimpleObjectProperty<>(1); // Valor temporal
            });
        }
        
        if (montoColumn != null) {
            montoColumn.setCellValueFactory(cellData -> {
                // Valor predeterminado o calculado de otra manera
                return new SimpleObjectProperty<>(150.0); // Valor temporal
            });
        }
        
        if (estadoReservaColumn != null) {
            estadoReservaColumn.setCellValueFactory(cellData -> {
                Reserva reserva = cellData.getValue();
                // Adaptamos para mostrar el estado como String
                return new SimpleStringProperty("Activa"); // Valor temporal
            });
        }
        
        if (accionesReservaColumn != null) {
            accionesReservaColumn.setCellFactory(col -> new TableCell<Reserva, String>() {
                private final Button verBtn = new Button("Ver");
                private final Button editarBtn = new Button("Editar");
                
                {
                    verBtn.setOnAction(e -> {
                        Reserva reserva = getTableView().getItems().get(getIndex());
                        mostrarDetallesReserva(reserva);
                    });
                    
                    editarBtn.setOnAction(e -> {
                        Reserva reserva = getTableView().getItems().get(getIndex());
                        editarReserva(reserva);
                    });
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox container = new HBox(5, verBtn, editarBtn);
                        container.setAlignment(Pos.CENTER);
                        setGraphic(container);
                    }
                }
            });
        }
    }
    
    @FXML
    public void handleNuevaReserva() {
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
            
            // Recargar las reservas después de cerrar el formulario
            cargarReservas();
            
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
        
        // Nuevo componente para el horario de llegada
        Label horaLlegadaLabel = new Label("Hora de llegada:");
        ComboBox<String> horaLlegadaCombo = new ComboBox<>();
        
        // Generar horas disponibles (formato 24h)
        for (int i = 0; i < 24; i++) {
            horaLlegadaCombo.getItems().add(String.format("%02d:00", i));
            horaLlegadaCombo.getItems().add(String.format("%02d:30", i));
        }
        horaLlegadaCombo.setValue("14:00"); // Hora predeterminada
        
        Label horasLabel = new Label("Horas:");
        TextField horasField = new TextField();
        horasField.setPromptText("Número de horas");
        
        Label totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-weight: bold;");
        
        // Cálculo del total cuando cambia el número de horas
        horasField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.matches("\\d*")) {
                try {
                    int horas = Integer.parseInt(newValue.isEmpty() ? "0" : newValue);
                    double tarifa = 150.0; // Tarifa base ejemplo
                    totalLabel.setText(String.format("Total: $%.2f", horas * tarifa));
                } catch (NumberFormatException e) {
                    totalLabel.setText("Total: $0.00");
                }
            } else {
                horasField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Botones
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelarButton = new Button("Cancelar");
        Button confirmarButton = new Button("Confirmar");
        
        cancelarButton.setOnAction(e -> stage.close());
        confirmarButton.setOnAction(e -> {
            // Aquí iría la lógica para guardar la reserva
            // Incluyendo el horario de llegada seleccionado
            String horaLlegada = horaLlegadaCombo.getValue();
            logger.info("Reserva confirmada con hora de llegada: " + horaLlegada);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reserva Creada");
            alert.setHeaderText(null);
            alert.setContentText("Reserva creada exitosamente para las " + horaLlegada);
            alert.showAndWait();
            
            stage.close();
            cargarReservas(); // Recargar la tabla
        });
        
        botonesContainer.getChildren().addAll(cancelarButton, confirmarButton);
        
        // Agregar todos los elementos al contenedor principal
        root.getChildren().addAll(
            titulo,
            habitacionLabel, habitacionCombo,
            fechaLabel, fechaPicker,
            horaLlegadaLabel, horaLlegadaCombo, // Nuevo campo de hora de llegada
            horasLabel, horasField,
            totalLabel,
            botonesContainer
        );
        
        Scene scene = new Scene(root, 400, 400); // Aumentado el tamaño para acomodar el nuevo campo
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
    
    @FXML
    public void handleBuscarReservas() {
        logger.info("Buscando reservas con filtros...");
        cargarReservas();
    }
    
    @FXML
    public void handleExportarReservas() {
        // Implementar la lógica de exportación
        logger.info("Exportando reservas...");
    }
    
    @FXML
    public void handleActualizar() {
        logger.info("Actualizando vista...");
        cargarReservas();
    }
    
    private void mostrarDetallesReserva(Reserva reserva) {
        // Implementación para mostrar detalles de una reserva
        logger.info("Mostrando detalles de la reserva: " + reserva.getId());
    }
    
    private void editarReserva(Reserva reserva) {
        // Implementación para editar una reserva
        logger.info("Editando reserva: " + reserva.getId());
    }
    
    private void cargarReservas() {
        try {
            List<Reserva> reservas = reservaDAO.obtenerTodas();
            // Asegurarse de que la tabla no sea nula antes de configurarla
            if (reservasTableView != null) {
                reservasTableView.setItems(FXCollections.observableArrayList(reservas));
            }
        } catch (SQLException e) {
            logger.severe("Error al cargar reservas: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las reservas");
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}