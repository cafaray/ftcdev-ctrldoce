package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.DocumentoEstatusDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.DocumentoEstatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentoEstatusDAOImpl implements DocumentoEstatusDAO {

    Connection conexion;
    
    @Override
    public DocumentoEstatus registrar(DocumentoEstatus documentoEstatus) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, documentoEstatus.getPersona()));
            params.add(new SpParam(2, Types.VARCHAR,documentoEstatus.getDocumentoElectronico()));
            params.add(new SpParam(3, Types.VARCHAR, documentoEstatus.getEstatus().getIndicador()));
            params.add(new SpParam(4, Types.VARCHAR, documentoEstatus.getComentario()));
            params.add(new SpParam(5, Types.VARCHAR, documentoEstatus.getSesion()));
            params.add(new SpParam(6, Types.INTEGER, null, true));
            params.add(new SpParam(7, Types.DATE, null, true));
            params.add(new SpParam(8, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraEstatusDocumento", params);
            if(vuelta!=null && vuelta.length==3){
                if(vuelta[2]!=null && !((String)vuelta[2]).isEmpty()){
                    throw new GeDocDAOException((String)vuelta[2]);
                }else{
                    documentoEstatus.setFecha((Date)vuelta[1]);
                    documentoEstatus.setIdentificador((Integer)vuelta[0]);
                    return documentoEstatus;
                }
            }else{
                throw new GeDocDAOException(String.format("El procedimiento devolvio diferentes parametros a los esperados. Se esperaban 3 y regresaron %s",vuelta!=null?vuelta.length:null));
            }
        }catch(SQLException e){
            throw new GeDocDAOException("Ocurrio una excepcion al registrar el estatus del documento. Revise el log para más detalle.", e);
        }finally{
            try{
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){
            }
        }
    }

    @Override
    public DocumentoEstatus actualizar(DocumentoEstatus documentoEstatus) throws GeDocDAOException {
        return registrar(documentoEstatus);
    }

    @Override
    public void eliminar(DocumentoEstatus documentoEstatus) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement("DELETE FROM jdem30t WHERE cdperson = ? AND cddocele = ? AND idnumact = ?;");
            stm.setString(1, documentoEstatus.getPersona());
            stm.setString(2, documentoEstatus.getDocumentoElectronico());
            stm.setInt(3, documentoEstatus.getIdentificador());
            stm.executeUpdate();
        }catch(SQLException e){
            throw new GeDocDAOException("Ocurrio una excepcion al registrar el estatus del documento. Revise el log para más detalle.", e);
        }finally{
            try{
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){
            }
        }
    }

    @Override
    public List<DocumentoEstatus> estatus(String persona, String documentoElectronico) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement("SELECT cdperson, cddocele, idnumact, instatus, dtfecsta, dscoment, "
                    + "(SELECT CONCAT(dsfirst, ' ', dslast) FROM jpem10t WHERE cdcontac = obtieneUsuario(cdusulog)) "
                    + "FROM jdem30t "
                    + "WHERE cdperson = ? AND cddocele = ?;");
            stm.setString(1, persona);
            stm.setString(2, documentoElectronico);
            ResultSet rst = stm.executeQuery();
            List<DocumentoEstatus> documentos = new ArrayList<DocumentoEstatus>();
            while(rst.next()){
                DocumentoEstatus documentoEstatus = new DocumentoEstatus();
                documentoEstatus.setPersona(persona);
                documentoEstatus.setDocumentoElectronico(documentoElectronico);
                documentoEstatus.setIdentificador(rst.getInt(3));
                documentoEstatus.setEstatus(DocumentoEstatus.Estatus.getEstatus(rst.getString(4).charAt(0)));
                documentoEstatus.setFecha(rst.getDate(5)!=null?rst.getDate(5):null);
                documentoEstatus.setComentario(rst.getString(6));
                documentoEstatus.setUsuario(rst.getString(7));
                documentos.add(documentoEstatus);
            }
            return documentos;
        }catch(SQLException e){
            throw new GeDocDAOException("Ocurrio una excepcion al verificar el estatus del documento. Revise el log para más detalle.", e);
        }finally{
            try{
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){
            }
        }
    }
    
}
