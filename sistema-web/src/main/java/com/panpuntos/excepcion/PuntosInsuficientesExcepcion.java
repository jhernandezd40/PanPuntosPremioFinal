package com.panpuntos.excepcion;

public class PuntosInsuficientesExcepcion extends NegocioExcepcion {

    public PuntosInsuficientesExcepcion(String cliente, int saldo, int requeridos) {
        super("Puntos insuficientes: " + cliente + " tiene " + saldo + " pts y la recompensa requiere " + requeridos + " pts");
    }
}
