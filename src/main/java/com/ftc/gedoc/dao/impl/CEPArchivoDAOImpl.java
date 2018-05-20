package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.gedoc.dao.CEPArchivoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.gedoc.utiles.UtilDAO;
import com.ftc.modelo.CEPArchivo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CEPArchivoDAOImpl implements CEPArchivoDAO {

    private final static String SELECT = "SELECT idceparc, cdperson, dstitulo, dsobserv, dsrefarc, dsstatus FROM cep_cabecera ";
    
    @Override
    public CEPArchivo registraCEP(CEPArchivo archivo) throws GeDocDAOException {
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            //registra el CEPArchivo
            StringBuilder sql = new StringBuilder("INSERT INTO cep_cabecera VALUES (");
            sql.append(UtilDAO.coverSimpleTildes(archivo.getIdentificador())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(archivo.getPersona())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(archivo.getTitulo())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(archivo.getObservaciones())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(archivo.getArchivos())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes("P")).append(", ");
            sql.append("getUser()").append(", ");
            sql.append(UtilDAO.coverSimpleTildes(CEPArchivoDAOImpl.class.getCanonicalName())).append(", ");
            sql.append("CURRENT_TIMESTAMP");
            sql.append(");");
            System.out.println("SQL FOR HEADER: "+sql.toString());
            PreparedStatement statement = conexion.prepareStatement(sql.toString());
            int i = statement.executeUpdate();
            return archivo;
        }catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            if (conexion!=null){
                try{
                conexion.close();
                } catch(SQLException e){
                }
            }
        }
    }

    @Override
    public CEPArchivo actualizaCEP(CEPArchivo archivo) throws GeDocDAOException {                
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            //CEPArchivo almacenado = obtieneCEP(archivo.getIdentificador());
            //if (almacenado!=null){
                StringBuilder sql = new StringBuilder("UPDATE cep_cabecera SET ");
                sql.append("dstitulo = ").append(UtilDAO.coverSimpleTildes(archivo.getTitulo())).append(", ");
                sql.append("dsobserv = ").append(UtilDAO.coverSimpleTildes(archivo.getObservaciones())).append(", ");
                sql.append("dsrefarc = ").append(UtilDAO.coverSimpleTildes(archivo.getArchivos())).append(", ");
                sql.append("dsstatus = ").append(UtilDAO.coverSimpleTildes(archivo.getEstatus())).append(", ");
                sql.append("programa = ").append(UtilDAO.coverSimpleTildes("UPD:".concat(String.valueOf(Calendar.getInstance().getTimeInMillis()))));
                sql.append(" WHERE idceparc = ").append(UtilDAO.coverSimpleTildes(archivo.getIdentificador()));
                sql.append(" AND cdperson = ").append(UtilDAO.coverSimpleTildes(archivo.getPersona()));
                sql.append(";");
                PreparedStatement stm = conexion.prepareStatement(sql.toString());
                int res = stm.executeUpdate();
                return res>0?archivo:null;
            //} else {
            //    throw new GeDocDAOException("Imposible actualizar un registro que no existe en la base de datos, verifique el identificador. " + archivo.getIdentificador());
            //}
        } catch(SQLException e){
            throw new GeDocDAOException("Imposible actualizar la referencia del archivo CEP", e);            
        } finally {
            if (conexion!=null){
                try{
                    conexion.close();
                } catch(SQLException e){}
            }
        }
    }

    @Override
    public boolean eliminaCEP(String identificador) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("DELETE FROM cep_cabecera WHERE ");
        sql.append("idceparc = ").append(UtilDAO.coverSimpleTildes(identificador));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            return stm.execute();
        } catch (SQLException e){
            throw  new GeDocDAOException(e.getMessage(), e);
        } finally {
            if (conexion!=null){
                try{
                conexion.close();
                } catch(SQLException e){
                }
            }
        }
    }

    /***
     * Lista los registros de archivos CEP asociados al proveedor.
     * @param proveedor Codigo de la persona proveedor
     * @return Lista de archivos CEP registrados
     * @throws GeDocDAOException 
     */    
    @Override
    public List<CEPArchivo> listar(String proveedor) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("SELECT A.idceparc, cdperson, dstitulo, dsobserv, dsrefarc, dsstatus, B.fecha, B.uuid FROM cep_cabecera A");
        sql.append(" INNER JOIN cep B ");        
        sql.append(" ON B.idceparc = A.idceparc ");
        if (!proveedor.equals("*")){
            sql.append(" WHERE cdperson = ").append(UtilDAO.coverSimpleTildes(proveedor));
        }
        sql.append(";");
        System.out.println("-----> GET CEP ".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPArchivo> lista = new ArrayList<CEPArchivo>();            
            while(rst.next()){
                CEPArchivo cep = mapCEP(rst);
                lista.add(cep);
            }
            return lista;
        } catch (SQLException e){
            throw  new GeDocDAOException(e.getMessage(), e);
        } finally {
            if (conexion!=null){
                try{
                conexion.close();
                } catch(SQLException e){
                }
            }
        }
    }
    
    private CEPArchivo mapCEP(ResultSet rst) throws SQLException {
        CEPArchivo archivo = new CEPArchivo();
        archivo.setIdentificador(rst.getString(1));
        archivo.setPersona(rst.getString(2));
        archivo.setTitulo(rst.getString(3));
        archivo.setObservaciones(rst.getString(4));
        archivo.setArchivos(rst.getString(5));
        archivo.setEstatus(rst.getString(6));
        if (rst.getTimestamp(7)!=null){
            archivo.setFecha(rst.getTimestamp(7));
        }
        if (rst.getString(8)!=null){
            archivo.setUuid(rst.getString(8));
        }
        return archivo;
    }

    @Override
    public CEPArchivo obtieneCEP(String identificador) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("SELECT A.idceparc, cdperson, dstitulo, dsobserv, dsrefarc, dsstatus, B.fecha, B.uuid FROM cep_cabecera A");
        sql.append(" INNER JOIN cep B ");  
        sql.append(" ON B.idceparc = A.idceparc ");
        sql.append(" WHERE A.idceparc = ").append(UtilDAO.coverSimpleTildes(identificador)).append(";");        
        System.out.println("-----> GET CEP ".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return mapCEP(rst);
            } else {
                return null;
            }
            
        } catch (SQLException e){
            throw  new GeDocDAOException(e.getMessage(), e);
        } finally {
            if (conexion!=null){
                try{
                conexion.close();
                } catch(SQLException e){
                }
            }
        }
    }

    @Override
    public List<CEPArchivo> listar(String proveedor, String fechaInicial, String fechaFinal) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("SELECT A.idceparc, cdperson, dstitulo, dsobserv, dsrefarc, dsstatus, B.fecha, B.uuid FROM cep_cabecera A");
        sql.append(" INNER JOIN cep B ");
        sql.append(" ON B.idceparc = A.idceparc ");
        StringBuilder where = new StringBuilder();
        if (!proveedor.equals("*")){
            where = where.append(" WHERE A.cdperson = ").append(UtilDAO.coverSimpleTildes(proveedor));
        }
        if (fechaInicial!=null && fechaInicial.length()>0){
            if (where.length()>0){
                where.append(" AND ");
            } else {
                where.append(" WHERE ");
            }
            where.append(" B.fecha BETWEEN (").append(UtilDAO.coverSimpleTildes(fechaInicial));
            where.append(" AND ");
            where.append(UtilDAO.coverSimpleTildes(fechaFinal)).append(")");
            
        }
        sql.append(where);
        sql.append(";");        
        System.out.println("-----> GET CEP BETWEEN DATES".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPArchivo> listado = new ArrayList<CEPArchivo>();
            while (rst.next()){
                CEPArchivo cep = mapCEP(rst);
                listado.add(cep);
            } 
            return listado;
        } catch (SQLException e){
            throw  new GeDocDAOException(e.getMessage(), e);
        } finally {
            if (conexion!=null){
                try{
                conexion.close();
                } catch(SQLException e){
                }
            }
        }
    }
    
}
