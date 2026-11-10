package com.panpuntos.modelo.pedido;

import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.EntidadBase;
import com.panpuntos.modelo.producto.Producto;

import java.math.BigDecimal;
import java.util.Map;

public class DetallePedido extends EntidadBase {

    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public DetallePedido() {
    }

    public DetallePedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
        setCantidad(cantidad);
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public int calcularPuntos() {
        return producto.calcularPuntos(cantidad);
    }

    public Map<Integer, Integer> unidadesRequeridas() {
        return producto.unidadesRequeridas(cantidad);
    }

    public void incrementar(int adicional) {
        setCantidad(cantidad + adicional);
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionExcepcion("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
