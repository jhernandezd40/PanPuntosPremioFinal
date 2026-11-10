package com.panpuntos.controlador.web;

import com.panpuntos.excepcion.AccesoDatosExcepcion;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice(basePackageClasses = ManejadorExcepcionesWeb.class)
public class ManejadorExcepcionesWeb {

    private static final Logger REGISTRO = LoggerFactory.getLogger(ManejadorExcepcionesWeb.class);
    private static final String VISTA_ERROR = "error";

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoExcepcion excepcion, Model modelo) {
        return mostrar(modelo, "Recurso no encontrado", excepcion.getMessage());
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String solicitudInvalida(ServerWebInputException excepcion, Model modelo) {
        return mostrar(modelo, "Solicitud inválida", "Los datos enviados no tienen un formato válido");
    }

    @ExceptionHandler(NegocioExcepcion.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String negocio(NegocioExcepcion excepcion, Model modelo) {
        return mostrar(modelo, "Operación no permitida", excepcion.getMessage());
    }

    @ExceptionHandler(AccesoDatosExcepcion.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String accesoDatos(AccesoDatosExcepcion excepcion, Model modelo) {
        REGISTRO.error("Error de acceso a datos", excepcion);
        return mostrar(modelo, "Error de base de datos",
                "No fue posible completar la operación con la base de datos. Intente nuevamente.");
    }

    private String mostrar(Model modelo, String titulo, String mensaje) {
        modelo.addAttribute("titulo", titulo);
        modelo.addAttribute("mensaje", mensaje);
        return VISTA_ERROR;
    }
}
