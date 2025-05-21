/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Gopar117
 */
public class ReportesController {
    private static final Logger logger = Logger.getLogger(ReportesController.class.getName());
    
    @FXML
    private Button reporteVentasButton;
    
    @FXML
    private Button reporteOcupacionButton;
    
    @FXML
    private Button reporteFinancieroButton;
    
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();
    
    @FXML
    public void initialize() {
        configurarBotones();
    }
    
    private void configurarBotones() {
        reporteVentasButton.setOnAction(event -> abrirReporteVentas());
        reporteOcupacionButton.setOnAction(event -> abrirReporteOcupacion());
        reporteFinancieroButton.setOnAction(event -> abrirReporteFinanciero());
    }
    
    private void abrirReporteVentas() {
        try {
            // Verificar si el recurso existe antes de intentar cargarlo
            URL fxmlUrl = getClass().getResource("/fxml/ReportesVentas.fxml");
            if (fxmlUrl != null) {
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Reporte de Ventas");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                logger.warning("FXML de ReporteVentas no encontrado, usando vista alternativa");
                crearReporteVentasAlternativo();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir reporte de ventas: " + e.getMessage());
            crearReporteVentasAlternativo();
        }
    }
    
    private void abrirReporteOcupacion() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ReporteOcupacion.fxml");
            if (fxmlUrl != null) {
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Reporte de Ocupación");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                logger.warning("FXML de ReporteOcupacion no encontrado, usando vista alternativa");
                crearReporteOcupacionAlternativo();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir reporte de ocupación: " + e.getMessage());
            crearReporteOcupacionAlternativo();
        }
    }
    
