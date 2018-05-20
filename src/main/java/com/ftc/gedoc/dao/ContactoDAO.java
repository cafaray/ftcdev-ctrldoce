package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Contacto;

import java.util.List;

public interface ContactoDAO {

    Contacto encuentraPorId(String id) throws GeDocDAOException;
    Contacto encuentraPorCorreo(String correo) throws GeDocDAOException;
    List<Contacto> listarContactosPorEmpresa(String empresa) throws GeDocDAOException;
    List<Contacto> listarContactos() throws GeDocDAOException;
    List<Contacto> listarContactosPorEmpresa(String empresa, String filtro) throws GeDocDAOException;

    List<Contacto> obtieneContactos(String sesion) throws GeDocDAOException;

    List<Contacto> obtieneContactos(String empresa, String tipo, String sesion) throws GeDocDAOException;

    Contacto buscaContactoPorCorreo(String empresa, String correo, String sesion) throws GeDocDAOException;

    List<Contacto> listaContactos(String sesion) throws GeDocDAOException;

    List<Contacto> listaContactosSuspendidos(String sesion) throws GeDocDAOException;

    boolean insertaContacto(Contacto contacto, String contrasenia, String sesion) throws GeDocDAOException;

    boolean actualizarContacto(Contacto contacto, String identificador, String sesion) throws GeDocDAOException;

    boolean eliminarContacto(String correo, String identificador, String sesion) throws GeDocDAOException;
}
