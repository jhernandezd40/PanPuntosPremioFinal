package com.panpuntos.servicio;

import com.panpuntos.dto.PagoFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.inventario.TipoMovimientoInventario;
import com.panpuntos.modelo.pago.Pago;
import com.panpuntos.modelo.pago.PagoEfectivo;
import com.panpuntos.modelo.pago.PagoTarjeta;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.repositorio.PagoRepositorio;
import com.panpuntos.repositorio.PedidoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PagoServicio {

    private final PedidoServicio pedidoServicio;
    private final PedidoRepositorio pedidoRepositorio;
    private final PagoRepositorio pagoRepositorio;
    private final InventarioServicio inventarioServicio;
    private final FidelizacionServicio fidelizacionServicio;

    public PagoServicio(PedidoServicio pedidoServicio, PedidoRepositorio pedidoRepositorio,
                        PagoRepositorio pagoRepositorio, InventarioServicio inventarioServicio,
                        FidelizacionServicio fidelizacionServicio) {
        this.pedidoServicio = pedidoServicio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.pagoRepositorio = pagoRepositorio;
        this.inventarioServicio = inventarioServicio;
        this.fidelizacionServicio = fidelizacionServicio;
    }

    @Transactional
    public Pago cobrar(int pedidoId, PagoFormulario formulario) {
        Pedido pedido = pedidoServicio.obtenerParaActualizar(pedidoId);
        if (!pedido.puedeCobrarse()) {
            throw new NegocioExcepcion("El pedido #" + pedidoId + " está " + pedido.getEstado().getDescripcion()
                    + " y no puede cobrarse");
        }
        Pago pago = crearPago(formulario);
        pago.setPedidoId(pedidoId);
        pago.procesar(pedido.getTotal());
        inventarioServicio.descontar(pedido.unidadesRequeridas(), TipoMovimientoInventario.VENTA,
                "Pedido #" + pedidoId);
        pagoRepositorio.guardar(pago);
        int puntos = fidelizacionServicio.acreditarPorPedido(pedido);
        pedido.marcarPagado(puntos);
        pedidoRepositorio.actualizarEstado(pedido);
        return pago;
    }

    @Transactional(readOnly = true)
    public Optional<Pago> obtenerPorPedido(int pedidoId) {
        return pagoRepositorio.buscarPorPedido(pedidoId);
    }

    @Transactional(readOnly = true)
    public BigDecimal ventasDeHoy() {
        return pagoRepositorio.totalDelDia(LocalDate.now());
    }

    private Pago crearPago(PagoFormulario formulario) {
        if (formulario.getMetodo() == null) {
            throw new ValidacionExcepcion("Seleccione el método de pago");
        }
        return switch (formulario.getMetodo()) {
            case EFECTIVO -> new PagoEfectivo(formulario.getMontoRecibido());
            case TARJETA -> new PagoTarjeta(formulario.getNumeroTarjeta(), formulario.getTitular());
        };
    }
}