    private void abrirReporteFinanciero() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ReporteFinanciero.fxml");
            if (fxmlUrl != null) {
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Reporte Financiero");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                logger.warning("FXML de ReporteFinanciero no encontrado, usando vista alternativa");
                crearReporteFinancieroAlternativo();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir reporte financiero: " + e.getMessage());
            crearReporteFinancieroAlternativo();
        }
    }
    
    // Métodos para crear interfaces alternativas
    
    private void crearReporteVentasAlternativo() {
        Stage stage = new Stage();
        stage.setTitle("Reporte de Ventas - Versión Simplificada");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        Label titulo = new Label("Reporte de Ventas");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox filtros = new HBox(10);
        filtros.setAlignment(Pos.CENTER_LEFT);
        
        DatePicker fechaInicio = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker fechaFin = new DatePicker(LocalDate.now());
        Button actualizarBtn = new Button("Actualizar");
        
        filtros.getChildren().addAll(
            new Label("Desde:"), fechaInicio,
            new Label("Hasta:"), fechaFin,
            actualizarBtn
        );
        
        // Resumen de datos
        HBox resumen = new HBox(30);
        resumen.setPadding(new Insets(20, 0, 20, 0));
        
        VBox vbox1 = new VBox(5);
        Label label1 = new Label("Total Ventas");
        Label valor1 = new Label("$45,678.00");
        valor1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox1.getChildren().addAll(label1, valor1);
        
        VBox vbox2 = new VBox(5);
        Label label2 = new Label("Reservas");
        Label valor2 = new Label("127");
        valor2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox2.getChildren().addAll(label2, valor2);
        
        VBox vbox3 = new VBox(5);
        Label label3 = new Label("Promedio Diario");
        Label valor3 = new Label("$1,522.60");
        valor3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox3.getChildren().addAll(label3, valor3);
        
        resumen.getChildren().addAll(vbox1, vbox2, vbox3);
        
        // Gráfico de ejemplo
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Ventas");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ventas por día");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas diarias");
        
        // Datos de ejemplo
        series.getData().add(new XYChart.Data<>("01/05", 2500));
        series.getData().add(new XYChart.Data<>("02/05", 3200));
        series.getData().add(new XYChart.Data<>("03/05", 2700));
        series.getData().add(new XYChart.Data<>("04/05", 3400));
        series.getData().add(new XYChart.Data<>("05/05", 2800));
        
        lineChart.getData().add(series);
        
        // Botón exportar
        Button exportarBtn = new Button("Exportar a Excel");
        exportarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        root.getChildren().addAll(titulo, filtros, resumen, lineChart, exportarBtn);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private void crearReporteOcupacionAlternativo() {
        Stage stage = new Stage();
        stage.setTitle("Reporte de Ocupación - Versión Simplificada");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        Label titulo = new Label("Reporte de Ocupación");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Resumen de ocupación
        HBox resumen = new HBox(30);
        resumen.setPadding(new Insets(20, 0, 20, 0));
        
        VBox vbox1 = new VBox(5);
        Label label1 = new Label("Tasa de Ocupación");
        Label valor1 = new Label("78%");
        valor1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox1.getChildren().addAll(label1, valor1);
        
        VBox vbox2 = new VBox(5);
        Label label2 = new Label("Habitaciones Ocupadas");
        Label valor2 = new Label("7/10");
        valor2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox2.getChildren().addAll(label2, valor2);
        
        VBox vbox3 = new VBox(5);
        Label label3 = new Label("Duración Promedio");
        Label valor3 = new Label("4.3 horas");
        valor3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox3.getChildren().addAll(label3, valor3);
        
        resumen.getChildren().addAll(vbox1, vbox2, vbox3);
        
        // Tabla de ocupación
        TableView<String> tabla = new TableView<>();
        tabla.setPlaceholder(new Label("No hay datos disponibles"));
        
        TableColumn<String, String> col1 = new TableColumn<>("Habitación");
        TableColumn<String, String> col2 = new TableColumn<>("Tipo");
        TableColumn<String, String> col3 = new TableColumn<>("% Ocupación");
        TableColumn<String, String> col4 = new TableColumn<>("Horas Usadas");
        TableColumn<String, String> col5 = new TableColumn<>("Ingresos");
        
        tabla.getColumns().addAll(col1, col2, col3, col4, col5);
        
        // Botón exportar
        Button exportarBtn = new Button("Exportar a Excel");
        exportarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        root.getChildren().addAll(titulo, resumen, tabla, exportarBtn);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private void crearReporteFinancieroAlternativo() {
        Stage stage = new Stage();
        stage.setTitle("Reporte Financiero - Versión Simplificada");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        Label titulo = new Label("Reporte Financiero");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Filtro de periodo
        HBox filtros = new HBox(10);
        filtros.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> periodoCombo = new ComboBox<>(
            FXCollections.observableArrayList(
                "Este mes", "Mes anterior", "Últimos 3 meses", "Este año", "Personalizado"
            )
        );
        periodoCombo.setValue("Este mes");
        
        Button actualizarBtn = new Button("Actualizar");
        
        filtros.getChildren().addAll(
            new Label("Periodo:"), periodoCombo,
            actualizarBtn
        );
        
        // Resumen financiero
        HBox resumen = new HBox(30);
        resumen.setPadding(new Insets(20, 0, 20, 0));
        
        VBox vbox1 = new VBox(5);
        Label label1 = new Label("Ingresos Totales");
        Label valor1 = new Label("$125,678.00");
        valor1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");
        vbox1.getChildren().addAll(label1, valor1);
        
        VBox vbox2 = new VBox(5);
        Label label2 = new Label("Gastos");
        Label valor2 = new Label("$45,123.00");
        valor2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
        vbox2.getChildren().addAll(label2, valor2);
        
        VBox vbox3 = new VBox(5);
        Label label3 = new Label("Beneficio Neto");
        Label valor3 = new Label("$80,555.00");
        valor3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        vbox3.getChildren().addAll(label3, valor3);
        
        resumen.getChildren().addAll(vbox1, vbox2, vbox3);
        
        // Tabla de transacciones
        TableView<String> tabla = new TableView<>();
        tabla.setPlaceholder(new Label("No hay datos disponibles"));
        
        TableColumn<String, String> col1 = new TableColumn<>("Fecha");
        TableColumn<String, String> col2 = new TableColumn<>("Tipo");
        TableColumn<String, String> col3 = new TableColumn<>("Concepto");
        TableColumn<String, String> col4 = new TableColumn<>("Monto");
        
        tabla.getColumns().addAll(col1, col2, col3, col4);
        
        // Botón exportar
        Button exportarBtn = new Button("Exportar a Excel");
        exportarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        root.getChildren().addAll(titulo, filtros, resumen, tabla, exportarBtn);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}