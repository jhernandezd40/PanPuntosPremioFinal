package com.panpuntos.modelo.pago;

import com.panpuntos.excepcion.PagoRechazadoExcepcion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PagoEfectivo extends Pago {

    private BigDecimal montoRecibido;
    private BigDecimal cambio = BigDecimal.ZERO;

    public PagoEfectivo() {
    }

    public PagoEfectivo(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    @Override
    public MetodoPago getMetodo() {
        return MetodoPago.EFECTIVO;
    }

    @Override
    protected void validarYAutorizar(BigDecimal total) {
        if (montoRecibido == null || montoRecibido.compareTo(total) < 0) {
            BigDecimal recibido = montoRecibido == null ? BigDecimal.ZERO : montoRecibido;
            BigDecimal faltante = total.subtract(recibido).setScale(2, RoundingMode.HALF_UP);
            throw new PagoRechazadoExcepcion("Monto insuficiente: el total es Q " + total.setScale(2, RoundingMode.HALF_UP)
                    + " y se recibió Q " + recibido.setScale(2, RoundingMode.HALF_UP) + " (faltan Q " + faltante + ")", null);
        }
        this.cambio = montoRecibido.subtract(total).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDetalle() {
        return "Recibido Q " + montoRecibido.setScale(2, RoundingMode.HALF_UP) + " · Cambio Q " + cambio;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }
}
