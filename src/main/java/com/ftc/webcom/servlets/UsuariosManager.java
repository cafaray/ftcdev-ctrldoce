package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.tsmi.mail.EnviaCorreo;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsuariosManager extends HttpServlet {

    String[] SERVICIOS = {Comunes.toMD5("xuser-deactive"),
        Comunes.toMD5("xuser-active"),
        Comunes.toMD5("xuser.reset"),
        Comunes.toMD5("xuser.reset")};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String identificador = request.getParameter("elemento") == null ? "" : request.getParameter("elemento");
        String mensaje = "";
        String sesion = request.getSession().getId();
        String servicio = request.getParameter("cmd");
        Connection conexion = null;
        try {

            SpParams params = new SpParams();
            if (servicio == null || servicio.length() <= 0) {
                mensaje = "No se tienen todos los parametros necesarios para la consulta. Notifique a sistemas.";
            } else if (servicio.endsWith(SERVICIOS[2]) && identificador.equals(Comunes.toMD5("elemento-reset.pwd"))) {
                conexion = Conexion.getConexion();
                String usuario = request.getParameter("usuario");
                String valenc = request.getParameter("contrasenia");
                //actualizaAccesoContacto(IN contacto CHAR(16), IN valenc VARCHAR(16), IN sesion VARCHAR(32), OUT error VARCHAR(250))
                params.add(new SpParam(1, Types.VARCHAR, usuario));
                params.add(new SpParam(2, Types.VARCHAR, valenc));
                params.add(new SpParam(3, Types.VARCHAR, sesion));
                params.add(new SpParam(4, Types.VARCHAR, null, true));
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "actualizaAccesoContacto", params);
                if (vuelta != null && vuelta.length == 1) {
                    if (String.valueOf(vuelta[0]).length() > 0) {
                        throw new SQLException((String) vuelta[0], "0");
                    } else {
                        mensaje = "Se ha actualizado la contrase�a.";
                    }
                } else {
                    throw new SQLException("La vuelta del servicio no es la esperada.", "0");
                }

            } else if (servicio.endsWith(SERVICIOS[1])) {
                conexion = Conexion.getConexion();
                //activaAccesoContacto(IN contacto CHAR(16), IN sesion VARCHAR(32), OUT error VARCHAR(250))
                params.add(new SpParam(1, Types.VARCHAR, identificador));
                params.add(new SpParam(2, Types.VARCHAR, sesion));
                params.add(new SpParam(3, Types.VARCHAR, null, true));
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "activaAccesoContacto", params);
                if (vuelta != null && vuelta.length == 1) {
                    if (String.valueOf(vuelta[0]).length() > 0) {
                        throw new SQLException((String) vuelta[0], "0");
                    } else {
                        mensaje = "El usuario ha quedado activado.";
                    }
                } else {
                    throw new SQLException("La vuelta del servicio no es la esperada.", "0");
                }
            } else if (servicio.endsWith(SERVICIOS[0])) {
                conexion = Conexion.getConexion();
                //suspendeAccesoContacto(IN contacto CHAR(16), IN sesion VARCHAR(32), OUT error VARCHAR(250))
                params.add(new SpParam(1, Types.VARCHAR, identificador));
                params.add(new SpParam(2, Types.VARCHAR, sesion));
                params.add(new SpParam(3, Types.VARCHAR, null, true));
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "suspendeAccesoContacto", params);
                if (vuelta != null && vuelta.length == 1) {
                    if (String.valueOf(vuelta[0]).length() > 0) {
                        throw new SQLException((String) vuelta[0], "0");
                    } else {
                        mensaje = "El usuario ha quedado suspendido.";
                    }
                } else {
                    throw new SQLException("La vuelta del servicio no es la esperada.", "0");
                }
            } else if (servicio.endsWith(SERVICIOS[3])) {
                String sc = getServletContext().getInitParameter("urlSite") + "xuser/zreset.jsp";
                try {
                    Properties props = new Properties();
                    props.load(new FileInputStream(getServletContext().getInitParameter("configLocation")));
                    String bdservidor = props.getProperty("servidor");
                    String bdbase = props.getProperty("base");
                    String bdusuario = props.getProperty("usuario");
                    String bdcontrasenia = props.getProperty("contrasenia");

                    props = new Properties();
                    props.load(new FileInputStream(getServletContext().getInitParameter("mailConfig")));
                    String cuenta = props.getProperty("cuenta");
                    String acceso = props.getProperty("acceso");

                    conexion = Conexion.getConnection2MySql(bdservidor, bdbase, bdusuario, bdcontrasenia);
                    //ingresaSolicitudReset(IN{correo,rfc,agente}, OUT{referencia,error})
                    params.add(new SpParam(1, Types.VARCHAR, request.getParameter("usuario")));
                    params.add(new SpParam(2, Types.VARCHAR, request.getParameter("rfc")));
                    params.add(new SpParam(3, Types.VARCHAR, request.getParameter("elemento")));
                    params.add(new SpParam(4, Types.VARCHAR, null, true));
                    params.add(new SpParam(5, Types.VARCHAR, null, true));
                    Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "ingresaSolicitudReset", params);
                    if (vuelta != null && vuelta.length == 2) {
                        if (String.valueOf(vuelta[1]).length() > 0) {
                            throw new SQLException((String) vuelta[1], "0");
                        } else {
                            StringBuilder mensajeCorreo = new StringBuilder("");
                            mensajeCorreo.append("<pHola</p><p>Para poder recuperar tu acceso deber&aacute;s de presionar en el siguiente enlace o copiarlo y pegarlo en tu navegador.</p>");
                            mensajeCorreo.append("<p>Si no has solicitado el restablecer tu acceso, has caso omiso a este mensaje.</p>");
                            mensajeCorreo.append("<p><a href = \"").append(sc).append("?solicitud=").append((String) vuelta[0]).append("\">Restablecer mi contrase&ntilde;a</a></p>");
                            mensajeCorreo.append("<p>Recurda que FTC te ofrece diferentes servicios de telecomunicaciones y c&oacte;mputo. Visitanos en <a href=\"http://www.ftcenlinea.com\">ftcenlinea.com</a></p>");
                            EnviaCorreo.enviar(request.getParameter("usuario"), mensajeCorreo.toString(), "Restablecer acceso.", cuenta, acceso, "H");
                            mensaje = "La solicitud se ha enviado a tu correo electr�nico.";
                        }
                    } else {
                        throw new SQLException("La vuelta del servicio no es la esperada.", "0");
                    }
                } catch (SQLException sqle) {
                    mensaje = sqle.getSQLState().equals("0") ? sqle.getMessage() : "Ocurrio un error al solictar el restablecer el acceso.";
                    Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), sqle);
                } finally {
                    if (conexion != null) {
                        if (!conexion.isClosed()) {
                            conexion.close();
                        }
                    }
                }
            } else if (servicio.equals(Comunes.toMD5(request.getRemoteAddr()) + Comunes.toMD5("xuser.zreset"))) {
                String contrasenia = request.getParameter("contrasenia");
                String desde = request.getParameter("desde");
                String solicitud = request.getParameter("elemento");
                if (servicio.startsWith(Comunes.toMD5(desde))) {
                    //aplicaSolicitudReset(IN{identificador,valenc,ipfrom}, OUT{error})
                    params.add(new SpParam(1, Types.VARCHAR, solicitud));
                    params.add(new SpParam(2, Types.VARCHAR, contrasenia));
                    params.add(new SpParam(3, Types.VARCHAR, desde));
                    params.add(new SpParam(4, Types.VARCHAR, null, true));
                    Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "aplicaSolicitudReset", params);
                    if (vuelta != null && vuelta.length == 1) {
                        if (String.valueOf(vuelta[0]).length() == 0) {
                            throw new SQLException((String) vuelta[0], "0");
                        } else {
                            mensaje = "El acceso se ha restablecido  correctamente.<script>location.replace(\"default.jsp\")</script>";
                        }
                    } else {
                        throw new SQLException("La vuelta del servicio no es la esperada.<script>location.replace(\"default.jsp\")</script>", "0");
                    }
                } else {
                    throw new Exception("Al parecer la solicitud se ha vencido, vuelva a solicitar el restablecer su acceso.<script>location.replace(\"default.jsp\")</script>");
                }
            } else {
                mensaje = "No se localizo el servicio solicitado.";
            }
        } catch (SQLException sqle) {
            mensaje = (sqle.getSQLState().equals("0") ? sqle.getMessage() : "SQLException al detallar el medicamento del paciente." + sqle.getSQLState() + "-" + sqle.getErrorCode());
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), sqle, (String) request.getSession().getAttribute("usuario"));
        } catch (Exception e) {
            mensaje = ("Exception: " + e);
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.print(mensaje);
            out.flush();
        }
    }

    @Override
    public String getServletInfo() {
        return "Manejador de usuarios";
    }
}
