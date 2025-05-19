/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.model.Transaccion.TipoTransaccion;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReporteVentasController {
    private static final Logger logger = Logger.getLogger(ReporteVentasController.class.getName());

    @FXML
    private DatePicker fechaInicioPicker;

    @FXML
    private DatePicker fechaFinPicker;

    @FXML
    private Label totalIngresosLabel;

    @FXML
    private Label totalEgresosLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label totalReservasLabel;

    @FXML
    private BarChart<String, Number> ingresosEgresosChart;

    @FXML
    private LineChart<String, Number> reservasChart;

    private TransaccionDAO transaccionDAO;
    private ReservaDAO reservaDAO;

    @FXML
    public void initialize() {
        transaccionDAO = new TransaccionDAO();
        reservaDAO = new ReservaDAO();

        // Configurar fechas iniciales (último mes)
        fechaInicioPicker.setValue(LocalDate.now().minusMonths(1));
        fechaFinPicker.setValue(LocalDate.now());

        // Agregar listeners para actualizar al cambiar las fechas
        fechaInicioPicker.valueProperty().addListener((obs, oldVal, newVal) -> actualizarReporte());
        fechaFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> actualizarReporte());

        actualizarReporte();
    }

    @FXML
    private void actualizarReporte() {
        try {
            if (!validarFechas()) {
                return;
            }

            LocalDateTime inicio = fechaInicioPicker.getValue().atStartOfDay();
            LocalDateTime fin = fechaFinPicker.getValue().atTime(LocalTime.MAX);

            actualizarTotales(inicio, fin);
            actualizarGraficos(inicio, fin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al actualizar reporte", e);
            mostrarError("Error al generar el reporte");
        }
    }

    private void actualizarTotales(LocalDateTime inicio, LocalDateTime fin) {
        try {
            BigDecimal totalIngresos = transaccionDAO.calcularTotalPorTipoYRango(TipoTransaccion.INGRESO, inicio, fin);
            totalIngresosLabel.setText(String.format("Total Ingresos: $%.2f", totalIngresos));

            BigDecimal totalEgresos = transaccionDAO.calcularTotalPorTipoYRango(TipoTransaccion.EGRESO, inicio, fin);
            totalEgresosLabel.setText(String.format("Total Egresos: $%.2f", totalEgresos));

            BigDecimal balance = totalIngresos.subtract(totalEgresos);
            balanceLabel.setText(String.format("Balance: $%.2f", balance));

            int totalReservas = reservaDAO.contarReservasPorRango(inicio, fin);
            totalReservasLabel.setText(String.format("Total Reservas: %d", totalReservas));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar totales", e);
            mostrarError("Error al calcular totales");
        }
    }

    private void actualizarGraficos(LocalDateTime inicio, LocalDateTime fin) {
        try {
            actualizarGraficoIngresosEgresos(inicio, fin);
            actualizarGraficoReservas(inicio, fin);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar gráficos", e);
            mostrarError("Error al generar gráficos");
        }
    }

    private void actualizarGraficoIngresosEgresos(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        XYChart.Series<String, Number> ingresosSeries = new XYChart.Series<>();
        ingresosSeries.setName("Ingresos");
        
        XYChart.Series<String, Number> egresosSeries = new XYChart.Series<>();
        egresosSeries.setName("Egresos");

        // Agregar datos por día
        LocalDate fechaActual = inicio.toLocalDate();
        while (!fechaActual.isAfter(fin.toLocalDate())) {
            String fecha = fechaActual.toString();
            LocalDateTime inicioDelDia = fechaActual.atStartOfDay();
            LocalDateTime finDelDia = fechaActual.atTime(LocalTime.MAX);

            BigDecimal ingresos = transaccionDAO.calcularTotalPorTipoYRango(TipoTransaccion.INGRESO, inicioDelDia, finDelDia);
            BigDecimal egresos = transaccionDAO.calcularTotalPorTipoYRango(TipoTransaccion.EGRESO, inicioDelDia, finDelDia);

            ingresosSeries.getData().add(new XYChart.Data<>(fecha, ingresos.doubleValue()));
            egresosSeries.getData().add(new XYChart.Data<>(fecha, egresos.doubleValue()));

            fechaActual = fechaActual.plusDays(1);
        }

        ingresosEgresosChart.getData().clear();
        ingresosEgresosChart.getData().addAll(ingresosSeries, egresosSeries);
    }

    private void actualizarGraficoReservas(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        XYChart.Series<String, Number> reservasSeries = new XYChart.Series<>();
        reservasSeries.setName("Reservas");

        // Agregar datos por día
        LocalDate fechaActual = inicio.toLocalDate();
        while (!fechaActual.isAfter(fin.toLocalDate())) {
            String fecha = fechaActual.toString();
            LocalDateTime inicioDelDia = fechaActual.atStartOfDay();
            LocalDateTime finDelDia = fechaActual.atTime(LocalTime.MAX);

            int cantidadReservas = reservaDAO.contarReservasPorRango(inicioDelDia, finDelDia);
            reservasSeries.getData().add(new XYChart.Data<>(fecha, cantidadReservas));

            fechaActual = fechaActual.plusDays(1);
        }

        reservasChart.getData().clear();
        reservasChart.getData().add(reservasSeries);
    }

    private boolean validarFechas() {
        if (fechaInicioPicker.getValue() == null || fechaFinPicker.getValue() == null) {
            mostrarError("Debe seleccionar ambas fechas");
            return false;
        }

        if (fechaInicioPicker.getValue().isAfter(fechaFinPicker.getValue())) {
            mostrarError("La fecha de inicio debe ser anterior a la fecha fin");
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}