package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.DocumentoEstatus;
import java.util.List;

public interface DocumentoEstatusDAO  {
    
    /***
     * registra un nuevo elemento en la unidad de persistencia de estatus del documento
     * @param documentoEstatus objeto a persistir
     * @return DocumentoEstatus con su identificador de registro
     * @throws GeDocDAOException 
     */
    DocumentoEstatus registrar(DocumentoEstatus documentoEstatus) throws GeDocDAOException;
    
    /***
     * actualiza un elemento dado de la unidad de persistencia
     * @param documentoEstatus objeto a actualizar
     * @return DocumentoEstatus objeto actualizado desde la unidad de persistencia
     * @throws GeDocDAOException 
     */
    DocumentoEstatus actualizar(DocumentoEstatus documentoEstatus) throws GeDocDAOException;
    
    /***
     * elimina un registro desde la unidad de persistencia
     * @param documentoEstatus objeto que se espera sea eliminado
     * @throws GeDocDAOException 
     */
    void eliminar(DocumentoEstatus documentoEstatus) throws GeDocDAOException;
    
    /***
     * estatus, lista los diferentes registros de estatus asociados al documento electronico de la persona que se envia
     * @param persona Codigo de la persona owner del documento 
     * @param documentoElectronico Codigo del documento electronico del cual se requiere conocer los estatus
     * @return java.util.List<DocumentoEstatus> listado con los diferentes estatus que tiene el documento
     * @throws GeDocDAOException 
     */
    List<DocumentoEstatus> estatus(String persona, String documentoElectronico) throws GeDocDAOException;
    
}
