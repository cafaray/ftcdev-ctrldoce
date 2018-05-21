<%@page import="com.ftc.modelo.Grupo"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.gedoc.bo.impl.GrupoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.GrupoBO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.grupo sobre permisos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            label, input, select{  }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
            h3{font-size: 1.1em; margin: .3em 0; }
        </style>
        <script>
            $(function() {
                $("#guardar").button().click(function(event) {
                    event.preventDefault();
                    $("#FORM_SECMODE").submit();
                });
                $("#regresar").button().click(function(event) {
                    event.preventDefault();
                    location.replace("grupo.jsp");
                });
            });
        </script>
        <%!
            private GrupoBO bo = new GrupoBOImpl();

            private char[] convierteBinario(int modo) {
                String binario = Integer.toBinaryString(modo);
                try {
                    binario = Comunes.rellenaCeros(binario, 25);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
                return binario.toCharArray();
            }
        %>
    </head>
    <body>
        <%
            String dsgrupo = request.getParameter("grupo") != null ? request.getParameter("grupo") : "--";
            String cmd = request.getParameter("cmd") != null ? request.getParameter("cmd") : "";
            String idgrupo = cmd.substring(Comunes.toMD5("a").length());
            try {
                Grupo grupo = bo.buscar(idgrupo);
                char[] modo;
                if (grupo != null) {
                    modo = convierteBinario((int) grupo.getModo());
                } else {
                    modo = Comunes.rellenaCeros("", 25).toCharArray();
                }
        %>
        <h2>Permisos para el grupo <%=dsgrupo%></h2>
        <form id="FORM_SECMODE" method="POST" action="../ws/grupo/gsecmod.do">
            <input type="hidden" name="idgrupo" id="idgrupo" value="<%=((Comunes.toMD5("chmod")+session.getId()) + idgrupo).toUpperCase() %>" />
            <table style="width:790px;border: 1px #ccc solid;">
                <tr>
                    <td colspan="4"><h3>M&oacute;dulos de la aplicaci&oacute;n</h3></td>
                </tr>
                <tr>
                    <th>Administrador</th>
                    <th>Proveedor</th>
                    <th>Cliente</th>
                    <th>Gestor gastos</th>                
                </tr>
                <tr>
                    <td style="vertical-align: top">
                        <ul style="list-style: none">
                            <li><input type="checkbox" value="1" <%=(modo[20] == '1' ? "checked" : "")%> name="usuarios" />&nbsp;Manejo de usuarios</li>
                            <li><input type="checkbox" value="1" <%=(modo[21] == '1' ? "checked" : "")%> name="cambiar" />&nbsp;Cambiar contrase&ntilde;a</li>
                            <li><input type="checkbox" value="1" <%=(modo[22] == '1' ? "checked" : "")%> name="suspender" />&nbsp;Suspender/Eliminar usuario</li>
                            <li><input type="checkbox" value="1" <%=(modo[23] == '1' ? "checked" : "")%> name="activar" />&nbsp;Activar usuario</li>
                            <li><input type="checkbox" value="1" <%=(modo[24] == '1' ? "checked" : "")%> name="grupos" />&nbsp;Gestor de grupos</li>
                        </ul>
                    </td>
                    <td style="vertical-align: top">
                        <ul style="list-style: none">
                            <li><input type="checkbox" value="1" <%=(modo[0] == '1' ? "checked" : "")%> name="pregistro" />&nbsp;Registro de empresa</li>
                            <li><input type="checkbox" value="1" <%=(modo[1] == '1' ? "checked" : "")%> name="pcontacto" />&nbsp;Registro de contacto</li>
                            <li><input type="checkbox" value="1" <%=(modo[2] == '1' ? "checked" : "")%> name="pverdoc" />&nbsp;Ver documentos</li>
                            <li><input type="checkbox" value="1" <%=(modo[3] == '1' ? "checked" : "")%> name="psubirdoc" />&nbsp;Subir documentos</li>
                            <li><input disabled="disabled" type="checkbox" value="1" <%=(modo[4] == '1' ? "checked" : "")%> name="pnotificacion" />&nbsp;Notificaciones</li>
                            <li><input disabled="disabled" type="checkbox" value="1" <%=(modo[5] == '1' ? "checked" : "")%> name="pestado" />&nbsp;Estado</li>
                            <li><input type="checkbox" value="1" <%=(modo[6] == '1' ? "checked" : "")%> name="peliminar" />&nbsp;Eliminar documentos</li>
                            <li><input type="checkbox" value="1" <%=(modo[7] == '1' ? "checked" : "")%> name="pdescarga" />&nbsp;Descarga documentos</li>
                        </ul>
                    </td>
                    <td style="vertical-align: top">
                        <ul style="list-style: none">
                            <li><input type="checkbox" value="1" <%=(modo[8] == '1' ? "checked" : "")%> name="cregistro" />&nbsp;Registro de empresa</li>
                            <li><input type="checkbox" value="1" <%=(modo[9] == '1' ? "checked" : "")%> name="ccontacto" />&nbsp;Registro de contacto</li>
                            <li><input type="checkbox" value="1" <%=(modo[10] == '1' ? "checked" : "")%> name="cverdoc" />&nbsp;Ver documentos</li>
                            <li><input type="checkbox" value="1" <%=(modo[11] == '1' ? "checked" : "")%> name="csubirdoc" />&nbsp;Subir documentos</li>
                            <li><input disabled="disabled" type="checkbox" value="1" <%=(modo[12] == '1' ? "checked" : "")%> name="cnotificacion" />&nbsp;Notificaciones</li>
                            <li><input disabled="disabled" type="checkbox" value="1" <%=(modo[13] == '1' ? "checked" : "")%> name="cestado" />&nbsp;Estado</li>
                            <li><input type="checkbox" value="1" <%=(modo[14] == '1' ? "checked" : "")%> name="celiminar" />&nbsp;Eliminar documentos</li>
                            <li><input type="checkbox" value="1" <%=(modo[15] == '1' ? "checked" : "")%> name="cdescarga" />&nbsp;Descarga documentos</li>
                        </ul>
                    </td>
                    <td style="vertical-align: top">
                        <ul style="list-style: none">
                            <li><input type="checkbox" value="1" <%=(modo[16] == '1' ? "checked" : "")%> name="viaticos" />&nbsp;Vi&aacute;ticos vendedores</li>
                            <li><input type="checkbox" value="1" <%=(modo[17] == '1' ? "checked" : "")%> name="caja" />&nbsp;Caja chica</li>
                            <li><input type="checkbox" value="1" <%=(modo[18] == '1' ? "checked" : "")%> name="aduanales" />&nbsp;Agentes aduanales</li>
                            <li><input type="checkbox" value="1" <%=(modo[19] == '1' ? "checked" : "")%> name="administrador" />&nbsp;Administrador Gastos</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" style="text-align: right">
                        <a id="guardar">Guardar</a>
                        <a id="regresar">Regresar</a>
                    </td>
                </tr>
            </table>
        </form>
        <%
            } catch (GeDocBOException e) {
                out.print(e.getMessage());
            }
        %>
    </body>
</html>
