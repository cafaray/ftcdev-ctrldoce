<%@page import="com.ftc.modelo.PeriodoCabecera"%>
<%@page import="com.ftc.modelo.Periodo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce-cifras control de per&iacute;odo :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae; height: 24px }
            .tr_non{background-color: whitesmoke;height: 24px}
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
                $(".cerrar").click(function(event) {
                    event.preventDefault();
                    location.replace("index.jsp");
                });
                $(".detalle").click(function(event) {
                    event.preventDefault();
                    $("#cmd").val($(this).attr('cmd'));
                    $("#FORM_RECORDS").submit();
                });
                 $("#regresar").button({
                    text: true,
                    icons: {primary: "ui-icon-arrowthick-1-w"}
                 }).click(function (event){
                    event.preventDefault();
                    location.replace("index.jsp");
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
            String tipo = request.getParameter("tipo");
            String sesion = session.getId();
            if (seguridad == null || session.isNew()) {

        %>
        <script>
            window.parent.location.replace("../default.jsp");
        </script>
        <%        } else {
            
            
            try {
                String cmd = request.getParameter("cmd")!=null?request.getParameter("cmd"):"";
                Periodo actual;
                String idPeriodo;
                if(cmd.startsWith(Comunes.toMD5("check-details-records-" + session.getId()))){
                    idPeriodo = cmd.substring(Comunes.toMD5("check-details-records-" + session.getId()).length());
                    actual = bo.obtenerPeriodoPorId(idPeriodo);
                }else{
                    actual = bo.actual(); 
                    idPeriodo = actual.getIdentificador();
                }
        %>
        <h2>Revisi&oacute;n de per&iacute;odo <%=String.format("%d-%d", actual.getAny(), actual.getPeriodo())%></h2>
        <a id="regresar">Regresar</a>
        <form id="FORM_RECORDS" action="revisarregistros.jsp">
            <input type="hidden" name="cmd" id="cmd" value="<%=Comunes.toMD5("algun-valor-x")%>" />
        </form>
        <h2>Resumen de gastos</h2>
        <table style="width:650px;border: 1px #ccc solid;">
            <tr class="tr_cab">                
                <th style="width: 110px">Asociados a:</th>
                <th style="width: 110px">Monto</th>                                
            </tr>            
            <%
                int cont = 0;
                DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                double total = 0d;
                
                List<PeriodoCabecera> cabeceras = bo.listaCabecerasAgrupadas(idPeriodo);
                if (cabeceras!=null){
                int count = 0;
                for (PeriodoCabecera pc : cabeceras){
                    String non_par = count%2 == 0? "tr_par":"tr_non";
                    double monto = pc.getImporte();
                    
            %>
            <tr class="<%=non_par%> detalle" cmd = "<%=Comunes.toMD5(session.getId().concat("detalle-cabecera-gasto-")).concat("==").concat(idPeriodo).concat(":").concat(pc.getAsociadoA()) %>" style="cursor: pointer" >
                <td style="text-align: left"><%=pc.getAsociadoA() %></td>
                <td style="text-align: right"><%=decimalFormat.format(monto) %></td>
            </tr>
        <%
                    count++;
                }
                %>
        </table>
                <%
                }else{
                    out.print("El valor de las cabeceras es nulo para el periodo especificado. Imposible crear la lista.");
                    %>
                    <script>
                        alert("El valor de las cabeceras es nulo para el periodo <%=idPeriodo %>. Revise con el administrador de periodos.");
                    </script>
                    <%
                }
            } catch (GeDocBOException e) {
                out.print(e.getMessage());
            } catch (NullPointerException e) {
                out.println("Al parecer la sesi&oacute;n expiro. Vuelve a iniciar sesi&oacute;n.");
            }
        }
        %>        
    </body>
</html>
