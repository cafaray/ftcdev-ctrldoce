<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.ftc.aq.Conexion"%>
<%@page import="java.sql.Types"%>
<%@page import="com.ftc.aq.SpParam"%>
<%@page import="com.ftc.aq.SpParams"%>
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
                // 210714: selecciona el archivo de configuraci�n por dominio:               
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
                    if (Conexion.validaUsuarioMySql(bdservidor, bdbase, bdusuario, bdcontrasenia)) {
                        conexion = Conexion.getConexion();
                        String rfcUsuario = request.getParameter("rfc");
                        SpParams params = new SpParams();
                        params.add(new SpParam(1, Types.VARCHAR, usuario));
                        params.add(new SpParam(2, Types.VARCHAR, contrasena));
                        params.add(new SpParam(3, Types.VARCHAR, rfcUsuario));
                        params.add(new SpParam(4, Types.VARCHAR, request.getRemoteAddr()));
                        params.add(new SpParam(5, Types.VARCHAR, session.getId()));
                        params.add(new SpParam(6, Types.VARCHAR, null, true));
                        params.add(new SpParam(7, Types.VARCHAR, null, true));
                        params.add(new SpParam(8, Types.VARCHAR, null, true));
                        params.add(new SpParam(9, Types.VARCHAR, null, true));
                        params.add(new SpParam(10, Types.VARCHAR, null, true));
                        Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "whois", params);
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
        <script language="javascript" type="text/javascript">
            document.getElementById("FORM_SEND_REQUEST").submit();
        </script>
        <%
                            } else {
                                throw new SQLException((String) vuelta[4], "0");
                            }
                        } else {
                            throw new SQLException("No se recuperaron los valores esperados al identificar el usuario", "0");
                        }
                    } else {
                        throw new SQLException("No se logr� establecer comunicaci�n con la base de datos.", "0");
                    }
//                } else {
//                    throw new Exception("Solicitud no identificada. <a href=\"default.jsp\">Regresar</a>");
//                }
            } catch (SQLException sqle) {
                session.invalidate();
                mensaje = sqle.getSQLState()==null||sqle.getSQLState().equals("0") ? String.format("Ocurrio una excepci�n al realizar el proceso.%n\t%s [%s-%d]", sqle.getMessage(), sqle.getSQLState(),sqle.getErrorCode()):"Algo malo ocurri� al comunicarse con la base de datos. Revise el log.";
                Comunes.escribeLog(logLocation, sqle, "inicioSesion:" + usuario); // + ":" + contrasena);
            } catch (Exception e) {
                session.invalidate();
                mensaje = "Ocurrio una excepci�n al realizar el proceso. " + e.getMessage();
                Comunes.escribeLog(logLocation, e, "inicioSesion:" + usuario); // + ":" + contrasena);
            } finally {
                if (conexion != null) {
                    if (!conexion.isClosed()) {
                        conexion.close();
                    }
                }
                
                out.println(mensaje);
                out.println("<a href='#' onclick='history.back(1)'>R E G R E S A R</a>");
                out.println("<script>setTimeout(\"location.replace('default.jsp');\",3521);</script>");
            }
        %>
        <div>
        </div>
    </body>
</html>
