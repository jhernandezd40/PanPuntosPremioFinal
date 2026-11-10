package com.panpuntos.modelo.producto;

public class Bebida extends Producto {

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.BEBIDA;
    }
}
