package com.panpuntos.controlador.api;

import com.panpuntos.dto.SaldoPuntosRespuesta;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.servicio.ClienteServicio;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin
public class ClienteApiControlador {

    private final ClienteServicio clienteServicio;

    public ClienteApiControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    @GetMapping("/{identificador}/puntos")
    public SaldoPuntosRespuesta consultarPuntos(@PathVariable String identificador) {
        Cliente cliente = clienteServicio.obtenerPorIdentificador(identificador);
        return new SaldoPuntosRespuesta(cliente.getIdentificador(), cliente.getNombre(), cliente.getPuntos(),
                cliente.isActivo());
    }
}
