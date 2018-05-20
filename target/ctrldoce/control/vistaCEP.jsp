<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.sql.SQLException"%>
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
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {

            Connection conexion = null;
            String mensaje = "";
            try {
                conexion = Conexion.getConexion();
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
