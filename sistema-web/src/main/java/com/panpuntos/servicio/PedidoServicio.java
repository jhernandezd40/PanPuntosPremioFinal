package com.panpuntos.servicio;

import com.panpuntos.dto.LineaPedidoFormulario;
import com.panpuntos.dto.PedidoFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.repositorio.ClienteRepositorio;
import com.panpuntos.repositorio.PedidoRepositorio;
import com.panpuntos.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final InventarioServicio inventarioServicio;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, ProductoRepositorio productoRepositorio,
                          ClienteRepositorio clienteRepositorio, InventarioServicio inventarioServicio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.inventarioServicio = inventarioServicio;
    }

    @Transactional
    public Pedido crear(PedidoFormulario formulario) {
        List<LineaPedidoFormulario> lineas = formulario.lineasCompletas();
        if (lineas.isEmpty()) {
            throw new ValidacionExcepcion("El pedido debe incluir al menos un producto");
        }
        Pedido pedido = new Pedido();
        pedido.setCliente(resolverCliente(formulario.getClienteId()));
        Map<Integer, Producto> productos = productoRepositorio.buscarPorIds(
                lineas.stream().map(LineaPedidoFormulario::getProductoId).distinct().toList());
        for (LineaPedidoFormulario linea : lineas) {
            Producto producto = productos.get(linea.getProductoId());
            if (producto == null) {
                throw new RecursoNoEncontradoExcepcion("Producto", linea.getProductoId());
            }
            pedido.agregarProducto(producto, linea.getCantidad());
        }
        pedido.validarContenido();
        inventarioServicio.verificarDisponibilidad(pedido.unidadesRequeridas());
        pedido.setFechaCreacion(LocalDateTime.now());
        pedidoRepositorio.guardar(pedido);
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar(EstadoPedido estado) {
        return pedidoRepositorio.listar(estado);
    }

    @Transactional(readOnly = true)
    public int contarPorEstado(EstadoPedido estado) {
        return pedidoRepositorio.contarPorEstado(estado);
    }

    @Transactional(readOnly = true)
    public Pedido obtener(int id) {
        return pedidoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pedido", id));
    }

    @Transactional
    public Pedido entregar(int id) {
        Pedido pedido = obtenerParaActualizar(id);
        pedido.marcarEntregado();
        pedidoRepositorio.actualizarEstado(pedido);
        return pedido;
    }

    @Transactional
    public Pedido anular(int id) {
        Pedido pedido = obtenerParaActualizar(id);
        pedido.anular();
        pedidoRepositorio.actualizarEstado(pedido);
        return pedido;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Pedido obtenerParaActualizar(int id) {
        return pedidoRepositorio.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pedido", id));
    }

    private Cliente resolverCliente(Integer clienteId) {
        if (clienteId == null) {
            return null;
        }
        Cliente cliente = clienteRepositorio.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cliente", clienteId));
        if (!cliente.isActivo()) {
            throw new NegocioExcepcion("El cliente " + cliente.getNombre() + " está desactivado");
        }
        return cliente;
    }
}
