package com.panpuntos.modelo.producto;

public enum CategoriaProducto {

    SANDWICH("Sándwich", "SAN"),
    BEBIDA("Bebida", "BEB"),
    ACOMPANAMIENTO("Acompañamiento", "ACO"),
    MENU("Menú completo", "MEN");

    private final String descripcion;
    private final String prefijo;

    CategoriaProducto(String descripcion, String prefijo) {
        this.descripcion = descripcion;
        this.prefijo = prefijo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public boolean manejaExistenciaPropia() {
        return this != MENU;
    }
}
