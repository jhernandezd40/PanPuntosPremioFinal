package com.panpuntos.repositorio;

import com.panpuntos.modelo.reporte.ExistenciaProducto;
import com.panpuntos.modelo.reporte.ProductoVendido;
import com.panpuntos.modelo.reporte.PuntosCliente;
import com.panpuntos.modelo.reporte.VentaMenu;
import com.panpuntos.modelo.reporte.VentaPeriodo;

import java.time.LocalDate;
import java.util.List;

public interface ReporteRepositorio {

    List<VentaPeriodo> ventasPorPeriodo(LocalDate desde, LocalDate hasta);

    List<ProductoVendido> productosMasVendidos(LocalDate desde, LocalDate hasta);

    List<ExistenciaProducto> productosConExistenciaBaja(int limite);

    List<PuntosCliente> puntosPorCliente();

    List<VentaMenu> ventasDeMenus(LocalDate desde, LocalDate hasta);
}
