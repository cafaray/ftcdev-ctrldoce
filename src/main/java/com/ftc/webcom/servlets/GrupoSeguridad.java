package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.GrupoBO;
import com.ftc.gedoc.bo.impl.GrupoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GrupoSeguridad extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        try {
            String idgrupo = request.getParameter("idgrupo")==null?"":request.getParameter("idgrupo");
            if(idgrupo!=null && !idgrupo.isEmpty() && idgrupo.startsWith((Comunes.toMD5("chmod")+session.getId()).toUpperCase())){
                idgrupo = idgrupo.substring((Comunes.toMD5("chmod")+session.getId()).length());

                String usuarios = request.getParameter("usuarios")==null?"0":request.getParameter("usuarios");
                String cambiar = request.getParameter("cambiar")==null?"0":request.getParameter("cambiar");
                String suspender = request.getParameter("suspender")==null?"0":request.getParameter("suspender");
                String activar = request.getParameter("activar")==null?"0":request.getParameter("activar");
                String grupos = request.getParameter("grupos")==null?"0":request.getParameter("grupos");

                String pregistro = request.getParameter("pregistro")==null?"0":request.getParameter("pregistro");
                String pcontacto = request.getParameter("pcontacto")==null?"0":request.getParameter("pcontacto");
                String pverdoc = request.getParameter("pverdoc")==null?"0":request.getParameter("pverdoc");
                String psubirdoc = request.getParameter("psubirdoc")==null?"0":request.getParameter("psubirdoc");
                String pnotificaciones = request.getParameter("pnotificaciones")==null?"0":request.getParameter("pnotificaciones");
                String pestado = request.getParameter("pestado")==null?"0":request.getParameter("pestado");
                String peliminar = request.getParameter("peliminar")==null?"0":request.getParameter("peliminar");
                String pdescarga = request.getParameter("pdescarga")==null?"0":request.getParameter("pdescarga");

                String cregistro = request.getParameter("cregistro")==null?"0":request.getParameter("cregistro");
                String ccontacto = request.getParameter("ccontacto")==null?"0":request.getParameter("ccontacto");
                String cverdoc = request.getParameter("cverdoc")==null?"0":request.getParameter("cverdoc");
                String csubirdoc = request.getParameter("csubirdoc")==null?"0":request.getParameter("csubirdoc");
                String cnotificaciones = request.getParameter("cnotificaciones")==null?"0":request.getParameter("cnotificaciones");
                String cestado = request.getParameter("cestado")==null?"0":request.getParameter("cestado");
                String celiminar = request.getParameter("celiminar")==null?"0":request.getParameter("celiminar");
                String cdescarga = request.getParameter("cdescarga")==null?"0":request.getParameter("cdescarga");

                String viaticos = request.getParameter("viaticos")==null?"0":request.getParameter("viaticos");
                String caja = request.getParameter("caja")==null?"0":request.getParameter("caja");
                String aduanales = request.getParameter("aduanales")==null?"0":request.getParameter("aduanales");
                String administrador = request.getParameter("administrador")==null?"0":request.getParameter("administrador");

                StringBuilder sModo = new StringBuilder();
                sModo.append(pregistro).append(pcontacto).append(pverdoc).append(psubirdoc).append(pnotificaciones).append(pestado).append(peliminar).append(pdescarga);
                sModo.append(cregistro).append(ccontacto).append(cverdoc).append(csubirdoc).append(cnotificaciones).append(cestado).append(celiminar).append(cdescarga);
                sModo.append(viaticos).append(caja).append(aduanales).append(administrador);
                sModo.append(usuarios).append(cambiar).append(suspender).append(activar).append(grupos);

                int modo = Integer.parseInt(sModo.toString(), 2);
                GrupoBO bo = new GrupoBOImpl();
                bo.asignarPermisos(idgrupo.toLowerCase(), modo);
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>::: ctrldoce.asignaci&oacute;n de permisos :::</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("Se actualizaron correctamente los permisos");
                out.println("<script>setTimeout('location.replace(\"../../xuser/grupo.jsp\")',1521);</script>");
                out.println("</body>");
                out.println("</html>");
            } else {
                throw new GeDocBOException("No se localizo un grupo valido.");
            }
        }catch(GeDocBOException e){
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>::: ctrldoce.asignaci&oacute;n de permisos :::</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println(e.getMessage());
            out.println("<script>setTimeout('location.replace(\"../../xuser/grupo.jsp\")',1521);</script>");
            out.println("</body>");
            out.println("</html>");
        } finally {
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
        return "::: ctrldoce.asignacion de permisos :::";
    }

}
