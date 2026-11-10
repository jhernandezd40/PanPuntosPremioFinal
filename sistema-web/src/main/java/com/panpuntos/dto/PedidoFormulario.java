package com.panpuntos.dto;

import java.util.ArrayList;
import java.util.List;

public class PedidoFormulario {

    private Integer clienteId;
    private List<LineaPedidoFormulario> lineas = new ArrayList<>();

    public List<LineaPedidoFormulario> lineasCompletas() {
        return lineas.stream().filter(LineaPedidoFormulario::estaCompleta).toList();
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<LineaPedidoFormulario> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaPedidoFormulario> lineas) {
        this.lineas = lineas;
    }
}
