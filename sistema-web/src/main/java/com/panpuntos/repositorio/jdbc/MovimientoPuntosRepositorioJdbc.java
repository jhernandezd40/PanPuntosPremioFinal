package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.fidelizacion.MovimientoPuntos;
import com.panpuntos.modelo.fidelizacion.TipoMovimientoPuntos;
import com.panpuntos.repositorio.MovimientoPuntosRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MovimientoPuntosRepositorioJdbc extends RepositorioJdbcBase implements MovimientoPuntosRepositorio {

    public MovimientoPuntosRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public void registrar(MovimientoPuntos movimiento) {
        int id = insertar("INSERT INTO movimiento_puntos (cliente_id, tipo, puntos, saldo_resultante, pedido_id, canje_id, "
                        + "descripcion, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                movimiento.getClienteId(), movimiento.getTipo(), movimiento.getPuntos(), movimiento.getSaldoResultante(),
                movimiento.getPedidoId(), movimiento.getCanjeId(), movimiento.getDescripcion(), movimiento.getFecha());
        movimiento.setId(id);
    }

    @Override
    public List<MovimientoPuntos> listarPorCliente(int clienteId) {
        return consultarLista("SELECT id, cliente_id, tipo, puntos, saldo_resultante, pedido_id, canje_id, descripcion, "
                + "fecha FROM movimiento_puntos WHERE cliente_id = ? ORDER BY fecha DESC, id DESC", this::mapear, clienteId);
    }

    private MovimientoPuntos mapear(ResultSet resultado) throws SQLException {
        MovimientoPuntos movimiento = new MovimientoPuntos();
        movimiento.setId(resultado.getInt("id"));
        movimiento.setClienteId(resultado.getInt("cliente_id"));
        movimiento.setTipo(TipoMovimientoPuntos.valueOf(resultado.getString("tipo")));
        movimiento.setPuntos(resultado.getInt("puntos"));
        movimiento.setSaldoResultante(resultado.getInt("saldo_resultante"));
        movimiento.setPedidoId(leerEnteroNulo(resultado, "pedido_id"));
        movimiento.setCanjeId(leerEnteroNulo(resultado, "canje_id"));
        movimiento.setDescripcion(resultado.getString("descripcion"));
        movimiento.setFecha(leerFecha(resultado, "fecha"));
        return movimiento;
    }
}
