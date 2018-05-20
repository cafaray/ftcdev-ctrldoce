package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.PersonaBO;
import com.ftc.gedoc.bo.impl.PersonaBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Persona;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Personas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=ISO-8859-1");
        PrintWriter out = response.getWriter();
        Connection conexion = null;
        String jsonPoutput = "";
        String sesion = request.getSession().getId();
        String tipo = request.getParameter("tipo");
        try {
            PersonaBO bo = new PersonaBOImpl();
            Collection<Persona> rst = bo.localizaPersonas(tipo.charAt(0), request.getParameter("term"), sesion);
            Iterator<Persona> personas = rst.iterator();
            String callBackJavaScripMethodName = request.getParameter("callback");
            String json = "[";
            while (personas.hasNext()) {
                Persona persona = personas.next();
                json += "{ label: '" + persona.getNombre() + " [" + persona.getRfc() + "]', id: '" + persona.getIdentificador() + "' },";
            }
            json += "]";
            jsonPoutput = callBackJavaScripMethodName + "(" + json + ");";
        } catch (GeDocBOException e) {
            jsonPoutput = e.getMessage();
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e,(String)request.getSession().getAttribute("usuario"));
        } finally {
            out.print(jsonPoutput);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=ISO-8859-1");
        PrintWriter out = response.getWriter();
        String mensaje = "";
        try {
            String comando = request.getParameter("cmd");
            String sesion = request.getSession().getId();
            if (comando.equals(Comunes.toMD5("registraPersona-" + sesion))) {
                PersonaBO bo = new PersonaBOImpl();
                String razon = request.getParameter("razon");
                String tipo = request.getParameter("tipo");
                String rfc = request.getParameter("rfc");
                Persona persona = new Persona(razon, rfc, tipo.charAt(0));
                if (bo.insertaPersona(persona, sesion)) {
                    mensaje = "Registro generado correctamente.";
                } else {
                    mensaje = "El registro no se ha generado. Revise el log para más detalle.";
                }
            } else {
                mensaje = "No se identifico la operación solicitada.";
            }
        } catch (NullPointerException e) {
            mensaje = "No se ha llamado al metodo de registro porque se encontraron valores nulos en los parametros ";
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } catch (GeDocBOException e){
            mensaje = e.getMessage();
        } finally {
            out.println(mensaje);
            out.flush();
            out.close();
        }
    }

    @Override
    public String getServletInfo() {
        return "Registro de personas";
    }
}
