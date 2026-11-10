package com.panpuntos.modelo.fidelizacion;

import com.panpuntos.modelo.EntidadBase;

import java.time.LocalDateTime;

public class Canje extends EntidadBase {

    private Integer clienteId;
    private String clienteIdentificador;
    private String clienteNombre;
    private Integer recompensaId;
    private String recompensaNombre;
    private Integer productoId;
    private String productoNombre;
    private int puntosUtilizados;
    private LocalDateTime fecha;

    public Canje() {
    }

    public Canje(Integer clienteId, Recompensa recompensa) {
        this.clienteId = clienteId;
        this.recompensaId = recompensa.getId();
        this.recompensaNombre = recompensa.getNombre();
        this.productoId = recompensa.getProducto().getId();
        this.productoNombre = recompensa.getProducto().getNombre();
        this.puntosUtilizados = recompensa.getPuntosRequeridos();
        this.fecha = LocalDateTime.now();
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteIdentificador() {
        return clienteIdentificador;
    }

    public void setClienteIdentificador(String clienteIdentificador) {
        this.clienteIdentificador = clienteIdentificador;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public Integer getRecompensaId() {
        return recompensaId;
    }

    public void setRecompensaId(Integer recompensaId) {
        this.recompensaId = recompensaId;
    }

    public String getRecompensaNombre() {
        return recompensaNombre;
    }

    public void setRecompensaNombre(String recompensaNombre) {
        this.recompensaNombre = recompensaNombre;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getPuntosUtilizados() {
        return puntosUtilizados;
    }

    public void setPuntosUtilizados(int puntosUtilizados) {
        this.puntosUtilizados = puntosUtilizados;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
