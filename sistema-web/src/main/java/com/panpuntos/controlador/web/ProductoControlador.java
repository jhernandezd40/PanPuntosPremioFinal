package com.panpuntos.controlador.web;

import com.panpuntos.dto.ProductoFormulario;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.producto.CategoriaProducto;
import com.panpuntos.modelo.producto.Producto;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoControlador {

    private static final String VISTA_FORMULARIO = "productos/formulario";

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    @ModelAttribute("categorias")
    public CategoriaProducto[] categorias() {
        return CategoriaProducto.values();
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) CategoriaProducto categoria, Model modelo) {
        modelo.addAttribute("productos", productoServicio.buscar(q, categoria));
        modelo.addAttribute("q", q);
        modelo.addAttribute("categoriaSeleccionada", categoria);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) CategoriaProducto categoria, Model modelo) {
        ProductoFormulario formulario = new ProductoFormulario();
        formulario.setCategoria(categoria == null ? CategoriaProducto.SANDWICH : categoria);
        modelo.addAttribute("formulario", formulario);
        return mostrarFormulario(modelo);
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("formulario") ProductoFormulario formulario, BindingResult errores,
                            Model modelo, WebSession sesion) {
        if (errores.hasErrors()) {
            return mostrarFormulario(modelo);
        }
        try {
            Producto producto = productoServicio.registrar(formulario);
            MensajesFlash.exito(sesion, "Producto " + producto.getNombre() + " registrado correctamente");
            return "redirect:/productos";
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrarFormulario(modelo);
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model modelo) {
        modelo.addAttribute("formulario", productoServicio.aFormulario(productoServicio.obtener(id)));
        return mostrarFormulario(modelo);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute("formulario") ProductoFormulario formulario,
                             BindingResult errores, Model modelo, WebSession sesion) {
        formulario.setId(id);
        if (errores.hasErrors()) {
            return mostrarFormulario(modelo);
        }
        try {
            productoServicio.actualizar(id, formulario);
            MensajesFlash.exito(sesion, "Producto actualizado correctamente");
            return "redirect:/productos";
        } catch (NegocioExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
            return mostrarFormulario(modelo);
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable int id, @RequestParam boolean activo, WebSession sesion) {
        productoServicio.cambiarEstado(id, activo);
        MensajesFlash.exito(sesion, activo ? "Producto activado" : "Producto desactivado");
        return "redirect:/productos";
    }

    private String mostrarFormulario(Model modelo) {
        Map<CategoriaProducto, List<Producto>> componentes = productoServicio.componentesDisponibles();
        modelo.addAttribute("sandwiches", componentes.get(CategoriaProducto.SANDWICH));
        modelo.addAttribute("bebidas", componentes.get(CategoriaProducto.BEBIDA));
        modelo.addAttribute("acompanamientos", componentes.get(CategoriaProducto.ACOMPANAMIENTO));
        return VISTA_FORMULARIO;
    }
}
