package com.panpuntos.modelo.fidelizacion;

import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.EntidadBase;
import com.panpuntos.modelo.producto.Producto;

public class Recompensa extends EntidadBase {

    private String nombre;
    private String descripcion;
    private int puntosRequeridos;
    private Producto producto;
    private boolean activa = true;

    public boolean estaDisponible() {
        return activa && producto != null && producto.estaDisponible(1);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public void setPuntosRequeridos(int puntosRequeridos) {
        if (puntosRequeridos <= 0) {
            throw new ValidacionExcepcion("Los puntos requeridos deben ser mayores a cero");
        }
        this.puntosRequeridos = puntosRequeridos;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
