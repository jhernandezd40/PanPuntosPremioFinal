package com.panpuntos.modelo.reporte;

import com.panpuntos.modelo.producto.CategoriaProducto;

public record ProductoVendido(String codigo, String nombre, CategoriaProducto categoria,
                              int ventaDirecta, int comoComponente) {

    public int total() {
        return ventaDirecta + comoComponente;
    }
}
