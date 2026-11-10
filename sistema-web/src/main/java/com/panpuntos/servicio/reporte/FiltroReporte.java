package com.panpuntos.servicio.reporte;

import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.util.Formato;

import java.time.LocalDate;

public record FiltroReporte(LocalDate desde, LocalDate hasta) {

    public FiltroReporte {
        LocalDate hoy = LocalDate.now();
        desde = desde == null ? hoy.withDayOfMonth(1) : desde;
        hasta = hasta == null ? hoy : hasta;
        if (desde.isAfter(hasta)) {
            throw new ValidacionExcepcion("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    public String descripcion() {
        return "Del " + Formato.fecha(desde) + " al " + Formato.fecha(hasta);
    }
}
