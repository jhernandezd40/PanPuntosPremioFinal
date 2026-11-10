package com.panpuntos.repositorio;

import com.panpuntos.modelo.pago.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface PagoRepositorio {

    int guardar(Pago pago);

    Optional<Pago> buscarPorPedido(int pedidoId);

    BigDecimal totalDelDia(LocalDate fecha);
}
