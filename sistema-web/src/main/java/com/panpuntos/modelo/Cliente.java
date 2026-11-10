package com.panpuntos.modelo;

import com.panpuntos.excepcion.PuntosInsuficientesExcepcion;
import com.panpuntos.excepcion.ValidacionExcepcion;

import java.time.LocalDateTime;

public class Cliente extends EntidadBase {

    private String identificador;
    private String nombre;
    private String telefono;
    private String correo;
    private int puntos;
    private boolean activo = true;
    private LocalDateTime fechaRegistro;

    public void acreditarPuntos(int cantidad) {
        if (cantidad < 0) {
            throw new ValidacionExcepcion("No se pueden acreditar puntos negativos");
        }
        this.puntos += cantidad;
    }

    public void debitarPuntos(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionExcepcion("La cantidad de puntos a debitar debe ser positiva");
        }
        if (!tieneSaldo(cantidad)) {
            throw new PuntosInsuficientesExcepcion(nombre, puntos, cantidad);
        }
        this.puntos -= cantidad;
    }

    public boolean tieneSaldo(int cantidad) {
        return puntos >= cantidad;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        if (puntos < 0) {
            throw new ValidacionExcepcion("El saldo de puntos no puede ser negativo");
        }
        this.puntos = puntos;
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
