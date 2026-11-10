package com.panpuntos.controlador.web;

import com.panpuntos.dto.AbastecimientoFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.inventario.MovimientoInventario;
import com.panpuntos.servicio.InventarioServicio;
import com.panpuntos.servicio.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.WebSession;

@Controller
@RequestMapping("/inventario")
public class InventarioControlador {

    private final InventarioServicio inventarioServicio;
    private final ProductoServicio productoServicio;

    public InventarioControlador(InventarioServicio inventarioServicio, ProductoServicio productoServicio) {
        this.inventarioServicio = inventarioServicio;
        this.productoServicio = productoServicio;
    }

    @GetMapping
    public String inicio(@RequestParam(required = false) Integer productoId, Model modelo) {
        AbastecimientoFormulario formulario = new AbastecimientoFormulario();
        formulario.setProductoId(productoId);
        modelo.addAttribute("formulario", formulario);
        return mostrar(productoId, modelo);
    }

    @PostMapping("/abastecer")
    public String abastecer(@Valid @ModelAttribute("formulario") AbastecimientoFormulario formulario,
                            BindingResult errores, Model modelo, WebSession sesion) {
        if (errores.hasErrors()) {
            return mostrar(null, modelo);
        }
        try {
            MovimientoInventario movimiento = inventarioServicio.abastecer(formulario);
            MensajesFlash.exito(sesion, "Se ingresaron " + movimiento.getCantidad()
                    + " unidades. Existencia actual: " + movimiento.getExistenciaResultante());
            return "redirect:/inventario";
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrar(null, modelo);
        }
    }

    private String mostrar(Integer productoFiltro, Model modelo) {
        modelo.addAttribute("productos", productoServicio.listarAbastecibles());
        modelo.addAttribute("movimientos", inventarioServicio.historial(productoFiltro));
        modelo.addAttribute("productoFiltro", productoFiltro);
        return "inventario/inicio";
    }
}
