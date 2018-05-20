package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.bo.DocumentoEstatusBO;
import com.ftc.gedoc.bo.NotificacionBO;
import com.ftc.gedoc.bo.impl.DocumentoBOImpl;
import com.ftc.gedoc.bo.impl.DocumentoEstatusBOImpl;
import com.ftc.gedoc.bo.impl.NotificacionBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.DocumentoEstatus;
import com.ftc.modelo.Notificacion;
import com.ftc.modelo.Documento;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManejaDocumentoEstatus extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        try {
            String cmd = request.getParameter("cmd");
            if(cmd.equals(Comunes.toMD5("docState:update-state".concat(session.getId())))){
                String idDocumento = request.getParameter("factura");
                String estatus = request.getParameter("estatus");
                String comentario = request.getParameter("comentario");
                if(idDocumento.length()>0){
                    DocumentoEstatusBO bo = new DocumentoEstatusBOImpl();
                    DocumentoBO boDoc = new DocumentoBOImpl();
                    Documento documento = boDoc.findById(idDocumento);
                    DocumentoEstatus de = bo.registrar(documento, DocumentoEstatus.Estatus.getEstatus(estatus.charAt(0)), comentario, session.getId());
                    if(de!=null){
                        out.print("Actualizaci&oacute;n realizada con &eacute;xito.");
                        documento.setEstatus(estatus);
                        if(estatus.equals("C")||estatus.equals("R")||estatus.equals("V")||estatus.equals("T")||estatus.equals("P")){
                            NotificacionBO notificaBo = new NotificacionBOImpl();                            
                            notificaBo.registrar(generaNotificacion(de, documento.getArchivos()));
                        }
                    } else {
                        out.print("Algo fallo y la actualizaci&oacute;n no se logr&oacute; completar.");
                    }                   
                }else{
                    out.print("No se reconoce el documento al que se cambiara el estatus.");
                }
            }
        } catch (GeDocBOException e){
            e.getCause().printStackTrace(System.out);
            out.print(e.getMessage());
        }catch(NullPointerException e){
            e.printStackTrace(System.out);
            out.print("No se localizo un parametro que se esperaba [NULL].");
        }catch(StringIndexOutOfBoundsException e){
            e.printStackTrace(System.out);
            out.print("El parametro econtrado no esta completo, faltan valores para calcular el argumento.");
        } finally {
            out.close();
        }
    }

    private Notificacion generaNotificacion(DocumentoEstatus documento, String archivos){
        Notificacion notificacion = new Notificacion();
        notificacion.setConCopia("");
        notificacion.setPersona(documento.getPersona());
        notificacion.setDocumentoElectronico(documento.getDocumentoElectronico());
        notificacion.setIdentificador(documento.getIdentificador());
        notificacion.setEstatus(documento.getEstatus().getDescripcion());        
        String estatus = documento.getEstatus().getIndicador();
        if(estatus.equals("C")){
            notificacion.setMensaje(String.format("Hay documentos [%s] en revisi�n.", archivos));
        } else if(estatus.equals("V")){
            notificacion.setMensaje(String.format("Hay documentos [%s] aprobados.", archivos));
        } else if(estatus.equals("R")){
            notificacion.setMensaje(String.format("Hay documentos [%s] rechazados.", archivos));
        } else if(estatus.equals("T")){
            notificacion.setMensaje(String.format("Hay documentos [%s] aprobados para pago.", archivos));
        } else if(estatus.equals("P")){
            notificacion.setMensaje(String.format("Hay documentos [%s] pagados.", archivos));
        } else {
            notificacion.setMensaje(String.format("Hay documentos [%s] que cambiaron de estatus.", archivos));
        }
        return notificacion;
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
        return "ManejaDocumentoEstatus";
    }

}
