package com.panpuntos.modelo;

import java.util.Objects;

public abstract class EntidadBase {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean esNuevo() {
        return id == null;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        EntidadBase entidad = (EntidadBase) otro;
        return id != null && id.equals(entidad.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
