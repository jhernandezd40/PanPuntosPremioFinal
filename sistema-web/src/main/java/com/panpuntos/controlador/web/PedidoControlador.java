package com.panpuntos.controlador.web;

import com.panpuntos.dto.LineaPedidoFormulario;
import com.panpuntos.dto.PagoFormulario;
import com.panpuntos.dto.PedidoFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.modelo.pago.MetodoPago;
import com.panpuntos.modelo.pago.Pago;
import com.panpuntos.modelo.pedido.EstadoPedido;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.servicio.ClienteServicio;
import com.panpuntos.servicio.PagoServicio;
import com.panpuntos.servicio.PedidoServicio;
import com.panpuntos.servicio.ProductoServicio;
import com.panpuntos.servicio.reporte.ExportadorPdf;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.WebSession;

import java.util.function.Supplier;

@Controller
@RequestMapping("/pedidos")
public class PedidoControlador {

    private static final String ATRIBUTO_PAGO = "pagoFormulario";

    private final PedidoServicio pedidoServicio;
    private final PagoServicio pagoServicio;
    private final ProductoServicio productoServicio;
    private final ClienteServicio clienteServicio;
    private final ExportadorPdf exportadorPdf;

    public PedidoControlador(PedidoServicio pedidoServicio, PagoServicio pagoServicio,
                             ProductoServicio productoServicio, ClienteServicio clienteServicio,
                             ExportadorPdf exportadorPdf) {
        this.pedidoServicio = pedidoServicio;
        this.pagoServicio = pagoServicio;
        this.productoServicio = productoServicio;
        this.clienteServicio = clienteServicio;
        this.exportadorPdf = exportadorPdf;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoPedido estado, Model modelo) {
        modelo.addAttribute("pedidos", pedidoServicio.listar(estado));
        modelo.addAttribute("estados", EstadoPedido.values());
        modelo.addAttribute("estadoSeleccionado", estado);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer clienteId, Model modelo) {
        PedidoFormulario formulario = new PedidoFormulario();
        formulario.setClienteId(clienteId);
        formulario.getLineas().add(new LineaPedidoFormulario());
        modelo.addAttribute("formulario", formulario);
        return mostrarFormulario(modelo);
    }

    @PostMapping
    public String crear(@ModelAttribute("formulario") PedidoFormulario formulario, Model modelo, WebSession sesion) {
        try {
            Pedido pedido = pedidoServicio.crear(formulario);
            MensajesFlash.exito(sesion, "Pedido #" + pedido.getId() + " creado en estado PENDIENTE");
            return "redirect:/pedidos/" + pedido.getId();
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            if (formulario.getLineas().isEmpty()) {
                formulario.getLineas().add(new LineaPedidoFormulario());
            }
            return mostrarFormulario(modelo);
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model modelo) {
        modelo.addAttribute("pedido", pedidoServicio.obtener(id));
        modelo.addAttribute("pago", pagoServicio.obtenerPorPedido(id).orElse(null));
        if (!modelo.containsAttribute(ATRIBUTO_PAGO)) {
            modelo.addAttribute(ATRIBUTO_PAGO, new PagoFormulario());
        }
        modelo.addAttribute("metodos", MetodoPago.values());
        return "pedidos/detalle";
    }

    @PostMapping("/{id}/cobrar")
    public String cobrar(@PathVariable int id, @ModelAttribute PagoFormulario pagoFormulario, WebSession sesion) {
        try {
            pagoServicio.cobrar(id, pagoFormulario);
            MensajesFlash.exito(sesion, "Pago aprobado. El pedido #" + id + " quedó PAGADO");
            return "redirect:/pedidos/" + id + "/comprobante";
        } catch (NegocioExcepcion e) {
            MensajesFlash.error(sesion, e.getMessage());
            pagoFormulario.setNumeroTarjeta(null);
            MensajesFlash.agregar(sesion, ATRIBUTO_PAGO, pagoFormulario);
            return "redirect:/pedidos/" + id;
        }
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable int id, WebSession sesion) {
        return ejecutarTransicion(id, sesion, () -> pedidoServicio.entregar(id), "entregado");
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable int id, WebSession sesion) {
        return ejecutarTransicion(id, sesion, () -> pedidoServicio.anular(id), "anulado");
    }

    @GetMapping("/{id}/comprobante")
    public String comprobante(@PathVariable int id, Model modelo) {
        modelo.addAttribute("pedido", pedidoServicio.obtener(id));
        modelo.addAttribute("pago", obtenerPago(id));
        return "pedidos/comprobante";
    }

    @GetMapping("/{id}/comprobante/pdf")
    public ResponseEntity<byte[]> comprobantePdf(@PathVariable int id) {
        byte[] contenido = exportadorPdf.comprobante(pedidoServicio.obtener(id), obtenerPago(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename("comprobante-pedido-" + id + ".pdf").build().toString())
                .body(contenido);
    }

    private Pago obtenerPago(int pedidoId) {
        return pagoServicio.obtenerPorPedido(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Comprobante del pedido", pedidoId));
    }

    private String ejecutarTransicion(int id, WebSession sesion, Supplier<Pedido> accion, String resultado) {
        try {
            accion.get();
            MensajesFlash.exito(sesion, "Pedido #" + id + " " + resultado + " correctamente");
        } catch (NegocioExcepcion e) {
            MensajesFlash.error(sesion, e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    private String mostrarFormulario(Model modelo) {
        modelo.addAttribute("productos", productoServicio.listarVendibles());
        modelo.addAttribute("clientes", clienteServicio.listarActivos());
        return "pedidos/formulario";
    }
}
