package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.NotificacionBO;
import com.ftc.gedoc.bo.impl.NotificacionBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Notificacion;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManejadorNotificaciones extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        NotificacionBO bo = new NotificacionBOImpl();
        try {
            String cmd = request.getParameter("cmd");
            String persona = (String) session.getAttribute("persona");
            if(cmd.startsWith(Comunes.toMD5("notificaciones.Consulta-notificacion-".concat(session.getId())))){
                List<Notificacion> notificaciones = bo.notificaciones(persona, "P");
                StringBuilder listado = new StringBuilder();
                if(notificaciones.size()>0){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
                for(Notificacion notificacion: notificaciones){
                    listado.append("<li>").append(dateFormat.format(new Date(notificacion.getFechaRegistro().getTime())));
                    listado.append(": ").append(notificacion.getMensaje()).append("</li>\n");
                }
                }else{
                    listado.append("<li>No se encontraron notificaciones.</li>");
                }
                out.print(listado.toString());
            
            }
        }catch(GeDocBOException e){
            out.print(String.format("{\"error\":\"%s\"}", e.getMessage()));
            e.printStackTrace(System.out);
        }catch(NullPointerException e){
            out.print(String.format("{\"error\":\"%s\"}", "No se recibieron los parametros esperados."));
            e.printStackTrace(System.out);
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
