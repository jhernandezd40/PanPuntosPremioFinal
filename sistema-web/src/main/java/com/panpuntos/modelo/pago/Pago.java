package com.panpuntos.modelo.pago;

import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.EntidadBase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public abstract class Pago extends EntidadBase {

    private Integer pedidoId;
    private BigDecimal monto = BigDecimal.ZERO;
    private LocalDateTime fecha;

    public abstract MetodoPago getMetodo();

    protected abstract void validarYAutorizar(BigDecimal total);

    public abstract String getDetalle();

    public final void procesar(BigDecimal total) {
        if (total == null || total.signum() <= 0) {
            throw new ValidacionExcepcion("El total a cobrar debe ser mayor a cero");
        }
        validarYAutorizar(total);
        this.monto = total.setScale(2, RoundingMode.HALF_UP);
        this.fecha = LocalDateTime.now();
    }

    public String getDescripcionMetodo() {
        return getMetodo().getDescripcion();
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
