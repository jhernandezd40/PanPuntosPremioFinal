package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.pago.MetodoPago;
import com.panpuntos.modelo.producto.CategoriaProducto;
import com.panpuntos.modelo.reporte.ExistenciaProducto;
import com.panpuntos.modelo.reporte.ProductoVendido;
import com.panpuntos.modelo.reporte.PuntosCliente;
import com.panpuntos.modelo.reporte.VentaMenu;
import com.panpuntos.modelo.reporte.VentaPeriodo;
import com.panpuntos.repositorio.ReporteRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ReporteRepositorioJdbc extends RepositorioJdbcBase implements ReporteRepositorio {

    private static final String PEDIDOS_COBRADOS =
            "p.estado IN ('PAGADO', 'ENTREGADO') AND p.fecha_pago >= ? AND p.fecha_pago < ?";

    public ReporteRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public List<VentaPeriodo> ventasPorPeriodo(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT pg.pedido_id, pg.fecha, COALESCE(c.nombre, 'Consumidor final') AS cliente, pg.metodo, "
                + "pg.monto, pg.referencia FROM pago pg JOIN pedido p ON p.id = pg.pedido_id "
                + "LEFT JOIN cliente c ON c.id = p.cliente_id WHERE pg.fecha >= ? AND pg.fecha < ? ORDER BY pg.fecha";
        return consultarLista(sql, resultado -> new VentaPeriodo(
                        resultado.getInt("pedido_id"),
                        leerFecha(resultado, "fecha"),
                        resultado.getString("cliente"),
                        MetodoPago.valueOf(resultado.getString("metodo")),
                        resultado.getBigDecimal("monto"),
                        resultado.getString("referencia")),
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }

    @Override
    public List<ProductoVendido> productosMasVendidos(LocalDate desde, LocalDate hasta) {
        String sql = "WITH ventas AS (SELECT d.producto_id, d.cantidad FROM detalle_pedido d "
                + "JOIN pedido p ON p.id = d.pedido_id WHERE " + PEDIDOS_COBRADOS + "), "
                + "unidades AS ("
                + "SELECT producto_id, cantidad AS directa, 0 AS componente FROM ventas "
                + "UNION ALL SELECT mc.sandwich_id, 0, v.cantidad FROM ventas v JOIN menu_componente mc ON mc.menu_id = v.producto_id "
                + "UNION ALL SELECT mc.bebida_id, 0, v.cantidad FROM ventas v JOIN menu_componente mc ON mc.menu_id = v.producto_id "
                + "UNION ALL SELECT mc.acompanamiento_id, 0, v.cantidad FROM ventas v JOIN menu_componente mc ON mc.menu_id = v.producto_id) "
                + "SELECT pr.codigo, pr.nombre, pr.categoria, SUM(u.directa) AS directa, SUM(u.componente) AS componente "
                + "FROM unidades u JOIN producto pr ON pr.id = u.producto_id "
                + "GROUP BY pr.id, pr.codigo, pr.nombre, pr.categoria "
                + "ORDER BY SUM(u.directa) + SUM(u.componente) DESC, pr.nombre";
        return consultarLista(sql, resultado -> new ProductoVendido(
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        CategoriaProducto.valueOf(resultado.getString("categoria")),
                        resultado.getInt("directa"),
                        resultado.getInt("componente")),
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }

    @Override
    public List<ExistenciaProducto> productosConExistenciaBaja(int limite) {
        String sql = "SELECT codigo, nombre, categoria, existencia, activo FROM producto "
                + "WHERE categoria <> 'MENU' AND existencia <= ? ORDER BY existencia, nombre";
        return consultarLista(sql, resultado -> new ExistenciaProducto(
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        CategoriaProducto.valueOf(resultado.getString("categoria")),
                        resultado.getInt("existencia"),
                        resultado.getBoolean("activo")),
                limite);
    }

    @Override
    public List<PuntosCliente> puntosPorCliente() {
        String sql = "SELECT c.identificador, c.nombre, c.puntos, c.activo, "
                + "COALESCE(SUM(CASE WHEN m.tipo = 'ACUMULACION' THEN m.puntos END), 0) AS acumulados, "
                + "COALESCE(SUM(CASE WHEN m.tipo = 'CANJE' THEN m.puntos END), 0) AS canjeados "
                + "FROM cliente c LEFT JOIN movimiento_puntos m ON m.cliente_id = c.id "
                + "GROUP BY c.id, c.identificador, c.nombre, c.puntos, c.activo ORDER BY c.puntos DESC, c.nombre";
        return consultarLista(sql, resultado -> new PuntosCliente(
                resultado.getString("identificador"),
                resultado.getString("nombre"),
                resultado.getInt("puntos"),
                resultado.getInt("acumulados"),
                resultado.getInt("canjeados"),
                resultado.getBoolean("activo")));
    }

    @Override
    public List<VentaMenu> ventasDeMenus(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT pr.codigo, pr.nombre, COUNT(DISTINCT p.id) AS pedidos, SUM(d.cantidad) AS cantidad, "
                + "SUM(d.subtotal) AS total FROM detalle_pedido d JOIN pedido p ON p.id = d.pedido_id "
                + "JOIN producto pr ON pr.id = d.producto_id WHERE pr.categoria = 'MENU' AND " + PEDIDOS_COBRADOS
                + " GROUP BY pr.id, pr.codigo, pr.nombre ORDER BY SUM(d.cantidad) DESC";
        return consultarLista(sql, resultado -> new VentaMenu(
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        resultado.getInt("pedidos"),
                        resultado.getInt("cantidad"),
                        resultado.getBigDecimal("total")),
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }
}
