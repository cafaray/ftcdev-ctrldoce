<%@page import="com.ftc.gedoc.exceptions.GeDocBOException"%>
<%@page import="com.ftc.gedoc.bo.impl.PersonaBOImpl"%>
<%@page import="com.ftc.gedoc.bo.PersonaBO"%>
<%@page import="com.ftc.modelo.Persona"%>
<%@page import="com.ftc.gedoc.bo.impl.ContactoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.ContactoBO"%>
<%@page import="com.ftc.modelo.Contacto"%>
<%@page import="com.ftc.gedoc.bo.impl.GrupoBOImpl"%>
<%@page import="com.ftc.gedoc.bo.GrupoBO"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.ftc.gedoc.utiles.Seguridad"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.ftc.aq.Comunes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>::: ctrldoce.registro de persona :::</title>
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
            .editar{width: 26px;text-align: left}
        </style>
        <script>
            $(function() {
                var nombre = $("#nombre"),
                        apellido = $("#apellido"),
                        correo = $("#correo"),
                        telefono = $("#telefono"),
                        movil = $("#movil"),
                        pwd = $("#contrasenia"),
                        rep = $("#confirmar"),
                        tipo = $("#tipo"),
                        grupo = $("#grupo"),
                        persona = $("#persona"),
                        eidentificador = $("#eidentificador"),
                        enombre = $("#enombre"),
                        eapellido = $("#eapellido"),
                        ecorreo = $("#ecorreo"),
                        etelefono = $("#etelefono"),
                        emovil = $("#emovil"),
                        allFields = $([]).add(nombre).add(apellido).add(correo).add(pwd).add(rep),
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
                function checkRegexp(o, regexp, n) {
                    if (!(regexp.test(o.val()))) {
                        o.addClass("ui-state-error");
                        updateTips(n);
                        return false;
                    } else {
                        return true;
                    }
                }
                $("#dialog-form").dialog({
                    autoOpen: false,
                    height: 570,
                    width: 350,
                    modal: true,
                    buttons: {
                        "Registrar": function() {
                            var bValid = true;
                            allFields.removeClass("ui-state-error");
                            updateTips("");
                            bValid = bValid && checkLength(nombre, "Nombre", 3, 35);
                            bValid = bValid && checkLength(apellido, "Apellido", 3, 35);
                            bValid = bValid && checkLength(correo, "Correo electr�nico", 10, 80);
                            bValid = bValid && checkLength(pwd, "Contrase�a", 6, 16);
                            bValid = bValid && checkLength(rep, "Contrase�a", 6, 16);
                            bValid = bValid && checkRegexp(nombre, /^[\w������������0-9. ]+$/i, "El campo nombre debe contener caracteres, sin n�meros.");
                            bValid = bValid && checkRegexp(apellido, /^[\w������������0-9. ]+$/i, "El campo apellidos debe contener caracteres, sin n�meros.");
                            //bValid = bValid && checkRegexp(apellido, /^[A-z]([0-9a-z. ])+$/i, "El campo apellidos debe contener caracteres, sin n�meros.");
                            bValid = bValid && checkRegexp(correo, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "Correo no validado. Ej. alguien@biotecsa.com");
                            if (pwd.val() != rep.val()) {
                                bValid = false;
                                rep.addClass("ui-state-error");
                                updateTips("La confirmaci�n de la contrase�a no es correcta.");
                                return false;
                            } else {
                                rep.addClass("ui-state-highlight");
                                var password = pwd.val();
                                var validLength = /.{4}/.test(password);
                                var hasNums = /\d/.test(password);
                                if (!(validLength && hasNums)) {
                                    //Adaptaci�n Biotecsa
//                                var validLength = /.{8}/.test(password);
//                                var hasCaps = /[A-z]/.test(password);
//                                var hasNums = /\d/.test(password);
//                                var hasSpecials = /[~!,\+@#%&_\$\^\*\?\-]/.test(password);
//                                if (!(validLength && hasCaps && hasNums && hasSpecials)) {
                                    pwd.addClass("ui-state-error");
                                    updateTips("La contrase�a no es segura.");
                                    return false;
                                }
                                //bValid = bValid && validLength && hasCaps && hasNums && hasSpecials;
                                bValid = bValid && validLength && hasNums;
                            }
                            //alert(bValid);
                            if (bValid) {
                                $.ajax({
                                    type: "POST",
                                    dataType: "text",
                                    data: {persona: persona.val(), nombre: nombre.val(), apellido: apellido.val(), correo: correo.val(), telefono: telefono.val(), movil: movil.val(), contrasenia: pwd.val(), tipo: tipo.val(), grupo: grupo.val(), cmd: "<%=Comunes.toMD5("registraContacto-" + session.getId())%>"},
                                    async: false,
                                    url: "../ws/contactos/registrar.do",
                                    success: function(data) {
                                        updateTips(data);
                                        alert(data);
                                        nombre.val("");
                                        apellido.val("");
                                        location.reload();
                                    },
                                    error: function(data) {
                                        updateTips(data);
                                    }
                                });
                                return false;
                            } else {
                                //Nothing
                            }
                        },
                        Cancel: function() {
                            allFields.val("").removeClass("ui-state-error");
                            $(this).dialog("close");
                        }
                    },
                    close: function() {
                        allFields.val("").removeClass("ui-state-error");
                    }
                });
                $("#dialog-form-e").dialog({
                    autoOpen: false,
                    height: 370,
                    width: 350,
                    modal: true,
                    buttons: {
                        "Actualizar": function() {
                            var bValid = true;
                            allFields.removeClass("ui-state-error");
                            bValid = bValid && checkLength(enombre, "Nombre", 3, 35);
                            bValid = bValid && checkLength(eapellido, "Apellido", 3, 35);
                            bValid = bValid && checkLength(ecorreo, "Correo electr�nico", 10, 80);
                            bValid = bValid && checkRegexp(enombre, /^[\w������������0-9. ]+$/i, "El campo nombre debe contener caracteres, sin n�meros.");
                            bValid = bValid && checkRegexp(eapellido, /^[\w������������0-9. ]+$/i, "El campo apellidos debe contener caracteres, sin n�meros.");
                            bValid = bValid && checkRegexp(ecorreo, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "Correo no validado. Ej. alguien@biotecsa.com");
                            if (bValid) {
                                $.ajax({
                                    type: "POST",
                                    dataType: "text",
                                    data: {nombre: enombre.val(), apellido: eapellido.val(), correo: ecorreo.val(), telefono: etelefono.val(), movil: emovil.val(), cmd: "<%=Comunes.toMD5("actualizarContacto-" + session.getId())%>" + eidentificador.val()},
                                    async: false,
                                    url: "../ws/contactos/actualizar.do",
                                    success: function(data) {
                                        updateTips(data);
                                        alert(data);
                                        enombre.val("");
                                        eapellido.val("");
                                        ecorreo.val("");
                                        location.reload();
                                    },
                                    error: function(data) {
                                        updateTips(data);
                                    }
                                });
                                return false;
                            } else {
                                //Nothing
                            }
                        },
                        Cancel: function() {
                            $(this).dialog("close");
                        },
                        "Eliminar": function(){
                            if(confirm("Se eliminaran todos los registros asociados a este usuario, esta seguro de continuar?")){
                                $.ajax({
                                    type: "POST",
                                    dataType: "text",
                                    data: {correo: ecorreo.val(), cmd: "<%=Comunes.toMD5("eliminarContacto-" + session.getId())%>" + eidentificador.val()},
                                    async: false,
                                    url: "../ws/contactos/actualizar.do",
                                    success: function(data) {
                                        updateTips(data);
                                        alert(data);
                                        location.reload();
                                    },
                                    error: function(data) {
                                        updateTips(data);
                                    }
                                });
                                return false;
                            }
                        }
                    },
                    close: function() {
                        allFields.val("").removeClass("ui-state-error");
                    }
                });
                $("#nuevoContacto")
                        .button()
                        .click(function() {
                    $("#dialog-form").dialog("open");
                });
                $(".editar").button({
                    text: false,
                    icons: {primary: "ui-icon-pencil"}
                });
                $(".editar").click(function() {
                    eidentificador.val($(this).attr("codigo"));
                    enombre.val($(this).attr("nombre"));
                    eapellido.val($(this).attr("apellido"));
                    ecorreo.val($(this).attr("correoe"));
                    etelefono.val($(this).attr("telefono"));
                    emovil.val($(this).attr("movil"));
                    $("#dialog-form-e").dialog("open");
                });
            });
        </script>
    </head>
    <body>
        <%
            String cmd = request.getParameter("cmd");
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
            String mensaje = "";
            try {
                boolean isOwner = String.valueOf(session.getAttribute("propietario")).equals("S");
        %>
        <h2>Contactos registrados</h2>
        <div id="listado">
            <table style="width:790px;border: 1px #ccc solid; table-layout: fixed">
                <tr class="tr_cab">
                    <th style="width:180px">Empresa</th>
                    <th style="width:200px">Nombre completo</th>
                    <th style="width:270px">Correo</th>
                    <th style="width:100px">Grupo</th>
                    <th style="width:40px">&nbsp;</th>
                </tr>
<!--            </table>
            <table cellspacing="1" cellpadding="1" style="width: 800px;overflow: hidden; overflow-y: scroll;border: 1px #ccc solid;height: 100px; table-layout: fixed">-->
                <%
                    List<Contacto> listado = null;
                    ContactoBO boContacto = new ContactoBOImpl();
                    if (isOwner) {
                        listado = boContacto.obtieneContactos("*", tipo, sesion);
                    } else {
                        listado = boContacto.obtieneContactos(sesion);
                    }
                    GrupoBO bo = new GrupoBOImpl();
                    Iterator<Contacto> contactos = listado.iterator();
                    int cont = 0;
                    while (contactos.hasNext()) {
                        cont++;
                        Contacto c = contactos.next();
                        String grupo = bo.buscar(c.getGrupo()).getGrupo();
                        String editar = String.format("<a title=\"Editar contacto\" class=\"editar\" codigo=\"%s\" nombre=\"%s\" apellido=\"%s\" correoe=\"%s\" telefono=\"%s\" movil=\"%s\">Editar</a>", c.getIdentificador(), c.getNombre(), c.getApellido(), c.getCorreo(), c.getTelefono(), c.getMovil());
                        out.println(String.format("<tr class=\"%s\">"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:%s\">%s</td>"
                                + "<td style=\"width:30px\">%s</td>"
                                + "</tr>",
                                cont % 2 == 0 ? "tr_par" : "tr_non", "180px", c.getRazonSocial(), 
                                "200px",c.getNombre().concat(" ").concat(c.getApellido()), "270px", c.getCorreo(), "100px", grupo, editar));
                    }
                %>
                
            </table>
            <p>
                <input type="button" value="Nuevo contacto" id ="nuevoContacto" />
            </p>
        </div>

        <div id="dialog-form" title="Crear nuevo contacto.">
            <form>
                <p class="validateTips"></p>
                <fieldset>
                    <input type="hidden" name="tipo" id="tipo" value="<%=tipo%>" />
                    <label for="persona">Empresa</label>
                    <select name="persona" id="persona">
                        <%
                        		PersonaBO boPersona = new PersonaBOImpl();
                            List<Persona> empresas = boPersona.obtienePersonas(tipo.charAt(0), sesion);
                            for (Persona empresa : empresas) {
                                String elemento = "";
                                if (persona.equals(empresa.getIdentificador())) {
                                    elemento = String.format("<option value=\"%s\" selected=\"true\">%s</option>", empresa.getIdentificador(), empresa.getNombre());
                                } else {
                                    if (isOwner) {
                                        elemento = String.format("<option value=\"%s\">%s</option>", empresa.getIdentificador(), empresa.getNombre());
                                    }
                                }
                                out.println(elemento);
                            }
                        %>
                    </select>
                    <label for="nombre">Nombre</label>
                    <input type="text" name="nombre" id="nombre" class="text ui-widget-content ui-corner-all" />
                    <label for="apellido">Apellidos</label>
                    <input type="text" name="apellido" id="apellido" class="text ui-widget-content ui-corner-all" />
                    <label for="grupo">Grupo de seguridad</label>
                    <select name="grupo" id="grupo">
                        <%
                            Map<String, String> listadoGrupos = Seguridad.listaGrupos(sesion);
                            Set<String> gruposLlaves = listadoGrupos.keySet();
                            Iterator<String> grupos = gruposLlaves.iterator();
                            while (grupos.hasNext()) {
                                String key = grupos.next();
                                String elemento = "";
                                if (key.equals(tipo)||isOwner) {
                                    elemento = String.format("<option value=\"%s\">%s</option>", key, listadoGrupos.get(key));
                                }
                                out.println(elemento);
                            }
                            if(isOwner){
                                out.println(String.format("<option value=\"%s\">%s</option>", "*", "Administrador"));
                            }
                        %>
                    </select>
                    <label for="correo">Correo electr&oacute;nico</label>
                    <input type="text" name="correo" id="correo" class="text ui-widget-content ui-corner-all" />
                    <label for="telefono">Tel&eacute;fono</label>
                    <input type="text" name="telefono" id="telefono" class="text ui-widget-content ui-corner-all" />
                    <label for="movil">Tel. M&oacute;vil</label>
                    <input type="text" name="movil" id="movil" class="text ui-widget-content ui-corner-all" />
                    <label for="contrasenia">Contrase&ntilde;a</label>
                    <input type="password" name="contrasenia" id="contrasenia" class="text ui-widget-content ui-corner-all" />
                    <label for="confirmar">Confirmar</label>
                    <input type="password" name="confirmar" id="confirmar" class="text ui-widget-content ui-corner-all" />
                </fieldset>
            </form>
        </div>
        <div id="dialog-form-e" title="Editar contacto.">
            <form>
                <p class="validateTips"></p>
                <fieldset>
                    <input type="hidden" id="eidentificador" name="eidentificador" />
                    <label for="enombre">Nombre</label>
                    <input type="text" name="enombre" id="enombre" class="text ui-widget-content ui-corner-all" />
                    <label for="eapellido">Apellidos</label>
                    <input type="text" name="eapellido" id="eapellido" class="text ui-widget-content ui-corner-all" />
                    <label for="ecorreo">Correo electr&oacute;nico</label>
                    <input type="text" name="ecorreo" id="ecorreo" class="text ui-widget-content ui-corner-all" />
                    <label for="etelefono">Tel&eacute;fono</label>
                    <input type="text" name="etelefono" id="etelefono" class="text ui-widget-content ui-corner-all" />
                    <label for="emovil">Tel. M&oacute;vil</label>
                    <input type="text" name="emovil" id="emovil" class="text ui-widget-content ui-corner-all" />
                </fieldset>
            </form>
        </div>
        <%
                } catch (GeDocBOException e) {
                    mensaje = e.getMessage();
                    Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) session.getAttribute("usuario"));
                } finally {
                    if (mensaje.length() > 0) {
                        out.println(String.format("<script>alert(\"%s\")</script>", mensaje));
                    }
                }
            }
        %>
    </body>
</html>
