package com.panpuntos.repositorio;

import com.panpuntos.modelo.fidelizacion.MovimientoPuntos;

import java.util.List;

public interface MovimientoPuntosRepositorio {

    void registrar(MovimientoPuntos movimiento);

    List<MovimientoPuntos> listarPorCliente(int clienteId);
}
