package com.panpuntos.repositorio;

import com.panpuntos.modelo.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositorio {

    List<Cliente> buscar(String texto);

    List<Cliente> listarActivos();

    Optional<Cliente> buscarPorId(int id);

    Optional<Cliente> buscarPorIdParaActualizar(int id);

    Optional<Cliente> buscarPorIdentificador(String identificador);

    boolean existeIdentificador(String identificador, Integer excluirId);

    boolean tieneHistorial(int id);

    int contarActivos();

    int guardar(Cliente cliente);

    void actualizar(Cliente cliente);

    void actualizarPuntos(int id, int puntos);

    void cambiarEstado(int id, boolean activo);

    void eliminar(int id);
}
