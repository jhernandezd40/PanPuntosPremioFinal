package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.reporte.PuntosCliente;
import com.panpuntos.repositorio.ReporteRepositorio;
import com.panpuntos.util.Formato;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class ReportePuntosClientes implements GeneradorReporte {

    private final ReporteRepositorio reporteRepositorio;

    public ReportePuntosClientes(ReporteRepositorio reporteRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
    }

    @Override
    public String clave() {
        return "puntos-clientes";
    }

    @Override
    public String titulo() {
        return "Puntos acumulados por cliente";
    }

    @Override
    public String descripcion() {
        return "Saldo actual, puntos acumulados y puntos canjeados de cada cliente";
    }

    @Override
    public boolean usaPeriodo() {
        return false;
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<PuntosCliente> clientes = reporteRepositorio.puntosPorCliente();
        TablaReporte tabla = new TablaReporte(titulo(), "Situación al momento de generar el reporte",
                "Identificador", "Cliente", "Acumulados", "Canjeados", "Saldo actual", "Activo");
        int saldoTotal = 0;
        for (PuntosCliente cliente : clientes) {
            tabla.agregarFila(cliente.identificador(), cliente.nombre(), cliente.acumulados(), cliente.canjeados(),
                    cliente.saldo(), Formato.siNo(cliente.activo()));
            saldoTotal += cliente.saldo();
        }
        tabla.agregarResumen("Clientes", clientes.size());
        tabla.agregarResumen("Puntos en circulación", saldoTotal);
        return tabla;
    }
}
