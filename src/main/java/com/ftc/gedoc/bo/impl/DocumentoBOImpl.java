package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.DocumentoBO;
import com.ftc.gedoc.dao.DocumentoDAO;
import com.ftc.gedoc.dao.impl.DocumentoDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Documento;

import java.util.List;

public class DocumentoBOImpl implements DocumentoBO {
    @Override
    public Documento recuperaDocumento(String identificador) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return dao.recuperaDocumento(identificador);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Documento> recuperaDocumentos(String elementos) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return  dao.recuperaDocumentos(elementos);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public int eliminaDocumento(Documento documento) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return dao.eliminaDocumento(documento);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public Documento findById(String id) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return dao.findById(id);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Documento> listadoDocumentos(String empresa, String tipo, String sesion) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return dao.listadoDocumentos(empresa,tipo, sesion);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Documento> listadoDocumentos(String empresa, String tipo, String fechaInicial, String fechaFinal, String sesion) throws GeDocBOException {
        try {
            DocumentoDAO dao = new DocumentoDAOImpl();
            return dao.listadoDocumentos(empresa,tipo, fechaInicial, fechaFinal, sesion);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
}
