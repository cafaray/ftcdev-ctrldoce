package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.bo.DocumentoEstatusBO;
import com.ftc.gedoc.bo.impl.DocumentoBOImpl;
import com.ftc.gedoc.bo.impl.DocumentoEstatusBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.DocumentoEstatus;
import com.ftc.modelo.Documento;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentosDescarga extends HttpServlet {

    private final static String DOCUMENTO_XML = "1";
    private final static String DOCUMENTO_PDF = "2";
    private final static String DOCUMENTO_XML_PDF = "3";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String docs = request.getParameter("documentos");
        String cmd = request.getParameter("cmd");
        String tipoDocumento = request.getParameter("tipo_documento");
        OutputStream out = response.getOutputStream();
        final File file = new File("/tmp/ctrldoce_download.zip");
        final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
        boolean hayArchivo = false;
        String sesion = request.getSession().getId();
        try {
            if (docs != null && !docs.isEmpty()) {
                if (cmd.equals(Comunes.toMD5("downloadFiles-" + request.getSession().getId()))) {
                    List<Documento> documentos;
                    try {                        
                        DocumentoBO docManager = new DocumentoBOImpl();
                        if(docs.equals("*")){
                            documentos = (List)request.getSession().getAttribute(request.getParameter("tipo"));
                        } else {
                            documentos = docManager.recuperaDocumentos(docs.substring(0,docs.length()-1));
                        }
                        String ubicacion = getServletContext().getInitParameter("fileLocation");
                        ubicacion = ubicacion.concat((String)request.getSession().getAttribute("rfc")).concat("/");
                        StringBuilder cadenaDocumentos = new StringBuilder();
                        for (Documento doc:documentos){
                            String located = ubicacion.concat(doc.getPersona()).concat("/");
                            //if(doc.getArchivos().contains(",")){                                
                                String[] tmp = doc.getArchivos().split(",");
                                for (String t:tmp){
                                    if(tipoDocumento.equals(DOCUMENTO_XML) && t.toLowerCase().endsWith("xml")){
                                        cadenaDocumentos.append(located).append(t.toLowerCase()).append(",");
                                    } else if(tipoDocumento.equals(DOCUMENTO_PDF) && t.toLowerCase().endsWith("pdf")){
                                        cadenaDocumentos.append(located).append(t.toLowerCase()).append(",");
                                    } else {
                                        cadenaDocumentos.append(located).append(t.toLowerCase()).append(",");
                                    }
                                }
                            /*} else {
                                    if(tipoDocumento.equals(DOCUMENTO_XML) && doc.getArchivos().toLowerCase().endsWith("xml")){
                                        cadenaDocumentos.append(located).append(doc.getArchivos().toLowerCase()).append(",");
                                    } else if(tipoDocumento.equals(DOCUMENTO_PDF) && doc.getArchivos().toLowerCase().endsWith("pdf")){
                                        cadenaDocumentos.append(located).append(doc.getArchivos().toLowerCase()).append(",");
                                    } else {
                                        cadenaDocumentos.append(located).append(doc.getArchivos().toLowerCase()).append(",");
                                    }
                                    */
                            //}
                        }
                        cadenaDocumentos.substring(0, cadenaDocumentos.length()-1);
                        String[] sDocumentos = cadenaDocumentos.toString().split(",");
                        for (String documento : sDocumentos) {
                            String zipDocumento; 
                            try {
                                DataInputStream dis = null;
                                try {                                    
                                    FileInputStream fis = new FileInputStream(documento);
                                    dis = new DataInputStream(fis);
                                } catch (FileNotFoundException e) {
                                    System.out.printf("=====> El documento %s no existe, no se podr� descargar.%n",documento);
                                }
                                if (dis != null) {
                                    hayArchivo = true;
                                    zipDocumento = documento.substring(documento.lastIndexOf("/") + 1);
                                    ZipEntry zipFile = new ZipEntry(zipDocumento);
                                    zip.putNextEntry(zipFile);
                                    byte[] x = new byte[dis.available()];
                                    dis.read(x);
                                    byte[] data = x;
                                    zip.write(data, 0, data.length);
                                }
                            } catch (ZipException e) {
                                System.out.printf("El documento %s no se podr� descargar, ocurri� una excepci�n.%n", documento);
                            }
                        }
                    } finally {
                        zip.closeEntry();
                        zip.close();
                    }
                    //se realiza la descarga del documento
                    FileInputStream in = null;
                    //OutputStream descarga = null;
                    if(hayArchivo){
                        //registra estatus de descarga
                        for (Documento doc:documentos){
                            try{
                                DocumentoEstatusBO bo = new DocumentoEstatusBOImpl();
                                bo.registrar(doc, DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_DESCARGA, "DocumentoDAOImpl descargado en ZIP File", sesion);
                            }catch(GeDocBOException e){
                                System.out.printf("=====> Ocurrio una excepcion al reportar el estatus de descarga para %s del documento%n\t\tDocumentoDAOImpl: %s-%s",sesion, doc.getPersona(), doc.getIdentificador());
                                e.printStackTrace(System.out);
                            }
                        }
                        
                        try {
                    
                            response.setContentType("application/zip");
                            response.setHeader("Content-disposition", "attachment; filename=ctrldoce_download.zip");

                            in = new FileInputStream(file);
                            byte[] buffer = new byte[4096];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }else{
                        out.write("No se localizaron archivos validos para descargar. Revise.".getBytes());
                    }
                } else {
                    out.write("No se logro validar la sesion del usuario. Revise.".getBytes());
                }
            } else {
                out.write("La lista de documentos esta vacia. Revise.".getBytes());
            }
        } catch(Exception e){
            throw new IOException("");
        } finally {
            if (out != null) {
                out.flush();
            }
        }
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
        return "DocuemntosDescarga";
    }

}
