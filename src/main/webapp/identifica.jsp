<%@page import="com.ftc.gedoc.exceptions.GeDocDAOException"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.sql.Types"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>:::Identificaci&oacute;n de usuario:::</title>
    </head>
    <body>
        <%
            String mensaje = "";
            String usuario = request.getParameter("usuario");
            String contrasena = request.getParameter("contrasena");
            String rfc = request.getParameter("rfc");
            String cmd = request.getParameter("cmd");
            String logLocation = getServletContext().getInitParameter("logLocation");
            //Connection conexion = null;
            try {
                //if (cmd.equals(Comunes.toMD5("IDENTIFICA_USUARIO" + session.getId()))) {
                // 210714: selecciona el archivo de configuración por dominio:               
                String dominio = "";//usuario.substring(arroba, usuario.indexOf(".", arroba));
                //System.out.println("=====> request:requestUri "+request.getRequestURI());
                //System.out.println("=====> request.servername: "+request.getServerName());
                //System.out.println("=====> request.contextPath: "+request.getContextPath());
                //System.out.println("=====> request.localAddress: "+request.getLocalName());
                String serverName = request.getServerName();
                serverName = serverName.replace(".com", "");
                serverName = serverName.replace("ctrldoce.", "");
                serverName = serverName.replace("www.", "");
                serverName = serverName.replace("localhost", "biotecsa");
                dominio = serverName;
                System.out.println("=====> Working for: "+dominio);
                    Properties props = new Properties();
                    String bdservidor = "", bdbase = "", bdusuario = "", bdcontrasenia = "";
                    if(dominio.equals("biotecsa")){
                        dominio = getServletContext().getInitParameter("configLocation");
                    }else{
                        String tmp = getServletContext().getInitParameter("configLocation");
                        tmp = tmp.substring(0, tmp.indexOf("."));
                        tmp = String.format("%s%s/.config",tmp,dominio);
                        dominio = tmp;
                    }
                    props.load(new FileInputStream(dominio));
                    bdservidor = props.getProperty("servidor");
                    bdbase = props.getProperty("base");
                    bdusuario = props.getProperty("usuario");
                    bdcontrasenia = props.getProperty("contrasenia");
                    rfc = props.getProperty("cliente"); //240714 --> cafaray se reasigna el rfc con el valor del archivo de configuracion
                    if (Seguridad.validaUsuario(bdservidor, bdbase, bdusuario, bdcontrasenia)) {                        
                        String rfcUsuario = request.getParameter("rfc");                        
                        Object[] vuelta = Seguridad.whoIs(usuario, contrasena, rfcUsuario, request.getRemoteAddr(), request.getSession().getId());
                        if (vuelta != null && vuelta.length == 5) {
                            if (String.valueOf(vuelta[4]).length() == 0) {
                                int seguridad = Integer.valueOf((String) vuelta[0]);
                                String identificador = (String) vuelta[1];
                                session.setAttribute("usuario", usuario);
                                session.setAttribute("seguridad", seguridad);
                                session.setAttribute("persona", (String)vuelta[3]);
                                session.setAttribute("identificador", identificador);
                                Seguridad objSeguridad = new Seguridad(seguridad);
                                session.setAttribute("codsec", objSeguridad);
                                session.setAttribute("rfc", rfc);
                                session.setAttribute("propietario", (String)vuelta[2]);
                                session.setAttribute("fecha", new Date(Calendar.getInstance().getTimeInMillis()));
                                session.setAttribute("serverName", serverName);
                                String shva = Comunes.toMD5(identificador + seguridad) + Comunes.toMD5(session.getId());
                                String url = String.format("ctrldoce/index.jsp?rfc=%s", rfc);
                                mensaje = String.format("Conexi&oacute;n establecida .................... [ Ok ]");
        %>
        <form id="FORM_SEND_REQUEST" action="index.jsp">
            <input type="hidden" name="url" value="<%=url%>" />
            <input type="hidden" name="shva" value="<%=shva%>" />
        </form>
        <script>
            document.getElementById("FORM_SEND_REQUEST").submit();
        </script>
        <%
                            } else {
                                mensaje = (String) vuelta[4];
                            }
                        } else {
                            mensaje = "No se recuperaron los valores esperados al identificar el usuario";
                        }
                    } else {
                        mensaje = "No se logr&oacute; establecer comunicaci&oacute;n con la base de datos.";
                    }
            } catch (GeDocDAOException e) {
                session.invalidate();
                mensaje = e.getMessage();
                Comunes.escribeLog(logLocation, e, "inicioSesion:" + usuario);            
            } finally {                
                out.println(mensaje);
                out.println("<a href='#' onclick='history.back(1)'>R E G R E S A R</a>");
                out.println("<script>setTimeout(\"location.replace('default.jsp');\",3521);</script>");
            }
        %>
        <div>
        </div>
    </body>
</html>
