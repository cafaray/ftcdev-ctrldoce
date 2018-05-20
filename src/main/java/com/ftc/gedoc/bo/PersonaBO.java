package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Persona;

import java.util.Collection;
import java.util.List;

public interface PersonaBO {

    List<Persona> obtienePersonas(char tipo, String sesion) throws GeDocBOException;

    Collection<Persona> localizaPersonas(char tipo, String nombre, String sesion)
            throws GeDocBOException;

    Persona localizaPersonasPorIdentificador(String identificador) throws GeDocBOException;

    Collection<Persona> localizaPersonasPorRFC(char tipo, String rfc, String sesion)
            throws GeDocBOException;

    boolean insertaPersona(Persona persona, String sesion) throws GeDocBOException;

}
