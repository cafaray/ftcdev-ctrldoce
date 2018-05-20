package com.ftc.gedoc.bo.impl;

import com.ftc.gedoc.bo.PersonaBO;
import com.ftc.gedoc.dao.PersonaDAO;
import com.ftc.gedoc.dao.impl.PersonaDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Persona;

import java.util.Collection;
import java.util.List;

public class PersonaBOImpl implements PersonaBO {
    @Override
    public List<Persona> obtienePersonas(char tipo, String sesion) throws GeDocBOException {
        try{
            PersonaDAO dao = new PersonaDAOImpl();
            return dao.obtienePersonas(tipo, sesion);
        }catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Persona> localizaPersonas(char tipo, String nombre, String sesion) throws GeDocBOException {
        try{
            PersonaDAO dao = new PersonaDAOImpl();
            return dao.localizaPersonas(tipo, nombre, sesion);
        }catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public Persona localizaPersonasPorIdentificador(String identificador) throws GeDocBOException {
        try{
            PersonaDAO dao = new PersonaDAOImpl();
            return dao.localizaPersonasPorIdentificador(identificador);
        }catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Persona> localizaPersonasPorRFC(char tipo, String rfc, String sesion) throws GeDocBOException {
        try{
            PersonaDAO dao = new PersonaDAOImpl();
            return dao.localizaPersonasPorRFC(tipo, rfc, sesion);
        }catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public boolean insertaPersona(Persona persona, String sesion) throws GeDocBOException {
        try{
            PersonaDAO dao = new PersonaDAOImpl();
            return dao.insertaPersona(persona, sesion);
        }catch (GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
}
