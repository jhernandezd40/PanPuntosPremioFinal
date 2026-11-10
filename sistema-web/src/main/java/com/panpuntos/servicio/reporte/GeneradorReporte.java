package com.panpuntos.servicio.reporte;

public interface GeneradorReporte {

    String clave();

    String titulo();

    String descripcion();

    default boolean usaPeriodo() {
        return true;
    }

    TablaReporte generar(FiltroReporte filtro);
}
