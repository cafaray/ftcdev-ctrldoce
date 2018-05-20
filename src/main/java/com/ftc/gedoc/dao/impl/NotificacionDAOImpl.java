package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.NotificacionDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Notificacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAOImpl implements NotificacionDAO{

    Connection conexion = null;
    
    @Override
    public Notificacion registrar(Notificacion notificacion) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            String sp = "insertaNotificacion";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, notificacion.getPersona()));
            params.add(new SpParam(2, Types.VARCHAR, notificacion.getDocumentoElectronico()));
            params.add(new SpParam(3, Types.INTEGER, notificacion.getIdentificador()));            
            params.add(new SpParam(4, Types.VARCHAR, notificacion.getMensaje()));
            params.add(new SpParam(5, Types.VARCHAR, notificacion.getConCopia()));
            params.add(new SpParam(6, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if(vuelta!=null && vuelta.length==1){
                if(!((String)vuelta[0]).isEmpty()){
                    throw new GeDocDAOException((String)vuelta[0]);
                }
                return notificacion;
            }else{
                throw new GeDocDAOException(String.format("El procedimiento devolvio diferentes parametros a los esperados. Se esperaban 2 y regresaron %s",vuelta!=null?vuelta.length:null));
            }
        }catch(SQLException e){
            e.printStackTrace(System.out);
            throw new GeDocDAOException("Ocurrion una excepción al registrar los datos de notificación. Revise el log de salida.", e);
        } finally {
            try {
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){}
        }
    }

    @Override
    public Notificacion actualizar(Notificacion notificacion) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            String sp = "actualizaNotificacion";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, notificacion.getPersona()));
            params.add(new SpParam(2, Types.VARCHAR, notificacion.getDocumentoElectronico()));
            params.add(new SpParam(3, Types.INTEGER, notificacion.getIdentificador()));
            params.add(new SpParam(4, Types.DATE, notificacion.getFecha()));
            params.add(new SpParam(5, Types.VARCHAR, notificacion.getEstatus()));
            params.add(new SpParam(6, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if(vuelta!=null && vuelta.length==1){
                if(((String)vuelta[0]).isEmpty()){
                    return notificacion;
                } else {
                    throw new GeDocDAOException((String)vuelta[0]);
                }
            }else{
                throw new GeDocDAOException(String.format("El procedimiento devolvio diferentes parametros a los esperados. Se esperaba 1 y regresaron %s",vuelta!=null?vuelta.length:null));
            }
        }catch(SQLException e){
            e.printStackTrace(System.out);
            throw new GeDocDAOException("Ocurrion una excepción al actualizar los datos de notificación. Revise el log de salida.", e);
        } finally {
            try {
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){}
        }
    }

    @Override
    public void eliminar(Notificacion notificacion) throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            String sql = "DELETE FROM jdem40t WHERE cdperson = ? AND cddocele = ? AND idnumact = ?;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            stm.setString(1, notificacion.getPersona());
            stm.setString(2, notificacion.getDocumentoElectronico());
            stm.setInt(3, notificacion.getIdentificador());
        }catch(SQLException e){
            e.printStackTrace(System.out);
            throw new GeDocDAOException("Ocurrion una excepción al actualizar los datos de notificación. Revise el log de salida.", e);
        } finally {
            try {
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){}
        }
    }

    @Override
    public List<Notificacion> listar() throws GeDocDAOException {
        try{
            conexion = Conexion.getConexion();
            List<Notificacion> notificaciones = new ArrayList<Notificacion>();
            String sql = "SELECT cdperson, cddocele, idnumact, dsmensaj, dtnotifi, instatus, dsccmail, tmstmp "
                    + " FROM jdem40t ORDER BY cdperson, cddocele, tmstmp desc;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            while(rst.next()){
                Notificacion notificacion = new Notificacion();
                notificacion.setConCopia(rst.getString(7));
                notificacion.setDocumentoElectronico(rst.getString(2));
                notificacion.setEstatus(rst.getString(6));
                notificacion.setFecha(rst.getDate(5));
                notificacion.setIdentificador(rst.getInt(3));
                notificacion.setMensaje(rst.getString(4));
                notificacion.setPersona(rst.getString(1));
                notificacion.setFechaRegistro(rst.getTimestamp(8));
                notificaciones.add(notificacion);
            }
            return notificaciones;
        }catch(SQLException e){
            e.printStackTrace(System.out);
            throw new GeDocDAOException("Ocurrion una excepción al listar los datos de notificación. Revise el log de salida.", e);
        } finally {
            try {
                if(conexion!=null && !conexion.isClosed()){
                    conexion.close();
                }
            }catch(SQLException e){}
        }
    }

    @Override
    public List<Notificacion> listar(String estatus) throws GeDocDAOException {
            List<Notificacion> notificaciones = listar();
            if(notificaciones.size()>0){
                List<Notificacion> filtro = new ArrayList<Notificacion>();
                for(Notificacion notificacion:notificaciones){
                    if(notificacion.getEstatus().equals(estatus)){
                        filtro.add(notificacion);
                    }
                }
                return filtro;
            }
            return notificaciones;
    }

    @Override
    public Notificacion buscarPorId(String persona, String documento, int identificador) throws GeDocDAOException {
        List<Notificacion> notificaciones = listar();
        for(Notificacion notificacion:notificaciones){
            if(notificacion.getPersona().equals(persona)
            && notificacion.getDocumentoElectronico().equals(documento)
            && notificacion.getIdentificador()==identificador){
                return notificacion;
            }            
        }
        return null;
    }
    
}
