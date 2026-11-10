package com.panpuntos.modelo.fidelizacion;

public enum TipoMovimientoPuntos {

    ACUMULACION("Acumulación", "success"),
    CANJE("Canje", "danger");

    private final String descripcion;
    private final String estilo;

    TipoMovimientoPuntos(String descripcion, String estilo) {
        this.descripcion = descripcion;
        this.estilo = estilo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstilo() {
        return estilo;
    }
}
