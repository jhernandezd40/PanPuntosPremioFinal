package com.panpuntos.modelo;

import com.panpuntos.excepcion.PagoRechazadoExcepcion;
import com.panpuntos.excepcion.PuntosInsuficientesExcepcion;
import com.panpuntos.modelo.pago.Pago;
import com.panpuntos.modelo.pago.PagoEfectivo;
import com.panpuntos.modelo.pago.PagoTarjeta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagoTest {

    private static final BigDecimal TOTAL = new BigDecimal("125.00");

    @Test
    void efectivoCalculaCambio() {
        PagoEfectivo pago = new PagoEfectivo(new BigDecimal("150.00"));
        pago.procesar(TOTAL);
        assertEquals(new BigDecimal("25.00"), pago.getCambio());
        assertEquals(TOTAL, pago.getMonto());
    }

    @Test
    void efectivoRechazaMontoInsuficiente() {
        Pago pago = new PagoEfectivo(new BigDecimal("100.00"));
        assertThrows(PagoRechazadoExcepcion.class, () -> pago.procesar(TOTAL));
    }

    @Test
    void tarjetaValidaSeApruebaConReferencia() {
        PagoTarjeta pago = new PagoTarjeta("4111 1111 1111 1111", "Ana López");
        pago.procesar(TOTAL);
        assertEquals("1111", pago.getUltimosDigitos());
        assertNotNull(pago.getReferencia());
    }

    @Test
    void tarjetaInvalidaOSinFondosSeRechaza() {
        assertThrows(PagoRechazadoExcepcion.class,
                () -> new PagoTarjeta("4111111111111112", "Ana").procesar(TOTAL));
        PagoRechazadoExcepcion rechazo = assertThrows(PagoRechazadoExcepcion.class,
                () -> new PagoTarjeta("4000000000000002", "Ana").procesar(TOTAL));
        assertNotNull(rechazo.getReferencia());
    }

    @Test
    void clienteNoPuedeCanjearSinSaldo() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.acreditarPuntos(12);
        assertThrows(PuntosInsuficientesExcepcion.class, () -> cliente.debitarPuntos(30));
        cliente.debitarPuntos(10);
        assertEquals(2, cliente.getPuntos());
    }
}
