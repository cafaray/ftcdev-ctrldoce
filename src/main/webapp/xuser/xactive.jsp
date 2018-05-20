<%@page import="java.util.LinkedList"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.ftc.gedoc.utiles.Contacto"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.Connection"%>
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
            label, input, select{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
        </style>
        <script>
            $(function() {
                var activar = $("#activar");
                $("#activar").click(function() {
                    var codigo = activar.attr("codigo");
                    $.ajax({
                        type: "POST",
                        dataType: "text",
                        data: {elemento: codigo, cmd:"<%=Comunes.toMD5(session.getId())+Comunes.toMD5("xuser-active") %>", sesion: "<%=session.getId() %>"},
                        async: false,
                        url: "../ws/xuser/manager.do",
                        success: function(data) {
                            alert(data);
                            location.reload();
                        },
                        error: function(data) {
                            alert("Alg�n error ocurri�." + data);
                        }
                    });
                    return false;
                });
            });

        </script>
    </head>
    <body>
        <%
            String cmd = request.getParameter("cmd");
            Object seguridad = session.getAttribute("codsec");
            String persona = (String) session.getAttribute("persona");
            String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("default.jsp");
        </script>
        <%        } else {
            Connection conexion = null;
            String mensaje = "";
            try {
                conexion = Conexion.getConexion();
                boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
        %>
        <h2>Contactos registrados</h2>
        <div id="listado">
            <table cellspacing="1" cellpadding="5" style="width:650px;border: 1px #ccc solid;">
                <tr><th width="200px">Empresa</th><th width="200px">Nombre completo</th><th width="120px">Correo</th><th width="30px">Suspender</th></tr>
            </table>
            <table style="width: 650px;overflow: hidden;overflow-y: scroll;border: 1px #ccc solid;">
                <%
                    conexion = Conexion.getConexion();
                    //boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
                    List<Contacto> listado = null;
                    if (String.valueOf(session.getAttribute("propietario")).equals("S")) {
                        listado = Contacto.listaContactosSuspendidos(conexion, sesion);
                    } else {
                        listado = new LinkedList<Contacto>();
                    }
                    Iterator<Contacto> contactos = listado.iterator();
                    while (contactos.hasNext()) {
                        Contacto c = contactos.next();
                        String suspender = "<input type=\"button\" class=\"ui-button ui-button-text-only ui-state-default ui-corner-all\" id=\"activar\" codigo=\"" + c.getIdentificador() + "\" value=\"Activar\" />";
                        out.println(String.format("<tr><td width=\"%s\">%s</td><td width=\"%s\">%s</td><td width=\"%s\">%s</td><td width=\"%s\">%s</td></tr>", "200px", c.getRazonSocial(), "200px", c.getNombre() + " " + c.getApellido(), "120px", c.getCorreo(), "30px", suspender));
                    }
                %>
            </table>
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
