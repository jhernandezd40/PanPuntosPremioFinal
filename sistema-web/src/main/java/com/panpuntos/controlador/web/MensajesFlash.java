package com.panpuntos.controlador.web;

import org.springframework.web.server.WebSession;

import java.util.HashMap;
import java.util.Map;

public final class MensajesFlash {

    private static final String CLAVE = "mensajesFlash";

    private MensajesFlash() {
    }

    public static void exito(WebSession sesion, String mensaje) {
        agregar(sesion, "exito", mensaje);
    }

    public static void error(WebSession sesion, String mensaje) {
        agregar(sesion, "error", mensaje);
    }

    public static void advertencia(WebSession sesion, String mensaje) {
        agregar(sesion, "advertencia", mensaje);
    }

    @SuppressWarnings("unchecked")
    public static void agregar(WebSession sesion, String clave, Object valor) {
        Map<String, Object> pendientes = (Map<String, Object>) sesion.getAttributes()
                .computeIfAbsent(CLAVE, llave -> new HashMap<String, Object>());
        pendientes.put(clave, valor);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> consumir(WebSession sesion) {
        Object pendientes = sesion.getAttributes().remove(CLAVE);
        return pendientes == null ? Map.of() : (Map<String, Object>) pendientes;
    }
}
