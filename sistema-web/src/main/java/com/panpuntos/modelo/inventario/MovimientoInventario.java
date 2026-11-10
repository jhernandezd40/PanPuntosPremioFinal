package com.panpuntos.modelo.inventario;

import com.panpuntos.modelo.EntidadBase;

import java.time.LocalDateTime;

public class MovimientoInventario extends EntidadBase {

    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private TipoMovimientoInventario tipo;
    private int cantidad;
    private int existenciaResultante;
    private String referencia;
    private LocalDateTime fecha;

    public MovimientoInventario() {
    }

    public MovimientoInventario(Integer productoId, TipoMovimientoInventario tipo, int cantidad,
                                int existenciaResultante, String referencia) {
        this.productoId = productoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.existenciaResultante = existenciaResultante;
        this.referencia = referencia;
        this.fecha = LocalDateTime.now();
    }

    public int getCantidadConSigno() {
        return tipo.esSalida() ? -cantidad : cantidad;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public TipoMovimientoInventario getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimientoInventario tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getExistenciaResultante() {
        return existenciaResultante;
    }

    public void setExistenciaResultante(int existenciaResultante) {
        this.existenciaResultante = existenciaResultante;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
