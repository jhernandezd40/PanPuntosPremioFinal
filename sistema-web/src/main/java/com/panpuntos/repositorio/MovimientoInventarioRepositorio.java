package com.panpuntos.repositorio;

import com.panpuntos.modelo.inventario.MovimientoInventario;

import java.util.List;

public interface MovimientoInventarioRepositorio {

    void registrar(MovimientoInventario movimiento);

    List<MovimientoInventario> listar(Integer productoId, int limite);
}
