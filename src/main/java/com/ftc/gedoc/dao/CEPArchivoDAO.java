package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CEPArchivo;
import java.util.List;

public interface CEPArchivoDAO {
    
    CEPArchivo registraCEP(CEPArchivo archivo) throws GeDocDAOException;
    CEPArchivo actualizaCEP(CEPArchivo archivo) throws GeDocDAOException;
    CEPArchivo obtieneCEP(String identificador) throws GeDocDAOException;
    boolean eliminaCEP(String identificador) throws GeDocDAOException;
    List<CEPArchivo> listar(String proveedor) throws GeDocDAOException;
    List<CEPArchivo> listar(String proveedor, String fechaInicial, String fechaFinal) throws GeDocDAOException;
}
