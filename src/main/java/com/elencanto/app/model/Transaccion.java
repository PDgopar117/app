/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author Gopar117
 */
public class Transaccion {
    
    private Long id;
    private TipoTransaccion tipo;
    private String concepto;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private Usuario usuario;
    private Reserva reserva; // Puede ser null si no está asociada
    
    public enum TipoTransaccion {
        INGRESO, EGRESO
    }
    
    // Constructor
    public Transaccion(Long id, TipoTransaccion tipo, String concepto, BigDecimal monto, 
                      LocalDateTime fecha, Usuario usuario, Reserva reserva) {
        this.id = id;
        this.tipo = tipo;
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
        this.usuario = usuario;
        this.reserva = reserva;
    }
    
    // Constructor vacío
    public Transaccion() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
    
    @Override
    public String toString() {
        return "Transaccion{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", concepto='" + concepto + '\'' +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
