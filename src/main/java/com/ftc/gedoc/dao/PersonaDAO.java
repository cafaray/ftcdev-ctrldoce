package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Persona;

import java.util.Collection;
import java.util.List;

public interface PersonaDAO {
    List<Persona> obtienePersonas(char tipo, String sesion) throws GeDocDAOException;

    Collection<Persona> localizaPersonas(char tipo, String nombre, String sesion)
            throws GeDocDAOException;

    Persona localizaPersonasPorIdentificador(String identificador) throws GeDocDAOException;

    Collection<Persona> localizaPersonasPorRFC(char tipo, String rfc, String sesion)
                    throws GeDocDAOException;

    boolean insertaPersona(Persona persona, String sesion) throws GeDocDAOException;
}
