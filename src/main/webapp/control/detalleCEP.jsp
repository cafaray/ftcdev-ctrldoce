<%@page import="com.ftc.modelo.CEPPago"%>
<%@page import="com.ftc.modelo.CEPConcepto"%>
<%@page import="com.ftc.modelo.CEPCabecera"%>
<%@page import="com.ftc.gedoc.dao.impl.CEPConceptoDAOImpl"%>
<%@page import="com.ftc.gedoc.dao.CEPConceptoDAO"%>
<%@page import="com.ftc.gedoc.dao.impl.CEPPagoDAOImpl"%>
<%@page import="com.ftc.gedoc.dao.CEPPagoDAO"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ftc.gedoc.dao.impl.CEPCabeceraDAOImpl"%>
<%@page import="com.ftc.gedoc.dao.CEPCabeceraDAO"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <%
        String cmd = request.getParameter("cmd");
        Seguridad seguridad = (Seguridad) session.getAttribute("codsec");
        String sesion = session.getId();
        String uuid = "";
        if (cmd!=null && cmd.startsWith(Comunes.toMD5("documento.detalle-".concat(session.getId())).toUpperCase())){
            uuid = cmd.substring(Comunes.toMD5("documento.detalle-".concat(session.getId())).toUpperCase().length());
        
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Listado de CEPS del Proveedor</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/regional.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/timepicker.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 75.5%;
            }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae;}
            .tr_par{background-color: #d0d0d0;color:#0078ae }
            .tr_non{background-color: whitesmoke}
            input.text { margin-bottom:12px; width:90px; padding: .4em; }
            #persona{width: 200px;}
            fieldset { padding:0; border:0; margin-top:25px;}
            h2 { font-size: 1.2em; margin: .6em 0; color: #0078ae}
            thead {font-size: 0.9em; text-align: center; color: #dddddd; background-color: #026890}
            tr {font-size: 0.9em; text-align: center; color: #0084ff; background-color: #dddddd}
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
        </style>
        <script>
            $(function() {
                $("#asociar").button({
                    text: true,
                    icons: {primary: "ui-icon-gear"}
                }).click(function() {                                        
                    var cmd = $(this).attr("cmd");      
                    var estatus = $(this).attr("estatus");
                    if (cmd.charAt(0)==='c' && estatus==="Pendiente"){
                        $.ajax({
                            type: "POST",
                            dataType: "text",
                            data: {cmd: cmd, asignado: "S"},
                            async: false,
                            url: "../ws/files/binddocs.do",
                            success: function(data) {                                
                                alert(data);
                                location.replace("listadoCEP.jsp");
                            },
                            error: function(data) {
                                alert(data);
                            }
                        });
                        return false;                        
                    } else {
                        alert("Imposible asociar el pago si no se encuentran todos los documentos asociados o ya ha sido asociado previamente.");
                    }
                    //location.replace("./detalleCEP.jsp?cmd="+cmd);
                });                
            });
        </script>
    </head>
    <body>
        <%            if (seguridad == null || session.isNew()) {
        %>
        <script>
            window.parent.location.replace("../default.jsp");
        </script>
        <%} else {
            String mensaje = "";
            boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
            boolean completo = true; // indica si se encontr� la relaci�n de todos los documentos asociados al pago
            String estatus = request.getParameter("estatus")!=null?request.getParameter("estatus"):"Sin estatus";
            try {
                CEPCabeceraDAO dao = new CEPCabeceraDAOImpl();
                CEPCabecera cabecera = dao.obtieneCEP(uuid);
                if (cabecera!=null){                    
                    String pagoUUID, pagoSerie, pagoFolio, pagoFechaTimbrado, rfcEmisor;
                    pagoUUID = cabecera.getUuid();
                    pagoSerie = cabecera.getSerie();
                    pagoFolio = cabecera.getFolio();
                    pagoFechaTimbrado = Comunes.date2String(cabecera.getFechaTimbrado(), 1);
                    rfcEmisor = cabecera.getRfcEmisor();
                    String newCmd = Comunes.toMD5("asociarDocele-".concat(session.getId())).toLowerCase().concat(cabecera.getIdentificador());

        %>
        <p class="documentoPago">
            <h2>Documento de pago <%=estatus%></h2>
            <table>
                <tr>
                    <th>UUID</th>
                    <th>Serie</th>
                    <th>Folio</th>
                    <th>Fecha</th>
                    <th>Emisor</th>
                </tr>
                <tr>
                    <td><%=pagoUUID %></td>
                    <td><%=pagoSerie %></td>
                    <td><%=pagoFolio %></td>
                    <td><%=pagoFechaTimbrado %></td>
                    <td><%=rfcEmisor %></td>
                </tr>
            </table>            
        </p>
        <p>
            <h2>Conceptos</h2>
            <table>
                <tr>
                    <th>Cantidad</th>
                    <th>Producto Servicio</th>
                    <th>Descripcion</th>
                </tr>
                <%
                    CEPConceptoDAO daoConcepto = new CEPConceptoDAOImpl(uuid);
                    List<CEPConcepto> conceptos = daoConcepto.listaCEPConcepto();
                    for (CEPConcepto concepto:conceptos){
                        String producto, descripcion;
                        int cantidad;
                        producto = concepto.getClaveProdServ();
                        descripcion = concepto.getDescripcion();
                        cantidad = concepto.getCantidad();
                %>
                <tr>
                    <td><%=cantidad %></td>
                    <td><%=producto %></td>
                    <td><%=descripcion %></td>
                </tr>
                <%
                    }
                %>                
            </table>            
        </p>

        <p>
        <h2>Pagos</h2>
            <table>
                <tr>
                    <th>Cta. Ordenante</th>
                    <th>Cta. Beneficiario</th>
                    <th>UUID</th>
                    <th>Serie/Folio</th>
                    <th style="width: 100px;">Saldo anterior</th>
                    <th style="width: 100px;">Pagado</th>
                    <th style="width: 100px;">Insoluto</th>
                </tr>
                <%
                    CEPPagoDAO daoPago = new CEPPagoDAOImpl(uuid);
                    List<CEPPago> pagos = daoPago.listaPagos();
                    for(CEPPago pago:pagos){
                        double saldoAnterior, pagado, insoluto;
                        saldoAnterior = pago.getDocumentoRelacionado().getSaldoAnt();
                        pagado = pago.getDocumentoRelacionado().getPagado();
                        insoluto = pago.getDocumentoRelacionado().getSaldoInsoluto();
                        
                        NumberFormat numberFormat = new DecimalFormat("#,###,##0.0#");
                        String docele = daoPago.obtieneAsociado(pago.getDocumentoRelacionado().getIdDocumento());
                        String foreGround = docele.length()>0?"#004f99":"red";
                %>
                <tr>
                    <td><%=pago.getCtaOrdenante() %></td>
                    <td><%=pago.getCtaBeneficiario() %></td>
                    <td style="color: <%=foreGround %>"><%=pago.getDocumentoRelacionado().getIdDocumento() %></td>
                    <td><%=pago.getDocumentoRelacionado().getSerie().concat("/").concat(pago.getDocumentoRelacionado().getFolio()) %></td>
                    <td style="text-align: right"><%=numberFormat.format(saldoAnterior) %></td>
                    <td style="text-align: right"><%=numberFormat.format(pagado) %></td>
                    <td style="text-align: right"><%=numberFormat.format(insoluto) %></td>
                    <%
                        if (docele.length()>0){
                            completo &= true;
                        } else {
                            completo = false;
                        }
                    %>
                </tr>
                <%
                    }
                    newCmd = (completo?"c":"i").concat(newCmd);
                %>
            </table>
        </p>
        <p>
            <a id="asociar" cmd="<%=newCmd %>" estatus = "<%=estatus%>">Asociar</a>
        </p>
        <%
                
                    } else {
                            out.print("No se ha localizado el documento de pago en la BD "+uuid);
                    }
                } catch (Exception e) {
                    mensaje = "Excepci�n al realizar el proceso. " + e.getMessage();
                    Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) session.getAttribute("usuario"));
                } finally {

                }
                if (mensaje.length() > 0) {
                    out.println(String.format("<script>alert(\"%s\")</script>", mensaje));
                }
            }
        } else {
            out.print("No se ha localizado un identificador valido de documento. Verifique que exista el UUID");
        }
        %>
        <script>
            function ver(url) {
                window.parent.frames[1].location = url;
            }
        </script>
    </body>
</html>
