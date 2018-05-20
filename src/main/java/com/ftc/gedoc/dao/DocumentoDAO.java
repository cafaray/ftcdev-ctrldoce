package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Documento;

import java.util.List;

public interface DocumentoDAO {
    Documento recuperaDocumento(String identificador) throws GeDocDAOException;

    List<Documento> recuperaDocumentos(String elementos) throws GeDocDAOException;

    int eliminaDocumento(Documento documento) throws GeDocDAOException;

    Documento findById(String id) throws GeDocDAOException;

    List<Documento> listadoDocumentos(String empresa, String tipo, String sesion) throws GeDocDAOException;

    List<Documento> listadoDocumentos(String empresa, String tipo, String fechaInicial, String fechaFinal,
                                      String sesion) throws GeDocDAOException;
}
