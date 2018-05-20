package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.ContactoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Contacto;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ContactoDAOImpl implements ContactoDAO {

    private Connection conexion;

    @Override
    public Contacto encuentraPorId(String id) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdperson, cdcontac, dsfirst, dslast, dsmail, dstelloc, dstelmov ");
            sql.append(" FROM jpem10t ");
            sql.append(" WHERE cdcontac = ?;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                Contacto contacto = new Contacto();
                contacto.setPersona(rst.getString(1));
                contacto.setIdentificador(rst.getString(2));
                contacto.setNombre(rst.getString(3));
                contacto.setApellido(rst.getString(4));
                contacto.setCorreo(rst.getString(5));
                contacto.setTelefono(rst.getString(6));
                contacto.setMovil(rst.getString(7));
                return contacto;
            }
            return null;
        } catch (SQLException e) {
            throw new GeDocDAOException(e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Contacto encuentraPorCorreo(String correo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdperson, cdcontac, dsfirst, dslast, dsmail, dstelloc, dstelmov ");
            sql.append(" FROM jpem10t ");
            sql.append(" WHERE dsmail = ?;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, correo);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                Contacto contacto = new Contacto();
                contacto.setPersona(rst.getString(1));
                contacto.setIdentificador(rst.getString(2));
                contacto.setNombre(rst.getString(3));
                contacto.setApellido(rst.getString(4));
                contacto.setCorreo(rst.getString(5));
                contacto.setTelefono(rst.getString(6));
                contacto.setMovil(rst.getString(7));
                return contacto;
            }
            return null;
        } catch (SQLException e) {
            throw new GeDocDAOException(e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<Contacto> listarContactosPorEmpresa(String empresa) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            List<Contacto> contactos = new ArrayList<Contacto>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdperson, cdcontac, dsfirst, dslast, dsmail, dstelloc, dstelmov ");
            sql.append(" FROM jpem10t ");
            sql.append(" WHERE cdperson = ?;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, empresa);
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                Contacto contacto = new Contacto();
                contacto.setPersona(rst.getString(1));
                contacto.setIdentificador(rst.getString(2));
                contacto.setNombre(rst.getString(3));
                contacto.setApellido(rst.getString(4));
                contacto.setCorreo(rst.getString(5));
                contacto.setTelefono(rst.getString(6));
                contacto.setMovil(rst.getString(7));
                contactos.add(contacto);
            }
            return contactos;
        } catch (SQLException e) {
            throw new GeDocDAOException(e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<Contacto> listarContactosPorEmpresa(String empresa, String filtro) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            List<Contacto> contactos = new ArrayList<Contacto>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdperson, cdcontac, dsfirst, dslast, dsmail, dstelloc, dstelmov ");
            sql.append(" FROM jpem10t ");
            sql.append(" WHERE cdperson = ? AND CONCAT(dsfirst, ' ', dslast) LIKE ?;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, empresa);
            stm.setString(2, "%"+filtro+"%");
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                Contacto contacto = new Contacto();
                contacto.setPersona(rst.getString(1));
                contacto.setIdentificador(rst.getString(2));
                contacto.setNombre(rst.getString(3));
                contacto.setApellido(rst.getString(4));
                contacto.setCorreo(rst.getString(5));
                contacto.setTelefono(rst.getString(6));
                contacto.setMovil(rst.getString(7));
                contactos.add(contacto);
            }
            return contactos;
        } catch (SQLException e) {
            throw new GeDocDAOException(e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }
    
    @Override
    public List<Contacto> listarContactos() throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            List<Contacto> contactos = new ArrayList<Contacto>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdperson, cdcontac, dsfirst, dslast, dsmail, dstelloc, dstelmov ");
            sql.append(" FROM jpem10t;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                Contacto contacto = new Contacto();
                contacto.setPersona(rst.getString(1));
                contacto.setIdentificador(rst.getString(2));
                contacto.setNombre(rst.getString(3));
                contacto.setApellido(rst.getString(4));
                contacto.setCorreo(rst.getString(5));
                contacto.setTelefono(rst.getString(6));
                contacto.setMovil(rst.getString(7));
                contactos.add(contacto);
            }
            return contactos;
        } catch (SQLException e) {
            throw new GeDocDAOException(e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }


    @Override
    public List<Contacto> obtieneContactos(String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Contacto> listado = new LinkedList<Contacto>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "obtieneContactos", params);
            while (rst.next()) {
                Contacto c = new Contacto();
                c.setPersona(rst.getString(1));
                c.setRazonSocial(rst.getString(2));
                c.setIdentificador(rst.getString(3));
                c.setNombre(rst.getString(4));
                c.setApellido(rst.getString(5));
                c.setCorreo(rst.getString(6));
                c.setTelefono(rst.getString(7));
                c.setMovil(rst.getString(8));
                c.setGrupo(rst.getString(9));
                listado.add(c);
            }
            return listado;
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible obtener los contactos, fallo en procedimiento \"obtieneContactos\": ", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public List<Contacto> obtieneContactos(String empresa, String tipo, String sesion) throws GeDocDAOException{
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Contacto> listado = new LinkedList<Contacto>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, empresa));
            params.add(new SpParam(2, Types.VARCHAR, tipo));
            params.add(new SpParam(3, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "obtieneContactosPersona", params);
            while (rst.next()) {
                Contacto c = new Contacto();
                c.setPersona(rst.getString(1));
                c.setRazonSocial(rst.getString(2));
                c.setIdentificador(rst.getString(3));
                c.setNombre(rst.getString(4));
                c.setApellido(rst.getString(5));
                c.setCorreo(rst.getString(6));
                c.setTelefono(rst.getString(7));
                c.setMovil(rst.getString(8));
                c.setGrupo(rst.getString(9));
                listado.add(c);
            }
            return listado;
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible obtener los contactos, fallo en procedimiento \"obtieneContactosPersona\": " + empresa, e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public Contacto buscaContactoPorCorreo(String empresa, String correo, String sesion) throws GeDocDAOException{
        List<Contacto> contactos = obtieneContactos(empresa, "",sesion);
        for(Contacto contacto:contactos){
            if(contacto.getCorreo().equals(correo)){
                return contacto;
            }
        }
        return null;
    }

    @Override
    public List<Contacto> listaContactos(String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            List<Contacto> listado = new LinkedList<Contacto>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "listaContactos", params);
            while (rst.next()) {
                Contacto c = new Contacto();
                c.setPersona(rst.getString(1));
                c.setRazonSocial(rst.getString(2));
                c.setIdentificador(rst.getString(3));
                c.setNombre(rst.getString(4));
                c.setApellido(rst.getString(5));
                c.setCorreo(rst.getString(6));
                c.setTelefono(rst.getString(7));
                c.setMovil(rst.getString(8));
                c.setGrupo(rst.getString(9));
                listado.add(c);
            }
            return listado;
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible obtener los contactos, fallo en procedimiento \"listaContactos\": ", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public List<Contacto> listaContactosSuspendidos(String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            List<Contacto> listado = new LinkedList<Contacto>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "listaContactosSuspendidos", params);
            while (rst.next()) {
                Contacto c = new Contacto();
                c.setPersona(rst.getString(1));
                c.setRazonSocial(rst.getString(2));
                c.setIdentificador(rst.getString(3));
                c.setNombre(rst.getString(4));
                c.setApellido(rst.getString(5));
                c.setCorreo(rst.getString(6));
                c.setTelefono(rst.getString(7));
                c.setMovil(rst.getString(8));
                c.setGrupo(rst.getString(9));
                listado.add(c);
            }
            return listado;
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible obtener los contactos, fallo en procedimiento \"listaContactosSuspendidos\": ", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public boolean insertaContacto(Contacto contacto, String contrasenia, String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            //registraContacto(IN{persona,primero,segundo,correo,telefono,movil,grupo,valenc,sesion},OUT{referencia,error})
            params.add(new SpParam(1, Types.VARCHAR, contacto.getPersona()));
            params.add(new SpParam(2, Types.VARCHAR, contacto.getNombre()));
            params.add(new SpParam(3, Types.VARCHAR, contacto.getApellido()));
            params.add(new SpParam(4, Types.VARCHAR, contacto.getCorreo()));
            params.add(new SpParam(5, Types.VARCHAR, contacto.getTelefono()));
            params.add(new SpParam(6, Types.VARCHAR, contacto.getMovil()));
            params.add(new SpParam(7, Types.VARCHAR, contacto.getGrupo()));
            params.add(new SpParam(8, Types.VARCHAR, contrasenia));
            params.add(new SpParam(9, Types.VARCHAR, sesion));
            params.add(new SpParam(10, Types.VARCHAR, null, true));
            params.add(new SpParam(11, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraContacto", params);
            if (vuelta != null && vuelta.length == 2) {
                if (String.valueOf(vuelta[1]).length() > 0) {
                    throw new GeDocDAOException("No se ha logrado realizar el registro, el procedimiento devolvio \"registraContacto\": " + (String) vuelta[1]);
                } else {
                    contacto.setIdentificador((String)vuelta[0]);
                    return true;
                }
            } else {
                String strVuelta = vuelta!=null?String.valueOf(vuelta.length):"nulo";
                throw new GeDocDAOException("No se ha logrado realizar el registro. Se esperaban 2 parametros de salida, se tienen " + strVuelta);
            }
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible registrar el contacto, fallo en procedimiento \"registraContacto\": ", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public boolean actualizarContacto(Contacto contacto, String identificador, String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            //actualizaContacto(IN{identificador,primero,segundo,correo,telefono,movil,sesion}, OUT{error})
            params.add(new SpParam(1, Types.VARCHAR, identificador));
            params.add(new SpParam(2, Types.VARCHAR, contacto.getNombre()));
            params.add(new SpParam(3, Types.VARCHAR, contacto.getApellido()));
            params.add(new SpParam(4, Types.VARCHAR, contacto.getCorreo()));
            params.add(new SpParam(5, Types.VARCHAR, contacto.getTelefono()));
            params.add(new SpParam(6, Types.VARCHAR, contacto.getMovil()));
            params.add(new SpParam(7, Types.VARCHAR, sesion));
            params.add(new SpParam(8, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "actualizaContacto", params);
            if (vuelta != null && vuelta.length == 1) {
                if (String.valueOf(vuelta[0]).length() > 0) {
                    throw new GeDocDAOException("Imposible actualizar el contacto, el procedimiento devolvio  \"actualizaContacto\":  " + (String) vuelta[0]);
                } else {
                    contacto.setIdentificador(identificador);
                    return true;
                }
            } else {
                String strVuelta = vuelta!=null?String.valueOf(vuelta.length):"nulo";
                throw new GeDocDAOException("No se ha logrado realizar el registro. Se esperaban 2 parametros de salida, se tienen " + strVuelta);
            }
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible actualizar el contacto, fallo en procedimiento \"actualizaContacto\": " + identificador , e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public boolean eliminarContacto(String correo, String identificador, String sesion) throws GeDocDAOException{
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            //eliminaContacto(IN{identificador,primero,segundo,correo,telefono,movil,sesion}, OUT{error})
            params.add(new SpParam(1, Types.VARCHAR, identificador));
            params.add(new SpParam(2, Types.VARCHAR, correo));
            params.add(new SpParam(3, Types.VARCHAR, sesion));
            params.add(new SpParam(4, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "eliminaContacto", params);
            if (vuelta != null && vuelta.length == 1) {
                if (String.valueOf(vuelta[0]).length() > 0) {
                    throw new GeDocDAOException("Imposible eliminar el contacto, el procedimiento devolvio  \"eliminaContacto\":  " + (String) vuelta[0]);
                } else {
                    return true;
                }
            } else {
                String strVuelta = vuelta!=null?String.valueOf(vuelta.length):"nulo";
                throw new GeDocDAOException("No se ha logrado eliminar el registro. Se esperaba 1 parametro de salida, se tienen " + strVuelta);
            }
        }catch (SQLException e){
            throw new GeDocDAOException("Imposible eliminar el contacto, fallo en procedimiento \"eliminaContacto\": " + identificador + ", " + correo , e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

}
