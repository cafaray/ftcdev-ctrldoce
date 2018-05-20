package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.GrupoBO;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Grupo;
import com.ftc.gedoc.dao.GrupoDAO;
import com.ftc.gedoc.dao.impl.GrupoDAOImpl;
import java.util.List;

public class GrupoBOImpl implements GrupoBO {

    GrupoDAO dao;

    @Override
    public List<Grupo> listar() throws GeDocBOException {
        dao = new GrupoDAOImpl();
        try {
            return dao.listado();
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e);
        }
    }

    @Override
    public Grupo actualizar(Grupo grupo) throws GeDocBOException {
        dao = new GrupoDAOImpl();
        if (grupo.getIdentificador() != null && grupo.getIdentificador().length() > 1) {
            try {
                return dao.actualizar(grupo);
            } catch (GeDocDAOException e) {
                throw new GeDocBOException(e);
            }
        } else if (grupo.getIdentificador() == null) {
            try {
                return dao.registrar(grupo);
            } catch (GeDocDAOException e) {
                throw new GeDocBOException(e);
            }
        } else {
            throw new GeDocBOException("Los grupos predefinidos por la aplicación no se pueden actualizar.");
        }
    }

    @Override
    public Grupo buscar(String id) throws GeDocBOException {
        dao = new GrupoDAOImpl();
        try {
            return dao.encuentraPorId(id);
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e);
        }
    }

    @Override
    public Grupo asignarPermisos(String id, long permisos) throws GeDocBOException {
        dao = new GrupoDAOImpl();
        try {
            if (id.length() > 1) {
                return dao.actualizaPermisosPorId(id, permisos);
            } else {
                throw new GeDocBOException("Los grupos predefinidos por la aplicación no se pueden actualizar.");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e);
        }
    }

    @Override
    public void eliminar(String id) throws GeDocBOException {
        dao = new GrupoDAOImpl();
        try {
            Grupo grupo = dao.encuentraPorId(id);
            if (grupo != null) {
                if (grupo.getIdentificador().length() > 1) {
                    dao.eliminar(grupo);
                } else {
                    throw new GeDocBOException("Los grupos predefinidos por la aplicación no se pueden eliminar.");
                }
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e);
        }
    }

}
