package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Grupo;
import java.util.List;

public interface GrupoBO {
    List<Grupo> listar()throws GeDocBOException;
    Grupo actualizar(Grupo grupo)throws GeDocBOException;
    Grupo buscar(String id) throws GeDocBOException;
    Grupo asignarPermisos(String id, long permisos) throws GeDocBOException;
    void eliminar(String id)throws GeDocBOException;
}
