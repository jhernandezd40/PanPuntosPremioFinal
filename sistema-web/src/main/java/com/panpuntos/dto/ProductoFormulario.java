package com.panpuntos.dto;

import com.panpuntos.modelo.producto.CategoriaProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductoFormulario {

    private Integer id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código admite máximo 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "El código solo admite letras, números y guiones")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    private String nombre;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaProducto categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El precio admite máximo 2 decimales")
    private BigDecimal precio;

    @Min(value = 0, message = "La existencia no puede ser negativa")
    private Integer existencia = 0;

    private Integer sandwichId;
    private Integer bebidaId;
    private Integer acompanamientoId;

    public boolean esMenu() {
        return categoria == CategoriaProducto.MENU;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public Integer getSandwichId() {
        return sandwichId;
    }

    public void setSandwichId(Integer sandwichId) {
        this.sandwichId = sandwichId;
    }

    public Integer getBebidaId() {
        return bebidaId;
    }

    public void setBebidaId(Integer bebidaId) {
        this.bebidaId = bebidaId;
    }

    public Integer getAcompanamientoId() {
        return acompanamientoId;
    }

    public void setAcompanamientoId(Integer acompanamientoId) {
        this.acompanamientoId = acompanamientoId;
    }
}
