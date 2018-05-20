<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.utiles.Documento"%>
<%@page import="com.ftc.gedoc.utiles.TipoComprobante"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoRegistro"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>::: ctrldoce.carga de archivos :::</title>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <!-- <script src="../js/uploadFiles.js"></script> -->
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            // label, input, select{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select, input.file { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            ul{list-style: none;}
            ul li{border-bottom: 1px silver solid;}
            #listado{height: 200px;}
            .green {background-color: rgb(126, 201, 126)}

        </style>
        <script>
            $(function() {
                $("#cancelar").button().click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    location.replace("registro.jsp?cmd=" + cmd);
                });

                $(".tipoComprobante").change(function(event) {
                    var cmd = $(this).attr("cmd");
                    var selected = $(this).val();
                    $.ajax({
                        url: '../ws/expenditure/record.do',
                        type: 'POST',
                        data: {cmd: cmd, valor: selected},
                        cache: false,
                        dataType: "text",
                        async: true,
                        success: function(data) {
                            console.info(data);
                        },
                        error: function(data) {
                            alert(data);
                        }
                    });
                });
//                $(".autoriza").change(function(event) {
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

                $(".elimina_documento").button({
                    text: false,
                    icons: {primary: "ui-icon-trash"}
                }).click(function(event) {
                    event.preventDefault();
                    if (confirm("Esta seguro de eleiminar este comprobante?")) {
                        var cmd = $(this).attr("cmd");
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: cmd},
                            cache: false,
                            dataType: "text",
                            async: false,
                            success: function(data) {
                                alert(data);
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
                });


                // Variable to store your files
                var files = new Array();
                var x = 0;
                // Add events
                //$('input[type=file]').on('change', prepareUpload);
                //$('form').on('submit', uploadFiles);

                $("#enviar").button().click(function(event) {
                    event.preventDefault();
                    uploadFiles(event);
                });

                var cont = 1;
                $("#adRow").button().click(function(event) {
                    event.preventDefault();
                    var newTxt = $('<li><label for="gasto_xml_' + cont + '">XML </label><input type="file" name="gasto_xml_' + cont + '" class="inputfilechange" id="gasto_xml_' + cont + '" multiple><br /><label for="gasto_pdf_' + cont + '">PDF </label><input type="file" name="gasto_pdf_' + cont + '" class="inputfilechange" id="gasto_pdf_' + cont + '" multiple></li>');
                    $("#a1").append(newTxt);
                    cont++;
                    //$(".inputfilechange").on('change', function(e){prepareUpload(e)} );
                    $(".inputfilechange").on('change', prepareUpload);
                });
                // Grab the files and set them to our variable
                $(".inputfilechange").on('change', prepareUpload);

                function prepareUpload(event) {
                    files[x++] = event.target.files;
                }

                // Catch the form submit and upload the files
                function uploadFiles(event) {

                    //console.info(event);        
                    event.stopPropagation(); // Stop stuff happening
                    event.preventDefault(); // Totally stop stuff happening

                    // START A LOADING SPINNER HERE

                    // Create a formdata object and add the files
                    //var data = new FormData();
                    var data = new FormData($("#FORM_LOAD_FILES")[0]);
                    //$.each(files, function(key, value) {
                    //    data.append(key, value);            
                    //});

                    console.info(data);

                    $.ajax({
                        url: '../ws/files/upload.do?files',
                        type: 'POST',
                        data: data,
                        cache: false,
                        processData: false, // Don't process the files
                        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
                        success: function(data) {
                            alert(data);
                            console.log(data);
                            $("#FORM_RELOAD").submit();
                        },
                        error: function(data) {
                            console.log(data);
                        }
                    });
                }

                function submitForm(event, data) {
                    // Create a jQuery object from the form
                    $form = $(event.target);

                    // Serialize the form data
                    var formData = $form.serialize();

                    // You should sterilise the file names
                    $.each(data.files, function(key, value) {
                        formData = formData + '&filenames[]=' + value;
                    });

                    $.ajax({
                        url: 'submit.php',
                        type: 'POST',
                        data: formData,
                        cache: false,
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR)
                        {
                            if (typeof data.error === 'undefined') {
                                // Success so call function to process the form
                                console.log('SUCCESS: ' + data.success);
                            } else {
                                // Handle errors here
                                console.log('ERRORS: ' + data.error);
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            // Handle errors here
                            console.log('ERRORS: ' + textStatus);
                        },
                        complete: function() {
                            // STOP LOADING SPINNER
                        }
                    });
                }
            });



        </script>        

    </head>
    <body>
        <%
            String cmd = request.getParameter("cmd") != null ? request.getParameter("cmd") : null;
            String idCabecera = cmd!=null?cmd.substring(Comunes.toMD5("cargaMasiva-" + session.getId()).length()):"";
            String tipoGasto = request.getParameter("tipo_gasto") == null ? "" : request.getParameter("tipo_gasto");
            PeriodoBo bo = new PeriodoBOImpl();
            Object seguridad = session.getAttribute("codsec");
            if (seguridad == null || session.isNew()) {

        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {
            try {
        %>
        <h2>Carga de archivos de gasto</h2>
        <form id="FORM_RELOAD">
            <input type="hidden" name="cmd" id="cmd_reload" value="<%=cmd!=null?cmd:"" %>">
            <input type="hidden" name="tipo_gasto" id="tipo_gasto_reload" value="<%=tipoGasto %>">
        </form>
        <form action="#" enctype="multipart/form-data" method="post" id="FORM_LOAD_FILES">
            <input type="hidden" name="persona" id="persona" value="<%=(String) session.getAttribute("persona")%>" />
            <input type="hidden" name="cmd" id="cmd" value="<%=Comunes.toMD5("upload-" + session.getId()).concat(idCabecera).toUpperCase()%>" />
            <ul id="a1">
                <li class="ui-tabs ui-tabs-active">
                    <label for="xml_upload_0">XML</label>
                    <input type="file" name="gasto_xml_0" class="inputfilechange" id="gasto_xml_0" multiple><br />
                    <label for="pdf_upload_0">PDF</label>
                    <input type="file" name="gasto_pdf_0" class="inputfilechange" id="gasto_pdf_0" multiple>
                </li>
            </ul>
            <a id="cancelar" cmd="<%=Comunes.toMD5("edita-cabecera" + session.getId()).concat(idCabecera).toUpperCase()%>">Regresar</a>
            <a id="adRow">Agregar otro archivo</a>
            <a id="enviar">Cargar archivos</a>
        </form>
        <h2>Pendientes de asignaci&oacute;n de tipo de gasto</h2>
        <div class="ui-corner-bl">
            <table cellspacing="1" cellpadding="5" style="width:800px;border: 1px #ccc solid;">
                <tr>
                    <th style="width: 100px;">Fecha</th>
                    <th style="width: 100px;">Importe</th>
                    <th style="width: 100px;">Impuesto</th>
                    <th style="width: 120px;">Tipo</th>
                    <th style="width: 140px;">Autoriza</th>
                    <th>Documento</th>
                    <th title="Eliminar" style="width: 40px;">&nbsp;</th>
                </tr>
<!--            </table>
            <table cellspacing="1" cellpadding="5" style="width:800px;height: 400px ;border: 1px #ccc solid;">-->
                <%

                    List<PeriodoRegistro> pendientes = bo.pendientesAprobacion(idCabecera);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                    for (PeriodoRegistro registro : pendientes) {
                %>
                <tr style="height: 24px;">
                    <td style="width: 100px;"><%=dateFormat.format(registro.getFecha())%></td>
                    <td><%=registro.getRegistro() %></td>
                    <td style="width: 100px;text-align: right"><%=decimalFormat.format(registro.getImporte())%></td>
                    <td style="width: 100px;text-align: right"><%=decimalFormat.format(registro.getImpuesto())%></td>
                    <%if (registro.getEvidencia() != null && !registro.getEvidencia().isEmpty()) {%>
                    <td style="width: 120px;">
                        <select class="tipoComprobante" cmd='<%=Comunes.toMD5("periodoRegistro-update:tipo".concat(session.getId())).concat(registro.getRegistro()).toUpperCase()%>'>
                            <option value="-1">(seleccione)</option>
                            <%
                                Map<String, String> tipos = TipoComprobante.listaTipoComprobante(tipoGasto);
                                Set<String> keys = tipos.keySet();
                                for (String key : keys) {
                                    out.print(String.format("<option value = \"%s\" %s>%s</option>%n", key, key.equals(registro.getTipo()) ? "selected" : "", tipos.get(key)));
                                }
                            %>
                        </select>
                    </td>
                    <td style="width: 140px;"><%=registro.getAutoriza() == null ? "" : registro.getAutoriza()%></td>
                    <%
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
                            out.print(String.format("<a target=\"_blank\" href=\"../vistaprev?archivo=%s\" title = \"%s\">%s</a>", path.append(archivo.toLowerCase()).toString(), archivo, image));
                        }
                        out.print("</td>");
                    %>                        
                    <% } else {%>
                    <td style="width: 120px;"><%=TipoComprobante.getDescripcion(registro.getTipo())%></td>
                    <td style="width: 140px;"><%=registro.getAutoriza() == null ? "" : registro.getAutoriza()%></td>
                    <td><%=registro.getNota()%></td>
                    <%}%>
                    <td style="width: 40px;"><a class="elimina_documento" id="eliminar" cmd="<%=Comunes.toMD5("xdoc-remove" + session.getId()).concat(registro.getRegistro()).toUpperCase()%>"><img src="../resources/images/trash.png" height="16" border="0" title="Eliminar documento de gasto" /></a></td>
                </tr>
                <%
                    }
                %>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
            </table>
        </div>
        <%
            } catch (GeDocBOException e) {
                out.print(e);
            }
            }
        %>
    </body>
</html>
