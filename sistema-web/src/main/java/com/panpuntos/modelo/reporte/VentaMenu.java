package com.panpuntos.modelo.reporte;

import java.math.BigDecimal;

public record VentaMenu(String codigo, String nombre, int pedidos, int cantidad, BigDecimal total) {
}
