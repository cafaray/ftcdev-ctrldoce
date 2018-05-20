package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.DocumentoEstatus;
import com.ftc.modelo.Documento;

import java.util.List;

public interface DocumentoEstatusBO {

    DocumentoEstatus registrar(Documento documento, DocumentoEstatus.Estatus estatus, String comentario, String sesion) throws GeDocBOException;
    
    DocumentoEstatus registrar(DocumentoEstatus documentoEstatus) throws GeDocBOException;

    List<DocumentoEstatus> listar(String empresa, String documentoElectronico) throws GeDocBOException;
    
}
