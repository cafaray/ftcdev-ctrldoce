<%@page import="java.util.Iterator"%>
<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.ftc.gedoc.utiles.Persona"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.registro de persona :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae }
            .tr_non{background-color: whitesmoke}
            label, input { display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog .ui-state-error { padding: .3em; }
            .ui-dialog {font-size: 77.5%;}
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
        </style>
        <script>
            $(function() {
                var name = $("#name"),
                        tipo = $("#tipo"),
                        rfc = $("#rfc"),
                        allFields = $([]).add(name).add(rfc).add(tipo),
                        tips = $(".validateTips");
                function updateTips(t) {
                    tips
                            .text(t)
                            .addClass("ui-state-highlight");
                    setTimeout(function() {
                        tips.removeClass("ui-state-highlight", 1500);
                    }, 500);
                }
                function checkLength(o, n, min, max) {
                    if (o.val().length > max || o.val().length < min) {
                        o.addClass("ui-state-error");
                        updateTips("La longitud del campo " + n + " debe estar entre " +
                                min + " y " + max + ".");
                        return false;
                    } else {
                        return true;
                    }
                }
                function checkRegexp(o, regexp, n) {
                    if (!(regexp.test(o.val()))) {
                        o.addClass("ui-state-error");
                        updateTips(n);
                        return false;
                    } else {
                        return true;
                    }
                }

                $("#dialog-form-upload").dialog({
                    autoOpen: false,
                    height: 200,
                    width: 350,
                    modal: true,
                    buttons: {
                        "Subir": function() {                            
                            $("#FORM_UPLOAD_FILE").submit();
                            return false;
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    },
                    close: function() {
                        allFields.val("").removeClass("ui-state-error");
                    }
                });
                $("#dialog-form").dialog({
                    autoOpen: false,
                    height: 250,
                    width: 350,
                    modal: true,
                    buttons: {
                        "Registrar": function() {
                            var bValid = true;
                            allFields.removeClass("ui-state-error");
                            bValid = bValid && checkLength(name, "Raz�n social", 3, 100);
                            bValid = bValid && checkRegexp(name, /^[\w������������0-9. ]+$/i, "El campo raz�n social debe contener caracteres alfanum�ricos.");
                            bValid = bValid && checkLength(rfc, "RFC", 12, 13);
                            bValid = bValid && checkRegexp(rfc, /^_|[A-Z]{4}([0-9]{6})([A-z0-9]{3})$/i, "RFC sin guiones, solo alfanum�ricos. Ej. _XXX000000ABC");
                            if (bValid) {
                                $.ajax({
                                    type: "POST",
                                    dataType: "text",
                                    data: {razon: name.val(), rfc: rfc.val(), tipo: tipo.val(), cmd: "<%=Comunes.toMD5("registraPersona-" + session.getId())%>"},
                                    async: false,
                                    url: "../ws/personas/registrar.do",
                                    success: function(data) {
                                        updateTips(data);
                                        alert(data);
                                        name.val("");
                                        rfc.val("");
                                        location.reload();
                                    },
                                    error: function(data) {
                                        updateTips(data);
                                    }
                                });
                                return false;
                            } else {
                                //Nothing
                            }
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    },
                    close: function() {
                        allFields.val("").removeClass("ui-state-error");
                    }
                });
                $("#nuevaEmpresa")
                        .button()
                        .click(function() {
                            $("#dialog-form").dialog("open");
                        });
                $("#cargaEmpresas")
                        .button()
                        .click(function() {
                            $("#dialog-form-upload").dialog("open");
                        });
            });
        </script>
    </head>
    <body>
        <%
            String cmd = request.getParameter("cmd");
            Object seguridad = session.getAttribute("codsec");
            String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {
        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {
            Connection conexion = null;
            String mensaje = "";
            try {
        %>
        <h2>Personas registradas</h2>
        <div id="listado">
            <table class="tr_cab" cellspacing="1" cellpadding="5" style="width:550px;border: 1px #ccc solid;">
                <tr><th width="300px">Raz&oacute;n social</th><th width="120px">RFC</th><th width="120px">Tipo</th></tr>
            </table>
            <table style="width: 550px;overflow: hidden;overflow-y: scroll;border: 1px #ccc solid;">
                <%
                    conexion = Conexion.getConexion();
                    List<Persona> listado = Persona.obtienePersonas(tipo.charAt(0), conexion, sesion);
                    Iterator<Persona> personas = listado.iterator();
                    int cont = 0;
                    while (personas.hasNext()) {
                        cont++;
                        Persona p = personas.next();
                        out.println(String.format("<tr class=\"%s\"><td width=\"%s\">%s</td><td width=\"%s\">%s</td><td width=\"%s\">%s</td></tr>", cont % 2 == 0 ? "tr_par" : "tr_non", "300px", p.getNombre(), "120px", p.getRfc(), "120px", p.getDescripcionTipo()));
                    }
                %>
            </table>
            <p>
                <input type="button" value="Nueva empresa" id ="nuevaEmpresa" />
                <input type="button" value="Carga masiva" id ="cargaEmpresas" />
            </p>
        </div>
        <div id="dialog-form" title="Crear nuevo <%=tipo.equals("P") ? "Proveedor" : tipo.equals("C") ? "Cliente" : ""%>.">
            <form>
                <p class="validateTips"></p>
                <fieldset>

                    <input type="hidden" name="tipo" id="tipo" value="<%=tipo%>" />
                    <label for="name">Raz&oacute;n social</label>
                    <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
                    <label for="rfc">RFC</label>
                    <input type="text" name="rfc" id="rfc" class="text ui-widget-content ui-corner-all" />
                </fieldset>
            </form>
        </div>
        <div id="dialog-form-upload" title="Carga masiva de <%=tipo.equals("P") ? "Proveedor" : tipo.equals("C") ? "Cliente" : ""%>.">
            <form action="../ws/files/upload.do" method="post" id="FORM_UPLOAD_FILE" enctype="multipart/form-data">
                <p class="validateTips"></p>
                <fieldset>
                    <input type="hidden" name="tipo" id="tipo" value="<%=tipo%>" />
                    <label for="name">Archivo</label>
                    <input type="file" name="archivo" id="archivo" class="text ui-widget-content ui-corner-all" />
                </fieldset>
            </form>
        </div>                    
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
    </body>
</html>
