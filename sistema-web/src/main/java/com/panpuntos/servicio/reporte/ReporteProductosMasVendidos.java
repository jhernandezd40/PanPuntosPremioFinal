package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.reporte.ProductoVendido;
import com.panpuntos.repositorio.ReporteRepositorio;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class ReporteProductosMasVendidos implements GeneradorReporte {

    private final ReporteRepositorio reporteRepositorio;

    public ReporteProductosMasVendidos(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    @Override
    public String clave() {
        return "productos-mas-vendidos";
    }

    @Override
    public String titulo() {
        return "Productos más vendidos";
    }

    @Override
    public String descripcion() {
        return "Unidades vendidas por producto, incluyendo las consumidas como componentes de menú";
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<ProductoVendido> productos = reporteRepositorio.productosMasVendidos(filtro.desde(), filtro.hasta());
        TablaReporte tabla = new TablaReporte(titulo(), filtro.descripcion(),
                "Posición", "Código", "Producto", "Categoría", "Venta directa", "En menús", "Total unidades");
        int posicion = 1;
        int unidades = 0;
        for (ProductoVendido producto : productos) {
            tabla.agregarFila(posicion++, producto.codigo(), producto.nombre(), producto.categoria().getDescripcion(),
                    producto.ventaDirecta(), producto.comoComponente(), producto.total());
            unidades += producto.total();
        }
        tabla.agregarResumen("Productos distintos", productos.size());
        tabla.agregarResumen("Unidades totales", unidades);
        return tabla;
    }
}
