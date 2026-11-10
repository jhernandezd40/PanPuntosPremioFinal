package com.panpuntos.controlador.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.WebSession;

@ControllerAdvice(basePackageClasses = ModeloGlobalWeb.class)
public class ModeloGlobalWeb {

    @ModelAttribute
    public void cargarMensajesFlash(WebSession sesion, Model modelo) {
        modelo.addAllAttributes(MensajesFlash.consumir(sesion));
    }
}
