package com.panpuntos.modelo.reporte;

import com.panpuntos.modelo.producto.CategoriaProducto;

public record ExistenciaProducto(String codigo, String nombre, CategoriaProducto categoria,
                                 int existencia, boolean activo) {

    public boolean agotado() {
        return existencia == 0;
    }
}
