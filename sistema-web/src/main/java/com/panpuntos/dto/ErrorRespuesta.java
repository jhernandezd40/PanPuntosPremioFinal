package com.panpuntos.dto;

import java.time.LocalDateTime;

public record ErrorRespuesta(int estado, String mensaje, LocalDateTime fecha) {

    public static ErrorRespuesta de(int estado, String mensaje) {
        return new ErrorRespuesta(estado, mensaje, LocalDateTime.now());
    }
}
