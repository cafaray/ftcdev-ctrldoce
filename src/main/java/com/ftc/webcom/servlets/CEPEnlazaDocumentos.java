package com.ftc.webcom.servlets;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.CEPArchivoBO;
import com.ftc.gedoc.bo.impl.CEPArchivoBOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.CEPArchivo;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CEPEnlazaDocumentos extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder mensaje = new StringBuilder("");
        String logLocation = getServletContext().getInitParameter("logLocation");
        try{
            HttpSession session = request.getSession();
            String cmd = request.getParameter("cmd");
            String estatus = "A"; // esta funcion es para asociar el documento, por lo que el "A" va a cajon
            if (cmd==null || !cmd.startsWith("c"+Comunes.toMD5("asociarDocele-".concat(session.getId())).toLowerCase())){
                throw new GeDocBOException("El comando que se solicita no se ha logrado identificar, verifique. " + cmd);
            }
            String identificador = cmd.substring(Comunes.toMD5("asociarDocele-".concat(session.getId())).length()+1); // el + 1 es por la c o i que viene de agregada desde la ventana
            CEPArchivoBO bo = new CEPArchivoBOImpl();
            CEPArchivo archivo = bo.actualizaCEP(identificador, estatus);
            if (archivo!=null){
                mensaje.append("Se ha asociado correctamente el documento.");
            } else {
                throw new GeDocBOException("Algo ocurrio y no se logro relacionar el documento. " + identificador);
            }
        } catch (GeDocBOException e) {
            mensaje.append("error:Exception on Servlet: ").append(e.getMessage());
            System.out.printf("TSMI:::Exception on Servlet %s, %s -->\n", this.getServletName(), e.getMessage());
            e.printStackTrace(System.out);
            System.out.printf("TSMI:::Exception on Servlet <--");
            com.ftc.aq.Comunes.escribeLog(logLocation, e, (String) request.getSession().getAttribute("usuario"));
        } finally {
            out.println(mensaje.toString());
            out.flush();
            out.close();
        }
    }

    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Bind the associated docs to the CEP";
    }

}
