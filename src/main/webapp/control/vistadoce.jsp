<%@page import="com.ftc.gedoc.utiles.comparators.DocumentoComparatorPorDocumento"%>
<%@page import="com.ftc.gedoc.utiles.comparators.DocumentoComparatorPorEstatus"%>
<%@page import="com.ftc.gedoc.utiles.DocumentoEstatus"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="com.ftc.gedoc.utiles.comparators.DocumentoComparatorPorFecha"%>
<%@page import="com.ftc.gedoc.utiles.comparators.DocumentoComparatorPorEmpresa"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.ftc.gedoc.utiles.Persona"%>
<%@page import="com.ftc.gedoc.utiles.Documento"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        int paginacion = 50;
        String cmd = request.getParameter("cmd");
        Seguridad seguridad = (Seguridad) session.getAttribute("codsec");
        String tipo = request.getParameter("tipo");
        String sesion = session.getId();
        int pagina;
        try {
            pagina = Integer.valueOf(request.getParameter("pagina") == null ? "1" : request.getParameter("pagina"));
        } catch (NumberFormatException e) {
            System.out.print("Pagina incorrecta o nula");
            pagina = 1;
        }

        //filtrado de datos
        String fechai = request.getParameter(Comunes.toMD5(sesion + "param-fi:")) == null ? "" : request.getParameter(Comunes.toMD5(sesion + "param-fi:"));
        String fechaf = request.getParameter(Comunes.toMD5(sesion + "param-ff:")) == null ? "" : request.getParameter(Comunes.toMD5(sesion + "param-ff:"));
        String identificador = request.getParameter(Comunes.toMD5(sesion + "param-id:")) == null ? "" : request.getParameter(Comunes.toMD5(sesion + "param-id:"));
        boolean mostrarTodo = false;
        boolean hayFiltro = !(fechai.isEmpty() && fechaf.isEmpty() && identificador.isEmpty());
        if(request.getParameter("todos") == null){
            mostrarTodo = false;
        }else{
            mostrarTodo = request.getParameter("todos").equals("S");
        } 
        hayFiltro = hayFiltro & !mostrarTodo;
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.Vista de documentos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/regional.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/timepicker.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 75.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae }
            .tr_non{background-color: whitesmoke}
            input.text { margin-bottom:12px; width:90px; padding: .4em; }
            #persona{width: 200px;}
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <script>
            $(function() {
                var identificador = $("#identificador"),
                        fechai = $("#fechai"),
                        fechaf = $("#fechaf"),
                        todos = $("#todos"),
                        rfc = $("#rfc"),
                elementos = "";
                $("input[type=checkbox]").on("click", countChecked);
                function countChecked() {
                    var n = $("input:checked").length;
                    //alert( n + (n === 1 ? " is" : " are") + " checked!" );
                    var elemento = $(this).attr("elemento");
                    if ($(this).attr("checked") === "checked") {
                        elementos += elemento + ",";
                    } else {
                        elementos = elementos.replace(elemento + ",", "");
                    }
                }
                ;

                $(".estatus").button({
                    text: false,
                    icons: {primary: "ui-icon-gear"}
                }).click(function() {
                    $("#cmd").val($(this).attr("cmd"));
                    $("#FORM_EXECUTE_STATUS").submit();
                });

                $("#empresa").click(function() {
                    $("#sortBy").val("e");
                    $("#FORM_SORT").submit();
                });
                $("#fecha").click(function() {
                    $("#sortBy").val("f");
                    $("#FORM_SORT").submit();
                });
                $("#documento").click(function() {
                    $("#sortBy").val("d");
                    $("#FORM_SORT").submit();
                });
                $("#estatus").click(function() {
                    $("#sortBy").val("s");
                    $("#FORM_SORT").submit();
                });

                $("#fechai").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    defaultDate: "0D",
                    changeMonth: true,
                    numberOfMonths: 2,
                    regional: "es",
                    maxDate: "0D",
                    onClose: function(selectedDate) {
                        $("#fechaf").datepicker("option", "minDate", selectedDate);
                    }
                });
                $("#fechaf").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    regional: "es",
                    defaultDate: "+1w",
                    changeMonth: true,
                    numberOfMonths: 2,
                    maxDate: "0D",
                    onClose: function(selectedDate) {
                        $("#fechai").datepicker("option", "maxDate", selectedDate);
                    }
                });
                $("#persona").autocomplete({
                    source: '../ws/personas/registrar.do?tipo=<%=tipo%>&callback=?',
                    minLength: 2,
                    select: function(event, ui) {
                        ui.item ? $('#identificador').val(ui.item.id) : $('#identificador').val("");
                    }
                });
                $("#rfc").autocomplete({
                    source: '../ws/personas/verrfc.do?tipo=<%=tipo%>&callback=?',
                    minLength: 2,
                    select: function(event, ui) {
                        ui.item ? $('#identificador').val(ui.item.id) : $("#identificador").va("");
                    }
                });
                $("#aplicar").button({
                    text : true,
                    icons: {primary: "ui-icon-search"}
                });
                $("#aplicar").button().click(function() {
                    todos.val(".");
                    //$("#filtro").val("-");
                    $("#FORM_FILTER").submit();
                    return false;
                });
                $("#quitar").button({
                    text: true,
                    icons: {primary: "ui-icon-circle-plus"}
                });
                $("#quitar").button().click(function() {
                    identificador.val("*");
                    fechai.val("");
                    fechaf.val("");
                    rfc.val("");
                    todos.val("S");
                    $("#FORM_FILTER").submit();
                    return false;
                });
                $("#descargar").button({
                    text: true,
                    icons: {primary:"ui-icon-circle-check"}
                });              
                $("#descargar_pdf").button({
                    text: true,
                    icons: {primary:"ui-icon-document"}
                });
                $("#descargar_xml").button({
                    text: true,
                    icons: {primary:"ui-icon-mail-open"}
                });
                $("#descargar").button().click(function() {
                    $("#tipo_documento").val(3);
                    if (elementos.length > 0) {
                        //if (confirm("Se decargaran los documentos seleccionados, esta seguro de continuar?")) {
                        $("#documentos").val(elementos);                            
                            //window.open("../ws/files/xdownload.do?documentos", "Download ZipFile");
                        //}
                    } else {
                        //if(confirm("Se descargaran todos los documentos de la lista, estas seguro de continuar?")){
                        $("#documentos").val("*");                           
                        //}
                    }
                    $("#FORM_DOWNLOAD").submit();

                });

                $("#descargar_xml").button().click(function() {
                    $("#tipo_documento").val(1);
                    if (elementos.length > 0) {
                        $("#documentos").val(elementos);                            
                    }else{
                        $("#documentos").val("*");                            
                    }
                    //if (confirm("Se decargaran todos los XMLs del listado, esta seguro de continuar?")) {
                    $("#FORM_DOWNLOAD").submit();
                    //}
                });
                
                $("#descargar_pdf").button().click(function (){
                    $("#tipo_documento").val(2);
                    //if (confirm("Se decargaran todos los PDFs del listado, esta seguro de continuar?")) {
                    if(elementos.length > 0) {
                        $("#documentos").val(elementos);
                    } else {
                        $("#documentos").val("*");                            
                    }
                    $("#FORM_DOWNLOAD").submit();
                    //}
                });
                
                $(".eliminar").button({
                    text: false,
                    icons: {primary: "ui-icon-trash"}
                });
                $(".eliminar").button({
                    text: false,
                    icons: {primary: "ui-icon-trash"}
                }).click(function() {
                    if (confirm("Una vez eliminado los archivos no se podr�n recuperar, quieres continuar?")) {
                        identificador.val($(this).attr("codigo"));
                        $.ajax({
                            type: "POST",
                            dataType: "text",
                            data: {cmd: "<%=Comunes.toMD5("eliminar-" + session.getId())%>" + identificador.val()},
                            async: false,
                            url: "../ws/files/upload.do",
                            success: function(data) {
                                updateTips(data);
                                alert(data);
                                location.reload();
                            },
                            error: function(data) {
                                updateTips(data);
                            }
                        });
                        return false;
                    }
                });
                $("#primero").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-first"}
                });
                $("#anterior").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-prev"}
                });
                $("#siguiente").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-next"}
                });
                $("#ultimo").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-end"}
                });
            });
        </script>
    </head>
    <body>
        <%
            if (seguridad == null || session.isNew()) {
        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%} else {
            String mensaje = "";
            Connection conexion = null;
            boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
            try {
        %>
        <div>
            <p>
            <form id="FORM_SORT" method="POST">
                <input type="hidden" id="sortBy" name="sortBy" value="" />
            </form>
            <form id="FORM_FILTER" method="POST">
                <input type="hidden" name="pagina" value="1" />
                <table cellspacing="5px" >
                    <tr>                        
                        <th colspan="2" align="center">Per&iacute;odo de fechas</th>
                        <th align="center">Empresa</th>
                        <th align="center">RFC</th>
                        <th></th>
                    </tr>
                    <tr>
                        <td><input class="ui-corner-all text" name="<%=Comunes.toMD5(sesion + "param-fi:")%>" type="text" id="fechai" /></td>
                        <td><input class="ui-corner-all text" name="<%=Comunes.toMD5(sesion + "param-ff:")%>" type="text" id="fechaf" /></td>
                        <td>
                            <input type="text" id="persona" class="text ui-corner-tl" autocomplete="true" />
                            <input type="hidden" id="identificador" name="<%=Comunes.toMD5(sesion + "param-id:")%>" maxlength="32" value="" style="width: 0px"/>
                            <input type="hidden" id="todos" name="todos" value="" />
                        </td>
                        <td>
                            <input type = "text" id="rfc" name="<%=Comunes.toMD5(sesion + "param-rfc:")%>" maxlength="14" value="" autocomplete="true" class="text ui-corner-tl" />
                        </td>
                        <td>
                            <a id="aplicar">Buscar</a>
                            <a id="quitar">Todos</a>
                        </td>
                    </tr>
                </table>
            </form>
            <form id="FORM_EXECUTE_STATUS" method="POST" action="documentose.jsp">
                <input type="hidden" name="cmd" id="cmd" value="<%=Comunes.toMD5(session.getId().concat("--"))%>" />                                
            </form>
        </p>
    </div>
    <div id="tabla" style="border: 2px #0078ae double">
        <table style="border: 1px #ccc ridge;">
            <tr class="tr_cab">
                <th style="width:25px">&nbsp;</th>
                <th id="empresa" width="200px"><a id="sort_empresa" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;&nbsp;Empresa</th>
                <th id="fecha" width="70px"><a id="sort_fecha" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;&nbsp;Fecha</th>
                <th id="documento" width="90px"><a id="sort_documento" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;&nbsp;Factura</th>
                <th width="100px">Documentos</th>
                <th id="estatus" width="90px"><a id="sort_estatus" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;&nbsp;Estatus</th>
                <th width="30px">&nbsp;</th>
                <th width="30px">&nbsp;</th>
            </tr>
            <%
                conexion = Conexion.getConexion();
                List<Documento> listado = null;
                String empresa = "";
                if (session.getAttribute(tipo) != null && !hayFiltro && !mostrarTodo) {
                    listado = (List<Documento>) session.getAttribute(tipo);
                } else {
                    if (isOwner) {
                        if (identificador.trim().length() == 0) {
                            empresa = "*";
                        } else {
                            empresa = identificador;
                        }
                        listado = Persona.listadoDocumentos(empresa, tipo, fechai, fechaf, sesion, conexion);
                    } else {
                        empresa = (String) session.getAttribute("persona");
                        listado = Persona.listadoDocumentos(empresa, tipo, fechai, fechaf, sesion, conexion);
                    }
                    session.setAttribute(tipo, listado);
                    pagina = 1;
                }
                if(listado.size()<=0) throw new Exception("Imposible listar los documentos, no se localizaron registros");
                if (request.getParameter("sortBy") != null) {
                    if (((String) request.getParameter("sortBy")).equals("f")) {
                        Collections.sort(listado, new DocumentoComparatorPorFecha());
                    } else if (((String)request.getParameter("sortBy")).equals("e")) { // empresa
                        Collections.sort(listado, new DocumentoComparatorPorEmpresa());
                    } else if(((String)request.getParameter("sortBy")).equals("s")) { //estatus
                        Collections.sort(listado, new DocumentoComparatorPorEstatus());                    
                    }  else if(((String)request.getParameter("sortBy")).equals("d")){ // documento
                        Collections.sort(listado, new DocumentoComparatorPorDocumento());                    
                    }
                } else {
                    // no hay parametros de ordenamiento
                }
                // --> cafaray 170720 para poder controlar el numero de elementos en el despliegue
                //Iterator<Documento> documentos = listado.iterator();
                // <--
                int cont = 0;
                StringBuilder docsDownload = new StringBuilder();
                int total = listado.size();
                int paginas = (total / paginacion) + 1;
                pagina = (pagina <= 1)?1:pagina;
                int from = (pagina-1) * paginacion;
                int to = (pagina == paginas)?total-1:(pagina * paginacion) - 1;
                
                List<Documento> documentos = listado.subList(from, to);
                for (Documento doc : documentos) {

                    cont++;
                    //Documento doc = documentos.next();
                    String[] files = new String[3];
                    int i = 0;
                    files[0] = "-";
                    files[1] = "-";
                    files[2] = "-";
                    String imagePdf = "<img src=\"../resources/images/pdf-icono.png\" height=\"26\" border=\"0\" />";
                    String imageXml = "<img src=\"../resources/images/xml-icono.png\" height=\"26\" border=\"0\" />";
                    String imageDef = "<img src=\"../resources/images/previa.png\" height=\"26\" border=\"0\" />";
                    StringBuilder locacion = new StringBuilder(application.getInitParameter("fileLocation"));
                    String rfc = (String) session.getAttribute("rfc");
                    locacion.append(rfc).append("/").append(doc.getPersona()).append("/");
                    for (String file : doc.archivos()) {
                        StringBuilder path = new StringBuilder(locacion.toString());
                        String image = file.toUpperCase().endsWith(".PDF") ? imagePdf : imageDef;
                        image = file.toUpperCase().endsWith(".XML") ? imageXml : image;
                        files[i++] = "<a target=\"_blank\" href=\"../vistaprev?archivo=" + path.append(file.toLowerCase()).toString() + "\" title = \"" + file + "\">" + image + "</a>";
                        //docsDownload.append(path.toString()).append(",");
                    }

            %>
            <tr class="<%=cont % 2 == 0 ? "tr_par" : "tr_non"%>">
                <td><input type="checkbox" class="selecciona_elemento" elemento = "<%=doc.getIdentificador()%>" /></td>
                <td><%=doc.getEmpresa()%></td>
                <td><%=Comunes.formatoFecha(doc.getFecha(), 1)%></td>
                <td title="<%=doc.getObservaciones()%>"><%=doc.getTitulo()%></td>
                <td>
                    <table>
                        <tr>
                            <td width="30px"><%=files[0]%></td>
                            <td width="30px"><%=files[1]%></td>
                            <td width="30px"><%=files[2]%></td>
                        </tr>
                    </table>
                </td>
                <td style="text-align: center"><%=doc.getDsEstatus()%></td>
                <td style="text-align: center"><a class="estatus" cmd="<%=Comunes.toMD5("documento.cambiar-estatus".concat(session.getId())).toUpperCase().concat(doc.getIdentificador())%>">Avanzar</a></td>
                <td style="text-align: center">
                    <%
                        boolean procesado = (doc.getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_APROBADA.getIndicador())
                                || doc.getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PAGADA.getIndicador())
                                || doc.getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PROGRAMADA.getIndicador())
                                || doc.getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PROCESADA.getIndicador()));
                        if ((!seguridad.seguridadProveedor().contains("x"))) {
                            out.print("&nbsp;");
                        } else {
                            if (procesado) {
                                out.print("&nbsp;");
                            } else {
                    %>
                    <a title="Eliminar" class="eliminar" codigo="<%=doc.getIdentificador()%>" >Eliminar</a>
                    <%
                            }
                        }
                    %>
                </td>
            </tr>
            <%
                }
            %>
            <tfoot>
            <td colspan="8" style="text-align: center">
                P&aacute;gina <%=pagina%> de <%=paginas%> 
                <br />
                <a id ="primero" href="javascript:ver('vistadoce.jsp?tipo=P&pagina=1');">&lt;&lt;</a>&nbsp;&nbsp;
                <a id ="anterior" href="javascript:ver('vistadoce.jsp?tipo=P&pagina=<%=(pagina - 1 < 0 ? 1 : pagina - 1)%>');">&lt;</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id ="siguiente" href="javascript:ver('vistadoce.jsp?tipo=P&pagina=<%=(pagina + 1 > paginas ? 1 : pagina + 1)%>');">&gt;</a>
                &nbsp;&nbsp;<a id ="ultimo" href="javascript:ver('vistadoce.jsp?tipo=P&pagina=<%=paginas%>');">&gt;&gt;</a>               
            </td>
            </tfoot>
        </table>

    </div>
    <%if (((String) session.getAttribute("propietario")).equals("S") && seguridad.seguridadProveedor().contains("d")) {%>
    <div id="div_descargar">
        <form id="FORM_DOWNLOAD" method="POST" target="_blank" action="../ws/files/xdownload.do">
            <input type="hidden" id = "documentos" name="documentos" value="*" />
            <input type="hidden" id = "mitipo" name="tipo" value="<%=tipo%>" />
            <input type="hidden" value="<%=Comunes.toMD5("downloadFiles-" + session.getId())%>" name="cmd" />
            <input type="hidden" id = "tipo_documento" name = "tipo_documento" value="<%=Comunes.toMD5("downloadFiles-3" + session.getId())%>" />
            <a id="descargar" style="visibility: visible">Descargar XML+PDF</a>
            <a id="descargar_xml" style="visibility: visible">Descargar XML</a>
            <a id="descargar_pdf" style="visibility: visible">Descargar PDF</a>
        </form>
    </div>
    <%}%>
    <%
            } catch (SQLException sqle) {
                mensaje = sqle.getSQLState().equals("0") ? sqle.getMessage() : "Excepci�n al realizar el proceso. " + sqle.getSQLState() + "-" + sqle.getErrorCode();
                Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), sqle, (String) session.getAttribute("usuario"));
            } catch (Exception e) {
                mensaje = "Excepci�n al realizar el proceso. " + e.getMessage();
                Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) session.getAttribute("usuario"));
            } finally {
                if (conexion != null) {
                    try {
                        if (!conexion.isClosed()) {
                            conexion.close();
                        }
                    } catch (SQLException sqle) {
                        //NOTHING TO DO
                    }
                }
                if (mensaje.length() > 0) {
                    out.println(String.format("<script>alert(\"%s\")</script>", mensaje));
                }
            }
        }
    %>
    <script>
        function ver(url) {
            window.parent.frames[1].location = url;
        }
    </script>
</body>

</html>
