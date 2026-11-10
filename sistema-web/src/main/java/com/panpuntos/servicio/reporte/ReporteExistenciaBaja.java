package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.reporte.ExistenciaProducto;
import com.panpuntos.repositorio.ReporteRepositorio;
import com.panpuntos.util.Formato;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class ReporteExistenciaBaja implements GeneradorReporte {

    public static final int LIMITE_EXISTENCIA_BAJA = 5;

    private final ReporteRepositorio reporteRepositorio;

    public ReporteExistenciaBaja(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    @Override
    public String clave() {
        return "existencia-baja";
    }

    @Override
    public String titulo() {
        return "Productos agotados o con existencia baja";
    }

    @Override
    public String descripcion() {
        return "Productos sin unidades o con existencia menor o igual a " + LIMITE_EXISTENCIA_BAJA;
    }

    @Override
    public boolean usaPeriodo() {
        return false;
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<ExistenciaProducto> productos = reporteRepositorio.productosConExistenciaBaja(LIMITE_EXISTENCIA_BAJA);
        TablaReporte tabla = new TablaReporte(titulo(), "Existencia menor o igual a " + LIMITE_EXISTENCIA_BAJA + " unidades",
                "Código", "Producto", "Categoría", "Existencia", "Situación", "Activo");
        long agotados = productos.stream().filter(ExistenciaProducto::agotado).count();
        for (ExistenciaProducto producto : productos) {
            tabla.agregarFila(producto.codigo(), producto.nombre(), producto.categoria().getDescripcion(),
                    producto.existencia(), producto.agotado() ? "AGOTADO" : "Existencia baja",
                    Formato.siNo(producto.activo()));
        }
        tabla.agregarResumen("Agotados", agotados);
        tabla.agregarResumen("Con existencia baja", productos.size() - agotados);
        return tabla;
    }
}
