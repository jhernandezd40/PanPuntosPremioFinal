package com.panpuntos.modelo.reporte;

public record PuntosCliente(String identificador, String nombre, int saldo, int acumulados,
                            int canjeados, boolean activo) {
}
