package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.Cliente;
import com.panpuntos.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepositorioJdbc extends RepositorioJdbcBase implements ClienteRepositorio {

    private static final String COLUMNAS =
            "SELECT id, identificador, nombre, telefono, correo, puntos, activo, fecha_registro FROM cliente";

    public ClienteRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public List<Cliente> buscar(String texto) {
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        return consultarLista(COLUMNAS + " WHERE identificador LIKE ? OR nombre LIKE ? ORDER BY nombre",
                this::mapear, patron, patron);
    }

    @Override
    public List<Cliente> listarActivos() {
        return consultarLista(COLUMNAS + " WHERE activo = TRUE ORDER BY nombre", this::mapear);
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return consultarUno(COLUMNAS + " WHERE id = ?", this::mapear, id);
    }

    @Override
    public Optional<Cliente> buscarPorIdParaActualizar(int id) {
        return consultarUno(COLUMNAS + " WHERE id = ? FOR UPDATE", this::mapear, id);
    }

    @Override
    public Optional<Cliente> buscarPorIdentificador(String identificador) {
        return consultarUno(COLUMNAS + " WHERE identificador = ?", this::mapear, identificador);
    }

    @Override
    public boolean existeIdentificador(String identificador, Integer excluirId) {
        return existe("SELECT COUNT(*) FROM cliente WHERE identificador = ? AND id <> ?",
                identificador, excluirId == null ? 0 : excluirId);
    }

    @Override
    public boolean tieneHistorial(int id) {
        return existe("SELECT (SELECT COUNT(*) FROM pedido WHERE cliente_id = ?) "
                + "+ (SELECT COUNT(*) FROM canje WHERE cliente_id = ?) "
                + "+ (SELECT COUNT(*) FROM movimiento_puntos WHERE cliente_id = ?)", id, id, id);
    }

    @Override
    public int contarActivos() {
        return consultarEntero("SELECT COUNT(*) FROM cliente WHERE activo = TRUE");
    }

    @Override
    public int guardar(Cliente cliente) {
        return insertar("INSERT INTO cliente (identificador, nombre, telefono, correo, puntos, activo, fecha_registro) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                cliente.getIdentificador(), cliente.getNombre(), cliente.getTelefono(), cliente.getCorreo(),
                cliente.getPuntos(), cliente.isActivo(), cliente.getFechaRegistro());
    }

    @Override
    public void actualizar(Cliente cliente) {
        actualizar("UPDATE cliente SET identificador = ?, nombre = ?, telefono = ?, correo = ? WHERE id = ?",
                cliente.getIdentificador(), cliente.getNombre(), cliente.getTelefono(), cliente.getCorreo(),
                cliente.getId());
    }

    @Override
    public void actualizarPuntos(int id, int puntos) {
        actualizar("UPDATE cliente SET puntos = ? WHERE id = ?", puntos, id);
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        actualizar("UPDATE cliente SET activo = ? WHERE id = ?", activo, id);
    }

    @Override
    public void eliminar(int id) {
        actualizar("DELETE FROM cliente WHERE id = ?", id);
    }

    private Cliente mapear(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(resultado.getInt("id"));
        cliente.setIdentificador(resultado.getString("identificador"));
        cliente.setNombre(resultado.getString("nombre"));
        cliente.setTelefono(resultado.getString("telefono"));
        cliente.setCorreo(resultado.getString("correo"));
        cliente.setPuntos(resultado.getInt("puntos"));
        cliente.setActivo(resultado.getBoolean("activo"));
        cliente.setFechaRegistro(leerFecha(resultado, "fecha_registro"));
        return cliente;
    }
}
