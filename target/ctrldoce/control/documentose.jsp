<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.bo.impl.DocumentoEstatusBOImpl"%>
<%@page import="com.ftc.gedoc.bo.DocumentoEstatusBO"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.utiles.DocumentoEstatus"%>
<%@page import="com.ftc.gedoc.utiles.Documento"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.estado de facturas :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae }
            .tr_non{background-color: whitesmoke}
            input.text { margin-bottom:12px; width:90px; padding: .4em; }
            #persona{width: 200px;}
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <script>
            $(function (){
                var factura = $("#factura"),
                estatus = $("#estatus"),
                comentario = $("#comentario");
                $("#actualizar").button().click(function(){
                    $.ajax({
                        type: "POST",
                        dataType: "text",
                        data: {cmd: "<%=Comunes.toMD5("docState:update-state".concat(session.getId()))%>", factura: factura.val(), estatus: estatus.val(), comentario:comentario.val()},
                        async: true,
                        url: "../ws/status/statusmanager.do",
                        success: function(data) {
                            location.reload();
                            alert(data);
                        },
                        error: function(data) {
                            updateTips(data);
                        }
                    });
                    return false;  
                });
                $("#regresar").button().click(function (){
                    location.replace("vistadoce.jsp?tipo=p");
                });
            });
        </script>
        <%!
            
            private DocumentoEstatus.Estatus nextStep(String current){
                
                DocumentoEstatus.Estatus[] estados = DocumentoEstatus.Estatus.values();
                int iCurrent = 0, elementos = estados.length;
                for(DocumentoEstatus.Estatus estatus: estados){
                    if(estatus.getIndicador().equals(current)){
                        if(iCurrent+1>=elementos){
                            return estados[elementos-1];
                        }else{
                            return estados[iCurrent+1];
                        }
                    }
                    iCurrent++;
                }
                return null;
            }
        %>
    </head>
    <body>
        <%
            try {
                String cmd = request.getParameter("cmd");
                String factura = "-- no existe --";
                Seguridad seguridad = (Seguridad)session.getAttribute("codsec");
                String estatusActual = "p";
                Documento documento = new Documento();
                if (cmd.startsWith(Comunes.toMD5("documento.cambiar-estatus".concat(session.getId())).toUpperCase())) {
                    factura = cmd.substring(Comunes.toMD5("documento.cambiar-estatus".concat(session.getId())).length());
                    documento = documento.findById(factura);
                    if (documento != null) {
                        DocumentoEstatusBO bo = new DocumentoEstatusBOImpl();
                        List<DocumentoEstatus> docEstatus = bo.listar(documento.getPersona(), documento.getIdentificador());
        %>
        <h2>Cambiar estatus de documento: <em>"<%=documento.getTitulo() %>"</em></h2>
        <table  style="border: 1px #ccc ridge;width: 95%">
            <tr class="tr_cab">
                <th style="width: 15%">Fecha</th>
                <th style="width: 15%">Estatus</th>
                <th style="width: 25%">Usuario</th>
                <th>Comentario</th>
            </tr> 
                <%
                int conteo = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                for(DocumentoEstatus estatus:docEstatus){
                    String estilo = (conteo++%2)==0?"tr_par":"tr_non"; 
                %>
                <tr class="<%=estilo%>">
                    <td><%=dateFormat.format(estatus.getFecha()) %></td>
                    <td><%=estatus.getEstatus().getDescripcion() %></td>
                    <td><%=estatus.getUsuario() %></td>
                    <td><%=estatus.getComentario() %></td>
            </tr>
        <%
                    }
        %>
        </table>
         <%if (((String) session.getAttribute("propietario")).equals("S") && seguridad.seguridadProveedor().contains("e")) {%>
        <form id="FORM_CHANGE_STATUS" method="POST" action="../ws/status/statusmanager.do">    
            <input type="hidden" name="factura" id="factura" value="<%=factura%>" />
            <p>
                Nuevo estatus: 
                <select id="estatus" name="status">
                    <option value="-1">-- seleccione --</option>
                    <%
                        DocumentoEstatus.Estatus next;
                        if(docEstatus.size()>0){
                            if(docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_REVISION)){
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_APROBADA;                                        
                                out.print(String.format("<option value = \"%s\">%s</option>", next.getIndicador(), next.getDescripcion()));
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_RECHAZADA;                                                        
                            } else if(docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_APROBADA)) {
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PROGRAMADA;
                            } else if(docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_RECHAZADA)) {
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_CANCELADA;
                            } else if(docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PAGADA)
                                    ||docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_CANCELADA)) {
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PROCESADA;
                            } else if(docEstatus.get(docEstatus.size()-1).getEstatus().equals(DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PROGRAMADA)) {
                                next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_PAGADA;
                            } else {
                                next = nextStep(docEstatus.get(docEstatus.size()-1).getEstatus().getIndicador());                                                                   
                            }
                        } else {
                            next = DocumentoEstatus.Estatus.ESTATUS_DOCUMENTO_CARGA;                                                        
                        }
                        out.print(String.format("<option value = \"%s\">%s</option>", next.getIndicador(), next.getDescripcion()));
                    %>
                </select>
            </p>
            <p>
                Comentario: <input type="text" name="comentario" id="comentario" value="" style="width: 300px" />
            </p>
            <p>
                <a id="actualizar" cmd="<%=Comunes.toMD5("docState:update-state".concat(session.getId()))%>">Actualizar</a>
                <a id="regresar">Regresar</a>
            </p>
        </form>
        <%}
                    } else {
                        out.print("No se ha localizado un documento v&aacute;lido");
                    }
                }
            } catch (GeDocBOException e) {
                out.print(e.getMessage());
            } catch (NullPointerException e) {
                out.print("Al parecer la sesi&oacute;n se ha terminado o no se logr&oacute; determinar la factura de trabajo.");
            } catch (ArrayIndexOutOfBoundsException e){
                out.print("No se logr� determinar el siguiente elemento de estatus");
            }
        %>
</body>
</html>
