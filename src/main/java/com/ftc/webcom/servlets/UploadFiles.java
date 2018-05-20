package com.ftc.webcom.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadFiles extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. 
            $data = array();
 
            if(isset($_GET['files'])) {  
                $error = false;
                $files = array();
 
                $uploaddir = './uploads/';
                foreach($_FILES as $file) {
                    if(move_uploaded_file($file['tmp_name'], $uploaddir .basename($file['name']))) {
			$files[] = $uploaddir .$file['name'];
                    } else {
                        $error = true;
                    }
                }
                $data = ($error) ? array('error' => 'There was an error uploading your files') : array('files' => $files);
            } else {
                $data = array('success' => 'Form was submitted', 'formData' => $_POST);
            }
 
            echo json_encode($data);
            */
            String files = request.getParameter("files");
            if(files!=null){
                
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
    }// </editor-fold>

}
