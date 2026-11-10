package com.panpuntos.modelo.pago;

import com.panpuntos.excepcion.PagoRechazadoExcepcion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class PagoTarjeta extends Pago {

    private static final String PREFIJO_FONDOS_INSUFICIENTES = "4000";
    private static final DateTimeFormatter FORMATO_REFERENCIA = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private String numeroTarjeta;
    private String titular;
    private String ultimosDigitos;
    private String referencia;

    public PagoTarjeta() {
    }

    public PagoTarjeta(String numeroTarjeta, String titular) {
        this.numeroTarjeta = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("[\\s-]", "");
        this.titular = titular;
    }

    @Override
    public MetodoPago getMetodo() {
        return MetodoPago.TARJETA;
    }

    @Override
    protected void validarYAutorizar(BigDecimal total) {
        this.referencia = generarReferencia();
        if (titular == null || titular.isBlank()) {
            throw new PagoRechazadoExcepcion("Pago rechazado: debe indicar el titular de la tarjeta", referencia);
        }
        if (!numeroTarjeta.matches("\\d{13,19}") || !cumpleLuhn(numeroTarjeta)) {
            throw new PagoRechazadoExcepcion("Pago rechazado: número de tarjeta inválido", referencia);
        }
        if (numeroTarjeta.startsWith(PREFIJO_FONDOS_INSUFICIENTES)) {
            throw new PagoRechazadoExcepcion("Pago rechazado por el emisor: fondos insuficientes", referencia);
        }
        this.ultimosDigitos = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        this.numeroTarjeta = null;
    }

    @Override
    public String getDetalle() {
        return "Tarjeta **** " + ultimosDigitos + " · Ref. " + referencia;
    }

    private static String generarReferencia() {
        int aleatorio = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TRX-" + LocalDateTime.now().format(FORMATO_REFERENCIA) + "-" + aleatorio;
    }

    static boolean cumpleLuhn(String numero) {
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getUltimosDigitos() {
        return ultimosDigitos;
    }

    public void setUltimosDigitos(String ultimosDigitos) {
        this.ultimosDigitos = ultimosDigitos;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
