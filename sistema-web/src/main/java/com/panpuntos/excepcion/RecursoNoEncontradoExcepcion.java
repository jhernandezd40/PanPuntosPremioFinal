package com.panpuntos.excepcion;

public class RecursoNoEncontradoExcepcion extends NegocioExcepcion {

    public RecursoNoEncontradoExcepcion(String recurso, Object identificador) {
        super(recurso + " con identificador '" + identificador + "' no existe");
    }
}
