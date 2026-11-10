package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.fidelizacion.Recompensa;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.repositorio.ProductoRepositorio;
import com.panpuntos.repositorio.RecompensaRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RecompensaRepositorioJdbc extends RepositorioJdbcBase implements RecompensaRepositorio {

    private static final String COLUMNAS =
            "SELECT id, nombre, descripcion, puntos_requeridos, producto_id, activa FROM recompensa";

    private final ProductoRepositorio productoRepositorio;

    public RecompensaRepositorioJdbc(DataSource fuenteDatos, ProductoRepositorio productoRepositorio) {
        super(fuenteDatos);
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    public List<Recompensa> listar(boolean soloActivas) {
        String sql = COLUMNAS + (soloActivas ? " WHERE activa = TRUE" : "") + " ORDER BY puntos_requeridos";
        List<FilaRecompensa> filas = consultarLista(sql, this::mapear);
        Map<Integer, Producto> productos = productoRepositorio.buscarPorIds(
                filas.stream().map(FilaRecompensa::productoId).distinct().toList());
        return filas.stream().map(fila -> fila.construir(productos.get(fila.productoId()))).toList();
    }

    @Override
    public Optional<Recompensa> buscarPorId(int id) {
        return consultarUno(COLUMNAS + " WHERE id = ?", this::mapear, id)
                .map(fila -> fila.construir(productoRepositorio.buscarPorId(fila.productoId()).orElse(null)));
    }

    @Override
    public int guardar(Recompensa recompensa) {
        return insertar("INSERT INTO recompensa (nombre, descripcion, puntos_requeridos, producto_id, activa) "
                        + "VALUES (?, ?, ?, ?, ?)",
                recompensa.getNombre(), recompensa.getDescripcion(), recompensa.getPuntosRequeridos(),
                recompensa.getProducto().getId(), recompensa.isActiva());
    }

    @Override
    public void actualizar(Recompensa recompensa) {
        actualizar("UPDATE recompensa SET nombre = ?, descripcion = ?, puntos_requeridos = ?, producto_id = ? WHERE id = ?",
                recompensa.getNombre(), recompensa.getDescripcion(), recompensa.getPuntosRequeridos(),
                recompensa.getProducto().getId(), recompensa.getId());
    }

    @Override
    public void cambiarEstado(int id, boolean activa) {
        actualizar("UPDATE recompensa SET activa = ? WHERE id = ?", activa, id);
    }

    private FilaRecompensa mapear(ResultSet resultado) throws SQLException {
        return new FilaRecompensa(resultado.getInt("id"), resultado.getString("nombre"),
                resultado.getString("descripcion"), resultado.getInt("puntos_requeridos"),
                resultado.getInt("producto_id"), resultado.getBoolean("activa"));
    }

    private record FilaRecompensa(int id, String nombre, String descripcion, int puntosRequeridos, int productoId,
                                  boolean activa) {

        Recompensa construir(Producto producto) {
            Recompensa recompensa = new Recompensa();
            recompensa.setId(id);
            recompensa.setNombre(nombre);
            recompensa.setDescripcion(descripcion);
            recompensa.setPuntosRequeridos(puntosRequeridos);
            recompensa.setProducto(producto);
            recompensa.setActiva(activa);
            return recompensa;
        }
    }
}
