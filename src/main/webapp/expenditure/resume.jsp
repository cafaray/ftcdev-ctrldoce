<%@page import="com.ftc.gedoc.utiles.Periodo"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.ftc.gedoc.utiles.comparators.ExpensesComparatorPorDocumento"%>
<%@page import="com.ftc.gedoc.utiles.comparators.ExpensesComparatorPorEstatus"%>
<%@page import="com.ftc.gedoc.utiles.comparators.ExpensesComparatorPorReferencia"%>
<%@page import="com.ftc.gedoc.utiles.comparators.ExpensesComparatorPorFecha"%>
<%@page import="com.ftc.gedoc.utiles.comparators.ExpensesComparatorPorAsignado"%>
<%@page import="java.util.Collections"%>
<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ftc.gedoc.bo.impl.ContactoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.ContactoBO"%>
<%@page import="com.ftc.gedoc.utiles.Contacto"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoRegistro"%>
<%@page import="com.ftc.gedoc.utiles.PeriodoCabecera"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.gedoc.bo.impl.PeriodoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PeriodoBo"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            String carpetaLog = getInitParameter("logLocation");
            try {
                
                Seguridad codsec = (Seguridad) session.getAttribute("codsec");
                boolean esAdmin = codsec.seguridadGastos().contains("s");
                ContactoBO boContacto = new ContactoBOImpl();
                String nombre = "", correo = "";
                Contacto contacto = null;
                String tipoGasto = request.getParameter("tipo_gasto") == null ? "" : request.getParameter("tipo_gasto");
                String periodo = request.getParameter("periodo") == null ? "." : request.getParameter("periodo");
                if (!esAdmin) {
                    contacto = boContacto.buscarPorCorreo((String) session.getAttribute("usuario"));
                    if (contacto != null) {
                        nombre = contacto.getNombre().concat(" ").concat(contacto.getApellido());
                        correo = contacto.getCorreo();
                    }
                }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.listado de gastos :::</title>
        <script src="../js/jquery-1.7.2.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/regional.js"></script>
        <script src="../js/jquery-ui-1.10.2/js/timepicker.js"></script>
        <link rel="stylesheet" href="../js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <style>
            body {
                font-family:  "Verdana", "Helvetica", "Trebuchet MS","Arial", "sans-serif";
                font-size: 80.5%;
            }
            
            file { display:block; }
            input.text { margin-bottom:12px;  padding: .2em; }
            fieldset { padding:0; border:0; margin-top:25px; }
            h1 { font-size: 1.2em; margin: .6em 0; }
            h2 { font-size: 1.2em; margin: .6em 0; }
            .tr_cab{background-color: #dddddd ;font-weight: bold;color: #0078ae; height: 10px;}
            .tr_par{background-color: #d0d0d0;color:#0078ae; height: 10px; }
            .tr_non{background-color: whitesmoke; height: 10px;}
            .tr_out{background-color: #ff9; height: 10px; color: red;}
            div#users-contain { width: 350px; margin: 20px 0; }
            div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
            div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }            
        </style>
        <script>
            $(function () {
                var dialog,
                asignar = $("#asignar"),
                        usuario = $("#usuario"),
                        documento = $("#documento"),
                        referencia = $("#referencia"),
                        tipo = $("#tipo_gasto"),
                        fecha = $("#fecha_gasto")
                        usuarioConsultado = "";
            <%if (!esAdmin) {%>
                asignar.attr("disabled", 'disabled');
            <%}%>
                usuario.attr("disabled", 'disabled');
                function registrar() {
                    if (usuario.val() === "") {
                        alert("Antes debe seleccionar un usuario valido.");
                    } else {                        
                        console.log(asignar.val()+", "+tipo.val()+", "+documento.val()+", "+referencia.val()+", "+fecha.val());
                        $.ajax({
                            type: "POST",
                            dataType: "text",
                            data: {asignar: asignar.val(), tipo: tipo.val(), documento: documento.val(), referencia:referencia.val(), fechaGasto:fecha.val(), cmd: "<%=Comunes.toMD5("registra-Cabecera" + session.getId())%>"},
                            async: false,
                            url: "../ws/expenditure/record.do",
                            success: function (data) {
                                alert(data);
                                asignar.val("");
                                usuario.val("");
                                fecha.val("");
                                location.reload();
                                dialog.dialog( "close" );
                                //console.log("Request did it ...");
                            },
                            error: function (data) {
                                updateTips(data);
                            }
                        });
                    }
                    return false;
                }
                
                dialog = $( "#dialog-form" ).dialog({
                    autoOpen: false,
                    height: 400,
                    width: 350,
                    modal: true,
                    buttons: {
                      "Registrar gasto": function(){
                          registrar();
                      },
                      Cancelar: function() {
                        //form[ 0 ].reset();
                        dialog.dialog( "close" );
                      }
                    },
                    close: function() {
                      //form[ 0 ].reset();
                      //allFields.removeClass( "ui-state-error" );
                    }
                });
                
                
                $("#sort_asignado").click(function() {
                    $("#sortBy").val("a");
                    $("#FORM_SORT").submit();
                });
                $("#sort_fecha").click(function() {
                    $("#sortBy").val("f");
                    $("#FORM_SORT").submit();
                });
                $("#sort_documento").click(function() {
                    $("#sortBy").val("d");
                    $("#FORM_SORT").submit();
                });
                $("#sort_referencia").click(function() {
                    $("#sortBy").val("r");
                    $("#FORM_SORT").submit();
                });                
                $("#sort_estatus").click(function() {
                    $("#sortBy").val("s");
                    $("#FORM_SORT").submit();
                });
                
                $("#asignar").change(function (event) {
                    //console.info(event);
                    usuario.val(usuarioConsultado);
                });

                $("#asignar").autocomplete({
                    source: '../ws/expenditure/xpspersonas.do?callback=?',
                    minLength: 2,
                    select: function (event, ui) {
                        //console.info(event);
                        //console.info("selecciono a " + ui.item.label + " " + ui.item.usuario);
                        usuarioConsultado = ui.item ? ui.item.usuario : '';
                        usuario.val(usuarioConsultado);
                    }
                });
                $("#buscar").button({
                    text: false,
                    icons: {primary: "ui-icon-search"}
                });
                
                $("#todos").button({
                    text: false,
                    icons: {primary: "ui-icon-arrowrefresh-1-s"}
                });
                $("#buscar").button().click(function(){
                    var form = $("#FORM_SEARCH_RECORDS");
                    $("#ver_todos").val(".");
                    form.submit();
                });
                $("#todos").button().click(function(){
                    var form = $("#FORM_SEARCH_RECORDS");
                    form.submit();
                });
                <%if(esAdmin){%>
                $("#asignado").autocomplete({
                    source: '../ws/expenditure/xpspersonas.do?callback=?',
                    minLength: 2,
                    select: function (event, ui) {
                        //console.info(event);
                        //console.info("selecciono a " + ui.item.label + " " + ui.item.usuario);
                        nombreConsultado = ui.item ? ui.item.label : '';
                        $("#asignado").val(nombreConsultado);
                    }
                });
                <%}%>
                $(".consultar").button({
                    text: false,
                    icons: {primary: "ui-icon-search"}
                }).click(function () {
                    event.preventDefault();
                    $("#cmd").val($(this).attr('cmd'));
                    $("#FORM_RECORDS").submit();
                });
                $(".editar").button({
                    text: false,
                    icons: {primary: "ui-icon-pencil"}
                }).click(function (event) {
                    event.preventDefault();
                    $("#cmd").val($(this).attr('cmd'));
                    $("#FORM_RECORDS").submit();
                });
                $(".eliminar").button({
                    text: false,
                    icons: {primary: "ui-icon-trash"}
                }).click(function (event) {
                    event.preventDefault();
                    $("#cmd").val($(this).attr('cmd'));
                    $("#FORM_RECORDS").attr("action", "../ws/expenditure/record.do");
                    $("#FORM_RECORDS").submit();
                });
                $( "#nueva" ).button({
                    text:true,
                    icons: {primary:"ui-icon-document"}
                }).on( "click", function() {
                    dialog.dialog( "open" );
                });
                $("#consultar_historico").button({
                    text: true,
                    icons: {primary: "ui-icon-search"}
                }).click(function(){
                    var periodoHistorico = $("#periodo").val();
                    if (periodoHistorico===''){
                        alert("Se requiere especificar un valor de periodo a consultar");
                        return;
                    } else {
                        var url = "./resume.jsp?filtrar=&tipo_gasto=<%=tipoGasto %>&periodo="+periodoHistorico;
                        console.log("Redirecting to url: "+url);
                        $(location).attr('href',url);
                        //ver(url);
                    }
                });
                $("#primero").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-first"}
                });
                $("#anterior").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-prev"}
                });
                $("#siguiente").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-next"}
                });
                $("#ultimo").button({
                    text: false,
                    icons: {primary: "ui-icon-seek-end"}
                });
                $("#fechai").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    defaultDate: "0D",
                    changeMonth: true,
                    numberOfMonths: 2,
                    regional: "es",
                    maxDate: "0D",
                    onClose: function(selectedDate) {
                        $("#fechaf").datepicker("option", "minDate", selectedDate);
                    }
                });
                $("#fechaf").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    defaultDate: "0D",
                    changeMonth: true,
                    numberOfMonths: 2,
                    regional: "es",
                    maxDate: "0D",
                    onClose: function(selectedDate) {
                        $("#fechai").datepicker("option", "maxDate", selectedDate);
                    }
                });    
                $("#fecha_gasto").datepicker({
                    showOn: "both",
                    buttonImage: "../js/calendario.png",
                    buttonImageOnly: true,
                    showAnim: "slideDown",
                    defaultDate: "0D",                    
                    changeMonth: true,
                    numberOfMonths: 2,
                    regional: "es",
                    maxDate: "0D",
                    minDate: "-365D"                    
                });
                $("#fecha_gasto").datepicker('setDate', new Date());
            });
            
            function ver(url){
                window.parent.frames[1].location = url;
            }
        </script>
    </head>
    <body>
        <%
            
            boolean filtrar = request.getParameter("filtrar")!=null;
            List<String> params =  new ArrayList<String>();
            if(filtrar){
                String fechaInicio = request.getParameter("fechai")!=null?request.getParameter("fechai"):"";
                String fechaTermino = request.getParameter("fechaf")!=null?request.getParameter("fechaf"):"";
                String asignadoA = request.getParameter("asignado")!=null?request.getParameter("asignado"):"";
                String documento = request.getParameter("documento_buscar")!=null?request.getParameter("documento_buscar"):"";
                String referencia = request.getParameter("referencia_buscar")!=null?request.getParameter("referencia_buscar"):"";
                String estatus = request.getParameter("estatus_buscar")!=null?request.getParameter("estatus_buscar"):"";

                if(!(fechaInicio.isEmpty() && fechaTermino.isEmpty())){
                    params.add(String.format("fechas:%s,%s", fechaInicio, fechaTermino));
                }
                if(!asignadoA.isEmpty()){
                    params.add("asignadoA:"+asignadoA);                            
                }
                if(!documento.isEmpty()){
                    params.add("documento:" + documento);                                                
                }
                if(!referencia.isEmpty()){
                    params.add("referencia:"+ referencia);                                                
                }
                if(!estatus.isEmpty()){
                    params.add("estatus:" + estatus);                                                
                }                
            }
            
            //listar los registros cabecera
            PeriodoBo bo = new PeriodoBOImpl();
            List<PeriodoCabecera> cabeceras = new ArrayList();
            List<PeriodoCabecera> listado = new ArrayList();
            Periodo periodoActual = bo.actual();
            periodo = periodo.equals(".")?periodoActual.getIdentificador():bo.obtenerPeriodoPorFecha(periodo).getIdentificador();
            int paginacion = 50;
            int pagina = request.getParameter("pagina")!=null? Integer.valueOf(request.getParameter("pagina")):1;
            int paginas = 0;
            int from, to;                                    
            if(session.getAttribute("listado-gasto-".concat(tipoGasto))==null || filtrar){                
                if (esAdmin) {
                    //listado = bo.listaCabecerasCompletas(bo.actual().getIdentificador(), tipoGasto, params.toString());
                } else {
                    params.add("asignadoA:" + nombre);
                }
                
                if(params.size()>0){
                    String[] array = new String[params.size()];
                    listado = bo.listaCabecerasCompletas(periodo, tipoGasto, params.toArray(array));
                    //listado = bo.listaCabecerasCompletas(bo.actual().getIdentificador(), tipoGasto, params.toArray(array));
                } else {
                    listado = bo.listaCabecerasCompletas(periodo, tipoGasto);
                    //listado = bo.listaCabecerasCompletas(bo.actual().getIdentificador(), tipoGasto);
                }
                session.setAttribute("listado-gasto-".concat(tipoGasto), listado);  
            } else {
                listado = (List<PeriodoCabecera>)session.getAttribute("listado-gasto-".concat(tipoGasto));
            }
            
            if (request.getParameter("sortBy") != null) {
                if(((String)request.getParameter("sortBy")).equals("a")) {
                    Collections.sort(listado, new ExpensesComparatorPorAsignado());
                } else if (((String) request.getParameter("sortBy")).equals("f")) {
                    Collections.sort(listado, new ExpensesComparatorPorFecha());
                } else if (((String)request.getParameter("sortBy")).equals("r")) { // referencia
                    Collections.sort(listado, new ExpensesComparatorPorReferencia());
                } else if(((String)request.getParameter("sortBy")).equals("s")) { //estatus
                    Collections.sort(listado, new ExpensesComparatorPorEstatus());                    
                }  else if(((String)request.getParameter("sortBy")).equals("d")){ // documento
                    Collections.sort(listado, new ExpensesComparatorPorDocumento());                    
                }
            } else {
                // no hay parametros de ordenamiento
            }
            paginas = (listado.size()/paginacion) + 1;
            pagina = (pagina<=0)?1:pagina;
            pagina = (pagina > paginas)?1:pagina;
            from = (pagina*paginacion) - paginacion;
            to = (pagina==paginas)?listado.size():pagina*paginacion;
            cabeceras = listado.subList(from, to);
        %>
        
        <div id="dialog-form" title="Nueva asignaci&oacute;n">            
            <input type="hidden" name="tipo_gasto" id="tipo_gasto" value="<%=tipoGasto%>" />
            <table style="width: 100%">
                <tr>
                    <td>
                        <label for="asignar" style="width: 150px;">Asignar a: </label>
                    </td>
                    <td>
                        <input type="text" autocomplete="true" name="asignar" id="asignar" class="text ui-widget-content ui-corner-all" 
                            value="<%=nombre%>" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="usuario" style="width: 200px;">Usuario: </label> 
                    </td>
                    <td>
                        <input type="text" name="usuario" id="usuario" class="text ui-widget-content ui-corner-all" readonly
                               value = "<%=(!esAdmin)?correo:""%>" />
                    </td>                   
                </tr>
                <tr>
                    <td>
                        <label for="documento">Documento: </label>
                    </td>
                    <td>
                        <input type="text" name="documento" id="documento" class="text ui-widget-content ui-corner-all" 
                        value="" maxlength="40" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="referencia">Referencia: </label>
                    </td>
                    <td>
                        <input type="text" name="referencia" id="referencia" class="text ui-widget-content ui-corner-all" 
                        value="" maxlength="60"  />                        
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="fecha_gasto">Fecha gasto: </label>
                    </td>
                    <td>
                        <input type="text" name="fecha_gasto" id="fecha_gasto" class="text ui-widget-content ui-corner-all" 
                        value="" maxlength="10"  />                        
                    </td>
                </tr>
            </table>
        </div>
        <div>
            <table style="width: 100%;text-align: right">
                <tr>
                    <td>
                        <input type="text" id="periodo" value="" maxlength="6" 
                               class="text ui-widget-content ui-corner-all" 
                               style="width: 100px;" title="Coloca el valor del periodo a consultar, basado en a&ntilde;o y mes, ej. 20172, 201710" />
                        <button id = "consultar_historico">Consultar hist&oacute;rico</button>
                    </td>
                    <td>
                        <button id = "nueva">Nueva asignaci&oacute;n</button>
                    </td>
                </tr>
            </table>
            <form id="FORM_SEARCH_RECORDS">
                <input type="hidden" name="tipo_gasto" value="<%=tipoGasto %>" />
                <input type="hidden" name="ver_todos" id="ver_todos" value="*" />
                <input type="hidden" name="filtrar" value="S" />
            <table style="width: 100%">
                <tr>
                    <th><label for="asignado">Asignado</label></th>
                    <th><label for="fecha">Fechas</label></th>
                    <th><label for="documento">Documento</label></th>
                    <th><label for="referencia">Referencia</label></th>
                    <th><label for="estatus">Estatus</label></th>
                    <th>&nbsp;</th>
                </tr>
                <tr>
                    <td><input type="text" name="asignado" id="asignado" value="<%=(!esAdmin?nombre:"")%>" style="width: 170px" class="text ui-widget-content ui-corner-all" <%=(!esAdmin?"readonly":"")%> /></td>
                    <td>
                        <input type="text" maxlength="10" name="fechai" id="fechai" value="" style="width: 90px" class="text ui-widget-content ui-corner-all" />
                        <input type="text" maxlength="10" name="fechaf" id="fechaf" value="" style="width: 90px" class="text ui-widget-content ui-corner-all" />
                    </td>
                    <td><input type="text" name="documento_buscar" id="documento_buscar" value="" style="width: 70px" class="text ui-widget-content ui-corner-all" /></td>
                    <td><input type="text" name="referencia_buscar" id="referencia_buscar" value="" style="width: 70px" class="text ui-widget-content ui-corner-all" /></td>
                    <td><input type="text" name="estatus_buscar" id="estatus_buscar" value="" style="width: 70px" class="text ui-widget-content ui-corner-all" /></td>
                    <td>
                        <a id="buscar">Buscar</a>
                        <a id="todos">Todos</a>
                    </td>
                </tr>
            </table>       
            </form>
        </div>
        <table cellspacing="1" cellpadding="5" style="width:790px;border: 1px #ccc solid;">
            <tr class="tr_cab">
                <th><a id="sort_asignado" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;Asignado a</th>
                <th style="width: 60px"><a id="sort_fecha" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;&nbsp;Fecha</th>
                <th style="width: 60px"><a id="sort_documento" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;Doc.</th>
                <th style="width: 60px"><a id="sort_referencia" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;Ref.</th>
                <th style="width: 80px"><a id="sort_estatus" href="#"><img src="../resources/images/sort.png" height="16" width="16" /></a>&nbsp;Estatus</th>
                <th style="width: 80px">Total</th>
                <th style="width: 27px" title="Editar">&nbsp;</th>
                <th style="width: 27px" title="Eliminar">&nbsp;</th>
            </tr>
            <!--        </table>
                    <table cellspacing="1" cellpadding="5" style="width:650px;height: 400px; border: 1px #ccc solid;overflow: hidden;overflow-y: scroll">-->
            <%
                int contador = 0;               
                for (PeriodoCabecera cabecera : cabeceras) {
                    double total = 0d;
                    contador++;
                    //for (PeriodoRegistro registro : cabecera.getRegistros()) {
                    //    total += registro.getImporte();
                    //}
                    DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0#");
                    total = cabecera.getImporte();    
                    String clase = contador%2==0?"tr_par":"tr_non";
                    int day = 1, month = periodoActual.getPeriodo(), any = periodoActual.getAny();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(any, month-1, day);
                    calendar.add(Calendar.DATE, -1);
                    String title = "";
                    if (cabecera.getFecha().before(calendar.getTime()) && periodo.equals(periodoActual.getIdentificador())){
                        clase = "tr_out";
                        title = "Elemento fuera del periodo y no aceptado a�n";
                    }        
            %>
            <tr style="height: 24px;" class="<%=clase %>" title = "<%=title %>">
                <td><%=cabecera.getAsociadoA()%></td>
                <td style="width: 60px"><%=cabecera.obtieneFecha()%></td>
                <td style="width: 40px"><%=cabecera.getDocumento()%></td>
                <td style="width: 40px"><%=cabecera.getReferencia()%></td>
                <td style="width: 60px;"><%=cabecera.getDsEstatus()%></td>
                <td style="width: 90px;text-align: right"><%=decimalFormat.format(total) %></td>
                <td style="width: 27px" title="Detalle">
                    <%if (cabecera.getEstatus().equals("A")) {%>
                    <a class="editar" cmd = "<%=Comunes.toMD5("edita-cabecera" + session.getId()).concat(cabecera.getIdentificador()).toUpperCase()%>" ><img src="../resources/images/editar.png" border="0" height="16px" /></a>
                        <%} else {%>
                    <a class="consultar" cmd = "<%=Comunes.toMD5("consulta-cabecera" + session.getId()).concat(cabecera.getIdentificador()).toUpperCase()%>" ><img src="../resources/images/consultar.png" border="0" height="16px" /></a>
                        <%}%>
                </td>
                <td style="width: 27px" title="Eliminar">
                    <%if (cabecera.getEstatus().equals("A")) {%>
                    <a class="eliminar" cmd = "<%=Comunes.toMD5("elimina-cabecera" + session.getId()).concat(cabecera.getIdentificador()).toUpperCase()%>" ><img src="../resources/images/trash.png" border="0" height="16px" /></a>
                    <%}%>
                </td>                
            </tr>
            <%
                }//cierre de listado
