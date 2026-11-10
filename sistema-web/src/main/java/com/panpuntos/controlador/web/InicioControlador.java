package com.panpuntos.controlador.web;

import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.servicio.ClienteServicio;
import com.panpuntos.servicio.PagoServicio;
import com.panpuntos.servicio.PedidoServicio;
import com.panpuntos.servicio.reporte.FiltroReporte;
import com.panpuntos.servicio.reporte.ReporteServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioControlador {

    private final ClienteServicio clienteServicio;
    private final PedidoServicio pedidoServicio;
    private final PagoServicio pagoServicio;
    private final ReporteServicio reporteServicio;

    public InicioControlador(ClienteServicio clienteServicio, PedidoServicio pedidoServicio,
                             PagoServicio pagoServicio, ReporteServicio reporteServicio) {
        this.clienteServicio = clienteServicio;
        this.pedidoServicio = pedidoServicio;
        this.pagoServicio = pagoServicio;
        this.reporteServicio = reporteServicio;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {
        modelo.addAttribute("clientesActivos", clienteServicio.contarActivos());
        modelo.addAttribute("pedidosPendientes", pedidoServicio.contarPorEstado(EstadoPedido.PENDIENTE));
        modelo.addAttribute("pedidosPorEntregar", pedidoServicio.contarPorEstado(EstadoPedido.PAGADO));
        modelo.addAttribute("ventasHoy", pagoServicio.ventasDeHoy());
        modelo.addAttribute("existenciaBaja", reporteServicio.generar("existencia-baja", new FiltroReporte(null, null)));
        modelo.addAttribute("pedidosRecientes", pedidoServicio.listar(EstadoPedido.PENDIENTE));
        return "inicio";
    }
}
