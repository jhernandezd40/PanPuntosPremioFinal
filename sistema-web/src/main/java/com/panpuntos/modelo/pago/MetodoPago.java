package com.panpuntos.modelo.pago;

public enum MetodoPago {

    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
