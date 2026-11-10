package com.panpuntos.modelo.producto;

import com.panpuntos.excepcion.ValidacionExcepcion;
import com.panpuntos.modelo.EntidadBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class Producto extends EntidadBase {

    private String codigo;
    private String nombre;
    private BigDecimal precio = BigDecimal.ZERO;
    private int existencia;
    private boolean activo = true;
    private LocalDateTime fechaRegistro;

    public abstract CategoriaProducto getCategoria();

    public int calcularPuntos(int cantidad) {
        return 0;
    }

    public Map<Integer, Integer> unidadesRequeridas(int cantidad) {
        return Map.of(getId(), cantidad);
    }

    public int unidadesDisponibles() {
        return existencia;
    }

    public boolean esVendible() {
        return activo;
    }

    public boolean estaDisponible(int cantidad) {
        return esVendible() && unidadesDisponibles() >= cantidad;
    }

    public void aumentarExistencia(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionExcepcion("La cantidad a ingresar debe ser mayor a cero");
        }
        this.existencia += cantidad;
    }

    public String getDescripcionCategoria() {
        return getCategoria().getDescripcion();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0) {
            throw new ValidacionExcepcion("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        if (existencia < 0) {
            throw new ValidacionExcepcion("La existencia no puede ser negativa");
        }
        this.existencia = existencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
