<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title></title>
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
            .ui-dialog {font-size: 77.5%;}
            .ui-dialog .ui-state-error { padding: .3em; }
            .validateTips { border: 1px solid transparent; padding: 0.3em; }
            #listado{height: 200px;}
        </style>
        <script>
            $(function() {
                var usuario = $("#usuario"),
                        contrasenia = $("#contrasenia"),
                        confirmar = $("#confirmar"),
                        allFields = $([]).add(usuario).add(contrasenia).add(confirmar),
                        tips = $(".validateTips");
                function updateTips(t) {
                    tips
                            .text(t)
                            .addClass("ui-state-highlight");
                    setTimeout(function() {
                        tips.removeClass("ui-state-highlight", 1500);
                    }, 500);
                }
                function checkLength(o, n, min, max) {
                    if (o.val().length > max || o.val().length < min) {
                        o.addClass("ui-state-error");
                        updateTips("La longitud del campo " + n + " debe estar entre " +
                                min + " y " + max + ".");
                        return false;
                    } else {
                        return true;
                    }
                }
                $("#enviar").click(function() {
                    var bValid = true;
                    allFields.removeClass("ui-state-error");
                    updateTips("");
                    bValid = bValid && checkLength(contrasenia, "Contrase�a", 8, 16);
                    bValid = bValid && checkLength(confirmar, "Contrase�a", 8, 16);
                    if (contrasenia.val() != confirmar.val()) {
                        bValid = false;
                        alert("La confirmaci�n de la contrase�a no es correcta.");
                        confirmar.addClass("ui-state-error");
                        updateTips("La confirmaci�n de la contrase�a no es correcta.");
                        return false;
                    } else {
                        confirmar.addClass("ui-state-highlight");
                        var password = contrasenia.val();
                        var validLength = /.{8}/.test(password);
                        var hasCaps = /[A-z]/.test(password);
                        var hasNums = /\d/.test(password);
                        var hasSpecials = /[~!,\+@#%&_\$\^\*\?\-]/.test(password);
                        if (!(validLength && hasCaps && hasNums && hasSpecials)) {
                            alert("La contrase�a no es segura.");
                            contrasenia.addClass("ui-state-error");
                            updateTips("La contrase�a no es segura.");
                            return false;
                        }
                        bValid = bValid && validLength && hasCaps && hasNums && hasSpecials;
                    }
                    if (bValid) {
                        $.ajax({
                            type: "POST",
                            dataType: "text",
                            data: {elemento: "<%=Comunes.toMD5("elemento-reset.pwd")%>", cmd: "<%=Comunes.toMD5(session.getId()) + Comunes.toMD5("xuser.reset")%>", sesion: "<%=session.getId()%>", usuario: usuario.val(), contrasenia: contrasenia.val()},
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
                    }else{
                        alert("No se lograron validar los campos para el cambio de la contrase�a.");
                    }
                    return false;
                });
            });
        </script>
    </head>
    <body>
        <div>
            <fieldset>
                <label for="usuario">Usuario</label>
                <input type="text" id="usuario" value="" class="text ui-widget-content ui-corner-all" />
                <label for="contrasenia">Contrase&ntilde;a</label>
                <input type="password" id="contrasenia" value=""  class="text ui-widget-content ui-corner-all"/>
                <label for="confirmar">Confirmar</label>
                <input type="password" id="confirmar" value="" class="text ui-widget-content ui-corner-all"/>
                <input type="button" id="enviar" value="Cambiar" class="ui-button ui-button-text-only ui-corner-all ui-state-default" />
            </fieldset>
        </div>
    </body>
</html>
