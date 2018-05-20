<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    session.removeAttribute("usuario");
    if (!session.isNew()) {
        session.invalidate();
    }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: Gestor de documentos electr&oacute;nicos :::</title>
         <link rel="stylesheet" type="text/css" href="css/login.css">
        <script src="js/jquery-1.7.2.min.js"></script>
        <script src="js/jquery-ui-1.10.2/js/jquery-ui-1.10.2.custom.min.js"></script>
        <link rel="stylesheet" href="js/jquery-ui-1.10.2/css/start/jquery-ui-1.10.2.custom.min.css" media="all" />
        <script>
            $(function() {
                var usuario = $("#usuario"),
                        contrasena = $("#contrasena"),
                        rfc = $("#rfc"),
                        rusuario = $("#rusuario"),
                        rrfc = $("#rrfc"),
                        allFields = $([]).add(rusuario).add(rrfc),
                        tips = $(".validateTips");                
                $("#restablecer").click(function() {
                    $("#dialog-form").dialog("open");
                });
                $("#enviar").button().click(function() {
                    if (usuario.val() == "") {
                        alert("Es necesario especificar el campo usuario.");
                        return;
                    }
                    if (contrasena.val() == "") {
                        alert("Es necesario especificar el campo contrase�a.");
                        return;
                    }
                    frm = document.getElementById("FORM_IDENTIFICA_USUARIO");
                    frm.method = "POST";
                    frm.action = "identifica.jsp";
                    frm.submit();
                });

                $('#contrasena').keypress(function(e) {
                    var key = e.which;
                    if (key == 13) {
                        $("#enviar").click();
                        return false;
                    }
                  });


                $("#dialog-form").dialog({
                    autoOpen: false,
                    height: 250,
                    width: 450,
                    modal: true,
                    buttons: {
                        Restablecer: function() {
                            $.ajax({
                                type: "POST",
                                dataType: "text",
                                data: {elemento: "<%=request.getHeader("USER-AGENT")%>", cmd: "<%=Comunes.toMD5("xuser.reset")%>", sesion: "<%=session.getId()%>", usuario: rusuario.val(), rfc: rrfc.val()},
                                async: false,
                                url: "ws/xuser/manager.do",
                                success: function(data) {
                                    alert(data);
                                    $("#dialog-form").dialog("close");
                                },
                                error: function(data) {
                                    alert("Alg�n error ocurri�." + data);
                                }
                            });
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    },
                    close: function() {
                        allFields.val("").removeClass("ui-state-error");
                    }
                });
            });
        </script>
    </head>
    <body style="background-image: url('resources/images/fondo.jpg')">
        <%
            String serverName = request.getServerName();
            serverName = serverName.replace(".com", "");
            serverName = serverName.replace("ctrldoce.", "");
            serverName = serverName.replace("www.", "");
            serverName = serverName.replace("localhost", "casadelpastelero");
        %>
        <section id="wrap_prin">
            <div id="wra_login" style="overflow: hidden;">
                <div id="login">
                    <!--<img src="theme/9824/img/logo.png"/>-->
                    <h1 style="margin-top:20px">Bienvenid@</h1>
                    <h2>ctrldoce<br /><b>Control de documentos electr&oacute;nicos</b></h2>

                    <div class="loginForm  grad_gray">
                        <div class="login_avatar">
                            <img src="resources/images/<%=serverName%>.png" style="width: 200px">

                        </div>
                        <form name="FORM_IDENTIFICA_USUARIO" id="FORM_IDENTIFICA_USUARIO">
                            <div class="login_form" id="frm_login" >
                                <div class="campoLogin" >
                                    <label class="labelLogin"><b>RFC</b></label>        
                                    <input type="text" name="rfc" id="rfc" placeholder="RFC de su empresa" maxlength="13" required />                                    
                                </div>
                                <div class="campoLogin" >
                                    <label class="labelLogin"><b>Usuario</b></label>        
                                    <input type="text" name="usuario" id="usuario" placeholder="Nombre del usuario" maxlength="80" required />                                    
                                </div>
                                <div class="campoLogin">  
                                    <label class="labelLogin"><b>Contrase&ntilde;a</b></label>
                                    <input type="password" name="contrasena" id="contrasena" placeholder="" maxlength="16" required/>
                                    
                                </div>

                                <div class="campoLogin">
                                    <a id="enviar" style="width: 100%">Acceder</a>
                                </div>
                                <!--
                                <div class="campoLogin">
                                    <p>
                                        <a href="#" id="restablecer">&iquest;Olvidaste tu contrase&ntilde;a?</a>
                                    </p>
                                </div>
                               -->
                            </div>
                        </form>            
                    </div>                    
                </div>
            </div>
        </section>

        <footer>
            <div id="wrap_footer">
                <div class="colums2">
                    <p>
                        <b>Desarrollado por Farias Telecomunicaciones y c&oacute;mputo &reg;.</b>  
                    </p>
                    <p>
                        121 Delia, Col. Guadalupe Tepeyac, Del. Gustavo A Madero, C.P.11320
                    </p>
                </div>
                <div class="colums2">
                    <p>Contrataci&oacute;n de servicio a: <b>ventas@ftcenlinea.com</b><br />
                    Soporte t&eacute;cnico: <b>soporte@ftcenlinea.com</b></p>             
                    <p>
                        Implementaci&oacute;n por parte de Carlos A Farias y Oscar G Farias
                    </p>
                </div>
            </div>
        </footer>
<!--        <div id="login">
            
            <form id="FORM_IDENTIFICA_USUARIO">
                <input type="hidden" value="<%=Comunes.toMD5("IDENTIFICA_USUARIO" + session.getId())%>" name="cmd" />
                <table align="center">
                    <tr>
                        <th colspan="2" style="font-size:16px;color: #2191c0">Sistema para la gesti&oacute;n de documentos electr&oacute;nicos</th>
                    </tr>
                    <tr>
                        <th colspan="2" style="font-size: 14px">Inicio de sesi&oacute;n</th>
                    </tr>
                    <tr>
                        <td align="right">RFC:&nbsp;</td>
                        <td><input type="text" name='rfc' id="rfc" class="ui-widget ui-corner-all ui-state-default" /></td>
                    </tr>
                    <tr>
                        <td align="right">Usuario:&nbsp;</td>
                        <td><input type="text" name='usuario' id="usuario" class="ui-widget ui-corner-all ui-state-default" /></td>
                    </tr>
                    <tr>
                        <td align="right">Contrase&ntilde;a:&nbsp;</td>
                        <td><input type="password" name ='contrasena' id="contrasena" class="ui-widget ui-corner-all ui-state-default" /></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="right">
                            <input type="button" id="restablecer" class="ui-button-text-only ui-corner-all ui-state-default" value="Restablecer contrase&ntilde;a" />
                            <button id="enviar" class="ui-button-text-icon-secondary ui-corner-all ui-state-default">Enviar</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>-->
        <div id="dialog-form" title="Restablecer acceso.">
            <form>
                <p class="validateTips"></p>
                <fieldset>
                    <table>
                        <tr>
                            <td>
                                <label for="rrfc" class="labelLogin">RFC</label>
                            </td>
                            <td>
                                <input type="text" id="rrfc" value="" placeholder="RFC de su empresa" maxlength="13" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label for="rusuario" class="labelLogin">Usuario</label>
                            </td>
                            <td>
                                <input type="text" id="rusuario" value="" placeholder="Usuario empresa" maxlength="80" />
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </form>
        </div>
    </body>
</html>
