package com.panpuntos.controlador.web;

import com.panpuntos.servicio.reporte.ExportadorPdf;
import com.panpuntos.servicio.reporte.FiltroReporte;
import com.panpuntos.servicio.reporte.GeneradorReporte;
import com.panpuntos.servicio.reporte.ReporteServicio;
import com.panpuntos.servicio.reporte.TablaReporte;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;
    private final ExportadorPdf exportadorPdf;

    public ReporteControlador(ReporteServicio reporteServicio, ExportadorPdf exportadorPdf) {
        this.reporteServicio = reporteServicio;
        this.exportadorPdf = exportadorPdf;
    }

    @GetMapping
    public String inicio(Model modelo) {
        modelo.addAttribute("reportes", reporteServicio.disponibles());
        return "reportes/inicio";
    }

    @GetMapping("/{clave}")
    public String ver(@PathVariable String clave,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                      Model modelo) {
        GeneradorReporte generador = reporteServicio.obtener(clave);
        FiltroReporte filtro = new FiltroReporte(desde, hasta);
        modelo.addAttribute("reporte", generador);
        modelo.addAttribute("filtro", filtro);
        modelo.addAttribute("tabla", reporteServicio.generar(clave, filtro));
        modelo.addAttribute("reportes", reporteServicio.disponibles());
        return "reportes/ver";
    }

    @GetMapping("/{clave}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable String clave,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        TablaReporte tabla = reporteServicio.generar(clave, new FiltroReporte(desde, hasta));
        byte[] contenido = exportadorPdf.exportar(tabla);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("reporte-" + clave + "-" + LocalDate.now() + ".pdf").build().toString())
                .body(contenido);
    }
}
