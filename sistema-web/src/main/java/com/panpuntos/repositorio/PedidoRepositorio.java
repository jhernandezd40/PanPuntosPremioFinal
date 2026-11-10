package com.panpuntos.repositorio;

import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.modelo.pedido.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepositorio {

    int guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(int id);

    Optional<Pedido> buscarPorIdParaActualizar(int id);

    List<Pedido> listar(EstadoPedido estado);

    List<Pedido> listarPorCliente(int clienteId);

    int contarPorEstado(EstadoPedido estado);

    void actualizarEstado(Pedido pedido);
}
