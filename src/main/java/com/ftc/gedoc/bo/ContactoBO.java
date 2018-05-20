package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Contacto;

import java.util.List;

public interface ContactoBO {

    Contacto buscarPorCorreo(String correo) throws GeDocBOException;
    List<Contacto> contactoPorEmpresa(String idEmpresa) throws GeDocBOException;
    List<Contacto> contactoPorEmpresa(String idEmpresa, String filtro) throws GeDocBOException;
    Contacto buscarPorId(String id) throws GeDocBOException;
    List<Contacto> obtieneContactos(String sesion) throws GeDocBOException;

    List<Contacto> obtieneContactos(String empresa, String tipo, String sesion) throws GeDocBOException;

    Contacto buscaContactoPorCorreo(String empresa, String correo, String sesion) throws GeDocBOException;

    List<Contacto> listaContactos(String sesion) throws GeDocBOException;

    List<Contacto> listaContactosSuspendidos(String sesion) throws GeDocBOException;

    boolean insertaContacto(Contacto contacto, String contrasenia, String sesion) throws GeDocBOException;

    boolean actualizarContacto(Contacto contacto, String identificador, String sesion) throws GeDocBOException;

    boolean eliminarContacto(String correo, String identificador, String sesion) throws GeDocBOException;

}
