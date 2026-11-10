package com.panpuntos.dto;

import com.panpuntos.modelo.pago.MetodoPago;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PagoFormulario {

    @NotNull(message = "Seleccione el método de pago")
    private MetodoPago metodo = MetodoPago.EFECTIVO;

    private BigDecimal montoRecibido;
    private String numeroTarjeta;
    private String titular;

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
}
