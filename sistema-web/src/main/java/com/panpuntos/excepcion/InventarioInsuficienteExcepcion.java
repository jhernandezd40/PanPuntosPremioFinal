package com.panpuntos.excepcion;

import java.util.List;
import java.util.stream.Collectors;

public class InventarioInsuficienteExcepcion extends NegocioExcepcion {

    private final List<Faltante> faltantes;

    public InventarioInsuficienteExcepcion(List<Faltante> faltantes) {
        super("Inventario insuficiente: " + faltantes.stream()
                .map(Faltante::toString)
                .collect(Collectors.joining("; ")));
        this.faltantes = List.copyOf(faltantes);
    }

    public List<Faltante> getFaltantes() {
        return faltantes;
    }

    public record Faltante(String producto, int requerido, int disponible) {

        @Override
        public String toString() {
            return producto + " (requerido: " + requerido + ", disponible: " + disponible + ")";
        }
    }
}
