<%@page import="com.ftc.modelo.Periodo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.gestor de gastos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 85.5%;
            }
            label, input, select{ display:block; }
            input.text { margin-bottom:12px; width:95%; padding: .4em; }
            select { margin-bottom:12px; width:95%; padding: .4em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            titulo { font-size: 1.2em; margin: .6em 0; font-weight: bold;}
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
            .aperturar{cursor: pointer; border: silver 1px groove; padding-top: 10px;}
            .cerrar{cursor: pointer; border: silver 1px groove; padding-top: 10px;}
            .cifras{cursor: pointer; border: silver 1px groove; padding-top: 10px;}
            .periodo {background-color: silver; }
            #revisar {cursor: pointer; font-weight: bold;}
        </style>
        <script>
            $(function() {
                var cmd = $("#cmd");
                $("#revisar").click(function (event){
                    event.preventDefault();
                    var sha = ($(this).attr("cmd"));
                    $(location).attr("href", "revisarcifras.jsp?cmd="+sha);
                });
                $(".cifras").click(function(event) {
                    event.preventDefault();                    
                    cmd.val($(this).attr('cmd'));
                    $("#FORM_RECORDS").submit();
                });                
                $(".aperturar").click(function(){
                    alert("El periodo ya ha sido cerrado, para realizar ajustes es necesario que existan movimientos con valores mayor a cero.");
                });
                $(".ajustar").click(function(event){
                    event.preventDefault();                    
                    $(location).attr('href', 'ajuste.jsp?cmd=' + $(this).attr('cmd'));
                    
                });
                $("#abrir").button({
                    text: true,
                    icons: {primary: "ui-icon-folder-open"}
                }).click(function (event){
                    event.preventDefault();
                    cmd.val($(this).attr('cmd'));
                    $.ajax({
                        url: '../ws/expenditure/record.do',
                        type: 'POST',
                        data: {cmd: cmd.val()},
                        cache: false,
                        dataType: "text",
                        async: true,
                        success: function(data) {
                            alert(data);
                            console.info(data);
                            location.reload();
                        },
                        error: function(data) {
                            alert(data);
                        }
                    });
                })
                $(".cerrar").click(function(event) {
                    event.preventDefault();
                    cmd.val($(this).attr('cmd'));
                    if (confirm("Una vez cerrado el per�odo actual no se podra abrir nuevamente, solo podran hacerse ajustes. Deseas continuar?")) {
                        $.ajax({
                            url: '../ws/expenditure/record.do',
                            type: 'POST',
                            data: {cmd: cmd.val()},
                            cache: false,
                            dataType: "text",
                            async: true,
                            success: function(data) {
                                alert(data);
                                console.info(data);
                                location.reload();
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                    }
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
        <%        } else {  %>
        <form id="FORM_RECORDS" method="POST" action="cifracontrol.jsp">
            <input type="hidden" id="cmd" name="cmd" value="" />            
        </form>
        <p style="width: 100%">
            <span style="font-size: 1.2em; margin: .6em 0; font-weight: bold;">Gestor de periodos</span>
            <span style="float: right">
        <a href="#" id="abrir" cmd="<%=Comunes.toMD5(session.getId().concat("generar-periodo-actual-")) %>">Generar per&iacute;odo actual</a>
        </span>
        </p>
        <table style="width:650px;border: 1px #ccc solid;">
            <tr>                
                <th style="width:100px">Per&iacute;odo</th>
                <th style="width:100px">Estatus</th>
                <th style="width:100px">F. Apertura</th>
                <th style="width:100px">F. Cierre</th>
                <th style="width:100px">Monto</th>
                <th style="width:50px" title="Ajuste a per&iacute;odo">&nbsp;</th>
                <th style="width:50px" title="Cerrar per&iacute;odo">&nbsp;</th>
                <th style="width:50px" title="Cifras control">&nbsp;</th>
            </tr>            
        </table>
        <table style="width: 650px;overflow: hidden;overflow-y: scroll;border: 1px #ccc solid;">
            <%
                List<Periodo> periodos = bo.listado();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                for (Periodo periodo : periodos) {
                    String id = periodo.getIdentificador();
                    int any = periodo.getAny();
                    int mes = periodo.getPeriodo();
                    Date fechaApertura = periodo.getApertura();
                    Date fechaCierre = periodo.getCierre();
                    //double monto = periodo.getMonto();
                    String cerrar, cifras, titleCerrar, titleCifras, ajuste, titleAjustar, imagenAjustar, aTR = "";
                    if(periodo.getCuenta()>0 && periodo.getDsEstatus().equals("Cerrado")){
                        imagenAjustar = "<img src = \"../resources/images/settings.png\" border=\"0\" height=\"24\" />";
                        ajuste = String.format("<a class=\"ajustar\" cmd=\"%s\" href=\"#\">%s</a>", 
                                Comunes.toMD5("xpenditures.ajustar-periodo:".concat(session.getId())).concat(periodo.getIdentificador()).toUpperCase(),
                            imagenAjustar);
                        titleAjustar = "Aplicar ajuste";
                    } else {
                        imagenAjustar = "<img src = \"../resources/images/settings_disabled.png\" border=\"0\" height=\"24\" />";
                        ajuste = String.format("<a href=\"javascript:alert('Nada que ajustar')\">%s</a>", imagenAjustar);
                        titleAjustar = "Nada que ajustar";
                        
                    }
                    if(periodo.getEstatus().equals("C")){
                        cerrar = String.format("<a class=\"aperturar\" cmd=\"%s\">%s</a>", Comunes.toMD5("xpenditures.aperturar-periodo".concat(session.getId())).toUpperCase(),
                            "<img src = \"../resources/images/aperturar.png\" border=\"0\" height=\"24\" />");
                        titleCerrar = "Ajustes a periodo";
                        cifras = String.format("<a class=\"cifras\" cmd=\"%s\">%s</a>", Comunes.toMD5("v-Cifra" + session.getId()).toUpperCase() + (id),
                            "<img src = \"../resources/images/cifrasControl.jpg\" border=\"0\" height=\"24\" />");
                        titleCifras = "Cifras control";
                    }else {
                        cerrar = String.format("<a class=\"cerrar\"  cmd=\"%s\">%s</a>", Comunes.toMD5("xpenditures.cerrar-periodo".concat(session.getId())).toUpperCase(),
                            "<img src = \"../resources/images/cerrarPeriodo.png\" border=\"0\" height=\"24\" />");
                        titleCerrar = "Cerrar periodo";
                        cifras = String.format("<a class=\"cifras\" cmd=\"%s\" style=\"pointer-events:none;\">%s</a>", Comunes.toMD5("v-Cifra" + session.getId()).toUpperCase() + (id),
                            "<img src = \"../resources/images/cifrasControl.jpg\" border=\"0\" height=\"24\" />");
                        titleCifras = "--";
                        aTR = "id=\"revisar\" cmd = \""+Comunes.toMD5("check-details-records-" + session.getId()).concat(periodo.getIdentificador())+"\"";
                    }
                    DecimalFormat numberFormat = new DecimalFormat("###,###,##0.##");
            %>
            <tr>                
                <td style="width:100px" class="periodo" <%=aTR %>><%=any%>-<%=mes%></td>
                <td style="width:100px"><%=periodo.getDsEstatus()%></td>
                <td style="width:100px"><%=dateFormat.format(fechaApertura)%></td>
                <td style="width:100px"><%=fechaCierre != null ? dateFormat.format(fechaCierre) : ""%></td>
                <td style="width:100px;text-align: right;"><%=numberFormat.format(periodo.getMonto()) %></td>
                <td style="width:50px" title="<%=titleAjustar %>"><%=ajuste%></td>
                <td style="width:50px" title="<%=titleCerrar %>"><%=cerrar%></td>
                <td style="width:50px" title="<%=titleCifras %>"><%=cifras%></td>
            </tr>
            <%
                }
            }
            %>
        </table>
    </body>
</html>
