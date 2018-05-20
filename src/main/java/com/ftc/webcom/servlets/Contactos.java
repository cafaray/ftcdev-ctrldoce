package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.gedoc.bo.ContactoBO;
import com.ftc.gedoc.bo.impl.ContactoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Contacto;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Contactos extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String mensaje = "";
        try {
            String comando = request.getParameter("cmd");
            String sesion = request.getSession().getId();
            if (comando.equals(Comunes.toMD5("registraContacto-" + sesion))) {
                String persona = request.getParameter("persona");
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String correo = request.getParameter("correo");
                String telefono = request.getParameter("telefono");
                String movil = request.getParameter("movil");
                String contrasenia = request.getParameter("contrasenia");
                String grupo = request.getParameter("grupo");

                Contacto contacto = new Contacto(persona, nombre, apellido, correo, telefono, movil, grupo);
                ContactoBO bo = new ContactoBOImpl();
                if (bo.insertaContacto(contacto, contrasenia, sesion)) {
                    mensaje = "Registro generado correctamente.";
                }else{
                    throw new Exception("No se logró determinar el estado del registro.");
                }
            } else {
                throw new Exception("No se identifico la operación solicitada.");
            }
        } catch (GeDocBOException e) {
            mensaje = e.getMessage();
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } catch (Exception e) {
            mensaje = "Excepción al registrar la persona. " + e.getMessage();
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.println(mensaje);
            out.flush();
            out.close();
        }
    }

    @Override
    public String getServletInfo() {
        return "Registrar contactos";
    }
}
