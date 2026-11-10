package com.panpuntos.excepcion;

public class AccesoDatosExcepcion extends RuntimeException {

    public AccesoDatosExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
