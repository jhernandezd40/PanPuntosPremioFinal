package com.panpuntos.servicio.reporte;

import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReporteServicio {

    private final Map<String, GeneradorReporte> generadores = new LinkedHashMap<>();

    public ReporteServicio(List<GeneradorReporte> generadores) {
        generadores.forEach(generador -> this.generadores.put(generador.clave(), generador));
    }

    public Collection<GeneradorReporte> disponibles() {
        return generadores.values();
    }

    public GeneradorReporte obtener(String clave) {
        return Optional.ofNullable(generadores.get(clave))
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Reporte", clave));
    }

    @Transactional(readOnly = true)
    public TablaReporte generar(String clave, FiltroReporte filtro) {
        return obtener(clave).generar(filtro);
    }
}
