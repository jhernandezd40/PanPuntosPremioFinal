package com.panpuntos.repositorio;

import com.panpuntos.modelo.fidelizacion.Recompensa;

import java.util.List;
import java.util.Optional;

public interface RecompensaRepositorio {

    List<Recompensa> listar(boolean soloActivas);

    Optional<Recompensa> buscarPorId(int id);

    int guardar(Recompensa recompensa);

    void actualizar(Recompensa recompensa);

    void cambiarEstado(int id, boolean activa);
}
