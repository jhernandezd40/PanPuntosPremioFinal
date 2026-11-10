package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.producto.Acompanamiento;
import com.panpuntos.modelo.producto.Bebida;
import com.panpuntos.modelo.producto.CategoriaProducto;
import com.panpuntos.modelo.producto.FabricaProducto;
import com.panpuntos.modelo.producto.Menu;
import com.panpuntos.modelo.producto.Producto;
import com.panpuntos.modelo.producto.Sandwich;
import com.panpuntos.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ProductoRepositorioJdbc extends RepositorioJdbcBase implements ProductoRepositorio {

    private static final String COLUMNAS =
            "SELECT id, codigo, nombre, categoria, precio, existencia, activo, fecha_registro FROM producto";

    public ProductoRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public List<Producto> buscar(String texto, CategoriaProducto categoria, boolean soloActivos) {
        StringBuilder sql = new StringBuilder(COLUMNAS).append(" WHERE (codigo LIKE ? OR nombre LIKE ?)");
        List<Object> parametros = new ArrayList<>();
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        parametros.add(patron);
        parametros.add(patron);
        if (categoria != null) {
            sql.append(" AND categoria = ?");
            parametros.add(categoria);
        }
        if (soloActivos) {
            sql.append(" AND activo = TRUE");
        }
        sql.append(" ORDER BY FIELD(categoria, 'MENU', 'SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO'), codigo");
        List<Producto> productos = consultarLista(sql.toString(), this::mapear, parametros.toArray());
        cargarComponentes(productos);
        return productos;
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        Optional<Producto> producto = consultarUno(COLUMNAS + " WHERE id = ?", this::mapear, id);
        producto.ifPresent(encontrado -> cargarComponentes(List.of(encontrado)));
        return producto;
    }

    @Override
    public Map<Integer, Producto> buscarPorIds(Collection<Integer> ids) {
        return consultarPorIds(ids, "");
    }

    @Override
    public Map<Integer, Producto> bloquearPorIds(Collection<Integer> ids) {
        return consultarPorIds(ids, " ORDER BY id FOR UPDATE");
    }

    @Override
    public boolean existeCodigo(String codigo, Integer excluirId) {
        return existe("SELECT COUNT(*) FROM producto WHERE codigo = ? AND id <> ?",
                codigo, excluirId == null ? 0 : excluirId);
    }

    @Override
    public int guardar(Producto producto) {
        return insertar("INSERT INTO producto (codigo, nombre, categoria, precio, existencia, activo, fecha_registro) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                producto.getCodigo(), producto.getNombre(), producto.getCategoria(), producto.getPrecio(),
                producto.getExistencia(), producto.isActivo(), producto.getFechaRegistro());
    }

    @Override
    public void actualizar(Producto producto) {
        actualizar("UPDATE producto SET codigo = ?, nombre = ?, precio = ? WHERE id = ?",
                producto.getCodigo(), producto.getNombre(), producto.getPrecio(), producto.getId());
    }

    @Override
    public void guardarComponentes(Menu menu) {
        actualizar("INSERT INTO menu_componente (menu_id, sandwich_id, bebida_id, acompanamiento_id) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE sandwich_id = VALUES(sandwich_id), bebida_id = VALUES(bebida_id), "
                        + "acompanamiento_id = VALUES(acompanamiento_id)",
                menu.getId(), menu.getSandwich().getId(), menu.getBebida().getId(), menu.getAcompanamiento().getId());
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        actualizar("UPDATE producto SET activo = ? WHERE id = ?", activo, id);
    }

    @Override
    public boolean descontarExistencia(int id, int cantidad) {
        return actualizar("UPDATE producto SET existencia = existencia - ? WHERE id = ? AND existencia >= ?",
                cantidad, id, cantidad) == 1;
    }

    @Override
    public void aumentarExistencia(int id, int cantidad) {
        actualizar("UPDATE producto SET existencia = existencia + ? WHERE id = ?", cantidad, id);
    }

    private Map<Integer, Producto> consultarPorIds(Collection<Integer> ids, String sufijo) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        String sql = COLUMNAS + " WHERE id IN (" + marcadores(ids) + ")" + sufijo;
        List<Producto> productos = consultarLista(sql, this::mapear, ids.toArray());
        cargarComponentes(productos);
        return productos.stream().collect(Collectors.toMap(Producto::getId, Function.identity(),
                (primero, segundo) -> primero, LinkedHashMap::new));
    }

    private void cargarComponentes(List<Producto> productos) {
        List<Menu> menus = productos.stream()
                .filter(Menu.class::isInstance)
                .map(Menu.class::cast)
                .toList();
        if (menus.isEmpty()) {
            return;
        }
        List<Integer> idsMenus = menus.stream().map(Producto::getId).toList();
        List<RelacionMenu> relaciones = consultarLista(
                "SELECT menu_id, sandwich_id, bebida_id, acompanamiento_id FROM menu_componente WHERE menu_id IN ("
                        + marcadores(idsMenus) + ")",
                resultado -> new RelacionMenu(resultado.getInt("menu_id"), resultado.getInt("sandwich_id"),
                        resultado.getInt("bebida_id"), resultado.getInt("acompanamiento_id")),
                idsMenus.toArray());
        List<Integer> idsComponentes = relaciones.stream()
                .flatMap(RelacionMenu::componentes)
                .distinct()
                .toList();
        if (idsComponentes.isEmpty()) {
            return;
        }
        Map<Integer, Producto> componentes = consultarLista(
                COLUMNAS + " WHERE id IN (" + marcadores(idsComponentes) + ")", this::mapear, idsComponentes.toArray())
                .stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));
        Map<Integer, Menu> menusPorId = menus.stream().collect(Collectors.toMap(Producto::getId, Function.identity()));
        for (RelacionMenu relacion : relaciones) {
            Menu menu = menusPorId.get(relacion.menuId());
            menu.setSandwich((Sandwich) componentes.get(relacion.sandwichId()));
            menu.setBebida((Bebida) componentes.get(relacion.bebidaId()));
            menu.setAcompanamiento((Acompanamiento) componentes.get(relacion.acompanamientoId()));
        }
    }

    private Producto mapear(ResultSet resultado) throws SQLException {
        Producto producto = FabricaProducto.crear(CategoriaProducto.valueOf(resultado.getString("categoria")));
        producto.setId(resultado.getInt("id"));
        producto.setCodigo(resultado.getString("codigo"));
        producto.setNombre(resultado.getString("nombre"));
        producto.setPrecio(resultado.getBigDecimal("precio"));
        producto.setExistencia(resultado.getInt("existencia"));
        producto.setActivo(resultado.getBoolean("activo"));
        producto.setFechaRegistro(leerFecha(resultado, "fecha_registro"));
        return producto;
    }

    private record RelacionMenu(int menuId, int sandwichId, int bebidaId, int acompanamientoId) {

        Stream<Integer> componentes() {
            return Stream.of(sandwichId, bebidaId, acompanamientoId);
        }
    }
}
