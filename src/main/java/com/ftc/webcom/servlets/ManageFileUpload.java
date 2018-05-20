package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.bo.DocumentoEstatusBO;
import com.ftc.gedoc.bo.PeriodoBo;
import com.ftc.gedoc.bo.impl.DocumentoBOImpl;
import com.ftc.gedoc.bo.impl.DocumentoEstatusBOImpl;
import com.ftc.gedoc.bo.impl.PeriodoBOImpl;
import com.ftc.modelo.FacturaCabecera;
import com.ftc.gedoc.utiles.Importar;
import com.ftc.modelo.PersonaContacto;
import com.ftc.gedoc.dao.FacturaCabeceraDAO;
import com.ftc.gedoc.dao.impl.FacturaCabeceraDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.DocumentoEstatus;
import com.ftc.modelo.PeriodoRegistro;
import com.ftc.services.invoice.CFDIReader;
import com.ftc.modelo.CFDICabecera;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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

public class ManageFileUpload extends HttpServlet {

    private final static String PATH_SEPARATOR = System.getProperty("file.separator");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    private String ejecutaComando(HttpServletRequest request) throws SQLException, Exception {
        StringBuilder mensaje = new StringBuilder("");
        try{
            String cmd = request.getParameter("cmd");
            
            if (cmd.startsWith(Comunes.toMD5("eliminar-" + request.getSession().getId()))) {
                Documento documento = new Documento();
                DocumentoBO bo = new DocumentoBOImpl();
                documento = bo.recuperaDocumento(cmd.substring(cmd.length() - 16));
                if (documento != null) {
                    //eliminamos de la base de datos:
                    int eliminado = bo.eliminaDocumento(documento);
                    System.out.printf("=====> ManageFileUpload.eliminar(%s) retorno: %d%n", Comunes.quitarCaracter(documento.getIdentificador(), '0'), eliminado);
                    String archivos = documento.getArchivos();
                    StringBuilder path = new StringBuilder(getServletContext().getInitParameter("fileLocation"));
                    String rfc = (String) request.getSession().getAttribute("rfc");
                    path.append(rfc).append(PATH_SEPARATOR);
                    path.append(documento.getPersona()).append(PATH_SEPARATOR);
                    String[] rutas = archivos.split(",");
                    if (rutas.length > 0) {
                        int xRutas = 0;
                        for (String ruta : rutas) {
                            File file = new File(path.toString() + ruta.toLowerCase());
                            if (file.exists()) {
                                file.delete();
                                xRutas++;
                            } else {
                                System.out.printf("El archivo \"%s\" no existe, no se puede eliminar. %n", file.getAbsolutePath());
                            }
                        }
                        mensaje.append(String.format("La acci&oacute;n de eliminar ha finalizado con %d documentos eliminados.", xRutas));
                    } else {
                        throw new Exception(String.format("La ruta del documento no es valida [%s]", documento.getArchivos()));
                    }
                } else {
                    throw new Exception("El documento solicitado no se localizo");
                }
            } else if(cmd.equals(Comunes.toMD5("ctrldoce.consulta-notificaciones".concat(request.getSession().getId())))) {
                
            } else {
                mensaje.append("La acci&oacte;n solicitada no es valida.");
                throw new Exception(mensaje.toString());
            }
        }catch(NullPointerException e){
            mensaje.append("No se localizo el elemento base de solicitud de ejecuci&oacute;n.");
        }
        return mensaje.toString();
    }

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

