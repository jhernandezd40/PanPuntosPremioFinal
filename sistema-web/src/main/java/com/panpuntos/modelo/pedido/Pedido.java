package com.panpuntos.modelo.pedido;

import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.EntidadBase;
import com.panpuntos.modelo.producto.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class Pedido extends EntidadBase {

    private Cliente cliente;
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    private final List<DetallePedido> detalles = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private int puntosOtorgados;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaAnulacion;

    public void agregarProducto(Producto producto, int cantidad) {
        if (!esModificable()) {
            throw new NegocioExcepcion("Solo se pueden agregar productos a un pedido pendiente");
        }
        if (!producto.esVendible()) {
            throw new NegocioExcepcion("El producto " + producto.getNombre() + " está desactivado y no puede venderse");
        }
        Optional<DetallePedido> existente = detalles.stream()
                .filter(detalle -> detalle.getProducto().getId().equals(producto.getId()))
                .findFirst();
        if (existente.isPresent()) {
            existente.get().incrementar(cantidad);
        } else {
            detalles.add(new DetallePedido(producto, cantidad));
        }
        recalcularTotal();
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public void recalcularTotal() {
        this.total = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calcularPuntos() {
        return detalles.stream().mapToInt(DetallePedido::calcularPuntos).sum();
    }

    public Map<Integer, Integer> unidadesRequeridas() {
        Map<Integer, Integer> requeridas = new TreeMap<>();
        for (DetallePedido detalle : detalles) {
            detalle.unidadesRequeridas().forEach((productoId, cantidad) ->
                    requeridas.merge(productoId, cantidad, Integer::sum));
        }
        return requeridas;
    }

    public void validarContenido() {
        if (detalles.isEmpty()) {
            throw new ValidacionExcepcion("El pedido debe incluir al menos un producto");
        }
    }

    public void marcarPagado(int puntos) {
        cambiarEstado(EstadoPedido.PAGADO);
        this.puntosOtorgados = puntos;
        this.fechaPago = LocalDateTime.now();
    }

    public void marcarEntregado() {
        cambiarEstado(EstadoPedido.ENTREGADO);
        this.fechaEntrega = LocalDateTime.now();
    }

    public void anular() {
        if (estado == EstadoPedido.PAGADO || estado == EstadoPedido.ENTREGADO) {
            throw new NegocioExcepcion("Un pedido ya pagado no puede anularse desde el módulo de ventas");
        }
        cambiarEstado(EstadoPedido.ANULADO);
        this.fechaAnulacion = LocalDateTime.now();
    }

    private void cambiarEstado(EstadoPedido destino) {
        if (!estado.puedeCambiarA(destino)) {
            throw new NegocioExcepcion("El pedido #" + getId() + " no puede pasar de "
                    + estado.getDescripcion() + " a " + destino.getDescripcion());
        }
        this.estado = destino;
    }

    public boolean esModificable() {
        return estado == EstadoPedido.PENDIENTE;
    }

    public boolean puedeCobrarse() {
        return estado.puedeCambiarA(EstadoPedido.PAGADO);
    }

    public boolean puedeEntregarse() {
        return estado.puedeCambiarA(EstadoPedido.ENTREGADO);
    }

    public boolean puedeAnularse() {
        return estado.puedeCambiarA(EstadoPedido.ANULADO);
    }

    public boolean tieneCliente() {
        return cliente != null;
    }

    public String getNombreCliente() {
        return cliente == null ? "Consumidor final" : cliente.getNombre();
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getPuntosOtorgados() {
        return puntosOtorgados;
    }

    public void setPuntosOtorgados(int puntosOtorgados) {
        this.puntosOtorgados = puntosOtorgados;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }
}
