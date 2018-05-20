package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.bo.PeriodoBo;
import com.ftc.gedoc.bo.impl.DocumentoBOImpl;
import com.ftc.gedoc.bo.impl.PeriodoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.dao.impl.DocumentoDAOImpl;
import com.ftc.modelo.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Expenditure extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");

            if (cmd.startsWith(Comunes.toMD5("v-Cifra" + request.getSession().getId()))) {
                String tipo = request.getParameter("tipo");
                String fecha = request.getParameter("fecha");
                String importe = request.getParameter("importe");
                String impuesto = request.getParameter("impuesto");
                String comprobante = request.getParameter("comprobante");
                String tipoGasto = request.getParameter("tipo_gasto");
                PeriodoRegistro registro = new PeriodoRegistro();
                registro.setFecha(Comunes.DMAtoFecha(fecha));
                registro.setTipo(tipo);
                registro.setImporte(Double.parseDouble(importe));
                registro.setImpuesto(Double.parseDouble(impuesto));
                if (comprobante.startsWith("xml:")) {
                    DocumentoDAOImpl documento = new DocumentoDAOImpl();
                    //mover el documento a 
                } else {
                    registro.setNota(comprobante);
                }
            } else if (cmd.startsWith(Comunes.toMD5("registra-Cabecera" + request.getSession().getId()))) {
                String asignar = request.getParameter("asignar");
                String tipo = request.getParameter("tipo");
                String documento = request.getParameter("documento");
                String referencia = request.getParameter("referencia");
                String fecha = request.getParameter("fechaGasto");
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoCabecera pc = new PeriodoCabecera();
                pc.setAsociadoA(asignar);
                pc.setTipo(tipo);
                pc.setDocumento(documento);
                pc.setReferencia(referencia);
                pc.setFecha(Comunes.DMAtoFecha(fecha)); // --> cafaray 281217 - manejo de registros fuera de periodo:
                pc = bo.insertaCabecera(bo.actual().getIdentificador(), pc);
                if (pc.getIdentificador().isEmpty()) {
                    out.print("Algo ocurrio y no se ingres&oacute; el nuevo registro para gasto.");
                } else {
                    out.print("Se actualizo con &eacute;xito el registro.");
                }
            } else if (cmd.startsWith(Comunes.toMD5("actualizaNota-" + request.getSession().getId()).toUpperCase())) {
                String documento = request.getParameter("documento");
                String importe = request.getParameter("importe");
                String impuesto = request.getParameter("impuesto");
                String fecha = request.getParameter("fecha");
                String tipo = request.getParameter("tipo");
                String autoriza = request.getParameter("autoriza") == null ? "" : request.getParameter("autoriza");
                String cabecera = cmd.substring(Comunes.toMD5("actualizaNota-" + request.getSession().getId()).length()).toLowerCase();
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoRegistro pr = new PeriodoRegistro();
                pr.setDescripcion("Nota de gasto");
                pr.setEstatus("A");
                pr.setFecha(Comunes.DMAtoFecha(fecha.replace("-", "/")));
                pr.setImporte(Double.parseDouble(importe));
                pr.setImpuesto(Double.parseDouble(impuesto));
                pr.setNota(documento);
                pr.setTipo(tipo);
                pr.setAutoriza(autoriza);
                pr = bo.insertaRegistro(cabecera, pr);
                if (pr.getRegistro().isEmpty()) {
                    out.print("Algo ocurrio y no se ingres&oacute; el nuevo registro para gasto.");
                } else {
                    out.print("Se actualizo con &eacute;xito el registro.");
                }
            } else if (cmd.startsWith(Comunes.toMD5("xdoc-remove".concat(request.getSession().getId())).toUpperCase())) {
                String registro = cmd.substring(Comunes.toMD5("xdoc-remove".concat(request.getSession().getId())).length());
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoRegistro periodoRegistro = bo.encuentraRegistroPorId(registro);
                if (periodoRegistro != null) {
                    Documento documento = new Documento();
                    DocumentoBO boDoc = new DocumentoBOImpl();
                    documento = periodoRegistro.getEvidencia() == null ? null : boDoc.findById(periodoRegistro.getEvidencia());
                    bo.eliminaRegistro(periodoRegistro);
                    int archivosEliminados = 0;
                    if (documento != null && documento.getArchivos() != null && !documento.getArchivos().isEmpty()) {
                        String carpeta = request.getSession().getAttribute("rfc")!=null?(String)request.getSession().getAttribute("rfc"):"";
                        eliminaArchivo(documento, carpeta);
                    }
                    out.printf("La acci&oacute;n de eliminar ha finalizado con &eacute;xito, se eliminaron %d archivos.", archivosEliminados);
                } else {
                    out.print("El registro indicado no existe en la unidad.");
                }
            } else if (cmd.startsWith(Comunes.toMD5("periodoRegistro-update:tipo".concat(request.getSession().getId())).toUpperCase())) {
                String idRegistro = cmd.substring(Comunes.toMD5("periodoRegistro-update:tipo".concat(request.getSession().getId())).length());
                String tipoComprobante = request.getParameter("valor");
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoRegistro periodoRegistro = bo.encuentraRegistroPorId(idRegistro);
                if (periodoRegistro != null) {
                    periodoRegistro = bo.actualizaTipoComprobante(periodoRegistro, tipoComprobante);
                    if (periodoRegistro.getTipo() != null) {
                        out.print("Se actualiz&oacute; el valor correctamente.");
                    } else {
                        out.print("Algo malo ocurri&oacute; al actualizar a " + idRegistro);
                    }
                } else {
                    out.print("No se localizo el registro especificado.");
                }
            } else if (cmd.startsWith(Comunes.toMD5("periodoRegistro-update:autoriza".concat(request.getSession().getId())).toUpperCase())) {
                String idRegistro = cmd.substring(Comunes.toMD5("periodoRegistro-update:autoriza".concat(request.getSession().getId())).length());
                String autoriza = request.getParameter("valor")!=null?request.getParameter("valor"):"";
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoRegistro periodoRegistro = bo.encuentraRegistroPorId(idRegistro);
                if (periodoRegistro != null) {
                    periodoRegistro = bo.actualizaAutoriza(periodoRegistro, autoriza.trim());
                    if (periodoRegistro.getAutoriza() != null) {
                        out.print("Se actualizo el valor correctamente.");
                    } else {
                        out.print("Algo malo ocurri&oacute; al actualizar a " + idRegistro);
                    }
                } else {
                    out.print("No se localizo el registro especificado.");
                }
            } else if (cmd.startsWith(Comunes.toMD5("elimina-cabecera".concat(request.getSession().getId())).toUpperCase())) {
                String idCabecera = cmd.substring(Comunes.toMD5("elimina-cabecera".concat(request.getSession().getId())).length());
                String tipoGasto = request.getParameter("tipo_gasto");
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoCabecera periodoCabecera = bo.encuentraCabeceraPorId(idCabecera);
                int eliminados = 0;
                if (periodoCabecera != null) {
                    List<PeriodoRegistro> registros = bo.listaRegistros(idCabecera);
                    for (PeriodoRegistro registro : registros) {
                        bo.eliminaRegistro(registro);
                        if(registro.getEvidencia()!=null && !registro.getEvidencia().isEmpty()){
                            Documento documento = new Documento();
                            DocumentoBO boDoc = new DocumentoBOImpl();
                            documento = boDoc.findById(registro.getEvidencia());
                            if(documento!=null){
                                String carpeta = request.getSession().getAttribute("rfc")!=null?(String)request.getSession().getAttribute("rfc"):"";
                                eliminados = eliminaArchivo(documento, carpeta);
                            }
                        }
                    }
                    bo.eliminaCabecera(periodoCabecera);
                    out.printf("Se borraron los registros correctamente, eliminando %d archivos.", eliminados);
                } else {
                    out.print("No se localizo el registro especificado.");
                }
            } else if(cmd.startsWith(Comunes.toMD5("xpenditures.cerrar-periodo".concat(request.getSession().getId())).toUpperCase())){
                PeriodoBo bo = new PeriodoBOImpl();
                CifraControl nuevo = bo.cierraPeriodo(bo.actual());
                out.print("Se ha cerrado correctamente el periodo y se han generado las cifras control correctamente.");
                //--> cafaray 211217: Ajuste de periodo:
                                      
            } else if (cmd.startsWith(Comunes.toMD5("xpenditures.ajustar-periodo-".concat(request.getSession().getId())).toUpperCase())){
                                               
                String periodo = cmd.substring(Comunes.toMD5("xpenditures.ajustar-periodo-".concat(request.getSession().getId())).toUpperCase().length());
                PeriodoBo bo = new PeriodoBOImpl();
                Periodo obPeriodo = bo.obtenerPeriodoPorId(periodo);
                if (obPeriodo!=null){
                    String ajuste = bo.cierraPeriodoAjuste(obPeriodo);
                    if (ajuste.length()>0){
                        out.print("Se ha cerrado correctamente el ajuste al periodo y se han generado las cifras control correctamente: " + ajuste);
                    } else {
                        out.print("Algo ocurrio y no se logro hacer el cierre del ajuste. Revise el log de operaciones para mas detalle.");
                    }
                } else {
                    out.print("El periodo especificado no se ha localizado o este periodo no se puede ajustar. "+periodo);
                }
                // <--
            } else if(cmd.startsWith(Comunes.toMD5("xpenditures.cerrar-registro-".concat(request.getSession().getId())).toUpperCase())){
                String idCabecera = cmd.substring(Comunes.toMD5("xpenditures.cerrar-registro-".concat(request.getSession().getId())).length()).toLowerCase();
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoCabecera pc = bo.cierraCabcera(idCabecera);
                if(pc!=null && pc.getEstatus()!=null && pc.getEstatus().equals("C")){
                    out.print("Se ha cerrado correctamente el registro de gastos.");
                } else {
                    out.print("Algo ocurrio y no se cerro el registro de gastos.");
                }
            } else if(cmd.startsWith(Comunes.toMD5("xpenditures.cerrar-registro-ajuste-".concat(request.getSession().getId())).toUpperCase())){
                String idCabecera = cmd.substring(Comunes.toMD5("xpenditures.cerrar-registro-ajuste-".concat(request.getSession().getId())).length()).toLowerCase();
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoCabecera pc = bo.cierraCabceraAjuste(idCabecera);
                if(pc!=null && pc.getEstatus()!=null && pc.getEstatus().equals("Q")){
                    out.print("Se ha cerrado correctamente el registro de gastos para aplicaci&oacute;n de ajuste.");
                } else {
                    out.print("Algo ocurrio y no se cerro el registro de gastos para el ajuste.");
                }          
            } else if(cmd.startsWith(Comunes.toMD5(request.getSession().getId().concat("generar-periodo-actual-")))){
                PeriodoBo bo = new PeriodoBOImpl();
                Periodo actual = bo.abrirPeriodo();
                if (actual!=null){
                    out.print("Se ha generado el periodo correctamente.");
                } else {
                    out.print("No se ha logrado obtener el periodo, pero al parecer este fue generado. Revise con el administrador de periodos");
                }
            } else {
                out.print("La operaci&oacte;n solicitada no fue identificada.");
            }
        } catch (GeDocBOException e) {
            out.print(e.getMessage());
        } catch (Exception e) {
            //response.setStatus(400);
            out.print("Ourri&oacte; un error que no se logr&oacute; interpretar, revise el log de operaciones.");
        } finally {
            out.close();
        }
    }

    private int eliminaArchivo(Documento documento, String carpeta) throws IOException {
        //elimina el archivo
        String archivos = documento.getArchivos();
        StringBuilder path = new StringBuilder(getServletContext().getInitParameter("fileLocation"));        
        path.append(carpeta).append("/");
        path.append(documento.getPersona()).append("/");
        String[] rutas = archivos.split(",");
        int xRutas = 0;
        if (rutas.length > 0) {            
            for (String ruta : rutas) {
                File file = new File(path.toString() + ruta);
                if (file.exists()) {
                    file.delete();
                    xRutas++;
                } else {
                    System.out.printf("El archivo \"%s\" no existe, no se puede eliminar.", file.getAbsolutePath());
                }
            }
        } else {
            throw new IOException(String.format("La ruta del documento no es valida [%s]", documento.getArchivos()));
        }
        return xRutas;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Expenditure";
    }// </editor-fold>

}
