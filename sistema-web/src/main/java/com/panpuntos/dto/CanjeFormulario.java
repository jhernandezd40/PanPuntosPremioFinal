package com.panpuntos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CanjeFormulario {

    @NotBlank(message = "Ingrese el identificador del cliente")
    private String identificadorCliente;

    @NotNull(message = "Seleccione una recompensa")
    private Integer recompensaId;

    public String getIdentificadorCliente() {
        return identificadorCliente;
    }

    public void setIdentificadorCliente(String identificadorCliente) {
        this.identificadorCliente = identificadorCliente;
    }

    public Integer getRecompensaId() {
        return recompensaId;
    }

    public void setRecompensaId(Integer recompensaId) {
        this.recompensaId = recompensaId;
    }
}
