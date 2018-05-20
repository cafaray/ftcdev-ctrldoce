package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Documento;

import java.util.List;

public interface DocumentoBO {

    Documento recuperaDocumento(String identificador) throws GeDocBOException;

    List<Documento> recuperaDocumentos(String elementos) throws GeDocBOException;

    int eliminaDocumento(Documento documento) throws GeDocBOException;

    Documento findById(String id) throws GeDocBOException;

    List<Documento> listadoDocumentos(String empresa, String tipo, String sesion) throws GeDocBOException;

    List<Documento> listadoDocumentos(String empresa, String tipo, String fechaInicial, String fechaFinal,
                                      String sesion) throws GeDocBOException;

}
