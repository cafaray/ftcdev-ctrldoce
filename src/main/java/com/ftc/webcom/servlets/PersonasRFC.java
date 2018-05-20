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

public class PersonasRFC extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection conexion = null;
        String jsonPoutput = "";
        String sesion = request.getSession().getId();
        String tipo = request.getParameter("tipo");
        try {
            PersonaBO bo = new PersonaBOImpl();
            Collection<Persona> rst = bo.localizaPersonasPorRFC(tipo.charAt(0), request.getParameter("term"), sesion);
            Iterator<Persona> personas = rst.iterator();
            String callBackJavaScripMethodName = request.getParameter("callback");
            String json = "[";
            while (personas.hasNext()) {
                Persona persona = personas.next();
                json += "{ label: '" + persona.getRfc() + "', id: '" + persona.getIdentificador() + "', nombre: '" + persona.getNombre() + "' },";
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Locate a person by tipo and RFC";
    }// </editor-fold>

}
