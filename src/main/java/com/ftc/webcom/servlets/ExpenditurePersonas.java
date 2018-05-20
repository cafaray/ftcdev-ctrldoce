package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.ContactoBO;
import com.ftc.gedoc.bo.impl.ContactoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Contacto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ExpenditurePersonas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();        
        String jsonPoutput = "";        
        HttpSession session = request.getSession();
        try {
            String filtro = request.getParameter("term");            
            String persona = session.getAttribute("persona")!=null?(String)session.getAttribute("persona"):"";
            //PeriodoBo bo = new PeriodoBOImpl();
            //List<String> usuarios = bo.filtraPersonasAsociadas(filtro);
            ContactoBO bo = new ContactoBOImpl();
            List<Contacto> contactos = bo.contactoPorEmpresa(persona, filtro);
            String callBackJavaScripMethodName = request.getParameter("callback");
            String json = "[";
            for (Contacto contacto: contactos) {
                StringBuilder nombre = new StringBuilder();
                nombre.append(contacto.getNombre()).append(" ").append(contacto.getApellido());                
                json += String.format("{label:'%s', usuario:'%s'},",nombre.toString(), contacto.getCorreo());
            }
            json += "]";
            jsonPoutput = callBackJavaScripMethodName + "(" + json + ");";
        } catch (GeDocBOException e) {
            jsonPoutput = "No hay resultado. " + e.getMessage();
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.print(jsonPoutput);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "AutoComplete para las personas asociadas a Gastos";
    }

}
