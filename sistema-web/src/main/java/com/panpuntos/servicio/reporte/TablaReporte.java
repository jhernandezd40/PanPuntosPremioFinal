package com.panpuntos.servicio.reporte;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TablaReporte {

    private final String titulo;
    private final String subtitulo;
    private final List<String> columnas;
    private final List<List<String>> filas = new ArrayList<>();
    private final Map<String, String> resumen = new LinkedHashMap<>();
    private final LocalDateTime generado = LocalDateTime.now();

    public TablaReporte(String titulo, String subtitulo, String... columnas) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.columnas = List.of(columnas);
    }

    public TablaReporte agregarFila(Object... valores) {
        if (valores.length != columnas.size()) {
            throw new IllegalArgumentException("La fila debe tener " + columnas.size() + " valores");
        }
        List<String> fila = new ArrayList<>();
        for (Object valor : valores) {
            fila.add(valor == null ? "" : valor.toString());
        }
        filas.add(Collections.unmodifiableList(fila));
        return this;
    }

    public TablaReporte agregarResumen(String etiqueta, Object valor) {
        resumen.put(etiqueta, valor == null ? "" : valor.toString());
        return this;
    }

    public boolean estaVacia() {
        return filas.isEmpty();
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public List<String> getColumnas() {
        return columnas;
    }

    public List<List<String>> getFilas() {
        return Collections.unmodifiableList(filas);
    }

    public Map<String, String> getResumen() {
        return Collections.unmodifiableMap(resumen);
    }

    public LocalDateTime getGenerado() {
        return generado;
    }
}
