<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.gedoc.bo.impl.GrupoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.GrupoBO"%>
<%@page import="com.ftc.gedoc.utiles.Grupo"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.gestor de grupos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            table th{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae;height: 24px; }
            .tr_non{background-color: whitesmoke;height: 24px; }
            label, input, select{  }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
        </style>
        <script>
            $(function() {                
                $(".Asignar_permisos").click(function() {
                    
                    $("#cmd").val($(".asignar_permisos").attr( "cmd" ));
                    $("#grupo").val($(".asignar_permisos").attr( "grupo" ));
                    $("#FORM_ACTION").submit();
                });
                $(".Aliminar").click(function() {                    
                    
                    var grupo = $(".eliminar").attr("grupo");
                    if(confirm("Esta seguro de querer eliminar el grupo "+grupo)){
                        $("#cmd").val($(".eliminar").attr("cmd"));
                        $("#FORM_ACTION").submit();
                    }
                });
                $("#nuevoGrupo").button().click(function(event) {
                    event.preventDefault();                
                    $("#FORM_NEWGROUP").submit();
                });
            });
            function ejecuta(grupo,cmd){
                if(cmd.indexOf('<%=Comunes.toMD5("e").toUpperCase()%>')>-1){
                    if(confirm("Realmente quiere eliminar el grupo "+grupo)){
                        document.getElementById("cmd").value= cmd;
                        document.getElementById("grupo").value = grupo;
                        document.getElementById("FORM_ACTION").submit();
                    }
                } else {
                    document.getElementById("cmd").value= cmd;
                    document.getElementById("grupo").value = grupo;
                    document.getElementById("FORM_ACTION").submit();
                }
            }
        </script>
    </head>
    <body>
        <%! 
            GrupoBO bo = new GrupoBOImpl();
        %>
        
        <%
            String cmd = request.getParameter("cmd");
            String id;
            if(cmd!=null){
                if(cmd.startsWith(Comunes.toMD5("a").toUpperCase())){
                    id = cmd.substring(Comunes.toMD5("a").length());
                    String grupo = request.getParameter("grupo")==null?"--":request.getParameter("grupo");
                    response.sendRedirect(String.format("gpermisos.jsp?cmd=%s&origen=%s&grupo=%s",cmd,request.getContextPath(),grupo));
                }else if(cmd.startsWith(Comunes.toMD5("e").toUpperCase())){
                    id = cmd.substring(Comunes.toMD5("e").length());
                    try{
                        bo.eliminar(id);
                    }catch(GeDocBOException e){
                        out.println(e.getMessage());
                    }
                } else if(cmd.equals(Comunes.toMD5(session.getId()+"r").toUpperCase())){
                    String nombre = request.getParameter("grupo")==null?"":request.getParameter("grupo").trim();                    
                    if(!nombre.isEmpty()){
                        Grupo grupo = new Grupo();
                        grupo.setGrupo(nombre);
                        bo.actualizar(grupo);
                    }else{
                        out.print("Imposible registrar un grupo sin nombre.");
                    }
                }
            }
        %>
        <form id="FORM_ACTION" method="POST">
            <input type="hidden" name="cmd" id="cmd" value="" />
            <input type="hidden" name="grupo" id="grupo" value="" />
        </form>
        <h2>Gestor de grupos</h2>
        <table cellspacing="1" cellpadding="5" style="width:650px;border: 1px #ccc solid;">
            <tr>
                <th style="width: 580px">Grupo</th>
                <th style="width: 35px">&nbsp;</th>
                <th style="width: 35px">&nbsp;</th>                
            </tr>
<!--        </table>
        <table style="width: 650px;overflow: hidden;overflow-y: scroll;border: 1px #ccc solid;">-->
            <% 
            List<Grupo> grupos = bo.listar();
            int cont = 0;
            String clase = "tr_non";
            for(Grupo grupo: grupos){
                StringBuilder linea = new StringBuilder();
                linea.append("<tr class=\"").append(clase).append("\">");
                linea.append("<td style=\"width: 580px\">").append(grupo.getGrupo()).append("</td>");
                linea.append("<td style=\"width: 35px\">").append(String.format("<label onClick=\"ejecuta('%s','%s');\" style=\"cursor:pointer;\" class=\"asignar_permisos\" grupo=\"\" cmd=\"\">%s</label>", grupo.getGrupo(), (Comunes.toMD5("a").toUpperCase() + grupo.getIdentificador()), 
                        "<img src=\"../resources/images/lock.png\" border=\"0\" title=\"Permisos\" height=\"26\" />")).append("</td>");
                linea.append("<td style=\"width: 35px\">").append(String.format("<label style=\"cursor:pointer;\" onClick=\"ejecuta('%s','%s');\" class=\"eliminar\" grupo=\"\" cmd=\"\">%s</label>", grupo.getGrupo(),(Comunes.toMD5("e").toUpperCase() + grupo.getIdentificador()), 
                        "<img src=\"../resources/images/trash.png\" border=\"0\" title=\"Eliminar\" height=\"26\" />")).append("</td>");
                linea.append("</tr>");
                out.print(linea.toString());
                clase = cont++%2==0?"tr_par":"tr_non";
            }
            %>
        </table>
        <p>
        <form id="FORM_NEWGROUP" method="POST">
            Nuevo Grupo:&nbsp;&nbsp;&nbsp;<input type="text" name="grupo" id="grupo" value="" class="ui-corner-tl" />
            <input type="hidden" name="cmd" value="<%=Comunes.toMD5(session.getId()+"r").toUpperCase() %>" />
            <a id="nuevoGrupo">Registrar</a>
        </form>
        </p>
    </body>
</html>
