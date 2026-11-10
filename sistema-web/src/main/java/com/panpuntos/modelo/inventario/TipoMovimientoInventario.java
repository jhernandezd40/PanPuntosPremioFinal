package com.panpuntos.modelo.inventario;

public enum TipoMovimientoInventario {

    ENTRADA("Entrada", "success"),
    VENTA("Venta", "primary"),
    CANJE("Canje", "info");

    private final String descripcion;
    private final String estilo;

    TipoMovimientoInventario(String descripcion, String estilo) {
        this.descripcion = descripcion;
        this.estilo = estilo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstilo() {
        return estilo;
    }

    public boolean esSalida() {
        return this != ENTRADA;
    }
}
