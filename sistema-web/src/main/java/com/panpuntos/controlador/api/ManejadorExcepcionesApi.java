package com.panpuntos.controlador.api;

import com.panpuntos.dto.ErrorRespuesta;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = ManejadorExcepcionesApi.class)
public class ManejadorExcepcionesApi {

    private static final Logger REGISTRO = LoggerFactory.getLogger(ManejadorExcepcionesApi.class);

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoExcepcion excepcion) {
        return responder(HttpStatus.NOT_FOUND, excepcion.getMessage());
    }

    @ExceptionHandler(NegocioExcepcion.class)
    public ResponseEntity<ErrorRespuesta> negocio(NegocioExcepcion excepcion) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, excepcion.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> general(Exception excepcion) {
        REGISTRO.error("Error en la API", excepcion);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(ErrorRespuesta.de(estado.value(), mensaje));
    }
}