%>
        <tr>
            <td colspan="8" style="text-align: center">
                P&aacute;gina <%=pagina%> de <%=paginas%><br />
                <a id ="primero" href="javascript:ver('resume.jsp?tipo_gasto=<%=tipoGasto%>&pagina=1');">&lt;&lt;</a>&nbsp;&nbsp;
                <a id = "anterior" href="javascript:ver('resume.jsp?tipo_gasto=<%=tipoGasto%>&pagina=<%=pagina-1%>');">&lt;</a>&nbsp;&nbsp;&nbsp;&nbsp;
                <a id = "siguiente" href="javascript:ver('resume.jsp?tipo_gasto=<%=tipoGasto%>&pagina=<%=pagina+1%>');">&gt;</a>&nbsp;&nbsp;
                <a id = "ultimo" href="javascript:ver('resume.jsp?tipo_gasto=<%=tipoGasto%>&pagina=<%=paginas%>');">&gt;&gt;</a>
                <!-- &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<  (hayFiltro?"<a id='quitar'>Quitar filtro</a>":"")%> -->
            </td>
        </tr>
    </table>

            <form id="FORM_SORT" method="POST">
                <input type="hidden" id="sortBy" name="sortBy" value="" />
            </form>
        <form id="FORM_RECORDS" action="registro.jsp" method="POST">
            <input type="hidden" name="cmd" id="cmd" value="" />
            <input type="hidden" name="tipo_gasto" id="tipo_gasto" value="<%=tipoGasto%>" />
        </form>
        <%
            } catch (NullPointerException e) {
                out.print("Al parecer no hay una sesion activa. Vuelva a iniciar sesion.");
            } catch (GeDocBOException e) {
                out.print(e.getMessage());
                Comunes.escribeLog(carpetaLog, e);
            }
        %>
    </body>
</html>
