<%@page import="com.ftc.gedoc.utiles.Documento"%>
<%@page import="com.ftc.gedoc.utiles.TipoComprobante"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoCabecera"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoRegistro"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.registro de gastos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            label, file{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            input.file { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <%! 
        String idCabecera = "";
        private Map<String, String> obtieneListadoTipo(String tipoGasto){
            PeriodoBo bo = new PeriodoBOImpl();
            try{
                Map<String, String> listado = bo.listadoTipoComprobante(tipoGasto);
                return listado;
            }catch(GeDocBOException e){
                return new TreeMap<String, String>();
            }
        }
        %>
         <%
              Object seguridad = session.getAttribute("codsec");
            String persona = (String) session.getAttribute("persona");
            String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {
            
            
            String tipoGasto = request.getParameter("tipo_gasto") == null ? "" : request.getParameter("tipo_gasto");
            String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");
            
            try {
                boolean esConsulta = false;
                if (cmd.startsWith(Comunes.toMD5("edita-cabecera" + session.getId()).toUpperCase()) 
                || cmd.startsWith(Comunes.toMD5("consulta-cabecera" + session.getId()).toUpperCase())) {
                    esConsulta = cmd.startsWith(Comunes.toMD5("consulta-cabecera" + session.getId()).toUpperCase());
                    if(!esConsulta){
                        idCabecera = cmd.substring(Comunes.toMD5("edita-cabecera" + session.getId()).length()).toLowerCase();
                    }else{
                        idCabecera = cmd.substring(Comunes.toMD5("consulta-cabecera" + session.getId()).length()).toLowerCase();
                    }
                    PeriodoBo bo = new PeriodoBOImpl();
                    PeriodoCabecera pc = bo.encuentraCabeceraPorId(idCabecera);
                    List<PeriodoRegistro> registros = bo.listaRegistros(idCabecera);
                    cmd = Comunes.toMD5("actualizaNota-" + session.getId()).concat(pc.getIdentificador());
        %>
        <script>
            $(function() {
                var documento = $("#documento"),
                        importe = $("#importe"),
                        impuesto = $("#impuesto"),
                        fecha = $("#fecha"),
                        tipo = $("#tipo"),                        
                        cmd = $("#cmd"),
                        allFields = $([]).add(documento).add(importe).add(impuesto).add(fecha).add(tipo),
                        tips = $(".validateTips");
                $("#registraNota").button().click(function(event) {
                    event.preventDefault();
                    $("#dialog-form-nota").dialog("open");
                });
                $("#cargaMasiva").button().click(function(event) {
                    event.preventDefault();
                    cmd.val($(this).attr("cmd"));
                    $('#FORM_SUBMIT_EXPENDITURES').attr('action', 'upload.jsp');
                    $('#FORM_SUBMIT_EXPENDITURES').attr('method', 'post');                    
                    $("#FORM_SUBMIT_EXPENDITURES").submit();
                });
                $(".elimina_documento").button({
                    text: false,
                    icons: {primary: "ui-icon-trash"}
                }).click(function(event) {
                    event.preventDefault();
                    <%if(esConsulta){ %>
                        alert("Esta operaci�n no esta permitida en modo consulta.");
                        <%} else { %>
                    if (confirm("Esta seguro de eliminar este comprobante?")) {
                        var cmd = $(this).attr("cmd");
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd:cmd},
                            cache: false,
                            dataType: "text",
                            async: false,
                            success: function(data) {
                                alert(data);
                                cmd.val("<%=Comunes.toMD5("edita-cabecera" + session.getId()).concat(idCabecera).toUpperCase()%>");
                                $("#FORM_SUBMIT_EXPENDITURES").submit();
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
                    <%}%>
                });
                $("#cancelar").button().click(function(event) {
                    event.preventDefault();
                    location.replace("resume.jsp?tipo_gasto="+$(this).attr("tipo_gasto"));
                });
                $("#dialog-form-nota").dialog({
                    autoOpen: false,
                    height: 420,
                    width: 350,
                    modal: true,
                    buttons: {
                        "Registrar": function() {                            
                            //alert($("#cmd").val());
                            $.ajax({
                                type: "POST",
                                dataType: "text",
                                data: {documento: documento.val(), 
                                    importe: importe.val(), 
                                    impuesto: impuesto.val(), 
                                    fecha: fecha.val(), 
                                    tipo: tipo.val(),                                     
                                    cmd: cmd.val()},
                                async: false,
                                url: "../ws/expenditure/record.do",
                                success: function(data) {
                                    console.info(data);
                                    //alert(data);
                                    documento.val("");
                                    importe.val("");
                                    impuesto.val("");
                                    cmd.val("<%=Comunes.toMD5("edita-cabecera" + session.getId()).concat(idCabecera).toUpperCase()%>");
                                    $("#FORM_SUBMIT_EXPENDITURES").submit();
                                },
                                error: function(data) {
                                    alert(data);
                                }
                            });
                            return false;
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    }
                });
                $("#fecha").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    defaultDate: "0D",
                    changeMonth: true,
                    numberOfMonths: 1,
                    regional: "es",
                    dateFormat:"dd-mm-yy",
                    maxDate: "0D"
                });
            });
        </script>
    </head>
    <body>
       
        <h2>Registro de gastos</h2>
        <form id="FORM_SUBMIT_EXPENDITURES">
            Asignados a: <%=pc.getAsociadoA()%>
            <input type="hidden" id="cmd" name="cmd" value="<%=cmd.toUpperCase() %>" />
            <input type="hidden" id="tipo_gasto" name="tipo_gasto" value="<%=tipoGasto %>" />
           </form>
            <table cellspacing="1" cellpadding="5" style="width:790px;border: 1px #ccc solid;">
                <tr>
                    <th style="width: 90px;">Fecha</th>
                    <th style="width: 90px;">Importe</th>
                    <th style="width: 90px;">Impuesto</th>                 
                    <th style="width: 120px;">Tipo</th>
                    <th style="width: 140px;">Autoriza</th>
                    <th>Documento</th>
                    <th style="width: 40px;">&nbsp;</th>
                </tr>
<!--            </table>
            <table cellspacing="1" cellpadding="5" style="width:790px;height: 400px; border: 1px #ccc solid;">-->
                <%
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                    for(PeriodoRegistro registro: registros){
                %>
                <tr style="height: 24px">
                    <td style="width: 90px;"><%=dateFormat.format(registro.getFecha()) %></td>
                    <td style="width: 90px;text-align: right;"><%=decimalFormat.format(registro.getImporte()) %></td>
                    <td style="width: 90px;text-align: right;"><%=decimalFormat.format(registro.getImpuesto()) %></td>
                    <td style="width: 120px;"><%=TipoComprobante.getDescripcion(registro.getTipo())%></td>
                    <td style="width: 140px;"><%=registro.getAutoriza()==null||registro.getAutoriza().isEmpty()?"No autorizado":registro.getAutoriza() %></td>
                    <%
                    if(registro.getEvidencia()!=null && !registro.getEvidencia().isEmpty()){
                        Documento documento = new Documento();
                        documento = documento.findById(registro.getEvidencia());
                        String imagePdf = "<img src=\"../resources/images/pdf-icono.png\" height=\"26\" border=\"0\" />";
                        String imageXml = "<img src=\"../resources/images/xml-icono.png\" height=\"26\" border=\"0\" />";
                        String imageDef = "<img src=\"../resources/images/previa.png\" height=\"26\" border=\"0\" />";
                        StringBuilder locacion = new StringBuilder(application.getInitParameter("fileLocation"));
                        String rfc = (String) session.getAttribute("rfc");
                        locacion.append(rfc).append("/").append(documento.getPersona()).append("/");
                        String[] archivos = documento.getArchivos()==null?new String[0]:documento.getArchivos().split(",");
                        out.print("<td>");
                        for (String archivo : archivos) {
                            StringBuilder path = new StringBuilder(locacion.toString());
                            String image = archivo.toUpperCase().endsWith(".PDF") ? imagePdf : imageDef;
                            image = archivo.toUpperCase().endsWith(".XML") ? imageXml : image;
                            out.print(String.format("<a target=\"_blank\" href=\"../vistaprev?archivo=%s\" title = \"%s\">%s</a>", path.append(archivo.toLowerCase()).toString(),archivo,image));
                        }
                        out.print("</td>"); 
                    } else {
                    %>
                     <td><%=registro.getNota()%></td>
                     <%}%>
                    <td style="width: 40px;"><a class="elimina_documento" id="eliminar" cmd="<%=Comunes.toMD5("xdoc-remove"+session.getId()).concat(registro.getRegistro()).toUpperCase() %>"><img src="../resources/images/trash.png" height="16" border="0" title="Eliminar documento de gasto" /></a></td>
                </tr>
                <%
                    }
                %>                
            </table>
            <table cellspacing="1" cellpadding="5" style="width:790px;border: 1px #ccc solid;">
                <tr>
                    <td colspan="6" style="text-align: right">
                        <%if(!esConsulta){ %>
                        <a id="cargaMasiva" cmd ="<%=(Comunes.toMD5("cargaMasiva-"+session.getId())).concat(idCabecera).toUpperCase() %>">Carga masiva</a>
                        <a id="registraNota">Registrar Nota</a>
                        <%}%>
                        <a id="cancelar" tipo_gasto = "<%=tipoGasto%>">Regresar</a>
                    </td>                        
                </tr>
            </table>
        
        <div id="dialog-form-nota" title="Crear nuevo registro.">
            <!--  <form> -->
                <p class="validateTips"></p>
                <fieldset>
                    <input type="hidden" name="tipo_gasto" id="tipo_gasto" value="<%=tipoGasto%>" />
                    <label for="Documento">Documento</label>
                    <input type="text" name="documento" id="documento" value="" class="text ui-widget-content ui-corner-all" />
                    <label for="importe">Importe</label>
                    <input type="text" name="importe" id="importe" class="text ui-widget-content ui-corner-all text-right" />
                    <label for="impuesto">Impuestos</label>
                    <input type="text" name="impuesto" id="impuesto" value="0" disabled="disabled" class="text ui-widget-content ui-corner-all text-right" />
                    <label for="fecha">Fecha</label>
                    <input type="text" readonly="true" name="fecha" id="fecha" class="text ui-widget-content ui-corner-all" style="width: 85%" >
                    <label for="tipo">Tipo</label>
                    <select name="tipo" id="tipo" class="text ui-widget-content ui-corner-all">
                        <%
                        Map<String, String> listado = obtieneListadoTipo(tipoGasto);
                        Set<String> keys = listado.keySet();
                        for(String key:keys){
                            out.print(String.format("\t\t\t<option value= \"%s\">%s</option>%n",key, listado.get(key)));
                        }
                        %>
                    </select>                    
                </fieldset>
            <!-- </form> -->
        </div>

        <%           
                }
            } catch (GeDocBOException e) {
                out.print(e.getMessage());
            } catch(NullPointerException e){
                %>
                <script>                    
                    alert("Al parcer la sesi�n ha expirado.");
                    window.getParent().location.replace("default.jsp");
                </script>
                <%
            }
            }
        %>
    </body>
</html>
