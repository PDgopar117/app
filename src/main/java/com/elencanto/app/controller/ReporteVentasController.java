/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
/**
 *
 * @author Gopar117
 */
public class ReporteVentasController {
    private static final Logger logger = Logger.getLogger(ReporteVentasController.class.getName());
    
    @FXML
    private DatePicker fechaInicioSelector;
    
    @FXML
    private DatePicker fechaFinSelector;
    
    @FXML
    private Button generarButton;
    
    @FXML
    private Button exportarButton;
    
    @FXML
    private Label totalIngresosLabel;
    
    @FXML
    private Label totalEgresosLabel;
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private Label totalReservasLabel;
    
    @FXML
    private LineChart<String, Number> ingresosChart;
    
    @FXML
    private TableView<ReporteVentaItem> detallesTabla;
    
    @FXML
    private TableColumn<ReporteVentaItem, String> fechaColumn;
    
    @FXML
    private TableColumn<ReporteVentaItem, BigDecimal> ingresosColumn;
    
    @FXML
    private TableColumn<ReporteVentaItem, BigDecimal> egresosColumn;
    
    @FXML
    private TableColumn<ReporteVentaItem, BigDecimal> balanceColumn;
    
    @FXML
    private TableColumn<ReporteVentaItem, Integer> reservasColumn;
    
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @FXML
    public void initialize() {
        configurarControles();
        configurarTabla();
        
        // Por defecto, cargar datos del mes actual
        fechaInicioSelector.setValue(LocalDate.now().withDayOfMonth(1));
        fechaFinSelector.setValue(LocalDate.now());
        
        generarReporte();
    }
    
    private void configurarControles() {
        generarButton.setOnAction(event -> generarReporte());
        exportarButton.setOnAction(event -> exportarReporte());
    }
    
