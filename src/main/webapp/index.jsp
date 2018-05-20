<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.utiles.Notificacion"%>
<%@page import="com.ftc.gedoc.bo.impl.NotificacionBOImpl"%>
<%@page import="com.ftc.gedoc.bo.NotificacionBO"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: Control de documentos electr&oacute;nicos :::</title>

        <link href='http://fonts.googleapis.com/css?family=Roboto+Condensed' rel='stylesheet' type='text/css'>
        <script src="js/jquery-1.7.2.min.js"></script>
        <script src="js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            #logo{
                height: 70px;
                width: 150px;
                border: 0px;
                //position: absolute;
                left: 30px;
                top: 5px;
            }
            h1,h2,h3,h4{
                font-family: 'Roboto Condensed', sans-serif;
            }
            .panel_notificacion{
                visibility: hidden;
                height: 220px;
                width: 250px;
            }
        </style>

    </head>
    <body style="background:white">
        <%
            String mensaje = "";
            if (session.getAttribute("usuario") != null) {
                String usuario = (String) session.getAttribute("usuario");
                String shva = request.getParameter("shva");
                if (shva != null && shva.length() > 0) {
                    String identificador = "", rfc = "", serverName = "", fecha = "";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    int seguridad = -1;
                    identificador = (String) session.getAttribute("identificador");
                    seguridad = (Integer) session.getAttribute("seguridad");
                    rfc = (String) session.getAttribute("rfc");
                    serverName = (String) session.getAttribute("serverName");
                    if (shva.startsWith(Comunes.toMD5(identificador + seguridad))
                            && shva.endsWith(Comunes.toMD5(session.getId()))) {
                        mensaje = "";
                        //analiza el valor de seguridad para establecer el men�:
        %>
        <div class="row" style="padding: 0 74px;border-bottom: 1px #a6c9e2 dotted;position: relative;">
            <img src="resources/images/<%=serverName%>.png" id="logo" />
            <div id="cabecera" style="float: right;font-family: Arial,Helvetica,sans-serif;font-size: 12px;color: #0078ae;text-align: right">
                <p>&nbsp;</p>
                <b>Fecha:</b>&nbsp;&nbsp;&nbsp;&nbsp;<%=dateFormat.format(new Date())%><br />
                <b>Usuario:&nbsp;&nbsp;</b><%=usuario%>

                <p>
                    <a href="javascript:cerrarSesion()">Cerrar Sesi&oacute;n</a>
                    <script language="javascript" type="text/javascript">
                        function cerrarSesion() {
                            window.parent.location.replace("default.jsp");
                        }
                    </script>
                </p>
                <%
                    //if(((String) session.getAttribute("propietario")).equals("S")){
                %>
                <!--                <p id="notificaciones"><a id="ver_notificaciones">Ver notificaciones</a></p>
                                    <div class="panel_notificacion">
                                        <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span></p>
                                        <ul style="list-style: none">-->
                <%
//                            try{
//                                NotificacionBO bo = new NotificacionBOImpl();
//                                List<Notificacion> notificaciones = bo.notificaciones();
//                                for(Notificacion notificacion:notificaciones){
//                                    if(notificacion.getEstatus().equals("P")){
//                                        out.print(String.format("<li>%s<span><a class=\"entendido\" cmd=\"%s\">Entendido</a></span></li>",
//                                                notificacion.getTitulo(),Comunes.toMD5("cierra-notificacion".concat(session.getId())).concat(String.valueOf(notificacion.getIdentificador()))));
//                                    }
//                                }
//                            }catch(GeDocBOException e){
//                                out.print("<li>".concat(e.getMessage()).concat("</li>"));
//                            }
                %>                            
                <!--                        </ul>
                                    </div>-->
                <%//}%>
            </div>
        </div>
        <div class="row" style="">                
            <div style="border: 0px black solid;width: 300px; height: 100%; float: left; background: #0078ae;color:white;border-right: 1px solid black;box-shadow: 1px 1px 6px #0077EF;">
                <iframe src="menu.jsp" style="width:300px;height: 600px;padding: 10px;border: 0px"></iframe>
                <p style="font-family: monospace; font-size: 1em; text-align: center; width: 300px; height: 100%; float: left; background: #0078ae;color:white;border-right: 1px solid black;box-shadow: 1px 1px 6px #0077EF;">
                    <b>Versi&oacute;n XML-SAT</b> 3.3
                </p>                
            </div>
            <div style="border: 0px red solid; width: 830px;height: 600px;padding: 10px; float: left">
                <iframe src="control/index.jsp" style="width:830px;height: 600px;padding: 10px;border: 0px"></iframe>
            </div>
        </div>

        <%    } else {
                        mensaje = "No se ha iniciado sesi�n correctamente.";
                    }
                } else {
                    mensaje = "No se ha iniciado sesi�n correctamente.";
                }
            } else {
                mensaje = "No se ha iniciado sesi�n.";
            }
            if (mensaje.length() > 0) {
        %>
        <script>
            alert("<%=mensaje%>");
            location.replace("default.jsp");
        </script>
        <%}%>

    </body>
</html>
