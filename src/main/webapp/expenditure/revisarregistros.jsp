<%@page import="com.ftc.gedoc.bo.impl.DocumentoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.DocumentoBO"%>
<%@page import="com.ftc.modelo.Documento"%>
<%@page import="com.ftc.modelo.PeriodoRegistro"%>
<%@page import="com.ftc.modelo.PeriodoCabecera"%>
<%@page import="com.ftc.gedoc.utiles.TipoComprobante"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.revisar registro de gastos :::</title>
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
            span{font-family: inherit}
            .fecha{width: 100px; display: inline-table; }
            .importe{width: 90px; display: inline-table;text-align: right}
            .impuesto{width: 100px; display: inline-table;text-align: right}
            .tipo{width: 130px; display: inline-table; text-align: center}
            .documento{width: 200px; display: inline-table}
        </style>        
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
            String asociado = "", idPeriodo = "";
            String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd");
        %>
        <script>
            $(function() {
                $("#accordion").accordion({
                    heightStyle: "content"
                });
                $(".autoriza").button({
                    text: false,
                    icons: {primary: "ui-icon-disk"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");

                    var autoriza = $("#autoriza-" + cmd).val();
                    if(autoriza==''){
                        alert("Es necesario especificar 'OK', o bien 'NO OK' para autorizar o rechazar el gasto.");
                    } else {
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: "<%=Comunes.toMD5("periodoRegistro-update:autoriza".concat(request.getSession().getId())).toUpperCase()%>" + cmd, valor: autoriza},
                            cache: false,
                            dataType: "text",
                            async: true,
                            success: function(data) {
                                console.info(data);
                                alert(data);
                                location.reload();
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
                });
                $("#regresar").button({
                    text: true,
                    icons: {primary: "ui-icon-arrowthick-1-w"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    location.replace("revisarcifras.jsp?cmd=" + cmd);
                });
                $(".cerrar_registro").button({
                    text: true,
                    icons: {primary: "ui-icon-locked"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    if (confirm("Estas seguro de cerrar el registro de gastos? De ser as� se autorizaran todos los registros que no han sido autorizados a�n.")) {
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: cmd},
                            cache: false,
                            dataType: "text",
                            async: true,
                            success: function(data) {
                                console.info(data);
                                alert(data);
                                location.reload();
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
                });
            });
        </script>    
    </head>
    <body>
        <%
            try {
                if (cmd.startsWith(Comunes.toMD5(session.getId().concat("detalle-cabecera-gasto-")))) {
                    String params = cmd.substring(Comunes.toMD5(session.getId().concat("detalle-cabecera-gasto-")).length() + 2);
                    idPeriodo = params.substring(0, params.indexOf(":"));
                    asociado = params.substring(params.indexOf(":") + 1);
        %>
        <a id="regresar" cmd="<%=Comunes.toMD5("check-details-records-" + session.getId()).concat(idPeriodo)%>">Regresar</a>
        <h2>Registro de gastos de <%=asociado%></h2>
        <div id="accordion">        

            <%

                PeriodoBo bo = new PeriodoBOImpl();
                List<PeriodoCabecera> cabeceras = bo.listaCabecerasPeriodoAsociado(idPeriodo, asociado);
                for (PeriodoCabecera pc : cabeceras) {
                    List<PeriodoRegistro> registros = bo.listaRegistros(pc.getIdentificador());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                    String cerrar = String.format("<a class=\"cerrar_registro\" cmd=\"%s\">Autorizar Registros</a>",
                            (Comunes.toMD5("xpenditures.cerrar-registro-".concat(session.getId())).concat(pc.getIdentificador())).toUpperCase());
                    String titulo = pc.getDocumento() + " - " + pc.getReferencia();

            %>
            <h3><%=cerrar%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=titulo%></h3>
            <div>
                <ul style="list-style: none">
                    <%
                        int count = 0;
                        for (PeriodoRegistro registro : registros) {
                            String bg = count++%2==0?"#d0d0d0;color:#0078ae;":"whitesmoke";
                            String actualiza = "";
                    %>
                    <li style="border: solid blue 1px; background: <%=bg %>; font-family: monospace; font-size: small;">

                        <span class="fecha"><%=dateFormat.format(registro.getFecha())%></span>
                        <span class="importe"><%=decimalFormat.format(registro.getImporte())%></span>
                        <span class="impuesto"><%=decimalFormat.format(registro.getImpuesto())%></span>
                        <span class="tipo"><%=registro.getTipo().equals("-1")?"N/E":registro.getTipo() %></span>
                        <span class="documento">
                            <%
                                if (registro.getEvidencia() != null && !registro.getEvidencia().isEmpty()) {
                                    Documento documento = new Documento();
                                    DocumentoBO boDoc = new DocumentoBOImpl();
                                    documento = boDoc.findById(registro.getEvidencia());
                                    String imagePdf = "<img src=\"../resources/images/pdf-icono.png\" height=\"26\" border=\"0\" />";
                                    String imageXml = "<img src=\"../resources/images/xml-icono.png\" height=\"26\" border=\"0\" />";
                                    String imageDef = "<img src=\"../resources/images/previa.png\" height=\"26\" border=\"0\" />";
                                    StringBuilder locacion = new StringBuilder(application.getInitParameter("fileLocation"));
                                    String rfc = (String) session.getAttribute("rfc");
                                    locacion.append(rfc).append("/").append(documento.getPersona()).append("/");
                                    String[] archivos = documento.getArchivos() == null ? new String[0] : documento.getArchivos().split(",");

                                    for (String archivo : archivos) {
                                        StringBuilder path = new StringBuilder(locacion.toString());
                                        String image = archivo.toUpperCase().endsWith(".PDF") ? imagePdf : imageDef;
                                        image = archivo.toUpperCase().endsWith(".XML") ? imageXml : image;
                                        out.print(String.format("<a target=\"_blank\" href=\"../vistaprev?archivo=%s\" title = \"%s\">%s</a>", path.append(archivo.toLowerCase()).toString(), archivo, image));
                                    }

                                } else {
                                    out.print(registro.getNota());
                                }
                            %>
                        </span>
                        <span>
                            <%
                                if (registro.getAutoriza() != null && !registro.getAutoriza().isEmpty()) {
                                    actualiza = "&nbsp;";
                                    out.print(registro.getAutoriza());

                                } else {
                                    actualiza = "<a href = \"#\" cmd = \"" + registro.getRegistro() + "\" class = \"autoriza\">Actualizar</a>";
                            %>
                            <input type="text" maxlength="120" class="text-right ui-corner-all" id="autoriza-<%=registro.getRegistro()%>" value="" placeholder="ok para autorizar" />
                            <%
                                }
                            %>
                        </span>

                        <span><%=actualiza%></span>
                    </li>

                    <%
                        }
                    %>                
                </ul>
            </div>
            <%
                }
            %>
        </div>
        <%
            } else {
                out.print("No se ha localizado una acci&oacute;n valida. Revise por favor." + cmd);
            }
        } catch (GeDocBOException e) {
            out.print(e.getMessage());
        } catch (NullPointerException e) {
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
