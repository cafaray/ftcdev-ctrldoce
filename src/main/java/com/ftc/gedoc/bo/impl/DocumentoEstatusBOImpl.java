package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.DocumentoEstatusBO;
import com.ftc.gedoc.dao.DocumentoEstatusDAO;
import com.ftc.gedoc.dao.impl.DocumentoEstatusDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.DocumentoEstatus;
import com.ftc.modelo.Documento;

import java.util.List;

public class DocumentoEstatusBOImpl implements DocumentoEstatusBO {

    private final DocumentoEstatusDAO dao;
    
    public DocumentoEstatusBOImpl(){
        dao = new DocumentoEstatusDAOImpl();
    }
    
    @Override
    public DocumentoEstatus registrar(Documento documento, DocumentoEstatus.Estatus estatus, String comentario, String sesion) throws GeDocBOException {
        try{
            DocumentoEstatus documentoEstatus = new DocumentoEstatus();
            documentoEstatus.setPersona(documento.getPersona());
            documentoEstatus.setDocumentoElectronico(documento.getIdentificador());
            documentoEstatus.setEstatus(estatus);
            documentoEstatus.setComentario(comentario);
            documentoEstatus.setSesion(sesion);            
            return dao.registrar(documentoEstatus);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
    @Override
    public DocumentoEstatus registrar(DocumentoEstatus documentoEstatus) throws GeDocBOException {
        try{
            return dao.registrar(documentoEstatus);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<DocumentoEstatus> listar(String empresa, String documentoElectronico) throws GeDocBOException {
        try{
            return dao.estatus(empresa, documentoElectronico);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
}
