package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.fidelizacion.Canje;
import com.panpuntos.repositorio.CanjeRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CanjeRepositorioJdbc extends RepositorioJdbcBase implements CanjeRepositorio {

    private static final String COLUMNAS = "SELECT cj.id, cj.cliente_id, c.identificador, c.nombre AS cliente_nombre, "
            + "cj.recompensa_id, r.nombre AS recompensa_nombre, cj.producto_id, p.nombre AS producto_nombre, "
            + "cj.puntos_utilizados, cj.fecha FROM canje cj "
            + "JOIN cliente c ON c.id = cj.cliente_id "
            + "JOIN recompensa r ON r.id = cj.recompensa_id "
            + "JOIN producto p ON p.id = cj.producto_id";

    public CanjeRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public int guardar(Canje canje) {
        int id = insertar("INSERT INTO canje (cliente_id, recompensa_id, producto_id, puntos_utilizados, fecha) "
                        + "VALUES (?, ?, ?, ?, ?)",
                canje.getClienteId(), canje.getRecompensaId(), canje.getProductoId(), canje.getPuntosUtilizados(),
                canje.getFecha());
        canje.setId(id);
        return id;
    }

    @Override
    public List<Canje> listarPorCliente(int clienteId) {
        return consultarLista(COLUMNAS + " WHERE cj.cliente_id = ? ORDER BY cj.fecha DESC", this::mapear, clienteId);
    }

    @Override
    public List<Canje> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return consultarLista(COLUMNAS + " WHERE cj.fecha >= ? AND cj.fecha < ? ORDER BY cj.fecha DESC", this::mapear,
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }

    private Canje mapear(ResultSet resultado) throws SQLException {
        Canje canje = new Canje();
        canje.setId(resultado.getInt("id"));
        canje.setClienteId(resultado.getInt("cliente_id"));
        canje.setClienteIdentificador(resultado.getString("identificador"));
        canje.setClienteNombre(resultado.getString("cliente_nombre"));
        canje.setRecompensaId(resultado.getInt("recompensa_id"));
        canje.setRecompensaNombre(resultado.getString("recompensa_nombre"));
        canje.setProductoId(resultado.getInt("producto_id"));
        canje.setProductoNombre(resultado.getString("producto_nombre"));
        canje.setPuntosUtilizados(resultado.getInt("puntos_utilizados"));
        canje.setFecha(leerFecha(resultado, "fecha"));
        return canje;
    }
}
