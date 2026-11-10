package com.panpuntos.modelo;

import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.modelo.producto.Acompanamiento;
import com.panpuntos.modelo.producto.Bebida;
import com.panpuntos.modelo.producto.Menu;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.modelo.producto.Sandwich;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    private Sandwich sandwich;
    private Bebida bebida;
    private Acompanamiento acompanamiento;
    private Menu menu;

    @BeforeEach
    void preparar() {
        sandwich = producto(new Sandwich(), 1, "35.00", 10);
        bebida = producto(new Bebida(), 2, "10.00", 3);
        acompanamiento = producto(new Acompanamiento(), 3, "15.00", 20);
        menu = producto(new Menu(), 4, "55.00", 0);
        menu.setSandwich(sandwich);
        menu.setBebida(bebida);
        menu.setAcompanamiento(acompanamiento);
    }

    @Test
    void calculaPuntosSegunFormula() {
        Pedido pedido = new Pedido();
        pedido.agregarProducto(sandwich, 3);
        pedido.agregarProducto(menu, 2);
        pedido.agregarProducto(bebida, 1);
        pedido.agregarProducto(acompanamiento, 1);
        assertEquals(3 * 2 + 2 * 8, pedido.calcularPuntos());
    }

    @Test
    void elMenuUsaPrecioPropioYDescuentaComponentes() {
        Pedido pedido = new Pedido();
        pedido.agregarProducto(menu, 2);
        pedido.agregarProducto(sandwich, 1);
        assertEquals(new BigDecimal("145.00"), pedido.getTotal());
        Map<Integer, Integer> requeridas = pedido.unidadesRequeridas();
        assertEquals(3, requeridas.get(sandwich.getId()));
        assertEquals(2, requeridas.get(bebida.getId()));
        assertEquals(2, requeridas.get(acompanamiento.getId()));
        assertFalse(requeridas.containsKey(menu.getId()));
    }

    @Test
    void laDisponibilidadDelMenuDependeDelComponenteMasEscaso() {
        assertEquals(3, menu.unidadesDisponibles());
    }

    @Test
    void agrupaLineasDelMismoProducto() {
        Pedido pedido = new Pedido();
        pedido.agregarProducto(sandwich, 1);
        pedido.agregarProducto(sandwich, 2);
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(3, pedido.getDetalles().get(0).getCantidad());
    }

    @Test
    void noPermiteVenderProductoDesactivado() {
        bebida.setActivo(false);
        Pedido pedido = new Pedido();
        assertThrows(NegocioExcepcion.class, () -> pedido.agregarProducto(bebida, 1));
        assertThrows(NegocioExcepcion.class, () -> pedido.agregarProducto(menu, 1));
    }

    @Test
    void respetaTransicionesDeEstado() {
        Pedido pedido = new Pedido();
        pedido.agregarProducto(sandwich, 1);
        pedido.marcarPagado(2);
        assertEquals(EstadoPedido.PAGADO, pedido.getEstado());
        assertThrows(NegocioExcepcion.class, pedido::anular);
        pedido.marcarEntregado();
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void pedidoPendientePuedeAnularsePeroNoEntregarse() {
        Pedido pedido = new Pedido();
        pedido.agregarProducto(sandwich, 1);
        assertThrows(NegocioExcepcion.class, pedido::marcarEntregado);
        pedido.anular();
        assertEquals(EstadoPedido.ANULADO, pedido.getEstado());
        assertThrows(NegocioExcepcion.class, () -> pedido.marcarPagado(0));
    }

    private static <T extends Producto> T producto(T producto, int id, String precio, int existencia) {
        producto.setId(id);
        producto.setNombre("Producto " + id);
        producto.setPrecio(new BigDecimal(precio));
        producto.setExistencia(existencia);
        return producto;
    }
}
