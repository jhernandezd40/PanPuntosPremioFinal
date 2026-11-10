package com.panpuntos.repositorio;

import com.panpuntos.modelo.fidelizacion.Canje;

import java.time.LocalDate;
import java.util.List;

public interface CanjeRepositorio {

    int guardar(Canje canje);

    List<Canje> listarPorCliente(int clienteId);

    List<Canje> listarPorPeriodo(LocalDate desde, LocalDate hasta);
}