    private FacturaCabecera facturaCabecera(File location, String persona) throws ParserConfigurationException, SAXException, IOException {
        FacturaCabecera facturaCabecera = null;
        System.out.print("=====> Subiendo archivo " + location.getAbsolutePath());
        CFDICabecera xmlDoc = CFDIReader.procesaXML(location.getAbsolutePath());
        if (xmlDoc != null) {
            facturaCabecera = new FacturaCabecera();
            facturaCabecera.setPersona(persona);
            //se alamacen mas abajo
            //facturaCabecera.setCdDocumento(fileName);
            facturaCabecera.setArchivo(location.getAbsolutePath());
            facturaCabecera.setDescuento(xmlDoc.getDescuento());
            facturaCabecera.setFolio(xmlDoc.getFolio());
            facturaCabecera.setFormaDePago(xmlDoc.getFormaDePago());
            facturaCabecera.setLocacion(location.getAbsolutePath());
            facturaCabecera.setLugarExpedicion(xmlDoc.getLugarExpedicion());
            facturaCabecera.setMetodoDePago(xmlDoc.getMetodoDePago());
            facturaCabecera.setMoneda(xmlDoc.getMoneda());
            facturaCabecera.setNombre(xmlDoc.getNombre());
            facturaCabecera.setNombreReceptor(xmlDoc.getNombreReceptor());
            facturaCabecera.setRfc(xmlDoc.getRfc());
            facturaCabecera.setRfcReceptor(xmlDoc.getRfcReceptor());
            facturaCabecera.setSerie(xmlDoc.getSerie());
            facturaCabecera.setStrFecha(xmlDoc.getStrFecha());
            facturaCabecera.setStrFechaTimbrado(xmlDoc.getStrFechaTimbrado());
            facturaCabecera.setSubTotal(xmlDoc.getSubTotal());
            facturaCabecera.setTipo(xmlDoc.getUuid() != null ? "cfdi" : "cfd");
            facturaCabecera.setTipoCambio(xmlDoc.getTipoCambio());
            facturaCabecera.setTotal(xmlDoc.getTotal());
            facturaCabecera.setTotalImpuestosTrasladados(xmlDoc.getTotalImpuestosTrasladados());
            facturaCabecera.setUuid(xmlDoc.getUuid());
            return facturaCabecera;
        } else {
            throw new IOException(String.format("No se logro leer el archivo %s como un CFD/CFDI valido.", location.getAbsolutePath()));
        }
    }

