<%@page import="com.ftc.gedoc.bo.impl.DocumentoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.DocumentoBO"%>
<%@page import="com.ftc.modelo.Documento"%>
<%@page import="com.ftc.modelo.PeriodoRegistro"%>
<%@page import="com.ftc.modelo.PeriodoCabecera"%>
<%@page import="com.ftc.modelo.Periodo"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.Ajuste de periodo :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            label, select{ display:block; }
            input.text { margin-bottom:20px; width:55%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
            span{font-family: inherit}
            .fecha{width: 100px;border-bottom: 1px solid chartreuse;border-top: 1px solid chartreuse; display: inline-table}
            .importe{width: 90px;border-bottom: 1px solid chartreuse;border-top: 1px solid chartreuse; display: inline-table;text-align: right}
            .impuesto{width: 100px;border-bottom: 1px solid chartreuse;border-top: 1px solid chartreuse; display: inline-table;text-align: right}
            .tipo{width: 130px;border-bottom: 1px solid chartreuse;border-top: 1px solid chartreuse; display: inline-table}
            .documento{width: 200px;border-bottom: 1px solid chartreuse;border-top: 1px solid chartreuse; display: inline-table}
            #cabecera_botones{width: 100%; text-align: right; background: #666;}
        </style>    
        <script>
            $(function() {
                $("#accordion").accordion({
                    heightStyle: "content"
                });
//                $(".autoriza").change(function(event) {
//                    event.preventDefault();
//                    var cmd = $(this).attr("cmd");
//                    var autoriza = $(this).val();
//                    $.ajax({
//                        url: '../ws/expenditure/record.do',
//                        type: 'POST',
//                        data: {cmd: cmd, valor: autoriza},
//                        cache: false,
//                        dataType: "text",
//                        async: true,
//                        success: function(data) {
//                            console.info(data);
//                        },
//                        error: function(data) {
//                            alert(data);
//                        }
//                    });
//                });
                
//                $(".autoriza").button({
//                    Text: true,
//                    icons: {primary: "ui-icon-disk"}
//                }).click(function(event){
//                    var cmd = $(this).attr("cmd");
//                    var autoriza = $("#"+cmd).val();
//                    alert("Autoriza a: "+autoriza);
//                    $.ajax({
//                        url: '../ws/expenditure/record.do',
//                        type: 'POST',
//                        data: {cmd: cmd, valor: autoriza},
//                        cache: false,
//                        dataType: "text",
//                        async: true,
//                        success: function(data) {
//                            alert(data);
//                            console.info(data);                            
//                        },
//                        error: function(data) {
//                            alert(data);
//                        }
//                    });
//                });
                
                $("#regresar").button({
                    text: true,
                    icons: {primary: "ui-icon-circle-arrow-w"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    location.replace("index.jsp");
                });
                $(".cerrar_registro").button({
                    text: true,
                    icons: {primary: "ui-icon-locked"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    if (confirm("Estas seguro de cerrar el registro de gastos, de ser as� no se podr� modificar ni recuperar.")) {
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: cmd},
                            cache: false,
                            dataType: "text",
                            async: true,
                            success: function(data) {
                                alert(data);
                                console.info(data);
                                location.reload();
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
                });
                $("#aplicar_ajuste").button({
                    text: true,
                    icons: {primary: "ui-icon-transferthick-e-w"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    if (confirm("Estas seguro de cerrar el ajuste del periodo, una vez realizado no se podr� modificar.")) {
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: cmd},
                            cache: false,
                            dataType: "text",
                            async: true,
                            success: function(data) {
                                alert(data);
                                console.info(data);
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

            Object seguridad = session.getAttribute("codsec");
            String persona = (String) session.getAttribute("persona");
            //String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script>
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {

            String cmd = request.getParameter("cmd") == null ? "" : request.getParameter("cmd").toLowerCase();
            String tipoGasto = "";
            String desTipoGasto = "";

            try {
                PeriodoBo bo = new PeriodoBOImpl();
                Periodo actual, hoy;
                String idPeriodo;
                //tipoGasto = cmd.substring(Comunes.toMD5("cabecera.detalle:".concat(session.getId())).length(),Comunes.toMD5("cabecera.detalle:".concat(session.getId())).length()+1);
                if (cmd.startsWith(Comunes.toMD5("xpenditures.ajustar-periodo:".concat(session.getId())))) {
                    idPeriodo = cmd.substring(Comunes.toMD5("xpenditures.ajustar-periodo:".concat(session.getId())).length());
                    actual = bo.obtenerPeriodoPorId(idPeriodo);
                } else {
                    actual = bo.actual();
                    idPeriodo = actual.getIdentificador();

                }
                hoy = bo.actual();
                if (!hoy.getIdentificador().equals(actual.getIdentificador())) {
                    if (actual != null) {
                        List<PeriodoCabecera> cabeceras = bo.listaCabecerasCompletas(actual.getIdentificador()); //, tipoGasto); --> cafaray 211217 para leer todos los tipos de gasto
        %>        
        <h2>Detalle de ajuste.</h2>
        <p id="cabecera_botones">
        <a id="aplicar_ajuste" cmd ="<%=Comunes.toMD5("xpenditures.ajustar-periodo-".concat(request.getSession().getId())).toUpperCase().concat(idPeriodo) %>">Aplica ajuste</a>
        <a id="regresar" cmd="<%=Comunes.toMD5("v-Cifra" + session.getId()).toUpperCase().concat(idPeriodo)%>">Regresar</a>
        </p>
        <div id="accordion">
            <%
                DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                for (PeriodoCabecera cabecera : cabeceras) {
                    
                    double importe = bo.totalRegistros(cabecera.getRegistros());
                    double totalAutorizado = bo.totalAutorizado(cabecera.getRegistros());
                    if (importe>0 || totalAutorizado>0){
                    if (tipoGasto.equals("t")) {
                        desTipoGasto = "Vi&aacute;ticos de vendedores";
                    } else if (tipoGasto.equals("h")) {
                        desTipoGasto = "Caja chica";
                    } else if (tipoGasto.equals("a")) {
                        desTipoGasto = "Agente aduanal";
                    }
                    String titulo = String.format("%s. Total de gastos: %s", cabecera.getAsociadoA(), decimalFormat.format(importe));
                    String cerrar = "";
                    if (cabecera.getEstatus().equals("A")) {
                        cerrar = String.format("<a class=\"cerrar_registro\" cmd=\"%s\">Cerrar Registro</a>",
                                (Comunes.toMD5("xpenditures.cerrar-registro-ajuste-".concat(session.getId())).concat(cabecera.getIdentificador())).toUpperCase());
                    }
            %>
            <h3><%=cerrar%>:&nbsp;&nbsp;&nbsp;&nbsp;<%=titulo%></h3>
            <div>
                <!-- <p>
                    <b>Autorizados al momento:</b> $ <%=decimalFormat.format(totalAutorizado)%>&nbsp;&nbsp;&nbsp;&nbsp;
                    <b>Pendientes de autorizar:</b> $ <%=decimalFormat.format(importe - totalAutorizado)%>
                </p> -->
                <ul style="list-style: none">
                    <%
                        for (PeriodoRegistro registro : cabecera.getRegistros()) {
                            String strDocumento = registro.getNota();
                            if (strDocumento.startsWith("XML") && registro.getEvidencia() != null) {
                                Documento documento = new Documento();
                                DocumentoBO boDoc = new DocumentoBOImpl();
                                documento = boDoc.findById(registro.getEvidencia());
                                if (documento != null) {
                                    String imagePdf = "<img src=\"../resources/images/pdf-icono.png\" height=\"26\" border=\"0\" />";
                                    String imageXml = "<img src=\"../resources/images/xml-icono.png\" height=\"26\" border=\"0\" />";
                                    String imageDef = "<img src=\"../resources/images/previa.png\" height=\"26\" border=\"0\" />";
                                    StringBuilder locacion = new StringBuilder(application.getInitParameter("fileLocation"));
                                    String rfc = (String) session.getAttribute("rfc");
                                    locacion.append(rfc).append("/").append(documento.getPersona()).append("/");
                                    String[] files = documento.getArchivos() != null ? documento.getArchivos().split(",") : new String[0];
                                    for (String file : files) {
                                        String image = file.toUpperCase().endsWith(".PDF") ? imagePdf : imageDef;
                                        image = file.toUpperCase().endsWith(".XML") ? imageXml : image;
                                        file = "<a target=\"_blank\" href=\"../control/vistaprev?archivo=" + locacion.append(file.toLowerCase()).toString() + "\" title = \"" + file + "\">" + image + "</a>";
                                        strDocumento.concat(file);
                                    }
                                } else {
                                    strDocumento = "Archivo no encontrado";
                                }
                            } else {
                                strDocumento = registro.getNota();
                            }

                    %>
                    <li>
                        <span class="fecha"><%=dateFormat.format(registro.getFecha())%></span>
                        <span class="importe"><%=decimalFormat.format(registro.getImporte())%></span>
                        <span class="impuesto"><%=decimalFormat.format(registro.getImpuesto())%></span>
                        
                        <span class="tipo"><%=registro.getTipo()%></span>
                        <span class="documento"><%=strDocumento%></span>
                        <!--
                        <b>Autorizaci&oacute;n de: </b>
                        <input type="text" class="text ui-corner-all"  disabled 
                               id="<%=Comunes.toMD5("periodoRegistro-update:autoriza".concat(session.getId())).concat(registro.getRegistro()).toUpperCase()%>" 
                               value="<%=registro.getAutoriza() == null || registro.getAutoriza().isEmpty()? "ok - cierre.ajuste" : registro.getAutoriza()%>" />
                        <a class="autoriza" href="#" cmd="<%=Comunes.toMD5("periodoRegistro-update:autoriza".concat(session.getId())).concat(registro.getRegistro()).toUpperCase() %>">Guardar</a>
                        -->
                    </li>
                    <%
                        
                        }
                    %>
                </ul>
            </div>


            <%
                    }
                }
            %>
        </div>
        <%

                        } else {
                            out.print("<p>No hemos localizado un per&iacute;odo valido con el identificador proporcionado. Por favor reporte a sistemas.</p>");
                        }
                    } else {
                        out.print("<p>Es imposible realizar ajustes al per&iacute;odo actual, ya que este se encuentra abierto a&uacute;n.</p>");
                    }
                } catch (GeDocBOException e) {
                    out.print(e.getMessage());
                } catch (NullPointerException e) {
                    out.print("Al parecer se cerro la sesi&oacute;n, es necesario que vuelvas a iniciar sesi&oacute;n.");
                }
            }
        %>
    </body>
</html>
