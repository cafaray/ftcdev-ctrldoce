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

public class ContactosActualizar extends HttpServlet {

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
            if (comando.startsWith(Comunes.toMD5("actualizarContacto-" + sesion))) {

                String identificador = comando.substring(comando.length()-16);
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String correo = request.getParameter("correo");
                String telefono = request.getParameter("telefono");
                String movil = request.getParameter("movil");
                Contacto contacto = new Contacto(nombre, apellido, correo, telefono, movil);
                ContactoBO bo = new ContactoBOImpl();
                if (bo.actualizarContacto(contacto, identificador, sesion)) {
                    mensaje = "Registro actualizado correctamente.";
                }else{
                    throw new GeDocBOException("No se logró determinar el estado del registro.");
                }
            } else if(comando.startsWith(Comunes.toMD5("eliminarContacto-" + sesion))){

                String identificador = comando.substring(comando.length()-16);                
                String correo = request.getParameter("correo");
                Contacto contacto = new Contacto();
                contacto.setCorreo(correo);
                ContactoBO bo = new ContactoBOImpl();
                if(bo.eliminarContacto(identificador, correo, sesion)){
                    mensaje = "Registro eliminado correctamente.";
                }else{
                    throw new GeDocBOException("No se logró determinar el estado del registro para su eliminación.");
                }                                
            } else {
                throw new GeDocBOException("No se identifico la operación solicitada.");
            }
        } catch (GeDocBOException e) {
            mensaje = e.getMessage();
            Comunes.escribeLog(getServletContext().getInitParameter("logLocation"), e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.println(mensaje);
            out.flush();
            out.close();
        }
    }
    @Override
    public String getServletInfo() {
        return "Actualiza ContactoDAOImpl";
    }
}
