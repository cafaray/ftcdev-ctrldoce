package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.gedoc.dao.CEPConceptoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.gedoc.utiles.UtilDAO;
import com.ftc.modelo.CEPConcepto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CEPConceptoDAOImpl implements CEPConceptoDAO {
    
    private final String SELECT = "SELECT uuid,claveProdServ,cantidad,claveUnidad,descripcion,valorUnitario,importe\n" +
                                "  FROM cep_concepto ";

    private String uuid;
    
    public CEPConceptoDAOImpl(String uuid){
        this.uuid = uuid;
    }
    
    public String getUuid(){
        return this.uuid;
    }
    
    @Override
    public CEPConcepto registraCEPConcepto(CEPConcepto cepConcepto) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("INSERT INTO cep_concepto VALUES (");
        sql.append(UtilDAO.coverSimpleTildes(uuid)).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(cepConcepto.getClaveProdServ())).append(", ");
        sql.append(cepConcepto.getCantidad()).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(cepConcepto.getClaveUnidad())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(cepConcepto.getDescripcion())).append(", ");
        sql.append(cepConcepto.getValorUnitario()).append(", ");
        sql.append(cepConcepto.getImporte());
        sql.append(");");
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            System.out.println("SQL for Insert cep_concepto:" + sql.toString());
            stm.executeUpdate();
            return cepConcepto;
        }catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally{
            try{
                if (conexion!=null){
                    conexion.close();
                }
            } catch(SQLException e){}
        }
    }

    @Override
    public CEPConcepto actualizaCEPConcepto(CEPConcepto cepConcepto) throws GeDocDAOException {
        this.eliminaCEPConcepto(cepConcepto.getClaveProdServ());
        return this.registraCEPConcepto(cepConcepto);
    }

    @Override
    public int eliminaCEPConcepto(String claveProductoServicio) throws GeDocDAOException {
        String sql = "DELETE FROM cep_concepto WHERE ";
        sql = sql.concat("uuid = ").concat(UtilDAO.coverSimpleTildes(this.uuid));
        sql = sql.concat(" AND claveProdServ = ").concat(UtilDAO.coverSimpleTildes(claveProductoServicio));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql);
            return stm.executeUpdate();
        } catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try{
                if (conexion!=null){
                    conexion.close();
                }
            } catch(SQLException e){}
        }
    }

    @Override
    public CEPConcepto obtieneCEPConcepto(String claveProductoServicio) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder(SELECT);
        sql.append(" WHERE uuid = ").append(UtilDAO.coverSimpleTildes(uuid));
        sql.append(" AND  claveProdServ = ").append(UtilDAO.coverSimpleTildes(claveProductoServicio)).append(";");
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return mapCepConcepto(rst);
            } else {
                return null;
            }
        }catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try{
                if (conexion!=null){
                    conexion.close();
                }
            } catch(SQLException e){}
        }
    }

    @Override
    public List<CEPConcepto> listaCEPConcepto() throws GeDocDAOException {
         StringBuilder sql = new StringBuilder(SELECT);
        sql.append(" WHERE uuid = ").append(UtilDAO.coverSimpleTildes(uuid)).append(";");        
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPConcepto> lista = new ArrayList<CEPConcepto>();
            while (rst.next()){
                CEPConcepto cep = new CEPConcepto();
                cep = mapCepConcepto(rst);
                lista.add(cep);
            } 
            return lista;
        }catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try{
                if (conexion!=null){
                    conexion.close();
                }
            } catch(SQLException e){}
        }  
    }
    
    private CEPConcepto mapCepConcepto(ResultSet rst) throws SQLException {
        CEPConcepto concepto = new CEPConcepto();
        concepto.setClaveProdServ(rst.getString(2));
        concepto.setCantidad(rst.getInt(3));
        concepto.setClaveUnidad(rst.getString(4));
        concepto.setDescripcion(rst.getString(5));
        concepto.setValorUnitario(rst.getDouble(6));
        concepto.setImporte(rst.getDouble(7));
        return concepto;                
    }
    
}
