package com.panpuntos.excepcion;

public class PagoRechazadoExcepcion extends NegocioExcepcion {

    private final String referencia;

    public PagoRechazadoExcepcion(String mensaje, String referencia) {
        super(referencia == null ? mensaje : mensaje + " (Ref. " + referencia + ")");
        this.referencia = referencia;
    }

    public String getReferencia() {
        return referencia;
    }
}
