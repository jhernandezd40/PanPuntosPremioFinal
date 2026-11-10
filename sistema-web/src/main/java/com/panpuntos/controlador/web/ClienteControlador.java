package com.panpuntos.controlador.web;

import com.panpuntos.dto.ClienteFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.servicio.ClienteServicio;
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

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    private static final String VISTA_FORMULARIO = "clientes/formulario";

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model modelo) {
        modelo.addAttribute("clientes", clienteServicio.buscar(q));
        modelo.addAttribute("q", q);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {
        modelo.addAttribute("formulario", new ClienteFormulario());
        return VISTA_FORMULARIO;
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("formulario") ClienteFormulario formulario, BindingResult errores,
                            Model modelo, WebSession sesion) {
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            Cliente cliente = clienteServicio.registrar(formulario);
            MensajesFlash.exito(sesion, "Cliente " + cliente.getNombre() + " registrado correctamente");
            return "redirect:/clientes/" + cliente.getId();
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return VISTA_FORMULARIO;
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model modelo) {
        modelo.addAttribute("cliente", clienteServicio.obtener(id));
        modelo.addAttribute("pedidos", clienteServicio.historialPedidos(id));
        modelo.addAttribute("movimientos", clienteServicio.historialPuntos(id));
        modelo.addAttribute("canjes", clienteServicio.historialCanjes(id));
        return "clientes/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model modelo) {
        modelo.addAttribute("formulario", clienteServicio.aFormulario(clienteServicio.obtener(id)));
        return VISTA_FORMULARIO;
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute("formulario") ClienteFormulario formulario,
                             BindingResult errores, Model modelo, WebSession sesion) {
        formulario.setId(id);
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            clienteServicio.actualizar(id, formulario);
            MensajesFlash.exito(sesion, "Cliente actualizado correctamente");
            return "redirect:/clientes/" + id;
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return VISTA_FORMULARIO;
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable int id, @RequestParam boolean activo, WebSession sesion) {
        clienteServicio.cambiarEstado(id, activo);
        MensajesFlash.exito(sesion, activo ? "Cliente activado" : "Cliente desactivado");
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, WebSession sesion) {
        if (clienteServicio.eliminarODesactivar(id)) {
            MensajesFlash.exito(sesion, "Cliente eliminado correctamente");
            return "redirect:/clientes";
        }
        MensajesFlash.advertencia(sesion,
                "El cliente tiene historial de compras o puntos: no se elimina físicamente, se desactivó");
        return "redirect:/clientes/" + id;
    }
}
