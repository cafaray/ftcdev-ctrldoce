package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.ContactoBO;
import com.ftc.gedoc.dao.ContactoDAO;
import com.ftc.gedoc.dao.impl.ContactoDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Contacto;

import java.util.List;

public class ContactoBOImpl implements ContactoBO{

    private ContactoDAO dao;
    
    public ContactoBOImpl(){
        dao = new ContactoDAOImpl();
    }
    
    @Override
    public Contacto buscarPorCorreo(String correo) throws GeDocBOException {
        try{
            return dao.encuentraPorCorreo(correo);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> contactoPorEmpresa(String idEmpresa) throws GeDocBOException {
        try{
            return dao.listarContactosPorEmpresa(idEmpresa);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public Contacto buscarPorId(String id) throws GeDocBOException {
        try{
            return dao.encuentraPorId(id);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> obtieneContactos(String sesion) throws GeDocBOException {
        try{
            return dao.obtieneContactos(sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> obtieneContactos(String empresa, String tipo, String sesion) throws GeDocBOException {
        try{
            return dao.obtieneContactos(empresa,tipo,sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public Contacto buscaContactoPorCorreo(String empresa, String correo, String sesion) throws GeDocBOException {
        try{
            return dao.buscaContactoPorCorreo(empresa,correo,sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> listaContactos(String sesion) throws GeDocBOException {
        try{
            return dao.listaContactos(sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> listaContactosSuspendidos(String sesion) throws GeDocBOException {
        try{
            return dao.listaContactosSuspendidos(sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public List<Contacto> contactoPorEmpresa(String idEmpresa, String filtro) throws GeDocBOException {
         try{
            return dao.listarContactosPorEmpresa(idEmpresa, filtro);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public boolean insertaContacto(Contacto contacto, String contrasenia, String sesion) throws GeDocBOException {
        try{
            return dao.insertaContacto(contacto,contrasenia,sesion);
        } catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage());
        }
    }

    @Override
    public boolean actualizarContacto(Contacto contacto, String identificador, String sesion) throws GeDocBOException {
        try{
            return dao.actualizarContacto(contacto,identificador,sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }

    @Override
    public boolean eliminarContacto(String correo, String identificador, String sesion) throws GeDocBOException {
        try{
            return dao.eliminarContacto(correo, identificador, sesion);
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e);
        }
    }
}
