package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.CEPArchivoBO;
import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.bo.impl.CEPArchivoBOImpl;
import com.ftc.gedoc.bo.impl.DocumentoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.services.invoice.CEPReader;
import com.ftc.modelo.CEPArchivo;
import com.ftc.modelo.CEPCabecera;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.ftc.modelo.Documento;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xml.sax.SAXException;


public class UploadCEP extends HttpServlet {
    
    private final static String PATH_SEPARATOR = System.getProperty("file.separator");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String factura = request.getParameter("factura");
            String cmd = request.getParameter("cmd");
            String persona = (String)request.getSession().getAttribute("persona");
            if (cmd.startsWith(Comunes.toMD5("upload-cep-".concat(persona)))){                
                out.println("Servlet UploadCEP at " + request.getContextPath());
                out.println("Documents for: " + persona);
                registraArchivo(request, response);
            } else {
                out.println("No se ha logrado identificar la operaci�n, " + cmd);
            }                        
        } finally {
            out.close();
        }
    }
    
    /***
     * Elimina los archivos del Filesystem, se usa la combinaci�n del path, un listado de archivos separados por coma
     * por cada archivo nombrado, se elimina del filesystem
     * @param path Ruta base
     * @param files Lista de archivos a eliminar separados por coma
     * @return
     * @throws IOException 
     */
    
    private int eliminaArchivos(String path, String files) throws IOException {            
        int x = 0;
        if (files.length() > 0) {
            String[] archivos = files.split(",");
            for (String archivo : archivos) {
                File location = new File(path.concat(PATH_SEPARATOR) + archivo.toLowerCase());
                if (location.exists()){
                    location.delete();
                    x++;
                } else {
                    System.out.printf("El archivo \"%s\" no existe, no se puede eliminar. %n", location.getAbsolutePath());
                }
            }
        }
        return x;
    }
    
    /***
     * A trav�s del parametro cmd se identifica la operaci�n y se ejecuta la acci�n correspondiente
     * @param request
     * @return
     * @throws SQLException
     * @throws Exception 
     */
    private String ejecutaComando(HttpServletRequest request) throws SQLException, Exception {
        StringBuilder mensaje = new StringBuilder("");
        try{
            String cmd = request.getParameter("cmd");
            // Eliminar data + archivos
            if (cmd.startsWith(Comunes.toMD5("eliminar-" + request.getSession().getId()))) {
                Documento documento = new Documento();
                DocumentoBO boDoc = new DocumentoBOImpl();
                documento = boDoc.recuperaDocumento(cmd.substring(cmd.length() - 16));
                if (documento != null) {
                    //eliminamos de la base de datos:
                    int eliminado = boDoc.eliminaDocumento(documento);
                    System.out.printf("=====> ManageFileUpload.eliminar(%s) retorno: %d%n", Comunes.quitarCaracter(documento.getIdentificador(), '0'), eliminado);
                    String archivos = documento.getArchivos();
                    StringBuilder path = new StringBuilder(getServletContext().getInitParameter("fileLocation"));
                    String rfc = (String) request.getSession().getAttribute("rfc");
                    path.append(rfc).append(PATH_SEPARATOR);
                    path.append(documento.getPersona()).append(PATH_SEPARATOR);
                    String[] rutas = archivos.split(",");
                    int eliminados = 0;
                    if (rutas.length > 0) {
                        eliminados = eliminaArchivos(path.toString(), archivos);
                    } else {
                        throw new Exception(String.format("La ruta del documento no es valida [%s]", documento.getArchivos()));
                    }                
                    mensaje.append(String.format("La acci�n de eliminar ha finalizado con %d documentos eliminados de %d esperados.", eliminados, rutas.length));
                } else {
                    throw new Exception("El documento solicitado no se localizo");
                }
            } else {
                mensaje.append("La acci�n solicitada no es valida.");
                throw new Exception(mensaje.toString());
            }
        }catch(NullPointerException e){
            mensaje.append("No se localizo el elemento base de solicitud de ejecuci�n.");
        }
        return mensaje.toString();
    }

    /***
     * Obtiene los archivos del Request para su carga en el filesystem, dejandolos en la carpeta temporal
     * @param request
     * @return
     * @throws FileUploadException 
     */
    private List<FileItem> obtenerItems(HttpServletRequest request) throws FileUploadException {
        long maxFileSize = (2 * 1024 * 1024);
        int maxMemSize = (2 * 1024 * 1024);
        //configura el entrono de manejo de archivos
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(maxMemSize);
        factory.setRepository(new File("/tmp"));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxFileSize);
        List<FileItem> items = upload.parseRequest(request);
        return items;
    }

    /***
     * Prepara la carpeta de documentos para no tener problemas de permisos durante el almacenaje
     * @param path
     * @return
     * @throws IOException 
     */
    private File carpetaTrabajo(String path) throws IOException {
        File location = new File(path);
        if (!location.exists()) {
            location.mkdir();
            location.setReadable(true, false);
            location.setWritable(true, false);
            location.setExecutable(true);
        }
        return location;
    }
    
    /***
     * Lee el archivo XML y extrae la informaci�n dejandola en el objeto CEPCabecera, para su posterior guardado en base de datos.
     * @param location Ruta donde se localiza el archivo
     * @param persona Proveedor al que esta asociado el archivo
     * @return CEPCabecera, modelo que representa al archivo XML CEP
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    private CEPCabecera cepCabecera(File location, String persona) throws ParserConfigurationException, SAXException, IOException {
        System.out.print("=====> Subiendo archivo " + location.getAbsolutePath());
        CEPReader xmlDoc = new CEPReader();
        CEPCabecera cep = xmlDoc.procesaXML(location.getAbsolutePath());
        if (cep != null) {
            return cep;
        } else {
            throw new IOException(String.format("No se logro leer el archivo %s como un CFD/CFDI valido.", location.getAbsolutePath()));
        }
    }
    
    /***
     * Lleva a cabo la carga del documento en el Filesystem y almacenado en la base de datos
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    private void registraArchivo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        
        response.setContentType("text/html;charset=ISO-8859-1");
        PrintWriter out = response.getWriter();
        StringBuilder mensaje = new StringBuilder("");
        String logLocation = getServletContext().getInitParameter("logLocation");
        try {
            if (request.getMethod().equals("POST") && request.getContextPath().endsWith("/ftcgedoc")) {
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);

                if (!isMultipart) { //es una accion requerida a trav�s del atributo cmd
                    String comando = ejecutaComando(request);
                    mensaje.append(comando);
                } else { // se trata de la carga de archivos
                    //obtenemos los items
                    List<FileItem> items = obtenerItems(request);
                    Iterator<FileItem> iter = items.iterator();
                    String titulo = "", observaciones = "", persona = "", rfc = "", idCabecera = "";
                    List<FileItem> xsubir = new LinkedList<FileItem>();
                    while (iter.hasNext()) {
                        FileItem item = iter.next();
                        if (item.isFormField()) {
                            String name = item.getFieldName();
                            if (name.equals("titulo")) {
                                titulo = item.getString();
                            } else if (name.equals("observaciones")) {
                                observaciones = item.getString();
                            } else if (name.equals("persona")) {
                                persona = item.getString();
                            } else if (name.equals("cmd")) { //variable donde viene el nombre de quien esta asociado el gasto                                
                                idCabecera = item.getString().substring(Comunes.toMD5("upload-cep-".concat(request.getSession().getId())).length());
                            }
                        } else {
                            String contentType = item.getContentType().toLowerCase();

                            if (contentType.equals("application/x-pdf")
                                    || contentType.equals("application/pdf")
                                    || contentType.equals("text/xml")) {
                                if (item.getFieldName().equals("archivo_pdf")) {
                                    if (contentType.equals("application/pdf")) {
                                        xsubir.add(item);
                                    } else {
                                        throw new Exception("Se esperaba el archivo de pago CEP en formato PDF, al parecer no es un PDF. " + contentType);
                                    }
                                }
                                if (item.getFieldName().equals("archivo_xml")) {
                                    if (contentType.equals("text/xml")) {
                                        xsubir.add(item);
                                    } else {
                                        throw new Exception("Se esperaba el archivo de pago CEP en formato XML, al parecer no es un XML. " + contentType);
                                    }
                                }                                
                            }
                        }
                    }

                    /* GUARDAMOS EL/LOS ARCHIVO
                     * primero conocemos la ubicaci�n del folder
                     * luego se sube uno a uno los archivos con su nombre origen
                     * finalmente se almacenan en la tabla
                     */
                    //String sesion = request.getSession().getId();
                    String folder = persona, archivos = "";

                    try {
                        //se asegura de establecer el directorio del proveedor/cliente
                        StringBuilder path = new StringBuilder(getServletContext().getInitParameter("fileLocationCEP"));                        
                        carpetaTrabajo(path.toString());
                        
                        rfc = (String) request.getSession().getAttribute("rfc");                        
                        path.append(rfc).append(PATH_SEPARATOR);
                        carpetaTrabajo(path.toString());

                        if (folder.trim().length() == 0) {
                            folder = "tmp";
                        }
                        path.append(folder);
                        File location = carpetaTrabajo(path.toString());
                        
                        int cont = 0;
                        int locacionExiste = 0;
                        CEPCabecera facturaCabecera = null;
                        //valida que hay un xml y que corresponda con el owner de la aplicacion; si no viene XML se deja pasar ya que no hay forma de validar

                        for (FileItem item : xsubir) {
                            item.getString("UTF-8");
                            location = new File(path.append(PATH_SEPARATOR) + item.getName().toLowerCase());
                            boolean existe = false;
                            boolean esXML = item.getName().toLowerCase().endsWith("xml");
                            if (!location.exists()) {
                                item.write(location);
                                System.out.println("     =====>  [OK]");
                                archivos = archivos.concat(item.getName()).concat(",");
                                cont++;
                            } else {
                                System.out.print("     =====> Archivo existente: " + location.getAbsolutePath());
                                locacionExiste++;
                                existe = true;
                                archivos = archivos.concat(esXML ? "" : item.getName()).concat(",");
                            }
                            if (esXML && !existe) {
                                //encontre el xml, entonces se trata de un cfdi o cfd  
                                try {
                                    facturaCabecera = cepCabecera(location, persona);
                                } catch (ParserConfigurationException e) {
                                    mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error de transformaci�n, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", item.getName()));
                                    e.printStackTrace(System.out);
                                    location.delete();
                                } catch (SAXException e) {
                                    mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error de lectura de los elementos, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", item.getName()));
                                    e.printStackTrace(System.out);
                                    location.delete();
                                } catch (IOException e) {
                                    mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error IO, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", item.getName()));
                                    e.printStackTrace(System.out);
                                    location.delete();
                                }
                                if (facturaCabecera!=null && !rfc.endsWith(facturaCabecera.getRfcReceptor())) { //no se trata de un archivo del proveedor hacia el owner
                                    //se borran los archivos cargados y se retorna:
                                    if (archivos.length() > 0) {
                                        eliminaArchivos(path.toString(), archivos);
                                    }
                                    mensaje.append(String.format("Es imposible determinar si el documento XML pertenece al proveedor. Se esperaba %s y se tiene %s.", rfc, facturaCabecera.getRfcReceptor()));
                                    return;
                                }
                            } else if (esXML && existe) {
                                mensaje.append(String.format("El archivo %s ya existe por lo que no podr� ser subido nuevamente.", item.getName()));
                                return;
                            }
                        }

                        if (archivos.length() > 0) {
                            CEPArchivoBO bo = new CEPArchivoBOImpl();
                            CEPArchivo cepArchivo = new CEPArchivo();
                            cepArchivo.setArchivos(archivos);
                            cepArchivo.setEstatus("P");
                            cepArchivo.setObservaciones(observaciones);
                            cepArchivo.setTitulo(titulo);                            
                            cepArchivo.setPersona(persona);
                            cepArchivo = bo.registraCEP(cepArchivo, facturaCabecera);
                            String registro = "";
                            mensaje.append(String.format(" Se almacenaron %d documento(s). %s", cont, cepArchivo.getIdentificador()));
                            if (locacionExiste > 0) {
                                mensaje.append("No se almacenaron ").append(locacionExiste).append(" documento(s) porque ya existen.");
                            }
                        } else {
                            mensaje.append("No se localizaron archivos nuevos para registrar.");
                        }
                    } catch (SQLException sqle) {
                        mensaje.append(sqle.getSQLState().equals("0") ? sqle.getMessage() : "Excepci�n al registrar los documentos. Reporte a sistemas." + sqle.getSQLState() + "-" + sqle.getErrorCode());
                        Comunes.escribeLog(logLocation, sqle, (String) request.getSession().getAttribute("usuario"));
                    } catch (IOException ioe) {
                        mensaje.append("No se lograr�n cargar los archivos. ").append(ioe.getMessage());
                        Comunes.escribeLog(logLocation, ioe, (String) request.getSession().getAttribute("usuario"));
                    }
                }
            } else {
                mensaje.append("TIPO DE LLAMADO NO RECONOCIDO.");
            }
        } catch (FileUploadBase.SizeLimitExceededException sizee) {
            mensaje.append("El tama�o del archivo (").append((sizee.getActualSize() / 2048000)).append(" MB) es superior al esperado (2MB).");
            System.out.printf("TSMI:::Exception on Servlet %s, %s -->\n", this.getServletName(), sizee.getMessage());
            sizee.printStackTrace(System.out);
            System.out.printf("TSMI:::Exception on Servlet <--");
            com.ftc.aq.Comunes.escribeLog(logLocation, sizee, (String) request.getSession().getAttribute("usuario"));
        } catch (Exception e) {
            mensaje.append("error:Exception on Servlet: ").append(e.getMessage());
            System.out.printf("TSMI:::Exception on Servlet %s, %s -->\n", this.getServletName(), e.getMessage());
            e.printStackTrace(System.out);
            System.out.printf("TSMI:::Exception on Servlet <--");
            com.ftc.aq.Comunes.escribeLog(logLocation, e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.println(mensaje.toString());
            out.flush();
            out.close();
        }
    }
    
    private String registraArchivos(String archivos, String persona, String titulo, String observaciones, String sesion, CEPCabecera facturaCabecera) {
        try{
            CEPArchivoBO bo = new CEPArchivoBOImpl();
            CEPArchivo archivo = new CEPArchivo();
            archivo.setArchivos(archivos);
            archivo.setObservaciones(observaciones);
            archivo.setTitulo(titulo);
            archivo.setPersona(persona);
            archivo = bo.registraCEP(archivo, facturaCabecera);
            return archivo.getIdentificador();
        }catch(GeDocBOException e){
            return null;
        }
    }
    
    private CEPCabecera readCEPPago(String file) {
        CEPCabecera response = null;
        try {
            CEPReader reader = new CEPReader();
            response = reader.procesaXML(file);
            return response;
        } catch(SAXException e){
            System.out.println("====> Fallo en la lectura del CEP de pago: " + e.getMessage());
            e.printStackTrace(System.out);
            return response;
        } catch(IOException e){
            System.out.println("====> Fallo en la lectura del CEP de pago: " + e.getMessage());
            e.printStackTrace(System.out);
            return response;
        } catch (ParserConfigurationException e) {
            System.out.println("====> Fallo en la lectura del CEP de pago: " + e.getMessage());
            e.printStackTrace(System.out);
            return response;
        }
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        registraArchivo(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for upload a CEP Document";
    }

}
