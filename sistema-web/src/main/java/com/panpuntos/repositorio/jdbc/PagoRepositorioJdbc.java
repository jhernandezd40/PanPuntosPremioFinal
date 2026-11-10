package com.panpuntos.repositorio.jdbc;

import com.panpuntos.modelo.pago.MetodoPago;
import com.panpuntos.modelo.pago.Pago;
import com.panpuntos.modelo.pago.PagoEfectivo;
import com.panpuntos.modelo.pago.PagoTarjeta;
import com.panpuntos.repositorio.PagoRepositorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class PagoRepositorioJdbc extends RepositorioJdbcBase implements PagoRepositorio {

    public PagoRepositorioJdbc(DataSource fuenteDatos) {
        super(fuenteDatos);
    }

    @Override
    public int guardar(Pago pago) {
        BigDecimal recibido = null;
        BigDecimal cambio = null;
        String referencia = null;
        String ultimosDigitos = null;
        String titular = null;
        if (pago instanceof PagoEfectivo efectivo) {
            recibido = efectivo.getMontoRecibido();
            cambio = efectivo.getCambio();
        } else if (pago instanceof PagoTarjeta tarjeta) {
            referencia = tarjeta.getReferencia();
            ultimosDigitos = tarjeta.getUltimosDigitos();
            titular = tarjeta.getTitular();
        }
        int id = insertar("INSERT INTO pago (pedido_id, metodo, monto, monto_recibido, cambio, referencia, "
                        + "ultimos_digitos, titular, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                pago.getPedidoId(), pago.getMetodo(), pago.getMonto(), recibido, cambio, referencia, ultimosDigitos,
                titular, pago.getFecha());
        pago.setId(id);
        return id;
    }

    @Override
    public Optional<Pago> buscarPorPedido(int pedidoId) {
        return consultarUno("SELECT id, pedido_id, metodo, monto, monto_recibido, cambio, referencia, ultimos_digitos, "
                + "titular, fecha FROM pago WHERE pedido_id = ?", this::mapear, pedidoId);
    }

    @Override
    public BigDecimal totalDelDia(LocalDate fecha) {
        return consultarUno("SELECT COALESCE(SUM(monto), 0) FROM pago WHERE fecha >= ? AND fecha < ?",
                resultado -> resultado.getBigDecimal(1), fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO);
    }

    private Pago mapear(ResultSet resultado) throws SQLException {
        MetodoPago metodo = MetodoPago.valueOf(resultado.getString("metodo"));
        Pago pago;
        if (metodo == MetodoPago.EFECTIVO) {
            PagoEfectivo efectivo = new PagoEfectivo(resultado.getBigDecimal("monto_recibido"));
            efectivo.setCambio(resultado.getBigDecimal("cambio"));
            pago = efectivo;
        } else {
            PagoTarjeta tarjeta = new PagoTarjeta();
            tarjeta.setReferencia(resultado.getString("referencia"));
            tarjeta.setUltimosDigitos(resultado.getString("ultimos_digitos"));
            tarjeta.setTitular(resultado.getString("titular"));
            pago = tarjeta;
        }
        pago.setId(resultado.getInt("id"));
        pago.setPedidoId(resultado.getInt("pedido_id"));
        pago.setMonto(resultado.getBigDecimal("monto"));
        pago.setFecha(leerFecha(resultado, "fecha"));
        return pago;
    }
}
