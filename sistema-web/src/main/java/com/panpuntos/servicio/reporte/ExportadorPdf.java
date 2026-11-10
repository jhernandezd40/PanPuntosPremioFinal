package com.panpuntos.servicio.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.panpuntos.excepcion.NegocioExcepcion;
import com.panpuntos.modelo.pago.Pago;
import com.panpuntos.modelo.pedido.DetallePedido;
import com.panpuntos.modelo.pedido.Pedido;
import com.panpuntos.util.Formato;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ExportadorPdf {

    private static final String NOMBRE_NEGOCIO = "Pan, Puntos y Premios";
    private static final Color COLOR_PRIMARIO = new Color(156, 82, 23);
    private static final Color COLOR_FONDO = new Color(250, 240, 228);

    private final Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_PRIMARIO);
    private final Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private final Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private final Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    private final Font fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

    public byte[] exportar(TablaReporte tabla) {
        boolean horizontal = tabla.getColumnas().size() > 5;
        return generar(horizontal, documento -> {
            agregarEncabezado(documento, tabla.getTitulo(), tabla.getSubtitulo());
            documento.add(new Paragraph("Generado: " + Formato.fechaHora(tabla.getGenerado()), fuenteSubtitulo));
            documento.add(new Paragraph(" "));
            if (tabla.estaVacia()) {
                documento.add(new Paragraph("No hay datos para los criterios seleccionados.", fuenteCelda));
            } else {
                PdfPTable tablaPdf = new PdfPTable(tabla.getColumnas().size());
                tablaPdf.setWidthPercentage(100);
                tablaPdf.setHeaderRows(1);
                tabla.getColumnas().forEach(columna -> tablaPdf.addCell(celdaEncabezado(columna)));
                boolean alterna = false;
                for (var fila : tabla.getFilas()) {
                    for (String valor : fila) {
                        tablaPdf.addCell(celda(valor, alterna));
                    }
                    alterna = !alterna;
                }
                documento.add(tablaPdf);
            }
            agregarResumen(documento, tabla.getResumen());
        });
    }

    public byte[] comprobante(Pedido pedido, Pago pago) {
        return generar(false, documento -> {
            agregarEncabezado(documento, "Comprobante de pago", "Pedido #" + pedido.getId());
            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            agregarDato(datos, "Cliente", pedido.getNombreCliente());
            if (pedido.tieneCliente()) {
                agregarDato(datos, "Identificador", pedido.getCliente().getIdentificador());
            }
            agregarDato(datos, "Fecha de pago", Formato.fechaHora(pago.getFecha()));
            agregarDato(datos, "Método de pago", pago.getDescripcionMetodo());
            agregarDato(datos, "Detalle", pago.getDetalle());
            agregarDato(datos, "Estado", pedido.getEstado().getDescripcion());
            documento.add(datos);
            documento.add(new Paragraph(" "));
            PdfPTable detalle = new PdfPTable(new float[]{4, 1, 2, 2});
            detalle.setWidthPercentage(100);
            for (String columna : new String[]{"Producto", "Cant.", "Precio", "Subtotal"}) {
                detalle.addCell(celdaEncabezado(columna));
            }
            for (DetallePedido linea : pedido.getDetalles()) {
                detalle.addCell(celda(linea.getProducto().getNombre(), false));
                detalle.addCell(celda(String.valueOf(linea.getCantidad()), false));
                detalle.addCell(celda(Formato.moneda(linea.getPrecioUnitario()), false));
                detalle.addCell(celda(Formato.moneda(linea.getSubtotal()), false));
            }
            documento.add(detalle);
            Map<String, String> totales = new LinkedHashMap<>();
            totales.put("Total pagado", Formato.moneda(pago.getMonto()));
            totales.put("Puntos acreditados", String.valueOf(pedido.getPuntosOtorgados()));
            agregarResumen(documento, totales);
            Paragraph gracias = new Paragraph("¡Gracias por su compra!", fuenteSubtitulo);
            gracias.setAlignment(Element.ALIGN_CENTER);
            gracias.setSpacingBefore(20);
            documento.add(gracias);
        });
    }

    private byte[] generar(boolean horizontal, ContenidoPdf contenido) {
        Rectangle tamano = horizontal ? PageSize.LETTER.rotate() : PageSize.LETTER;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(tamano, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            contenido.escribir(documento);
        } catch (DocumentException e) {
            throw new NegocioExcepcion("No fue posible generar el documento PDF", e);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
        return salida.toByteArray();
    }

    private void agregarEncabezado(Document documento, String titulo, String subtitulo) throws DocumentException {
        Paragraph negocio = new Paragraph(NOMBRE_NEGOCIO, fuenteNegrita);
        documento.add(negocio);
        Paragraph parrafoTitulo = new Paragraph(titulo, fuenteTitulo);
        parrafoTitulo.setSpacingBefore(4);
        documento.add(parrafoTitulo);
        if (subtitulo != null && !subtitulo.isBlank()) {
            documento.add(new Paragraph(subtitulo, fuenteSubtitulo));
        }
    }

    private void agregarResumen(Document documento, Map<String, String> resumen) throws DocumentException {
        if (resumen.isEmpty()) {
            return;
        }
        PdfPTable tablaResumen = new PdfPTable(2);
        tablaResumen.setWidthPercentage(50);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tablaResumen.setSpacingBefore(12);
        resumen.forEach((etiqueta, valor) -> {
            PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteNegrita));
            celdaEtiqueta.setBackgroundColor(COLOR_FONDO);
            celdaEtiqueta.setPadding(5);
            tablaResumen.addCell(celdaEtiqueta);
            PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteCelda));
            celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaValor.setPadding(5);
            tablaResumen.addCell(celdaValor);
        });
        documento.add(tablaResumen);
    }

    private void agregarDato(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteNegrita));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPadding(3);
        tabla.addCell(celdaEtiqueta);
        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteCelda));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPadding(3);
        tabla.addCell(celdaValor);
    }

    private PdfPCell celdaEncabezado(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteEncabezado));
        celda.setBackgroundColor(COLOR_PRIMARIO);
        celda.setPadding(5);
        return celda;
    }

    private PdfPCell celda(String texto, boolean alterna) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteCelda));
        celda.setPadding(4);
        if (alterna) {
            celda.setBackgroundColor(COLOR_FONDO);
        }
        return celda;
    }

    @FunctionalInterface
    private interface ContenidoPdf {

        void escribir(Document documento) throws DocumentException;
    }
}
