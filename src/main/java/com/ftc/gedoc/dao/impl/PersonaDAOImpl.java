package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.PersonaDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Persona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PersonaDAOImpl implements PersonaDAO {


    @Override
    public List<Persona> obtienePersonas(char tipo, String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Persona> listado = new LinkedList<Persona>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.CHAR, tipo));
            params.add(new SpParam(2, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "obtienePersonas", params);
            while (rst.next()) {
                Persona p = new Persona();
                p.setIdentificador(rst.getString(1));
                p.setNombre(rst.getString(2));
                p.setRfc(rst.getString(3));
                p.setTipo(rst.getString(4).charAt(0));
                listado.add(p);
            }
            return listado;
        } catch(SQLException e){
            throw new GeDocDAOException("Imposible obtener el listado de personas, ocurrió una excepción en \"obtienePersonas\". Revise el log.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public Collection<Persona> localizaPersonas(char tipo, String nombre, String sesion)
            throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Persona> listado = new LinkedList<Persona>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.CHAR, tipo));
            params.add(new SpParam(2, Types.CHAR, nombre));
            params.add(new SpParam(3, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "localizaPersonas", params);
            while (rst.next()) {
                Persona p = new Persona();
                p.setIdentificador(rst.getString(1));
                p.setNombre(rst.getString(2));
                p.setRfc(rst.getString(3));
                p.setTipo(rst.getString(4).charAt(0));
                listado.add(p);
            }
            return listado;
        } catch(SQLException e){
            throw new GeDocDAOException("Imposible obtener el listado de personas, ocurrió una excepción en \"localizaPersonas\". Revise el log.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public Persona localizaPersonasPorIdentificador(String identificador) throws GeDocDAOException {
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            String sql = "SELECT cdperson,dsrazsoc,dsrfc,intipprs FROM jpem00t where cdperson = '"+identificador+"'";
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                Persona p = new Persona();
                p.setIdentificador(rst.getString(1));
                p.setNombre(rst.getString(2));
                p.setRfc(rst.getString(3));
                p.setTipo(rst.getString(4).charAt(0));
                return p;
            } else {
                return null;
            }        
        } catch(SQLException e){
            throw new GeDocDAOException("Imposible localizar a la persona, ocurrio una excepcion en \"SELECT:localizaPersonasPorIdentificador\" - " + identificador, e);
        } finally {
            if (conexion!=null){
                try{
                    conexion.close();
                }catch(SQLException e){}
            }
        }                
    }

    @Override
    public Collection<Persona> localizaPersonasPorRFC(char tipo, String rfc, String sesion)
            throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Persona> listado = new LinkedList<Persona>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.CHAR, tipo));
            params.add(new SpParam(2, Types.CHAR, rfc));
            params.add(new SpParam(3, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "localizaPersonasPorRFC", params);
            while (rst.next()) {
                Persona p = new Persona();
                p.setIdentificador(rst.getString(1));
                p.setNombre(rst.getString(2));
                p.setRfc(rst.getString(3));
                p.setTipo(rst.getString(4).charAt(0));
                listado.add(p);
            }
            return listado;
        } catch(SQLException e){
            throw new GeDocDAOException("Imposible localizar a la persona, ocurrio una excepcion en \"localizaPersonasPorRFC\" - " + rfc, e);
        } finally {
            if (conexion!=null){
                try{
                    conexion.close();
                }catch(SQLException e){}
            }
        }
    }

    @Override
    public boolean insertaPersona(Persona persona, String sesion) throws GeDocDAOException {
        Object[] vuelta = null;
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            //registraPersona(IN{razonsocial,rfc,tipo,sesion},OUT{referencia,error}
            params.add(new SpParam(1, Types.VARCHAR, persona.getNombre()));
            params.add(new SpParam(2, Types.VARCHAR, persona.getRfc()));
            params.add(new SpParam(3, Types.VARCHAR, persona.getTipo()));
            params.add(new SpParam(4, Types.VARCHAR, sesion));
            params.add(new SpParam(5, Types.VARCHAR, null, true));
            params.add(new SpParam(6, Types.VARCHAR, null, true));
            vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraPersona", params);
            if (vuelta != null && vuelta.length == 2) {
                if (String.valueOf(vuelta[1]).length() > 0) {
                    throw new GeDocDAOException((String)vuelta[1]);
                } else {
                    try {
                        persona.setIdentificador((String) vuelta[0]);
                    } catch (NullPointerException e) {
                        throw new GeDocDAOException("Al parecer no se ha logrado registrar a la persona, el identificador es nulo.");
                    }
                    return true;
                }
            } else {
                String desVuelta = vuelta==null?"nula":String.valueOf(vuelta.length);
                throw new GeDocDAOException(String.format("La respuesta del procedimiento no es reconocida, se esperaban 2 y la vuelta es %s", desVuelta));
            }
        }catch (SQLException e){
            throw new GeDocDAOException("No se ha logrado obtener la respuesta del procedimiento.", e);
        } finally {
            if (conexion!=null){
                try{
                    conexion.close();
                }catch(SQLException e){}
            }
        }
    }

}
