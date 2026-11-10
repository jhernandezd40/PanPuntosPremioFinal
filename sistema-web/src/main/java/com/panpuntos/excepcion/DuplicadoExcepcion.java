package com.panpuntos.excepcion;

public class DuplicadoExcepcion extends ValidacionExcepcion {

    public DuplicadoExcepcion(String campo, String valor) {
        super("Ya existe un registro con " + campo + " '" + valor + "'");
    }
}
