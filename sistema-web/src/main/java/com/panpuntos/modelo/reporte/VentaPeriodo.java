package com.panpuntos.modelo.reporte;

import com.panpuntos.modelo.pago.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VentaPeriodo(int pedidoId, LocalDateTime fecha, String cliente, MetodoPago metodo,
                           BigDecimal monto, String referencia) {
}
