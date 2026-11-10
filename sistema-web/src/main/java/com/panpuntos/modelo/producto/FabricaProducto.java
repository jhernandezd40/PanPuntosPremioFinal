package com.panpuntos.modelo.producto;

public final class FabricaProducto {

    private FabricaProducto() {
    }

    public static Producto crear(CategoriaProducto categoria) {
        return switch (categoria) {
            case SANDWICH -> new Sandwich();
            case BEBIDA -> new Bebida();
            case ACOMPANAMIENTO -> new Acompanamiento();
            case MENU -> new Menu();
        };
    }
}
