package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.reporte.VentaMenu;
import com.panpuntos.repositorio.ReporteRepositorio;
import com.panpuntos.util.Formato;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(6)
public class ReporteVentasMenus implements GeneradorReporte {

    private final ReporteRepositorio reporteRepositorio;

    public ReporteVentasMenus(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    @Override
    public String clave() {
        return "ventas-menus";
    }

    @Override
    public String titulo() {
        return "Ventas de menús completos";
    }

    @Override
    public String descripcion() {
        return "Menús completos vendidos en pedidos pagados del período";
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<VentaMenu> menus = reporteRepositorio.ventasDeMenus(filtro.desde(), filtro.hasta());
        TablaReporte tabla = new TablaReporte(titulo(), filtro.descripcion(),
                "Código", "Menú", "Pedidos", "Unidades", "Total vendido");
        int unidades = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (VentaMenu menu : menus) {
            tabla.agregarFila(menu.codigo(), menu.nombre(), menu.pedidos(), menu.cantidad(), Formato.moneda(menu.total()));
            unidades += menu.cantidad();
            total = total.add(menu.total());
        }
        tabla.agregarResumen("Menús vendidos", unidades);
        tabla.agregarResumen("Total", Formato.moneda(total));
        return tabla;
    }
}
