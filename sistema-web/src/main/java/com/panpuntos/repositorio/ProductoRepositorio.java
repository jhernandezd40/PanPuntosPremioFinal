package com.panpuntos.repositorio;

import com.panpuntos.modelo.producto.CategoriaProducto;
import com.panpuntos.modelo.producto.Menu;
import com.panpuntos.modelo.producto.Producto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductoRepositorio {

    List<Producto> buscar(String texto, CategoriaProducto categoria, boolean soloActivos);

    Optional<Producto> buscarPorId(int id);

    Map<Integer, Producto> buscarPorIds(Collection<Integer> ids);

    Map<Integer, Producto> bloquearPorIds(Collection<Integer> ids);

    boolean existeCodigo(String codigo, Integer excluirId);

    int guardar(Producto producto);

    void actualizar(Producto producto);

    void guardarComponentes(Menu menu);

    void cambiarEstado(int id, boolean activo);

    boolean descontarExistencia(int id, int cantidad);

    void aumentarExistencia(int id, int cantidad);
}
