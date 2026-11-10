package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.pago.MetodoPago;
import com.panpuntos.modelo.reporte.VentaPeriodo;
import com.panpuntos.repositorio.ReporteRepositorio;
import com.panpuntos.util.Formato;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class ReporteVentasPeriodo implements GeneradorReporte {

    private final ReporteRepositorio reporteRepositorio;

    public ReporteVentasPeriodo(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    @Override
    public String clave() {
        return "ventas-periodo";
    }

    @Override
    public String titulo() {
        return "Ventas por período";
    }

    @Override
    public String descripcion() {
        return "Pagos registrados en el período, desglosados por efectivo y tarjeta";
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<VentaPeriodo> ventas = reporteRepositorio.ventasPorPeriodo(filtro.desde(), filtro.hasta());
        TablaReporte tabla = new TablaReporte(titulo(), filtro.descripcion(),
                "Pedido", "Fecha de pago", "Cliente", "Método", "Referencia", "Monto");
        Map<MetodoPago, BigDecimal> totales = new EnumMap<>(MetodoPago.class);
        Map<MetodoPago, Integer> cantidades = new EnumMap<>(MetodoPago.class);
        for (MetodoPago metodo : MetodoPago.values()) {
            totales.put(metodo, BigDecimal.ZERO);
            cantidades.put(metodo, 0);
        }
        for (VentaPeriodo venta : ventas) {
            tabla.agregarFila("#" + venta.pedidoId(), Formato.fechaHora(venta.fecha()), venta.cliente(),
                    venta.metodo().getDescripcion(), Formato.texto(venta.referencia()), Formato.moneda(venta.monto()));
            totales.merge(venta.metodo(), venta.monto(), BigDecimal::add);
            cantidades.merge(venta.metodo(), 1, Integer::sum);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (MetodoPago metodo : MetodoPago.values()) {
            tabla.agregarResumen(metodo.getDescripcion() + " (" + cantidades.get(metodo) + " ventas)",
                    Formato.moneda(totales.get(metodo)));
            total = total.add(totales.get(metodo));
        }
        tabla.agregarResumen("Total general (" + ventas.size() + " ventas)", Formato.moneda(total));
        return tabla;
    }
}
