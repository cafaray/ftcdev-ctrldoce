package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Grupo;
import java.util.List;

public interface GrupoDAO {
    List<Grupo> listado() throws GeDocDAOException;
    Grupo registrar(Grupo grupo) throws GeDocDAOException;
    Grupo actualizaPermisosPorId(String id, long permisos) throws GeDocDAOException;
    Grupo actualizar(Grupo grupo) throws GeDocDAOException;
    void eliminar(Grupo grupo) throws GeDocDAOException;
    void eliminarPorId(String id) throws GeDocDAOException;
    Grupo encuentraPorId(String id) throws GeDocDAOException;
    
}