    private String registraArchivos(String archivos, String persona, String titulo, String observaciones, String sesion, FacturaCabecera facturaCabecera) throws SQLException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            archivos = archivos.endsWith(",") ? archivos.substring(0, archivos.length() - 1) : archivos;
            //registraDocumento(IN{archivo,titulo,observaciones,sesion},OUT{referencia,error})            
            params.add(new SpParam(1, Types.VARCHAR, persona));
            params.add(new SpParam(2, Types.VARCHAR, archivos));
            params.add(new SpParam(3, Types.VARCHAR, titulo));
            params.add(new SpParam(4, Types.VARCHAR, observaciones));
            params.add(new SpParam(5, Types.VARCHAR, sesion));
            params.add(new SpParam(6, Types.VARCHAR, null, true)); //referencia
            params.add(new SpParam(7, Types.VARCHAR, null, true)); //error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraDocumento", params);
            if (vuelta != null && vuelta.length == 2) {
                if (String.valueOf(vuelta[1]).length() > 0) {
                    return String.valueOf(vuelta[1]);
                } else {
                    String referencia = String.valueOf(vuelta[0]);
                    if (facturaCabecera != null) {
                        FacturaCabeceraDAO dao = new FacturaCabeceraDAOImpl();
                        facturaCabecera.setCdDocumento(referencia);
                        String referenciaDetalle = dao.insertar(facturaCabecera, sesion);
                        System.out.printf("%nSe ha generado la referencia %s de lectura del XML del documento %s%n", referenciaDetalle, referencia);
                        //se registra el nuevo estatus
                        DocumentoEstatus estatus = new DocumentoEstatus();
                        estatus.setPersona(persona);
                        estatus.setDocumentoElectronico(referencia);
                        estatus.setEstatus(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_CARGA);
                        estatus.setSesion(sesion);
                        estatus.setComentario("Registro de documento a trav�s de gesti�n de cargas.");
                        DocumentoEstatusBO bo = new DocumentoEstatusBOImpl();
                        try {
                            bo.registrar(estatus);
                        } catch (GeDocBOException ex) {
                            System.out.printf("====> DocumentoEstatus no fue registrado, excepcion: %s con %s.%n\t\tPersonaDAOImpl: %s \t\tReferencia:%s", ex.getMessage(), sesion, persona,referencia);
                            ex.printStackTrace(System.out);
                        }
                    }
                    return referencia;
                }
            } else {
                throw new SQLException(String.format("Algo ocurrio en el llamado al procedimiento,  no se obtuvieron los parametros de salida esperados. Se esperaba %d y se tienen %d", 2, vuelta != null ? vuelta.length : 0), "0");
            }
        } finally {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        }
    }

    private String registraArchivosGasto(String archivos, String persona, String sesion, FacturaCabecera facturaCabecera, String idCabecera) throws SQLException {
        try {
            if (!idCabecera.isEmpty() || facturaCabecera != null) {
                String documento = registraArchivos(archivos, persona, "Registro de gasto", "Registro de gastos desde carga masiva", sesion, facturaCabecera);
                //guarda el registro del gasto
                PeriodoBo bo = new PeriodoBOImpl();
                PeriodoRegistro pr = new PeriodoRegistro();
                pr.setDescripcion("Nota de gasto");
                pr.setEstatus("A");
                if(facturaCabecera!=null){
                    try {
                        pr.setFecha(Comunes.toFecha(facturaCabecera.getStrFecha().substring(0, 10).replace("-", PATH_SEPARATOR)));
                    } catch (Exception e) {
                        pr.setFecha(null);
                        System.out.println("=====> No se logro transformar la fecha en el registro de gasto " + facturaCabecera.getStrFecha());
                    }
                    pr.setImporte(facturaCabecera.getTotal());
                    pr.setImpuesto(facturaCabecera.getTotalImpuestosTrasladados());
                    pr.setNota("XML:");
                }
                pr.setNota("PDF");
                pr.setEvidencia(documento);
                pr.setTipo("-1");
                pr.setAutoriza("");
                pr = bo.insertaRegistro(idCabecera, pr);
                if (pr.getRegistro().isEmpty()) {
                    return "Algo ocurrio y no se ingres� el nuevo registro para gasto.";
                } else {
                    return "Se actualizo con �xito el registro.";
                }
            } else {
                return String.format("No se localizo el elemento de gasto o el archivo XML para la carga [%s]", (idCabecera != null ? idCabecera : "null"));
            }
        } catch (GeDocBOException e) {
            e.printStackTrace(System.out);
            return e.getMessage();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
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
                                idCabecera = item.getString().substring(Comunes.toMD5("upload-" + request.getSession().getId()).length());
                            }
                        } else {
                            String contentType = item.getContentType().toLowerCase();

                            if (contentType.equals("application/x-pdf")
                                    || contentType.equals("application/pdf")
                                    || contentType.equals("text/xml")) {
                                if (item.getFieldName().equals("archivo1") || item.getFieldName().equals("archivo3")) {
                                    if (contentType.equals("application/pdf")) {
                                        xsubir.add(item);
                                    } else {
                                        throw new Exception("El archivo de factura PDF al parecer no es un PDF.");
                                    }
                                }
                                if (item.getFieldName().equals("archivo2")) {
                                    if (contentType.equals("text/xml")) {
                                        xsubir.add(item);
                                    } else {
                                        throw new Exception("El archivo de factura XML al parecer no es un XML.");
                                    }
                                }
                                if (item.getFieldName().startsWith("gasto_xml_") || item.getFieldName().startsWith("gasto_pdf_")) {
                                    xsubir.add(item);
                                }
                            } else if (contentType.equals("text/csv")) { //seguro se trata de carga masiva de proveedores o clientes:                                
                                File file = new File("/tmp/" + item.getName());
                                if (file.exists()) {
                                    file.delete();
                                }
                                item.write(file);
                                List<PersonaContacto> personas = Importar.importar(file.getAbsolutePath());
                                mensaje.append(String.format("Se almacenaron %d personas en la base de datos desde el archivo %s.", personas.size(), file.getName()));
                                System.out.println(mensaje.toString());
                                return;
                            }
                        }
                    }

                    /* GUARDAMOS EL/LOS ARCHIVO
                     * primero conocemos la ubicaci�n del folder
                     * luego se sube uno a uno los archivos con su nombre origen
                     * finalmente se almacenan en la tabla
                     */
                    String sesion = request.getSession().getId();
                    String folder = persona, archivos = "";

                    try {
                        //se asegura de establecer el directorio del proveedor/cliente
                        StringBuilder path = new StringBuilder(getServletContext().getInitParameter("fileLocation"));
                        rfc = (String) request.getSession().getAttribute("rfc");
                        path.append(rfc).append(PATH_SEPARATOR);
                        if (folder.trim().length() == 0) {
                            folder = "tmp";
                        }
                        path.append(folder);
                        File location = carpetaTrabajo(path.toString());
                        int cont = 0;
                        int locacionExiste = 0;
                        FacturaCabecera facturaCabecera = null;
                        //valida que hay un xml y que corresponda con el owner de la aplicacion; si no viene XML se deja pasar ya que no hay forma de validar
                        boolean esGasto = false;

                        for (FileItem item : xsubir) {
                            item.getString("UTF-8");
                            esGasto = (item.getFieldName().startsWith("gasto_xml_") || item.getFieldName().startsWith("gasto_pdf_"));
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
                                    facturaCabecera = facturaCabecera(location, persona);
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
                                if (facturaCabecera!=null && !rfc.endsWith(facturaCabecera.getRfc()) //no se trata de cliente
                                        && !rfc.endsWith(facturaCabecera.getRfcReceptor())) { // es un proveedor pero no se le emite al due�o
                                    //se borran los archivos cargados y se retorna:
                                    if (archivos.length() > 0) {
                                        eliminaArchivos(path.toString(), archivos);
                                    }
                                    mensaje.append(String.format("Es imposible determinar si el documento XML pertenece al dominio del propietario. Se esperaba %s y se tiene %s.", rfc, facturaCabecera.getRfcReceptor()));
                                    return;
                                }
                            } else if (esXML && existe) {
                                mensaje.append(String.format("El archivo %s ya existe por lo que no podr� ser subido nuevamente.", item.getName()));
                                return;
                            }
                        }

                        if (archivos.length() > 0) {
                            String registro = "";
                            if (!esGasto) {
                                registro = registraArchivos(archivos, persona, titulo, observaciones, sesion, facturaCabecera);
                            } else {
                                //deben de venir en pares, as� que se analiza la cadena y se envia cuantos pares vengan
                                String[] files = archivos.split(",");

                                for (int x = 0; x < files.length; x++) {
                                    String XML = "", PDF = "";
                                    if (files[x].toLowerCase().endsWith(".xml")) { //primero debe de venir el XML
                                        XML = files[x];
                                        if (++x < files.length && files[x].toLowerCase().endsWith(".pdf")) {
                                            PDF = files[x];
                                            archivos = PDF + "," + XML;
                                        } else {
                                            archivos = XML;
                                            --x;
                                        }
                                    } else if (files[x].toLowerCase().endsWith(".pdf")) {
                                        PDF = files[x];
                                        archivos = PDF;
                                    }
                                    if (!XML.isEmpty()) {
                                        location = new File(path.append(PATH_SEPARATOR) + XML.toLowerCase());
                                        try {
                                            facturaCabecera = facturaCabecera(location, persona);
                                            registro = registraArchivosGasto(archivos, persona, sesion, facturaCabecera, idCabecera);
                                        } catch (ParserConfigurationException e) {
                                            mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error de transformaci�n, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", XML));
                                            e.printStackTrace(System.out);
                                            location.delete();
                                        } catch (SAXException e) {
                                            mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error de lectura de los elementos, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", XML));
                                            e.printStackTrace(System.out);
                                            location.delete();
                                        } catch (IOException e) {
                                            mensaje.append(String.format("La lectura del XML no se logr� realizar debido a un error IO, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", XML));
                                            e.printStackTrace(System.out);
                                            location.delete();
                                        } catch(SQLException e) {
                                            mensaje.append(String.format("El registro del detalle del XML no se logr� realizar debido a un error SQL, revise el archivo %s, probablemente la estructura no es un CFD o CFDI. En caso de que este correcto el archivo notifique a sistemas.", XML));
                                            e.printStackTrace(System.out);
                                            location.delete();
                                        }
                                    } else {
                                        try{
                                            registro = registraArchivos(archivos, persona, "Archivo de gasto", "" ,sesion, null);
                                            //registro = registraArchivosGasto(archivos, persona, sesion, null, idCabecera);
                                        }catch(SQLException e){
                                            mensaje.append(String.format("El registro no se logr� realizar debido a un error SQL, revise el archivo %s.", archivos));
                                            e.printStackTrace(System.out);
                                            location.delete();
                                        }
                                    }
                                }
                            }
                            mensaje.append(String.format(" Se almacenaron %d documento(s).", cont));
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

    private int eliminaArchivos(String path, String files) throws IOException {
        int x = 0;
        if (files.length() > 0) {
            String[] archivos = files.split(",");
            for (String archivo : archivos) {
                File location = new File(path.concat(PATH_SEPARATOR) + archivo.toLowerCase());
                location.delete();
                x++;
            }
        }
        return x;
    }

    @Override
    public String getServletInfo() {
        return "Manejador de archivos de carga";
    }
}
