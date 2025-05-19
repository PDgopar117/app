/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;

import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Reserva;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ExportUtil {
    private static final Logger logger = Logger.getLogger(ExportUtil.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Exporta una lista de transacciones a un archivo Excel
     * @param transacciones Lista de transacciones a exportar
     * @param filePath Ruta del archivo donde se guardará el Excel
     * @return true si la exportación fue exitosa, false en caso contrario
     */
    public static boolean exportarTransaccionesAExcel(List<Transaccion> transacciones, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transacciones");
            
            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tipo", "Concepto", "Monto", "Fecha", "Usuario", "Reserva"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Transaccion transaccion : transacciones) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cellId = row.createCell(0);
                cellId.setCellValue(transaccion.getId());
                
                Cell cellTipo = row.createCell(1);
                cellTipo.setCellValue(transaccion.getTipo().toString());
                
                Cell cellConcepto = row.createCell(2);
                cellConcepto.setCellValue(transaccion.getConcepto());
                
                Cell cellMonto = row.createCell(3);
                cellMonto.setCellValue(transaccion.getMonto().doubleValue());
                
                Cell cellFecha = row.createCell(4);
                cellFecha.setCellValue(transaccion.getFecha().format(DATE_FORMATTER));
                
                Cell cellUsuario = row.createCell(5);
                cellUsuario.setCellValue(transaccion.getUsuarioId());
                
                Cell cellReserva = row.createCell(6);
                if (transaccion.getReservaId() != null) {
                    cellReserva.setCellValue(transaccion.getReservaId());
                }
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Escribir el archivo
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                return true;
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al exportar transacciones a Excel", e);
            return false;
        }
    }
    
    /**
     * Exporta una lista de reservas a un archivo Excel
     * @param reservas Lista de reservas a exportar
     * @param filePath Ruta del archivo donde se guardará el Excel
     * @return true si la exportación fue exitosa, false en caso contrario
     */
    public static boolean exportarReservasAExcel(List<Reserva> reservas, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reservas");
            
            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Habitación", "Check-in", "Check-out", "Total Pagado", "Estado"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cellId = row.createCell(0);
                cellId.setCellValue(reserva.getId());
                
                Cell cellHabitacion = row.createCell(1);
                cellHabitacion.setCellValue(reserva.getHabitacionId());
                
                Cell cellCheckIn = row.createCell(2);
                cellCheckIn.setCellValue(reserva.getCheckIn().format(DATE_FORMATTER));
                
                Cell cellCheckOut = row.createCell(3);
                cellCheckOut.setCellValue(reserva.getCheckOut().format(DATE_FORMATTER));
                
                Cell cellTotalPagado = row.createCell(4);
                cellTotalPagado.setCellValue(reserva.getTotalPagado().doubleValue());
                
                Cell cellEstado = row.createCell(5);
                cellEstado.setCellValue(reserva.getEstado().toString());
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Escribir el archivo
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                return true;
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al exportar reservas a Excel", e);
            return false;
        }
    }
}