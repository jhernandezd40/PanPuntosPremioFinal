package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.pedido.DetallePedido;
import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.repositorio.PedidoRepositorio;
import com.panpuntos.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PedidoRepositorioJdbc extends RepositorioJdbcBase implements PedidoRepositorio {

    private static final String COLUMNAS = "SELECT p.id, p.cliente_id, p.estado, p.total, p.puntos_otorgados, "
            + "p.fecha_creacion, p.fecha_pago, p.fecha_entrega, p.fecha_anulacion, "
            + "c.identificador AS cliente_identificador, c.nombre AS cliente_nombre, c.puntos AS cliente_puntos, "
            + "c.activo AS cliente_activo FROM pedido p LEFT JOIN cliente c ON c.id = p.cliente_id";

    private final ProductoRepositorio productoRepositorio;

    public PedidoRepositorioJdbc(DataSource fuenteDatos, ProductoRepositorio productoRepositorio) {
        super(fuenteDatos);
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    public int guardar(Pedido pedido) {
        int id = insertar("INSERT INTO pedido (cliente_id, estado, total, puntos_otorgados, fecha_creacion) "
                        + "VALUES (?, ?, ?, ?, ?)",
                pedido.tieneCliente() ? pedido.getCliente().getId() : null, pedido.getEstado(), pedido.getTotal(),
                pedido.getPuntosOtorgados(), pedido.getFechaCreacion());
        pedido.setId(id);
        for (DetallePedido detalle : pedido.getDetalles()) {
            int idDetalle = insertar("INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    id, detalle.getProducto().getId(), detalle.getCantidad(), detalle.getPrecioUnitario(),
                    detalle.getSubtotal());
            detalle.setId(idDetalle);
        }
        return id;
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        return consultarUno(COLUMNAS + " WHERE p.id = ?", this::mapear, id).map(this::cargarDetalles);
    }

    @Override
    public Optional<Pedido> buscarPorIdParaActualizar(int id) {
        return consultarUno(COLUMNAS + " WHERE p.id = ? FOR UPDATE", this::mapear, id).map(this::cargarDetalles);
    }

    @Override
    public List<Pedido> listar(EstadoPedido estado) {
        return consultarLista(COLUMNAS + " WHERE (? IS NULL OR p.estado = ?) ORDER BY p.fecha_creacion DESC, p.id DESC",
                this::mapear, estado, estado);
    }

    @Override
    public List<Pedido> listarPorCliente(int clienteId) {
        return consultarLista(COLUMNAS + " WHERE p.cliente_id = ? ORDER BY p.fecha_creacion DESC, p.id DESC",
                this::mapear, clienteId);
    }

    @Override
    public int contarPorEstado(EstadoPedido estado) {
        return consultarEntero("SELECT COUNT(*) FROM pedido WHERE estado = ?", estado);
    }

    @Override
    public void actualizarEstado(Pedido pedido) {
        actualizar("UPDATE pedido SET estado = ?, puntos_otorgados = ?, fecha_pago = ?, fecha_entrega = ?, "
                        + "fecha_anulacion = ? WHERE id = ?",
                pedido.getEstado(), pedido.getPuntosOtorgados(), pedido.getFechaPago(), pedido.getFechaEntrega(),
                pedido.getFechaAnulacion(), pedido.getId());
    }

    private Pedido cargarDetalles(Pedido pedido) {
        List<FilaDetalle> filas = consultarLista(
                "SELECT id, producto_id, cantidad, precio_unitario FROM detalle_pedido WHERE pedido_id = ? ORDER BY id",
                resultado -> new FilaDetalle(resultado.getInt("id"), resultado.getInt("producto_id"),
                        resultado.getInt("cantidad"), resultado.getBigDecimal("precio_unitario")),
                pedido.getId());
        Map<Integer, Producto> productos = productoRepositorio.buscarPorIds(
                filas.stream().map(FilaDetalle::productoId).distinct().toList());
        for (FilaDetalle fila : filas) {
            DetallePedido detalle = new DetallePedido();
            detalle.setId(fila.id());
            detalle.setProducto(productos.get(fila.productoId()));
            detalle.setCantidad(fila.cantidad());
            detalle.setPrecioUnitario(fila.precioUnitario());
            pedido.agregarDetalle(detalle);
        }
        return pedido;
    }

    private Pedido mapear(ResultSet resultado) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(resultado.getInt("id"));
        pedido.setEstado(EstadoPedido.valueOf(resultado.getString("estado")));
        pedido.setTotal(resultado.getBigDecimal("total"));
        pedido.setPuntosOtorgados(resultado.getInt("puntos_otorgados"));
        pedido.setFechaCreacion(leerFecha(resultado, "fecha_creacion"));
        pedido.setFechaPago(leerFecha(resultado, "fecha_pago"));
        pedido.setFechaEntrega(leerFecha(resultado, "fecha_entrega"));
        pedido.setFechaAnulacion(leerFecha(resultado, "fecha_anulacion"));
        Integer clienteId = leerEnteroNulo(resultado, "cliente_id");
        if (clienteId != null) {
            Cliente cliente = new Cliente();
            cliente.setId(clienteId);
            cliente.setIdentificador(resultado.getString("cliente_identificador"));
            cliente.setNombre(resultado.getString("cliente_nombre"));
            cliente.setPuntos(resultado.getInt("cliente_puntos"));
            cliente.setActivo(resultado.getBoolean("cliente_activo"));
            pedido.setCliente(cliente);
        }
        return pedido;
    }

    private record FilaDetalle(int id, int productoId, int cantidad, BigDecimal precioUnitario) {
    }
}
