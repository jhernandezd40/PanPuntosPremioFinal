package com.panpuntos.modelo.producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Menu extends Producto {

    public static final int PUNTOS_POR_UNIDAD = 8;

    private Sandwich sandwich;
    private Bebida bebida;
    private Acompanamiento acompanamiento;

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.MENU;
    }

    @Override
    public int calcularPuntos(int cantidad) {
        return cantidad * PUNTOS_POR_UNIDAD;
    }

    @Override
    public Map<Integer, Integer> unidadesRequeridas(int cantidad) {
        Map<Integer, Integer> requeridas = new LinkedHashMap<>();
        for (Producto componente : getComponentes()) {
            requeridas.merge(componente.getId(), cantidad, Integer::sum);
        }
        return requeridas;
    }

    @Override
    public int unidadesDisponibles() {
        return getComponentes().stream()
                .mapToInt(Producto::unidadesDisponibles)
                .min()
                .orElse(0);
    }

    @Override
    public boolean esVendible() {
        return isActivo()
                && tieneComponentesCompletos()
                && getComponentes().stream().allMatch(Producto::isActivo);
    }

    public boolean tieneComponentesCompletos() {
        return sandwich != null && bebida != null && acompanamiento != null;
    }

    public List<Producto> getComponentes() {
        List<Producto> componentes = new ArrayList<>();
        componentes.add(sandwich);
        componentes.add(bebida);
        componentes.add(acompanamiento);
        componentes.removeIf(Objects::isNull);
        return Collections.unmodifiableList(componentes);
    }

    public Sandwich getSandwich() {
        return sandwich;
    }

    public void setSandwich(Sandwich sandwich) {
        this.sandwich = sandwich;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public Acompanamiento getAcompanamiento() {
        return acompanamiento;
    }

    public void setAcompanamiento(Acompanamiento acompanamiento) {
        this.acompanamiento = acompanamiento;
    }
}
