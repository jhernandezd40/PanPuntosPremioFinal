package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.inventario.MovimientoInventario;
import com.panpuntos.modelo.inventario.TipoMovimientoInventario;
import com.panpuntos.repositorio.MovimientoInventarioRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MovimientoInventarioRepositorioJdbc extends RepositorioJdbcBase implements MovimientoInventarioRepositorio {

    public MovimientoInventarioRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public void registrar(MovimientoInventario movimiento) {
        int id = insertar("INSERT INTO movimiento_inventario (producto_id, tipo, cantidad, existencia_resultante, "
                        + "referencia, fecha) VALUES (?, ?, ?, ?, ?, ?)",
                movimiento.getProductoId(), movimiento.getTipo(), movimiento.getCantidad(),
                movimiento.getExistenciaResultante(), movimiento.getReferencia(), movimiento.getFecha());
        movimiento.setId(id);
    }

    @Override
    public List<MovimientoInventario> listar(Integer productoId, int limite) {
        String sql = "SELECT m.id, m.producto_id, p.codigo, p.nombre, m.tipo, m.cantidad, m.existencia_resultante, "
                + "m.referencia, m.fecha FROM movimiento_inventario m JOIN producto p ON p.id = m.producto_id "
                + "WHERE (? IS NULL OR m.producto_id = ?) ORDER BY m.fecha DESC, m.id DESC LIMIT ?";
        return consultarLista(sql, this::mapear, productoId, productoId, limite);
    }

    private MovimientoInventario mapear(ResultSet resultado) throws SQLException {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(resultado.getInt("id"));
        movimiento.setProductoId(resultado.getInt("producto_id"));
        movimiento.setProductoCodigo(resultado.getString("codigo"));
        movimiento.setProductoNombre(resultado.getString("nombre"));
        movimiento.setTipo(TipoMovimientoInventario.valueOf(resultado.getString("tipo")));
        movimiento.setCantidad(resultado.getInt("cantidad"));
        movimiento.setExistenciaResultante(resultado.getInt("existencia_resultante"));
        movimiento.setReferencia(resultado.getString("referencia"));
        movimiento.setFecha(leerFecha(resultado, "fecha"));
        return movimiento;
    }
}
