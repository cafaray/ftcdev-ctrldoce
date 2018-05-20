package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.CEPArchivo;
import com.ftc.modelo.CEPCabecera;
import java.util.List;

public interface CEPArchivoBO {
    
    CEPArchivo registraCEP(CEPArchivo cep, CEPCabecera xml) throws GeDocBOException;
    List<CEPArchivo> listaCEP(String persona) throws GeDocBOException;
    CEPArchivo obtieneCEP(String identificador) throws GeDocBOException;
    List<CEPArchivo> listaCEP(String persona, String fechaInicio, String fechaFinal) throws GeDocBOException;
    CEPArchivo actualizaCEP(String identificador, String estatus) throws GeDocBOException;
}
