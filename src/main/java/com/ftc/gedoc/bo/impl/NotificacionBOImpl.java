package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.NotificacionBO;
import com.ftc.gedoc.dao.NotificacionDAO;
import com.ftc.gedoc.dao.impl.NotificacionDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Notificacion;
import java.util.ArrayList;
import java.util.List;

public class NotificacionBOImpl implements NotificacionBO{

    NotificacionDAO dao = new NotificacionDAOImpl();
    
    public NotificacionBOImpl(){}
    
    @Override
    public Notificacion registrar(Notificacion notificacion) throws GeDocBOException {
        try{
            return dao.registrar(notificacion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
    @Override
    public Notificacion cancelarNotificacion(Notificacion notificacion) throws GeDocBOException {
        try{
            if(notificacion!=null){
                notificacion.setEstatus("C");
                return dao.actualizar(notificacion);
            }else{
                throw new GeDocBOException("No se localizo la notificación");
            }
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public int cancelarNotificaciones(List<Notificacion> notificaciones) throws GeDocBOException {
        for(Notificacion notificacion:notificaciones){
            cancelarNotificacion(notificacion);
        }
        return notificaciones.size();
    }

    @Override
    public List<Notificacion> notificaciones(String empresa) throws GeDocBOException {
        try{
            List<Notificacion> notificaciones = dao.listar();
            List<Notificacion> filtro = new ArrayList<Notificacion>();
            for(Notificacion notificacion:notificaciones){
                if(empresa.equals(notificacion.getPersona())){
                    filtro.add(notificacion);
                }
            }
            return filtro;
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Notificacion> notificaciones(String empresa, String estatus) throws GeDocBOException {
        try{
            List<Notificacion> notificaciones = dao.listar();
            List<Notificacion> filtro = new ArrayList<Notificacion>();
            for(Notificacion notificacion:notificaciones){
                if(empresa.equals(notificacion.getPersona()) && estatus.equals(notificacion.getEstatus())){
                    filtro.add(notificacion);
                }
            }
            return filtro;
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
}
