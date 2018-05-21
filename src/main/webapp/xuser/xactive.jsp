<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.bo.impl.ContactoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.ContactoBO"%>
<%@page import="com.ftc.modelo.Contacto"%>
<%@page import="java.util.LinkedList"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
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
        <script>
            window.parent.location.replace("default.jsp");
        </script>
        <%        } else {            
            String mensaje = "";
            try {                
                boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
        %>
        <h2>Contactos registrados</h2>
        <div id="listado">
            <table style="width:650px;border: 1px #ccc solid;">
                <tr><th width="200px">Empresa</th><th width="200px">Nombre completo</th><th width="120px">Correo</th><th width="30px">Suspender</th></tr>
            </table>
            <table style="width: 650px;overflow: hidden;overflow-y: scroll;border: 1px #ccc solid;">
                <%
                    
                    //boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
                    List<Contacto> listado = null;
                    if (String.valueOf(session.getAttribute("propietario")).equals("S")) {
                    		ContactoBO bo = new ContactoBOImpl();
                        listado = bo.listaContactosSuspendidos(sesion);
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
                } catch (GeDocBOException e) {
                    mensaje = e.getMessage();
                    Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) session.getAttribute("usuario"));
                } finally {
                    if (mensaje.length() > 0) {
                        out.println(String.format("<script>alert(\"%s\")</script>", mensaje));
                    }
                }
            }
        %>

    </body>
</html>
