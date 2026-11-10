package com.panpuntos.modelo.producto;

public class Acompanamiento extends Producto {

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.ACOMPANAMIENTO;
    }
}
