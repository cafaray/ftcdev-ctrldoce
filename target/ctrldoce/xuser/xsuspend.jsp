<%@page import="com.ftc.gedoc.bo.impl.GrupoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.GrupoBO"%>
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
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae;height: 24px; }
            .tr_non{background-color: whitesmoke;height: 24px; }
            label, input, select{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 500px; width: 800px}
        </style>
        <script>
            $(function() {
                $(".suspender").button({
                    text: false,
                    icons: {primary: "ui-icon-locked"}
                }).click(function() {
                    if(confirm("Realmente quieres suspender este usuario")){
                        var codigo = $(this).attr("codigo");
                        $.ajax({
                            type: "POST",
                            dataType: "text",
                            data: {elemento: codigo, cmd:"<%=Comunes.toMD5(session.getId())+Comunes.toMD5("xuser-deactive") %>", sesion: "<%=session.getId() %>"},
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
                    }
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
            <table cellspacing="1" cellpadding="1" style="table-layout: fixed; width:780px;border: 1px #ccc solid;">
                <tr class="tr_cab">
                    <th style="width:180px;">Empresa</th>
                    <th style="width:180px">Nombre</th>
                    <th style="width:280px">Correo</th>
                    <th style="width:100px">Grupo</th>
                    <th style="width:40px" title="Suspender">&nbsp;</th>
                </tr>
            <!-- </table>
            <table cellspacing="1" cellpadding="1" style="table-layout: fixed; width:810px;border: 1px #ccc solid; height: 440px; overflow: hidden; overflow-y:scroll; display: block;">
            -->
                <%
                    conexion = Conexion.getConexion();
                    //boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
                    List<Contacto> listado = null;
                    if (String.valueOf(session.getAttribute("propietario")).equals("S")) {
                        listado = Contacto.listaContactos(conexion, sesion);
                    } else {
                        listado = new LinkedList<Contacto>();
                    }
                    Iterator<Contacto> contactos = listado.iterator();
                    String clase = "tr_par";
                    int cont = 1;
                    GrupoBO bo = new GrupoBOImpl();                    
                    while (contactos.hasNext()) {
                        Contacto c = contactos.next();
                        //String suspender = "<input type=\"button\" class=\"ui-button ui-button-text-only ui-state-default ui-corner-all\" id=\"suspender\" codigo=\"" + c.getIdentificador() + "\" value=\"Suspender\" />";
                        String suspender = String.format("<a class=\"suspender\" codigo=\"%s\">&nbsp;</a>", c.getIdentificador());
                        String grupo = bo.buscar(c.getGrupo()).getGrupo();
                        out.println(String.format("<tr class=\"%s\">"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td></tr>",
                                clase, "200px", c.getRazonSocial(), "300px", c.getNombre().concat(" ").concat(c.getApellido()), 
                                "170px", c.getCorreo(), "100px", grupo, "40px", suspender));
                        clase = cont++%2==0?"tr_par":"tr_non";
                    }
                %>
                <tr><td colspan="5">&nbsp;</td></tr>
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
