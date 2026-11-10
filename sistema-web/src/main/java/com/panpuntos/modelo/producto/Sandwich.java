package com.panpuntos.modelo.producto;

public class Sandwich extends Producto {

    public static final int PUNTOS_POR_UNIDAD = 2;

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.SANDWICH;
    }

    @Override
    public int calcularPuntos(int cantidad) {
        return cantidad * PUNTOS_POR_UNIDAD;
    }
}
