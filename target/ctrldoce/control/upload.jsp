<%@page import="com.ftc.gedoc.utiles.Persona"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.Connection"%>
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
            label, input, file{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            input.file { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <script>
            $(function() {
                $("#validadorSat").click(function (){
                   window.open("https://www.consulta.sat.gob.mx/sicofi_web/moduloecfd_plus/validadorcomprobantes/validador.asp","_blank","");
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
                conexion = Conexion.getConexion();
                String persona = (String) session.getAttribute("persona");
                boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
        %>
        <div>
            <p class="validateTips"></p>
            <form action="../ws/files/upload.do" method="post" id="FORM_UPLOAD_FILE" enctype="multipart/form-data">
                <fieldset>
                    <label for="persona">Empresa</label>
                    <select name="persona" id="persona">
                        <%
                            List<Persona> empresas = Persona.obtienePersonas(tipo.charAt(0), conexion, sesion);
                            for (Persona empresa : empresas) {
                                String elemento = "";
                                if (persona.equals(empresa.getIdentificador())) {
                                    elemento = String.format("<option value=\"%s\" selected=\"true\">%s</option>", empresa.getIdentificador(), empresa.getNombre());
                                } else {
                                    if (isOwner) {
                                        elemento = String.format("<option value=\"%s\">%s</option>", empresa.getIdentificador(), empresa.getNombre());
                                    }
                                }
                                out.println(elemento);
                            }
                        %>
                    </select>
                    <label for="titulo">Factura</label>
                    <input type="text" id="titulo" name="titulo" class="text ui-widget-content ui-corner-all" />
                    <label for="observaciones">Concepto</label>
                    <input type="text" id="observaciones" name="observaciones" class="text ui-widget-content ui-corner-all" />
                    <!-- Adaptaci�n Biotecsa -->
                    <label for="archivo1">Archivo PDF</label>
                    <input type="file" name="archivo1" />
                    <label for="archivo2">Archivo XML</label>
                    <input type="file" name="archivo2" />
                    <label for="archivo3">Anexo/COA PDF</label>
                    <input type="file" name="archivo3" />
                    <p>
                        <input type="button" id="validadorSat" value="Validador SAT" class="ui-button ui-button-text-only ui-corner-all ui-state-default" />
                        <input type="submit" id="guardar" value ="Guardar" class="ui-button ui-button-text-only ui-corner-all ui-state-default" />
                    </p>
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
