<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        Object codseg = session.getAttribute("codsec");
        String isOwner = (String) session.getAttribute("propietario");
        if (codseg == null) {
            out.print(String.format("<b style=\"color:%s\">Al parecer no tienes configurados los permisos de acceso.</b>", "red", ""));
        } else {
            Seguridad seguridad = (Seguridad) session.getAttribute("codsec");
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title></title>
        <script src="js/jquery-1.7.2.min.js"></script>
        <script src="js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <link href='http://fonts.googleapis.com/css?family=Roboto+Condensed' rel='stylesheet' type='text/css'>
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }

            h1,h2,h3,h4,a{
                font-family: 'Roboto Condensed', sans-serif;
            }
            .lista_notificaciones{list-style: none;font-size: .8em;}
            #accordion-sizer {
                padding: 3px;
                width: 250px;
                height: 500px;
            }
            #panel_notificacion{
                background-color: whitesmoke;
                float: right;
                border-bottom: solid silver 2px;
                border-right: solid silver 2px;
                width: 100%;
                height: 170px;
                margin-top: 10px;
                margin-right: 5px;                
                overflow: hidden;
                overflow-y: scroll;
            }
        </style>
        <script>
            $(function() {
                $("#opciones").accordion({
                    heightStyle: "fill"
                });
        <%
            String secprv = seguridad.seguridadProveedor();
            if (secprv.contains("n") && !isOwner.equals("S")) {
        %>
                buscarNotificacion();
                function buscarNotificacion() {
                    $.ajax({
                        type: "POST",
                        dataType: "html",
                        data: {cmd: "<%=Comunes.toMD5("notificaciones.Consulta-notificacion-".concat(session.getId()))%>"},
                        async: true,
                        url: "ws/notifier/manage.do",
                        success: function(data) {
                            data = "<ul class='lista_notificaciones'>" + data + "</ul>";
                            updateNotifier(data);
                            //alert(data);
                        },
                        error: function(data) {
                            alert("error: " + data);
                        }
                    });
                    return false;
                }
                function updateNotifier(data) {
                    $("#notificaciones").html(data);
                }
                setInterval(buscarNotificacion, 100000);
            <%
            }
            %>
            });
            function ver(url) {
                window.parent.frames[1].location = url;
            }
        </script>
    </head>
    <body>
        <div id="accordion-sizer">
            <h2 style="color:white;">Panel de control</h2>
            <div id="opciones" >
                <%
                    if (seguridad.esProveedor()) {
                        String accesos = seguridad.seguridadProveedor();
                        String comando = "";
                %>
                <h3>Proveedores</h3>
                <div>
                    <ul style="list-style: none; padding: 5px">
                        <%
                            comando = accesos.contains("r") ? String.format("<li><a href=\"javascript:ver('%s');\">%s</a></li>", "control/personas.jsp?tipo=P", "Registrar proveedor") : "";
                            out.println(comando);

                            comando = accesos.contains("c") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/contactos.jsp?tipo=P", "Registrar usuario") : "";
                            out.println(comando);

                            comando = accesos.contains("v") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/vistadoce.jsp?tipo=P", "Ver documentos") : "";
                            out.println(comando);

                            comando = accesos.contains("s") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/upload.jsp?tipo=P", "Subir documentos") : "";
                            out.println(comando);

                            comando = accesos.contains("s") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/listadoCEP.jsp?tipo=P", "Gesti&oacute;n de CEP") : "";
                            out.println(comando);
                            //comando = accesos.contains("n") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/notificacion.jsp", "Notificar") : "";
                            //out.println(comando);
                            //comando = accesos.contains("e") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/documentose.jsp", "Editar estatus") : "";
                            //out.println(comando);

                        %>
                    </ul>
                </div>
                <%                    }
                    if (seguridad.esCliente()) {
                        String accesos = seguridad.seguridadCliente();
                        String comando = "";
                %>
                <h3>Clientes</h3>
                <div>
                    <ul style="list-style: none; padding: 5px">
                        <%
                            comando = accesos.contains("r") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/personas.jsp?tipo=C", "Registrar cliente") : "";
                            out.println(comando);

                            comando = accesos.contains("c") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/contactos.jsp?tipo=C", "Registrar usuario") : "";
                            out.println(comando);

                            comando = accesos.contains("v") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/vistadoce.jsp?tipo=C", "Ver documentos") : "";
                            out.println(comando);

                            comando = accesos.contains("s") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/upload.jsp?tipo=C", "Subir documentos") : "";
                            out.println(comando);

                            //comando = accesos.contains("n") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/notificacion.jsp", "Notificar") : "";
                            //out.println(comando);
                            //comando = accesos.contains("e") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/documentose.jsp", "Editar estatus") : "";
                            //out.println(comando);

                        %>
                    </ul>
                </div>
                <%                    }
                    if (seguridad.esGastos()) {
                        String accesos = seguridad.seguridadGastos();
                        String comando = "";
                %>



                <h3>Control de gastos</h3>
                <div>
                    <ul style="list-style: none; padding: 5px">
                        <%
                            comando = accesos.contains("t") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "expenditure/resume.jsp?tipo_gasto=t", "Vi&aacute;ticos vendedores") : "";
                            out.println(comando);

                            comando = accesos.contains("h") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "expenditure/resume.jsp?tipo_gasto=h", "Caja chica") : "";
                            out.println(comando);

                            comando = accesos.contains("a") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "expenditure/resume.jsp?tipo_gasto=a", "Agentes Aduanales") : "";
                            out.println(comando);

                            comando = accesos.contains("s") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "expenditure/index.jsp", "Administrar per&iacute;odos") : "";
                            out.println(comando);
                        %>
                    </ul>
                </div>
                <%
                    }
                    if (seguridad.esAdmin()) {
                        String accesos = seguridad.seguridadAdmin();
                        String comando = "";
                %>



                <h3>Administrador</h3>
                <div>
                    <ul style="list-style: none; padding: 5px">
                        <%
                            //comando = accesos.contains("u") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "usuarios.jsp", "Visor de usuarios") : "";
                            //out.println(comando);
                            comando = accesos.contains("u") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "control/contactos.jsp?tipo=-", "Usuarios") : "";
                            out.println(comando);

                            comando = accesos.contains("-") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "xuser/xsuspend.jsp", "Suspender") : "";
                            out.println(comando);

                            comando = accesos.contains("+") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "xuser/xactive.jsp", "Activar") : "";
                            out.println(comando);

                            comando = accesos.contains("*") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "xuser/xreset.jsp", "Cambiar contrase&ntilde;a") : "";
                            out.println(comando);

                            comando = accesos.contains("g") ? String.format("<li><a href=\"javascript:ver('%s')\">%s</a></li>", "xuser/grupo.jsp", "Gestor de grupos") : "";
                            out.println(comando);
                        %>
                    </ul>
                </div>
                <%}%>
                <h3>Sesi&oacute;n</h3>
                <div>
                    <ul style="list-style: none; padding: 5px;">
                        <li><a href="javascript:cerrarSesion();">Cerrar sesi&oacute;n</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <%
            secprv = seguridad.seguridadProveedor();
            if (secprv.contains("n") && !isOwner.equals("S")) {
        %>
        <div id="panel_notificacion">
            <h3 style="padding: 3px;">Panel de notificaciones</h3>                        
            <hr />
            <div id="notificaciones">
                <ul class="lista_notificaciones">

                </ul>
            </div>
        </div>
        <%}%>

        <script>
            function cerrarSesion() {
                window.parent.location.replace("default.jsp");
            }
        </script>
    </body>
    <%
        }
    %>
</html>
