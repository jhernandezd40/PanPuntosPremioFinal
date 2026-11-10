package com.panpuntos.servicio;

import com.panpuntos.dto.ProductoFormulario;
import com.panpuntos.excepcion.DuplicadoExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.producto.Acompanamiento;
import com.panpuntos.modelo.producto.Bebida;
import com.panpuntos.modelo.producto.CategoriaProducto;
import com.panpuntos.modelo.producto.FabricaProducto;
import com.panpuntos.modelo.producto.Menu;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.modelo.producto.Sandwich;
import com.panpuntos.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final InventarioServicio inventarioServicio;

    public ProductoServicio(ProductoRepositorio productoRepositorio, InventarioServicio inventarioServicio) {
        this.productoRepositorio = productoRepositorio;
        this.inventarioServicio = inventarioServicio;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscar(String texto, CategoriaProducto categoria) {
        return productoRepositorio.buscar(texto, categoria, false);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarVendibles() {
        return productoRepositorio.buscar(null, null, true).stream()
                .filter(producto -> producto.estaDisponible(1))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarAbastecibles() {
        return productoRepositorio.buscar(null, null, false).stream()
                .filter(producto -> producto.getCategoria().manejaExistenciaPropia())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepositorio.buscar(null, null, true);
    }

    @Transactional(readOnly = true)
    public Map<CategoriaProducto, List<Producto>> componentesDisponibles() {
        Map<CategoriaProducto, List<Producto>> agrupados = productoRepositorio.buscar(null, null, true).stream()
                .filter(producto -> producto.getCategoria().manejaExistenciaPropia())
                .collect(Collectors.groupingBy(Producto::getCategoria,
                        () -> new EnumMap<>(CategoriaProducto.class), Collectors.toList()));
        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            agrupados.putIfAbsent(categoria, List.of());
        }
        return agrupados;
    }

    @Transactional(readOnly = true)
    public Producto obtener(int id) {
        return productoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Producto", id));
    }

    @Transactional
    public Producto registrar(ProductoFormulario formulario) {
        String codigo = normalizarCodigo(formulario.getCodigo());
        validarCodigoUnico(codigo, null);
        Producto producto = FabricaProducto.crear(formulario.getCategoria());
        aplicarDatos(producto, formulario, codigo);
        producto.setActivo(true);
        producto.setFechaRegistro(LocalDateTime.now());
        if (producto instanceof Menu menu) {
            asignarComponentes(menu, formulario);
            menu.setExistencia(0);
        } else {
            producto.setExistencia(formulario.getExistencia() == null ? 0 : formulario.getExistencia());
        }
        producto.setId(productoRepositorio.guardar(producto));
        if (producto instanceof Menu menu) {
            productoRepositorio.guardarComponentes(menu);
        }
        inventarioServicio.registrarEntradaInicial(producto);
        return producto;
    }

    @Transactional
    public Producto actualizar(int id, ProductoFormulario formulario) {
        Producto producto = obtener(id);
        String codigo = normalizarCodigo(formulario.getCodigo());
        validarCodigoUnico(codigo, id);
        aplicarDatos(producto, formulario, codigo);
        productoRepositorio.actualizar(producto);
        if (producto instanceof Menu menu) {
            asignarComponentes(menu, formulario);
            productoRepositorio.guardarComponentes(menu);
        }
        return producto;
    }

    @Transactional
    public void cambiarEstado(int id, boolean activo) {
        obtener(id);
        productoRepositorio.cambiarEstado(id, activo);
    }

    public ProductoFormulario aFormulario(Producto producto) {
        ProductoFormulario formulario = new ProductoFormulario();
        formulario.setId(producto.getId());
        formulario.setCodigo(producto.getCodigo());
        formulario.setNombre(producto.getNombre());
        formulario.setCategoria(producto.getCategoria());
        formulario.setPrecio(producto.getPrecio());
        formulario.setExistencia(producto.getExistencia());
        if (producto instanceof Menu menu && menu.tieneComponentesCompletos()) {
            formulario.setSandwichId(menu.getSandwich().getId());
            formulario.setBebidaId(menu.getBebida().getId());
            formulario.setAcompanamientoId(menu.getAcompanamiento().getId());
        }
        return formulario;
    }

    private void asignarComponentes(Menu menu, ProductoFormulario formulario) {
        if (formulario.getSandwichId() == null || formulario.getBebidaId() == null
                || formulario.getAcompanamientoId() == null) {
            throw new ValidacionExcepcion("Un menú debe incluir un sándwich, una bebida y un acompañamiento");
        }
        menu.setSandwich(componente(formulario.getSandwichId(), Sandwich.class, "sándwich"));
        menu.setBebida(componente(formulario.getBebidaId(), Bebida.class, "bebida"));
        menu.setAcompanamiento(componente(formulario.getAcompanamientoId(), Acompanamiento.class, "acompañamiento"));
    }

    private <T extends Producto> T componente(int id, Class<T> tipo, String descripcion) {
        Producto producto = obtener(id);
        if (!tipo.isInstance(producto)) {
            throw new ValidacionExcepcion("El producto " + producto.getNombre() + " no es un " + descripcion);
        }
        return tipo.cast(producto);
    }

    private void validarCodigoUnico(String codigo, Integer excluirId) {
        if (productoRepositorio.existeCodigo(codigo, excluirId)) {
            throw new DuplicadoExcepcion("código", codigo);
        }
    }

    private void aplicarDatos(Producto producto, ProductoFormulario formulario, String codigo) {
        producto.setCodigo(codigo);
        producto.setNombre(formulario.getNombre().trim());
        producto.setPrecio(formulario.getPrecio());
    }

    private static String normalizarCodigo(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase();
    }
}
