package com.panpuntos.servicio;

import com.panpuntos.dto.CanjeFormulario;
import com.panpuntos.dto.RecompensaFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.PuntosInsuficientesExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.fidelizacion.Canje;
import com.panpuntos.modelo.fidelizacion.MovimientoPuntos;
import com.panpuntos.modelo.fidelizacion.Recompensa;
import com.panpuntos.modelo.inventario.TipoMovimientoInventario;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.repositorio.CanjeRepositorio;
import com.panpuntos.repositorio.ClienteRepositorio;
import com.panpuntos.repositorio.MovimientoPuntosRepositorio;
import com.panpuntos.repositorio.ProductoRepositorio;
import com.panpuntos.repositorio.RecompensaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FidelizacionServicio {

    private final ClienteRepositorio clienteRepositorio;
    private final RecompensaRepositorio recompensaRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CanjeRepositorio canjeRepositorio;
    private final MovimientoPuntosRepositorio movimientoPuntosRepositorio;
    private final InventarioServicio inventarioServicio;

    public FidelizacionServicio(ClienteRepositorio clienteRepositorio, RecompensaRepositorio recompensaRepositorio,
                                ProductoRepositorio productoRepositorio, CanjeRepositorio canjeRepositorio,
                                MovimientoPuntosRepositorio movimientoPuntosRepositorio,
                                InventarioServicio inventarioServicio) {
        this.clienteRepositorio = clienteRepositorio;
        this.recompensaRepositorio = recompensaRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.canjeRepositorio = canjeRepositorio;
        this.movimientoPuntosRepositorio = movimientoPuntosRepositorio;
        this.inventarioServicio = inventarioServicio;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public int acreditarPorPedido(Pedido pedido) {
        int puntos = pedido.calcularPuntos();
        if (!pedido.tieneCliente() || puntos == 0) {
            return 0;
        }
        Cliente cliente = clienteRepositorio.buscarPorIdParaActualizar(pedido.getCliente().getId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cliente", pedido.getCliente().getId()));
        cliente.acreditarPuntos(puntos);
        clienteRepositorio.actualizarPuntos(cliente.getId(), cliente.getPuntos());
        movimientoPuntosRepositorio.registrar(
                MovimientoPuntos.acumulacion(cliente.getId(), puntos, cliente.getPuntos(), pedido.getId()));
        return puntos;
    }

    @Transactional
    public Canje canjear(CanjeFormulario formulario) {
        String identificador = formulario.getIdentificadorCliente().trim().toUpperCase();
        Cliente encontrado = clienteRepositorio.buscarPorIdentificador(identificador)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cliente", identificador));
        Cliente cliente = clienteRepositorio.buscarPorIdParaActualizar(encontrado.getId()).orElseThrow();
        if (!cliente.isActivo()) {
            throw new NegocioExcepcion("Canje rechazado: el cliente " + cliente.getNombre() + " está desactivado");
        }
        Recompensa recompensa = obtenerRecompensa(formulario.getRecompensaId());
        if (!recompensa.isActiva()) {
            throw new NegocioExcepcion("Canje rechazado: la recompensa " + recompensa.getNombre() + " no está activa");
        }
        Producto producto = recompensa.getProducto();
        if (producto == null || !producto.esVendible()) {
            throw new NegocioExcepcion("Canje rechazado: el producto de la recompensa no está disponible");
        }
        if (!cliente.tieneSaldo(recompensa.getPuntosRequeridos())) {
            throw new PuntosInsuficientesExcepcion(cliente.getNombre(), cliente.getPuntos(),
                    recompensa.getPuntosRequeridos());
        }
        inventarioServicio.descontar(producto.unidadesRequeridas(1), TipoMovimientoInventario.CANJE,
                "Canje - " + cliente.getIdentificador());
        cliente.debitarPuntos(recompensa.getPuntosRequeridos());
        clienteRepositorio.actualizarPuntos(cliente.getId(), cliente.getPuntos());
        Canje canje = new Canje(cliente.getId(), recompensa);
        canje.setClienteIdentificador(cliente.getIdentificador());
        canje.setClienteNombre(cliente.getNombre());
        canjeRepositorio.guardar(canje);
        movimientoPuntosRepositorio.registrar(MovimientoPuntos.canje(cliente.getId(), recompensa.getPuntosRequeridos(),
                cliente.getPuntos(), canje.getId(), recompensa.getNombre()));
        return canje;
    }

    @Transactional(readOnly = true)
    public List<Recompensa> listarRecompensas(boolean soloActivas) {
        return recompensaRepositorio.listar(soloActivas);
    }

    @Transactional(readOnly = true)
    public Recompensa obtenerRecompensa(int id) {
        return recompensaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Recompensa", id));
    }

    @Transactional
    public Recompensa registrarRecompensa(RecompensaFormulario formulario) {
        Recompensa recompensa = new Recompensa();
        aplicarDatos(recompensa, formulario);
        recompensa.setActiva(true);
        recompensa.setId(recompensaRepositorio.guardar(recompensa));
        return recompensa;
    }

    @Transactional
    public Recompensa actualizarRecompensa(int id, RecompensaFormulario formulario) {
        Recompensa recompensa = obtenerRecompensa(id);
        aplicarDatos(recompensa, formulario);
        recompensaRepositorio.actualizar(recompensa);
        return recompensa;
    }

    @Transactional
    public void cambiarEstadoRecompensa(int id, boolean activa) {
        obtenerRecompensa(id);
        recompensaRepositorio.cambiarEstado(id, activa);
    }

    public RecompensaFormulario aFormulario(Recompensa recompensa) {
        RecompensaFormulario formulario = new RecompensaFormulario();
        formulario.setId(recompensa.getId());
        formulario.setNombre(recompensa.getNombre());
        formulario.setDescripcion(recompensa.getDescripcion());
        formulario.setPuntosRequeridos(recompensa.getPuntosRequeridos());
        formulario.setProductoId(recompensa.getProducto() == null ? null : recompensa.getProducto().getId());
        return formulario;
    }

    private void aplicarDatos(Recompensa recompensa, RecompensaFormulario formulario) {
        Producto producto = productoRepositorio.buscarPorId(formulario.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Producto", formulario.getProductoId()));
        recompensa.setNombre(formulario.getNombre().trim());
        recompensa.setDescripcion(formulario.getDescripcion() == null ? null : formulario.getDescripcion().trim());
        recompensa.setPuntosRequeridos(formulario.getPuntosRequeridos());
        recompensa.setProducto(producto);
    }
}
