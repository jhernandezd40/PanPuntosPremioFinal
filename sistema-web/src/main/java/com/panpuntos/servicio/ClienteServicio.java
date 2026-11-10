package com.panpuntos.servicio;

import com.panpuntos.dto.ClienteFormulario;
import com.panpuntos.excepcion.DuplicadoExcepcion;
import com.panpuntos.excepcion.RecursoNoEncontradoExcepcion;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.fidelizacion.Canje;
import com.panpuntos.modelo.fidelizacion.MovimientoPuntos;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.repositorio.CanjeRepositorio;
import com.panpuntos.repositorio.ClienteRepositorio;
import com.panpuntos.repositorio.MovimientoPuntosRepositorio;
import com.panpuntos.repositorio.PedidoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final MovimientoPuntosRepositorio movimientoPuntosRepositorio;
    private final CanjeRepositorio canjeRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio, PedidoRepositorio pedidoRepositorio,
                           MovimientoPuntosRepositorio movimientoPuntosRepositorio, CanjeRepositorio canjeRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.movimientoPuntosRepositorio = movimientoPuntosRepositorio;
        this.canjeRepositorio = canjeRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscar(String texto) {
        return clienteRepositorio.buscar(texto);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepositorio.listarActivos();
    }

    @Transactional(readOnly = true)
    public Cliente obtener(int id) {
        return clienteRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cliente", id));
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorIdentificador(String identificador) {
        String normalizado = normalizarIdentificador(identificador);
        return clienteRepositorio.buscarPorIdentificador(normalizado)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cliente", normalizado));
    }

    @Transactional(readOnly = true)
    public int contarActivos() {
        return clienteRepositorio.contarActivos();
    }

    @Transactional
    public Cliente registrar(ClienteFormulario formulario) {
        String identificador = normalizarIdentificador(formulario.getIdentificador());
        validarIdentificadorUnico(identificador, null);
        Cliente cliente = new Cliente();
        aplicarDatos(cliente, formulario, identificador);
        cliente.setPuntos(0);
        cliente.setActivo(true);
        cliente.setFechaRegistro(LocalDateTime.now());
        cliente.setId(clienteRepositorio.guardar(cliente));
        return cliente;
    }

    @Transactional
    public Cliente actualizar(int id, ClienteFormulario formulario) {
        Cliente cliente = obtener(id);
        String identificador = normalizarIdentificador(formulario.getIdentificador());
        validarIdentificadorUnico(identificador, id);
        aplicarDatos(cliente, formulario, identificador);
        clienteRepositorio.actualizar(cliente);
        return cliente;
    }

    @Transactional
    public void cambiarEstado(int id, boolean activo) {
        obtener(id);
        clienteRepositorio.cambiarEstado(id, activo);
    }

    @Transactional
    public boolean eliminarODesactivar(int id) {
        obtener(id);
        if (clienteRepositorio.tieneHistorial(id)) {
            clienteRepositorio.cambiarEstado(id, false);
            return false;
        }
        clienteRepositorio.eliminar(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Pedido> historialPedidos(int clienteId) {
        return pedidoRepositorio.listarPorCliente(clienteId);
    }

    @Transactional(readOnly = true)
    public List<MovimientoPuntos> historialPuntos(int clienteId) {
        return movimientoPuntosRepositorio.listarPorCliente(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Canje> historialCanjes(int clienteId) {
        return canjeRepositorio.listarPorCliente(clienteId);
    }

    public ClienteFormulario aFormulario(Cliente cliente) {
        ClienteFormulario formulario = new ClienteFormulario();
        formulario.setId(cliente.getId());
        formulario.setIdentificador(cliente.getIdentificador());
        formulario.setNombre(cliente.getNombre());
        formulario.setTelefono(cliente.getTelefono());
        formulario.setCorreo(cliente.getCorreo());
        return formulario;
    }

    private void validarIdentificadorUnico(String identificador, Integer excluirId) {
        if (clienteRepositorio.existeIdentificador(identificador, excluirId)) {
            throw new DuplicadoExcepcion("identificador", identificador);
        }
    }

    private void aplicarDatos(Cliente cliente, ClienteFormulario formulario, String identificador) {
        cliente.setIdentificador(identificador);
        cliente.setNombre(formulario.getNombre().trim());
        cliente.setTelefono(textoOpcional(formulario.getTelefono()));
        cliente.setCorreo(textoOpcional(formulario.getCorreo()));
    }

    private static String normalizarIdentificador(String identificador) {
        return identificador == null ? "" : identificador.trim().toUpperCase();
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
