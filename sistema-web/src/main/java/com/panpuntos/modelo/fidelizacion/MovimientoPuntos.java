package com.panpuntos.modelo.fidelizacion;

import com.panpuntos.modelo.EntidadBase;

import java.time.LocalDateTime;

public class MovimientoPuntos extends EntidadBase {

    private Integer clienteId;
    private TipoMovimientoPuntos tipo;
    private int puntos;
    private int saldoResultante;
    private Integer pedidoId;
    private Integer canjeId;
    private String descripcion;
    private LocalDateTime fecha;

    public MovimientoPuntos() {
    }

    private MovimientoPuntos(Integer clienteId, TipoMovimientoPuntos tipo, int puntos, int saldoResultante,
                             String descripcion) {
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.puntos = puntos;
        this.saldoResultante = saldoResultante;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public static MovimientoPuntos acumulacion(Integer clienteId, int puntos, int saldo, Integer pedidoId) {
        MovimientoPuntos movimiento = new MovimientoPuntos(clienteId, TipoMovimientoPuntos.ACUMULACION, puntos, saldo,
                "Puntos por pedido #" + pedidoId);
        movimiento.pedidoId = pedidoId;
        return movimiento;
    }

    public static MovimientoPuntos canje(Integer clienteId, int puntos, int saldo, Integer canjeId, String recompensa) {
        MovimientoPuntos movimiento = new MovimientoPuntos(clienteId, TipoMovimientoPuntos.CANJE, puntos, saldo,
                "Canje: " + recompensa);
        movimiento.canjeId = canjeId;
        return movimiento;
    }

    public int getPuntosConSigno() {
        return tipo == TipoMovimientoPuntos.CANJE ? -puntos : puntos;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public TipoMovimientoPuntos getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimientoPuntos tipo) {
        this.tipo = tipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(int saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Integer getCanjeId() {
        return canjeId;
    }

    public void setCanjeId(Integer canjeId) {
        this.canjeId = canjeId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
