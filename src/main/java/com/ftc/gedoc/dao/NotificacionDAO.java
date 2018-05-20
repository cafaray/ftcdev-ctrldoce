package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Notificacion;
import java.util.List;

public interface NotificacionDAO {
    
    Notificacion registrar(Notificacion notificacion) throws GeDocDAOException;
    Notificacion actualizar(Notificacion notificacion) throws GeDocDAOException;
    void eliminar(Notificacion notificacion) throws GeDocDAOException;
    List<Notificacion> listar() throws GeDocDAOException;
    List<Notificacion> listar(String estatus) throws GeDocDAOException;
    Notificacion buscarPorId(String persona, String documento, int identificador) throws GeDocDAOException;
}
