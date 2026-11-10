package com.panpuntos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteFormulario {

    private Integer id;

    @NotBlank(message = "El identificador es obligatorio")
    @Size(max = 30, message = "El identificador admite máximo 30 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "El identificador solo admite letras, números y guiones")
    private String identificador;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    private String nombre;

    @Pattern(regexp = "^$|^[0-9+\\- ]{7,20}$", message = "El teléfono no es válido")
    private String telefono;

    @Email(message = "El correo no es válido")
    @Size(max = 100, message = "El correo admite máximo 100 caracteres")
    private String correo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
