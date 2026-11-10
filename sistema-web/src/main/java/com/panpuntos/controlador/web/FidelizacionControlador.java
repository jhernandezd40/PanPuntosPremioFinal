package com.panpuntos.controlador.web;

import com.panpuntos.dto.CanjeFormulario;
import com.panpuntos.dto.RecompensaFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.fidelizacion.Canje;
import com.panpuntos.servicio.ClienteServicio;
import com.panpuntos.servicio.FidelizacionServicio;
import com.panpuntos.servicio.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/fidelizacion")
public class FidelizacionControlador {

    private static final String VISTA_RECOMPENSA = "fidelizacion/recompensa";

    private final FidelizacionServicio fidelizacionServicio;
    private final ClienteServicio clienteServicio;
    private final ProductoServicio productoServicio;

    public FidelizacionControlador(FidelizacionServicio fidelizacionServicio, ClienteServicio clienteServicio,
                                   ProductoServicio productoServicio) {
        this.fidelizacionServicio = fidelizacionServicio;
        this.clienteServicio = clienteServicio;
        this.productoServicio = productoServicio;
    }

    @GetMapping
    public String inicio(@RequestParam(required = false) String identificador, Model modelo) {
        CanjeFormulario formulario = new CanjeFormulario();
        formulario.setIdentificadorCliente(identificador);
        modelo.addAttribute("formulario", formulario);
        if (identificador != null && !identificador.isBlank()) {
            try {
                Cliente cliente = clienteServicio.obtenerPorIdentificador(identificador);
                modelo.addAttribute("cliente", cliente);
            } catch (NegocioExcepcion e) {
                modelo.addAttribute("error", e.getMessage());
            }
        }
        return mostrar(modelo);
    }

    @PostMapping("/canjear")
    public String canjear(@Valid @ModelAttribute("formulario") CanjeFormulario formulario, BindingResult errores,
                          Model modelo, WebSession sesion) {
        if (errores.hasErrors()) {
            return mostrar(modelo);
        }
        try {
            Canje canje = fidelizacionServicio.canjear(formulario);
            MensajesFlash.exito(sesion, "Canje aprobado: " + canje.getRecompensaNombre() + " para "
                    + canje.getClienteNombre() + " (-" + canje.getPuntosUtilizados() + " pts)");
            return "redirect:/fidelizacion?identificador="
                    + UriUtils.encodeQueryParam(canje.getClienteIdentificador(), StandardCharsets.UTF_8);
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrar(modelo);
        }
    }

    @GetMapping("/recompensas/nueva")
    public String nuevaRecompensa(Model modelo) {
        modelo.addAttribute("formulario", new RecompensaFormulario());
        return mostrarFormularioRecompensa(modelo);
    }

    @PostMapping("/recompensas")
    public String registrarRecompensa(@Valid @ModelAttribute("formulario") RecompensaFormulario formulario,
                                      BindingResult errores, Model modelo, WebSession sesion) {
        if (errores.hasErrors()) {
            return mostrarFormularioRecompensa(modelo);
        }
        try {
            fidelizacionServicio.registrarRecompensa(formulario);
            MensajesFlash.exito(sesion, "Recompensa registrada correctamente");
            return "redirect:/fidelizacion";
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrarFormularioRecompensa(modelo);
        }
    }

    @GetMapping("/recompensas/{id}/editar")
    public String editarRecompensa(@PathVariable int id, Model modelo) {
        modelo.addAttribute("formulario", fidelizacionServicio.aFormulario(fidelizacionServicio.obtenerRecompensa(id)));
        return mostrarFormularioRecompensa(modelo);
    }

    @PostMapping("/recompensas/{id}")
    public String actualizarRecompensa(@PathVariable int id,
                                       @Valid @ModelAttribute("formulario") RecompensaFormulario formulario,
                                       BindingResult errores, Model modelo, WebSession sesion) {
        formulario.setId(id);
        if (errores.hasErrors()) {
            return mostrarFormularioRecompensa(modelo);
        }
        try {
            fidelizacionServicio.actualizarRecompensa(id, formulario);
            MensajesFlash.exito(sesion, "Recompensa actualizada correctamente");
            return "redirect:/fidelizacion";
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrarFormularioRecompensa(modelo);
        }
    }

    @PostMapping("/recompensas/{id}/estado")
    public String cambiarEstadoRecompensa(@PathVariable int id, @RequestParam boolean activa, WebSession sesion) {
        fidelizacionServicio.cambiarEstadoRecompensa(id, activa);
        MensajesFlash.exito(sesion, activa ? "Recompensa activada" : "Recompensa desactivada");
        return "redirect:/fidelizacion";
    }

    private String mostrar(Model modelo) {
        modelo.addAttribute("recompensas", fidelizacionServicio.listarRecompensas(false));
        return "fidelizacion/inicio";
    }

    private String mostrarFormularioRecompensa(Model modelo) {
        modelo.addAttribute("productos", productoServicio.listarActivos());
        return VISTA_RECOMPENSA;
    }
}
