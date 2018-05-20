package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.DocumentoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Documento;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DocumentoDAOImpl implements DocumentoDAO {


    @Override
    public Documento recuperaDocumento(String identificador) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT A.cdperson, nombreEmpresa(A.cdperson) empresa, cddocele, dsfiles, dstitle,dsobserv, instatus, DATE(A.tmstmp) "
                    + " FROM jdem10t A INNER JOIN jpem00t B ON A.cdperson = B.cdperson "
                    + " WHERE cddocele = ?;";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, identificador);
            ResultSet rst = statement.executeQuery();
            if (rst != null) {
                if (rst.first()) {
                    Documento documento = new Documento();
                    documento.setPersona(rst.getString(1));
                    documento.setEmpresa(rst.getString(2));
                    documento.setIdentificador(rst.getString(3));
                    documento.setArchivos(rst.getString(4));
                    documento.setTitulo(rst.getString(5));
                    documento.setObservaciones(rst.getString(6));
                    documento.setFecha(rst.getDate(8));
                    return documento;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new GeDocDAOException("Imposible recuperar el documento con identificador, fallo en \"SELECT-recuperaDocumentos\": " + identificador, e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public List<Documento> recuperaDocumentos(String elementos) throws GeDocDAOException {
        Connection conexion = null;
        try {
            String[] cadenaIdentificador = elementos.split(",");
            StringBuilder identificadores = new StringBuilder();
            for (String id:cadenaIdentificador){
                identificadores.append("'").append(id).append("',");
            }
            conexion = Conexion.getConexion();
            String sql = "SELECT A.cdperson, nombreEmpresa(A.cdperson) empresa, cddocele, dsfiles, dstitle,dsobserv, instatus, DATE(A.tmstmp) "
                    + " FROM jdem10t A INNER JOIN jpem00t B ON A.cdperson = B.cdperson "
                    + " WHERE cddocele IN (" + identificadores.substring(0, identificadores.length()-1) + ");";
            PreparedStatement statement = conexion.prepareStatement(sql);
            //statement.setString(1, identificador);
            List<Documento> documentos = new ArrayList<Documento>();
            ResultSet rst = statement.executeQuery();
                while (rst.next()) {
                    Documento documento = new Documento();
                    documento.setPersona(rst.getString(1));
                    documento.setEmpresa(rst.getString(2));
                    documento.setIdentificador(rst.getString(3));
                    documento.setArchivos(rst.getString(4));
                    documento.setTitulo(rst.getString(5));
                    documento.setObservaciones(rst.getString(6));
                    documento.setFecha(rst.getDate(8));
                    documentos.add(documento);
                }
                return documentos;
        } catch (SQLException e) {
            throw new GeDocDAOException("Imposible listar los documentos, fallo en \"SELECT-recuperaDocumentos\": " + elementos , e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public int eliminaDocumento(Documento documento) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            String sql = "DELETE FROM jdem10t WHERE cddocele = ?;";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, documento.getIdentificador());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeDocDAOException("Imposible elimiar el documento, fallo en \"SELECT-eliminaDocumento\": " + documento.getIdentificador(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public Documento findById(String id) throws GeDocDAOException {
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement("SELECT A.cdperson, A.cddocele, A.dsfiles, A.dstitle, A.dsobserv, A.instatus, A.tmstmp, "
                    + " (SELECT dsrazsoc FROM jpem00t WHERE cdperson = A.cdperson)"
                    + " FROM jdem10t A WHERE A.cddocele = ?;");
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            Documento documento = new Documento();
            if (rst.next()) {
                documento.setArchivos(rst.getString(3));
                documento.setEmpresa(rst.getString(8));
                documento.setFecha(rst.getTimestamp(7));
                documento.setIdentificador(rst.getString(2));
                documento.setObservaciones(rst.getString(5));
                documento.setPersona(rst.getString(1));
                documento.setTitulo(rst.getString(4));
            }
            return documento;
        } catch (SQLException e) {
            throw new GeDocDAOException("Imposible localizar el documento, fallo en el \"SELECT-findById\": " + id, e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public List<Documento> listadoDocumentos(String empresa, String tipo, String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            List<Documento> listado = new LinkedList<Documento>();
            //listaDocumentos(IN persona CHAR(16), IN sesion VARCHAR(32))
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, empresa));
            params.add(new SpParam(2, Types.VARCHAR, tipo));
            params.add(new SpParam(3, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "listaDocumentos", params);
            while (rst.next()) {
                Documento documento = new Documento(rst.getString(5), rst.getString(6), rst.getString(4), rst.getString(1));
                documento.setEmpresa(rst.getString(2));
                documento.setFecha(new Date(rst.getDate(8).getTime()));
                documento.setEstatus(rst.getString(7));
                listado.add(documento);
            }
            return listado;
        } catch (NullPointerException e){
            throw new GeDocDAOException("Hay un valor nulo en el documento localizado con el procedimiento \"listaDocumentos\": " + empresa, e);
        } catch (SQLException e){
            throw new GeDocDAOException("Imposible localizar el documento, fallo en el procedimiento \"listaDocumentos\": " + empresa, e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }

    @Override
    public List<Documento> listadoDocumentos(String empresa, String tipo, String fechaInicial, String fechaFinal,
                                                    String sesion) throws GeDocDAOException {
        Connection conexion = null;
        try {
            List<Documento> listado = new LinkedList<Documento>();
            java.sql.Date fechai = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            java.sql.Date fechaf = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            try {
                if (fechaInicial.trim().length() == 0) {
                    fechai = Comunes.DMAtoFechaSQL("01/01/2000");
                } else {
                    fechai = Comunes.DMAtoFechaSQL(fechaInicial);
                    if (fechaFinal.trim().length() == 0) {
                        fechaFinal = fechaInicial;
                    }
                    fechaf = Comunes.DMAtoFechaSQL(fechaFinal);
                }
            } catch (Exception ex) {
                throw new GeDocDAOException("Imposible listar los documentos, no se han logrado deducir las fechas de consulta en procedimiento \"listadoDocumentos\": "
                        + String.format("{empresa:%s, fecha_inicial:%s, fechaFinal:%s}", empresa, fechaInicial, fechaFinal), ex);
            }
            //listaDocumentosFiltro(IN{persona,tipo,fechai,fechaf,sesion})
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, empresa));
            params.add(new SpParam(2, Types.VARCHAR, tipo));
            params.add(new SpParam(3, Types.DATE, fechai));
            params.add(new SpParam(4, Types.DATE, fechaf));
            params.add(new SpParam(5, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "listaDocumentosFiltro", params);
            while (rst.next()) {
                Documento documento = new Documento(rst.getString(5), rst.getString(6), rst.getString(4), rst.getString(1));
                documento.setEmpresa(rst.getString(2));
                documento.setFecha(new Date(rst.getDate(8).getTime()));
                documento.setIdentificador(rst.getString(3));
                documento.setEstatus(rst.getString(7));
                listado.add(documento);
            }
            return listado;
        } catch (NullPointerException e){
            throw new GeDocDAOException("Hay un valor nulo en el documento iterado con el procedimiento \"listaDocumentosFiltro\": "
                    + String.format("{empresa:%s, fecha_inicial:%s, fechaFinal:%s}", empresa, fechaInicial, fechaFinal), e);
        } catch (SQLException e){
            throw new GeDocDAOException("Imposible listar los documentos, fallo en el procedimiento \"listaDocumentosFiltro\": "
                    + String.format("{empresa:%s, fecha_inicial:%s, fechaFinal:%s}", empresa, fechaInicial, fechaFinal), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            }catch (SQLException e){}
        }
    }
}
