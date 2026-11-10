package com.panpuntos.modelo.pedido;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoPedido {

    PENDIENTE("Pendiente", "warning"),
    PAGADO("Pagado", "primary"),
    ENTREGADO("Entregado", "success"),
    ANULADO("Anulado", "secondary");

    private final String descripcion;
    private final String estilo;

    EstadoPedido(String descripcion, String estilo) {
        this.descripcion = descripcion;
        this.estilo = estilo;
    }

    public Set<EstadoPedido> transicionesPermitidas() {
        return switch (this) {
            case PENDIENTE -> EnumSet.of(PAGADO, ANULADO);
            case PAGADO -> EnumSet.of(ENTREGADO);
            case ENTREGADO, ANULADO -> EnumSet.noneOf(EstadoPedido.class);
        };
    }

    public boolean puedeCambiarA(EstadoPedido destino) {
        return transicionesPermitidas().contains(destino);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstilo() {
        return estilo;
    }
}
