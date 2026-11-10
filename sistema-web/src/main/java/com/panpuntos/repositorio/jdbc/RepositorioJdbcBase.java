package com.panpuntos.repositorio.jdbc;

import com.panpuntos.excepcion.AccesoDatosExcepcion;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class RepositorioJdbcBase {

    private final DataSource fuenteDatos;

    protected RepositorioJdbcBase(DataSource fuenteDatos) {
        this.fuenteDatos = fuenteDatos;
    }

    protected <T> List<T> consultarLista(String sql, MapeadorFila<T> mapeador, Object... parametros) {
        Connection conexion = obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, parametros);
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<T> elementos = new ArrayList<>();
                while (resultado.next()) {
                    elementos.add(mapeador.mapear(resultado));
                }
                return elementos;
            }
        } catch (SQLException e) {
            throw new AccesoDatosExcepcion("Error al consultar datos", e);
        } finally {
            liberarConexion(conexion);
        }
    }

    protected <T> Optional<T> consultarUno(String sql, MapeadorFila<T> mapeador, Object... parametros) {
        List<T> elementos = consultarLista(sql, mapeador, parametros);
        return elementos.isEmpty() ? Optional.empty() : Optional.of(elementos.get(0));
    }

    protected int consultarEntero(String sql, Object... parametros) {
        return consultarUno(sql, resultado -> resultado.getInt(1), parametros).orElse(0);
    }

    protected boolean existe(String sql, Object... parametros) {
        return consultarEntero(sql, parametros) > 0;
    }

    protected int actualizar(String sql, Object... parametros) {
        Connection conexion = obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, parametros);
            return sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosExcepcion("Error al actualizar datos", e);
        } finally {
            liberarConexion(conexion);
        }
    }

    protected int insertar(String sql, Object... parametros) {
        Connection conexion = obtenerConexion();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(sentencia, parametros);
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    return llaves.getInt(1);
                }
                throw new AccesoDatosExcepcion("No se obtuvo el identificador generado", null);
            }
        } catch (SQLException e) {
            throw new AccesoDatosExcepcion("Error al insertar datos", e);
        } finally {
            liberarConexion(conexion);
        }
    }

    protected static String marcadores(Collection<?> valores) {
        return valores.stream().map(valor -> "?").collect(Collectors.joining(", "));
    }

    protected static LocalDateTime leerFecha(ResultSet resultado, String columna) throws SQLException {
        return resultado.getObject(columna, LocalDateTime.class);
    }

    protected static Integer leerEnteroNulo(ResultSet resultado, String columna) throws SQLException {
        int valor = resultado.getInt(columna);
        return resultado.wasNull() ? null : valor;
    }

    private void asignarParametros(PreparedStatement sentencia, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            int posicion = i + 1;
            Object parametro = parametros[i];
            if (parametro == null) {
                sentencia.setNull(posicion, Types.NULL);
            } else if (parametro instanceof Enum<?> enumeracion) {
                sentencia.setString(posicion, enumeracion.name());
            } else if (parametro instanceof String texto) {
                sentencia.setString(posicion, texto);
            } else if (parametro instanceof Integer entero) {
                sentencia.setInt(posicion, entero);
            } else if (parametro instanceof Boolean logico) {
                sentencia.setBoolean(posicion, logico);
            } else if (parametro instanceof BigDecimal decimal) {
                sentencia.setBigDecimal(posicion, decimal);
            } else if (parametro instanceof LocalDateTime fechaHora) {
                sentencia.setObject(posicion, fechaHora);
            } else if (parametro instanceof LocalDate fecha) {
                sentencia.setObject(posicion, fecha);
            } else {
                sentencia.setObject(posicion, parametro);
            }
        }
    }

    private Connection obtenerConexion() {
        return DataSourceUtils.getConnection(fuenteDatos);
    }

    private void liberarConexion(Connection conexion) {
        DataSourceUtils.releaseConnection(conexion, fuenteDatos);
    }
}