    private void configurarTabla() {
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        ingresosColumn.setCellValueFactory(new PropertyValueFactory<>("ingresos"));
        egresosColumn.setCellValueFactory(new PropertyValueFactory<>("egresos"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        reservasColumn.setCellValueFactory(new PropertyValueFactory<>("reservas"));
    }
    
    private void generarReporte() {
        LocalDate fechaInicio = fechaInicioSelector.getValue();
        LocalDate fechaFin = fechaFinSelector.getValue();
        
        if (fechaInicio == null || fechaFin == null) {
            mostrarAlerta("Error", "Debe seleccionar fechas válidas.");
            return;
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error", "La fecha de inicio debe ser anterior a la fecha final.");
            return;
        }
        
        // Convertir fechas a LocalDateTime para consultas
        LocalDateTime fechaInicioTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinTime = fechaFin.atTime(23, 59, 59);
        
        // Calcular totales
        BigDecimal totalIngresos = calcularTotalIngresos(fechaInicioTime, fechaFinTime);
        BigDecimal totalEgresos = calcularTotalEgresos(fechaInicioTime, fechaFinTime);
        BigDecimal balance = totalIngresos.subtract(totalEgresos);
        int totalReservas = contarReservas(fechaInicioTime, fechaFinTime);
        
        // Actualizar etiquetas
        totalIngresosLabel.setText(String.format("$%,.0f", totalIngresos));
        totalEgresosLabel.setText(String.format("$%,.0f", totalEgresos));
        balanceLabel.setText(String.format("$%,.0f", balance));
        totalReservasLabel.setText(String.valueOf(totalReservas));
        
        // Generar datos para la tabla y gráfico
        List<ReporteVentaItem> items = generarDatosReporte(fechaInicio, fechaFin);
        
        // Actualizar tabla
        ObservableList<ReporteVentaItem> data = FXCollections.observableArrayList(items);
        detallesTabla.setItems(data);
        
        // Actualizar gráfico
        actualizarGrafico(items);
    }
    
    private BigDecimal calcularTotalIngresos(LocalDateTime inicio, LocalDateTime fin) {
        return transaccionDAO.calcularTotalPorTipoYRango(Transaccion.TipoTransaccion.INGRESO, inicio, fin);
    }
    
    private BigDecimal calcularTotalEgresos(LocalDateTime inicio, LocalDateTime fin) {
        return transaccionDAO.calcularTotalPorTipoYRango(Transaccion.TipoTransaccion.EGRESO, inicio, fin);
    }
    
    private int contarReservas(LocalDateTime inicio, LocalDateTime fin) {
        return reservaDAO.contarReservasPorRango(inicio, fin);
    }
    
    private List<ReporteVentaItem> generarDatosReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        return fechaInicio.datesUntil(fechaFin.plusDays(1))
            .map(fecha -> {
                LocalDateTime inicioDia = fecha.atStartOfDay();
                LocalDateTime finDia = fecha.atTime(23, 59, 59);
                
                BigDecimal ingresosDia = transaccionDAO.calcularTotalPorTipoYRango(
                        Transaccion.TipoTransaccion.INGRESO, inicioDia, finDia);
                
                BigDecimal egresosDia = transaccionDAO.calcularTotalPorTipoYRango(
                        Transaccion.TipoTransaccion.EGRESO, inicioDia, finDia);
                
                BigDecimal balanceDia = ingresosDia.subtract(egresosDia);
                
                int reservasDia = reservaDAO.contarReservasPorRango(inicioDia, finDia);
                
                return new ReporteVentaItem(
                        fecha.format(formatter),
                        ingresosDia,
                        egresosDia,
                        balanceDia,
                        reservasDia
                );
            })
            .collect(Collectors.toList());
    }
    
    private void actualizarGrafico(List<ReporteVentaItem> items) {
        // Limpiar series anteriores
        ingresosChart.getData().clear();
        
        // Crear series de datos
        XYChart.Series<String, Number> seriesIngresos = new XYChart.Series<>();
        seriesIngresos.setName("Ingresos");
        
        XYChart.Series<String, Number> seriesEgresos = new XYChart.Series<>();
        seriesEgresos.setName("Egresos");
        
        // Agregar datos a las series
        for (ReporteVentaItem item : items) {
            seriesIngresos.getData().add(new XYChart.Data<>(item.getFecha(), item.getIngresos()));
            seriesEgresos.getData().add(new XYChart.Data<>(item.getFecha(), item.getEgresos()));
        }
        
        // Añadir series al gráfico
        ingresosChart.getData().add(seriesIngresos);
        ingresosChart.getData().add(seriesEgresos);
    }
    
    private void exportarReporte() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fileChooser.setInitialFileName("reporte_ventas_" + 
                                     LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");
        
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Escribir cabecera
                writer.write("Fecha,Ingresos,Egresos,Balance,Reservas\n");
                
                // Escribir datos
                for (ReporteVentaItem item : detallesTabla.getItems()) {
                    writer.write(String.format("%s,%s,%s,%s,%d\n",
                                            item.getFecha(),
                                            item.getIngresos(),
                                            item.getEgresos(),
                                            item.getBalance(),
                                            item.getReservas()));
                }
                
                mostrarAlerta("Éxito", "Reporte exportado correctamente.");
                
            } catch (Exception e) {
                logger.severe("Error al exportar reporte: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo exportar el reporte.");
            }
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Clase para los items de la tabla de reporte
    public static class ReporteVentaItem {
        private String fecha;
        private BigDecimal ingresos;
        private BigDecimal egresos;
        private BigDecimal balance;
        private int reservas;
        
        public ReporteVentaItem(String fecha, BigDecimal ingresos, BigDecimal egresos, 
                              BigDecimal balance, int reservas) {
            this.fecha = fecha;
            this.ingresos = ingresos;
            this.egresos = egresos;
            this.balance = balance;
            this.reservas = reservas;
        }
        
        public String getFecha() {
            return fecha;
        }
        
        public BigDecimal getIngresos() {
            return ingresos;
        }
        
        public BigDecimal getEgresos() {
            return egresos;
        }
        
        public BigDecimal getBalance() {
            return balance;
        }
        
        public int getReservas() {
            return reservas;
        }
    }
    
}
