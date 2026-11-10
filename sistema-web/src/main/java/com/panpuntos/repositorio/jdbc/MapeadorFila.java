package com.panpuntos.repositorio.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MapeadorFila<T> {

    T mapear(ResultSet resultado) throws SQLException;
}
