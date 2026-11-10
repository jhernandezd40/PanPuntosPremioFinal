package com.panpuntos.servicio.reporte;

import com.panpuntos.modelo.fidelizacion.Canje;
import com.panpuntos.repositorio.CanjeRepositorio;
import com.panpuntos.util.Formato;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(5)
public class ReporteHistorialCanjes implements GeneradorReporte {

    private final CanjeRepositorio canjeRepositorio;

    public ReporteHistorialCanjes(CanjeRepositorio canjeRepositorio) {
        this.canjeRepositorio = canjeRepositorio;
    }

    @Override
    public String clave() {
        return "historial-canjes";
    }

    @Override
    public String titulo() {
        return "Historial de canjes";
    }

    @Override
    public String descripcion() {
        return "Recompensas canjeadas por los clientes en el período";
    }

    @Override
    public TablaReporte generar(FiltroReporte filtro) {
        List<Canje> canjes = canjeRepositorio.listarPorPeriodo(filtro.desde(), filtro.hasta());
        TablaReporte tabla = new TablaReporte(titulo(), filtro.descripcion(),
                "Canje", "Fecha", "Identificador", "Cliente", "Recompensa", "Producto", "Puntos");
        int puntos = 0;
        for (Canje canje : canjes) {
            tabla.agregarFila("#" + canje.getId(), Formato.fechaHora(canje.getFecha()), canje.getClienteIdentificador(),
                    canje.getClienteNombre(), canje.getRecompensaNombre(), canje.getProductoNombre(),
                    canje.getPuntosUtilizados());
            puntos += canje.getPuntosUtilizados();
        }
        tabla.agregarResumen("Canjes realizados", canjes.size());
        tabla.agregarResumen("Puntos utilizados", puntos);
        return tabla;
    }
}
