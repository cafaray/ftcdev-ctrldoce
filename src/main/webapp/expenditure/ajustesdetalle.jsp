<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.utiles.CifraControlAjuste"%>
<%@page import="com.ftc.gedoc.utiles.CifraControl"%>
<%@page import="com.ftc.gedoc.utiles.Periodo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoRegistro"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoCabecera"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String idPeriodo = request.getParameter("periodo") == null ? "" : request.getParameter("periodo");
    if (idPeriodo.length() > 0) {
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce-cifras control de per&iacute;odo - ajustes :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae; font-size: x-small}
            .tr_par{background-color: #d0d0d0;color:#0078ae; height: 24px; font-size: x-small}
            .tr_non{background-color: whitesmoke;height: 24px; font-size: x-small }
            label, input, select{ display:block; }
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

                $("#regresar").button({
                    text: true,
                    icons: {primary: "ui-icon-arrowthick-1-w"}
                }).click(function(event) {
                    event.preventDefault();
                    var cmd = $(this).attr("cmd");
                    location.replace("cifracontrol.jsp?cmd=" + cmd);
                });
            });
        </script>
        <%!
            PeriodoBo bo = new PeriodoBOImpl();
        %>
    </head>
    <body>

        <%
            Object seguridad = session.getAttribute("codsec");
            String persona = (String) session.getAttribute("persona");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script language="javascript" type="text/javascript">
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {

            try {
                //String cmd = request.getParameter("cmd")!=null?request.getParameter("cmd"):"";
                Periodo actual;
                //if(cmd.startsWith(Comunes.toMD5("v-Cifra" + session.getId()).toUpperCase())){
                //    idPeriodo = cmd.substring(Comunes.toMD5("v-Cifra" + session.getId()).length());
                actual = bo.obtenerPeriodoPorId(idPeriodo);
                //}else{
                //    actual = bo.actual(); 
                //    idPeriodo = actual.getIdentificador();
                //}
%>
        <h2>Ajustes de per&iacute;odo <%=String.format("%d-%d", actual.getAny(), actual.getPeriodo())%> - Cifras control </h2>
        <a id="regresar" cmd="<%=Comunes.toMD5("v-Cifra" + session.getId()).toUpperCase().concat(idPeriodo)%>">Regresar</a>
        <p>
        <table cellspacing="1" cellpadding="5" style="width:650px;border: 1px #ccc solid;">
            <tr class="tr_cab">
                <th>Fecha</th>
                <td>Total</td>
                <td>Asociado</td>
                <td>Tipo</td>
                <td>M&aacute;x. Asociados</td>
                <td>M&aacute;x. Tipo</td>
                <td>M&iacute;n. Asociado</td>
                <td>M&iacute;n. Tipo</td>
            </tr>            

            <%
                DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                List<CifraControlAjuste> ajustes = bo.getCifraControlAjuste(actual);
                int count = 0;
                for (CifraControlAjuste ajuste : ajustes) {
                    String idAjuste = ajuste.getAjuste();
                    count++;
                    String tr_style = count % 2 == 0 ? "tr_par" : "tr_non";
            %>
            <tr class="<%=tr_style%>">
                <td style="text-align: center"><%=dateFormat.format(ajuste.fechaAjuste)%></td>
                <td style="text-align: right"><%=decimalFormat.format(ajuste.getMonto())%></td>
                <td style="text-align: right"><%=ajuste.getRegistrosAsociado()%></td>
                <td style="text-align: right"><%=ajuste.getRegistrosTipo()%></td>
                <td style="text-align: right"><%=decimalFormat.format(ajuste.getMaxAsociado())%></td>
                <td style="text-align: right"><%=decimalFormat.format(ajuste.getMaxTipo())%></td>
                <td style="text-align: right"><%=decimalFormat.format(ajuste.getMinAsociado())%></td>
                <td style="text-align: right"><%=decimalFormat.format(ajuste.getMinTipo())%></td>
            </tr>           
    <%
                    }
                %>
            </table>
    </p>

            <%
                } catch (GeDocBOException e) {
                    out.print(e.getMessage());
                } catch (NullPointerException e) {
                    out.println("Al parecer la sesi&oacute;n expiro. Vuelve a iniciar sesi&oacute;n.");
                }
            }
        } else {
            out.print("Error: Al parecer no se han recibido los parametros correctos");
        }
    %>
</body>
</html>
