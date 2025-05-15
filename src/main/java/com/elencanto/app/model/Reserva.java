/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 *
 * @author Gopar117
 */
public class Reserva {
    
    private Long id;
    private Habitacion habitacion;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private BigDecimal totalPagado;
    private EstadoReserva estado;
    
    public enum EstadoReserva {
        ACTIVA, FINALIZADA, CANCELADA
    }
    
    // Constructor
    public Reserva(Long id, Habitacion habitacion, LocalDateTime checkIn, LocalDateTime checkOut, 
                   BigDecimal totalPagado, EstadoReserva estado) {
        this.id = id;
        this.habitacion = habitacion;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPagado = totalPagado;
        this.estado = estado;
    }
    
    // Constructor vacío
    public Reserva() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
    
    // Método para calcular tiempo restante
    public Duration getTiempoRestante() {
        if (estado != EstadoReserva.ACTIVA || checkOut == null) {
            return Duration.ZERO;
        }
        return Duration.between(LocalDateTime.now(), checkOut);
    }
    
    // Método para calcular la duración total
    public Duration getDuracionTotal() {
        if (checkIn == null || checkOut == null) {
            return Duration.ZERO;
        }
        return Duration.between(checkIn, checkOut);
    }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", habitacion=" + habitacion.getNumero() +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", estado=" + estado +
                '}';
    }
}
