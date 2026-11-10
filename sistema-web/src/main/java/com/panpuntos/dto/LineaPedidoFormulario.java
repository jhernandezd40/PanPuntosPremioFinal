package com.panpuntos.dto;

public class LineaPedidoFormulario {

    private Integer productoId;
    private Integer cantidad = 1;

    public boolean estaCompleta() {
        return productoId != null && cantidad != null;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
