package com.panpuntos.servicio;

import com.panpuntos.dto.AbastecimientoFormulario;
import com.panpuntos.excepcion.InventarioInsuficienteExcepcion;
import com.panpuntos.excepcion.InventarioInsuficienteExcepcion.Faltante;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.inventario.MovimientoInventario;
import com.panpuntos.modelo.inventario.TipoMovimientoInventario;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.repositorio.MovimientoInventarioRepositorio;
import com.panpuntos.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventarioServicio {

    private static final int LIMITE_HISTORIAL = 200;

    private final ProductoRepositorio productoRepositorio;
    private final MovimientoInventarioRepositorio movimientoRepositorio;

    public InventarioServicio(ProductoRepositorio productoRepositorio,
                              MovimientoInventarioRepositorio movimientoRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.movimientoRepositorio = movimientoRepositorio;
    }

    @Transactional
    public MovimientoInventario abastecer(AbastecimientoFormulario formulario) {
        int cantidad = formulario.getCantidad() == null ? 0 : formulario.getCantidad();
        if (cantidad <= 0) {
            throw new ValidacionExcepcion("La cantidad a ingresar debe ser mayor a cero");
        }
        Producto producto = productoRepositorio.bloquearPorIds(List.of(formulario.getProductoId()))
                .get(formulario.getProductoId());
        if (producto == null) {
            throw new RecursoNoEncontradoExcepcion("Producto", formulario.getProductoId());
        }
        if (!producto.getCategoria().manejaExistenciaPropia()) {
            throw new NegocioExcepcion("Los menús no manejan existencia propia; abastezca sus componentes");
        }
        producto.aumentarExistencia(cantidad);
        productoRepositorio.aumentarExistencia(producto.getId(), cantidad);
        String referencia = formulario.getReferencia() == null || formulario.getReferencia().isBlank()
                ? "Abastecimiento" : formulario.getReferencia().trim();
        MovimientoInventario movimiento = new MovimientoInventario(producto.getId(), TipoMovimientoInventario.ENTRADA,
                cantidad, producto.getExistencia(), referencia);
        movimientoRepositorio.registrar(movimiento);
        return movimiento;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void registrarEntradaInicial(Producto producto) {
        if (producto.getExistencia() > 0) {
            movimientoRepositorio.registrar(new MovimientoInventario(producto.getId(), TipoMovimientoInventario.ENTRADA,
                    producto.getExistencia(), producto.getExistencia(), "Inventario inicial"));
        }
    }

    @Transactional(readOnly = true)
    public void verificarDisponibilidad(Map<Integer, Integer> requeridas) {
        validar(requeridas, productoRepositorio.buscarPorIds(requeridas.keySet()));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void descontar(Map<Integer, Integer> requeridas, TipoMovimientoInventario tipo, String referencia) {
        if (!tipo.esSalida()) {
            throw new ValidacionExcepcion("El tipo de movimiento debe ser una salida de inventario");
        }
        Map<Integer, Producto> bloqueados = productoRepositorio.bloquearPorIds(requeridas.keySet());
        validar(requeridas, bloqueados);
        for (Map.Entry<Integer, Integer> requerido : requeridas.entrySet()) {
            Producto producto = bloqueados.get(requerido.getKey());
            int cantidad = requerido.getValue();
            if (!productoRepositorio.descontarExistencia(producto.getId(), cantidad)) {
                throw new InventarioInsuficienteExcepcion(List.of(
                        new Faltante(producto.getNombre(), cantidad, producto.getExistencia())));
            }
            int resultante = producto.getExistencia() - cantidad;
            producto.setExistencia(resultante);
            movimientoRepositorio.registrar(new MovimientoInventario(producto.getId(), tipo, cantidad, resultante,
                    referencia));
        }
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> historial(Integer productoId) {
        return movimientoRepositorio.listar(productoId, LIMITE_HISTORIAL);
    }

    private void validar(Map<Integer, Integer> requeridas, Map<Integer, Producto> productos) {
        List<Faltante> faltantes = new ArrayList<>();
        for (Map.Entry<Integer, Integer> requerido : requeridas.entrySet()) {
            Producto producto = productos.get(requerido.getKey());
            if (producto == null) {
                throw new RecursoNoEncontradoExcepcion("Producto", requerido.getKey());
            }
            if (!producto.isActivo()) {
                throw new NegocioExcepcion("El producto " + producto.getNombre() + " está desactivado y no puede venderse");
            }
            if (producto.getExistencia() < requerido.getValue()) {
                faltantes.add(new Faltante(producto.getNombre(), requerido.getValue(), producto.getExistencia()));
            }
        }
        if (!faltantes.isEmpty()) {
            throw new InventarioInsuficienteExcepcion(faltantes);
        }
    }
}
