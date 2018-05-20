package com.ftc.webcom.servlets;

import com.ftc.services.invoice.CFDIReader;
import com.ftc.modelo.CFDICabecera;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFileTemp extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            File file;
            int maxFileSize = 200 * 1024;
            int maxMemSize = 2000 * 1024;
            ServletContext context = getServletContext();
            String filePath = "/tmp/"; //context.getInitParameter("factura");
            String contentType = request.getContentType();
            if ((contentType.indexOf("multipart/form-data") >= 0)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(maxMemSize);
                factory.setRepository(new File(filePath));                
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(maxFileSize);
                try {
                    List fileItems = upload.parseRequest(request);
                    Iterator i = fileItems.iterator();
                    while (i.hasNext()) {
                        FileItem fi = (FileItem) i.next();
                        if (!fi.isFormField()) {
                            String fieldName = fi.getFieldName();
                            String fileName = fi.getName();
                            boolean isInMemory = fi.isInMemory();
                            long sizeInBytes = fi.getSize();
                            file = new File(filePath + fileName.substring(fileName.lastIndexOf("/") + 1));
                            fi.write(file);
                            StringBuilder archivo = new StringBuilder();
                            archivo.append(filePath).append(fileName);
                            System.out.println("Uploaded temp file: " + archivo.toString());
                            //lee el archivo xml
                            CFDICabecera xmlDoc = CFDIReader.procesaXML(archivo.toString());
                            if (xmlDoc != null) {
                                out.print("fecha:"+xmlDoc.getStrFecha()+",importe:"+xmlDoc.getTotal()+",impuestos:"+xmlDoc.getTotalImpuestosTrasladados());
                            } else {
                                out.printf("No se logro leer el archivo %s como un CFD/CFDI valido.", fileName);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else {
                out.println("No file to upload.");
            }
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
        return "Short description";
    }

}
