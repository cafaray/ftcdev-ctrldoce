<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: Subir documentos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            label, file{ display:block; }
            input.text { margin-bottom:12px; width:25%; padding: .4em; }
            input.file { margin-bottom:12px; width:90%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <%
            String cmd = request.getParameter("cmd");
            Object seguridad = session.getAttribute("codsec");
            String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script>
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {

            String mensaje = "";
            try {
                String persona = (String) session.getAttribute("persona");
                boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");                
                String empresa = (String) session.getAttribute("persona");                
                String rfc = (String) session.getAttribute("rfc");                
        %>
        <script>
            $(function() {
                
            });
        </script>
    </head>
    <body>
        <h1>Hello World!</h1>
<%
                } catch (Exception e) {
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
