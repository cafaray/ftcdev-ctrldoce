package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.gedoc.dao.CEPCabeceraDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.gedoc.utiles.UtilDAO;
import com.ftc.modelo.CEPCabecera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CEPCabeceraDAOImpl implements CEPCabeceraDAO {

    private static final String SELECT_CEP = "SELECT uuid,version,serie,folio,fecha,subTotal,moneda ,total,tipoDeComprobante,lugarExpedicion,xmlnsPago10,rfcEmisor,\n" +
"    nombreEmisor,regimenFiscalEmisor,rfcReceptor,nombreReceptor,usoCFDIReceptor,rfcProvCertif,versionTibreFiscal,\n" +
"    fechaTimbrado,noCertificadoSAT,versionPagos,idceparc \n" +
"    FROM cep \n";
    
    public CEPCabeceraDAOImpl() {
    }

    @Override
    public CEPCabecera registraCEP(CEPCabecera cep)  throws GeDocDAOException {
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            //registra la cabecera del registro
            StringBuilder sql = new StringBuilder("INSERT INTO cep VALUES (");
            sql.append(UtilDAO.coverSimpleTildes(cep.getUuid())).append(", ");
            sql.append(cep.getVersion()).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getSerie())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getFolio())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(Comunes.formatoFecha(cep.getFecha(),-3))).append(", ");
            sql.append(cep.getSubTotal()).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getMoneda())).append(", ");
            sql.append(cep.getTotal()).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getTipoDeComprobante())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getLugarExpedicion())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getXmlnsPago10())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getRfcEmisor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getNombreEmisor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getRegimenFiscalEmisor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getRfcReceptor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getNombreReceptor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getUsoCFDIReceptor())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getRfcProvCertif())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getVersionTimbreFiscal())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(Comunes.formatoFecha(cep.getFechaTimbrado(),-3))).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getNoCertificadoSAT())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getVersionPagos())).append(", ");
            sql.append(UtilDAO.coverSimpleTildes(cep.getIdentificador()));
            sql.append(");");
            System.out.println("SQL for Insert CEP:" + sql.toString());
            PreparedStatement statement = conexion.prepareStatement(sql.toString());
            statement.executeUpdate();
            return cep;
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
    public CEPCabecera actualizaCEP(CEPCabecera cep)  throws GeDocDAOException{
        eliminaCEP(cep.getUuid());
        registraCEP(cep);
        return cep;
    }

    @Override
    public CEPCabecera obtieneCEP(String uuid)  throws GeDocDAOException{
        StringBuilder sql = new StringBuilder(SELECT_CEP);
        sql.append(" WHERE uuid = ").append(UtilDAO.coverSimpleTildes(uuid)).append(";");        
        System.out.println("-----> GET CEP ".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return mapCep(rst);
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
    public void eliminaCEP(String uuid)  throws GeDocDAOException{
        StringBuilder sql = new StringBuilder("DELETE FROM cep WHERE ");
        sql.append("uuid = ").append(UtilDAO.coverSimpleTildes(uuid));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.execute();
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
     * 
     * @param proveedor RFC del emisor del documento que representa al proveedor, y que tiene que ser el asociado a la persona
     * en caso de que se haya logado un usuario del proveedor
     * @return Listado de contenidos del documento CEP.XML
     * @throws GeDocDAOException 
     */
    @Override
    public List<CEPCabecera> listaCEP(String proveedor)  throws GeDocDAOException{
        StringBuilder sql = new StringBuilder(SELECT_CEP);
        sql.append(" WHERE rfcEmisor= ").append(UtilDAO.coverSimpleTildes(proveedor)).append(";");
        System.out.println("-----> GET CEP ".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPCabecera> lista = new ArrayList<CEPCabecera>();            
            while(rst.next()){
                CEPCabecera cep = mapCep(rst);
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
 
    /***
     * 
     * @param proveedor RFC del emisor del documento que representa al proveedor, y que tiene que ser el asociado a la persona
     * @param fechaInicial fecha de inicio en el rango de busqueda
     * @param fechaFinal fecha límite para el rango de busqueda
     * en caso de que se haya logado un usuario del proveedor
     * @return Listado de contenidos del documento CEP.XML
     * @throws GeDocDAOException 
     */
    @Override
    public List<CEPCabecera> listaCEP(String proveedor, Date fechaInicial, Date fechaFinal) throws GeDocDAOException{
        StringBuilder sql = new StringBuilder(SELECT_CEP);
        sql.append(" WHERE rfcEmisor= ").append(UtilDAO.coverSimpleTildes(proveedor));
        sql.append(" AND fecha >= ").append(UtilDAO.coverSimpleTildes(Comunes.date2String(fechaInicial, -3)));
        sql.append(" AND fecha <= ").append(UtilDAO.coverSimpleTildes(Comunes.date2String(fechaFinal, -3))).append(";");
        System.out.println("-----> GET CEP ".concat(sql.toString()));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPCabecera> lista = new ArrayList<CEPCabecera>();            
            while(rst.next()){
                CEPCabecera cep = mapCep(rst);
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
    
    private CEPCabecera mapCep(ResultSet rst) throws SQLException{
        CEPCabecera cep = new CEPCabecera();
        cep.setUuid(rst.getString(1));
        cep.setVersion(rst.getString(2));
        cep.setSerie(rst.getString(3));
        cep.setFolio(rst.getString(4));
        cep.setFecha(rst.getDate(5));
        cep.setSubTotal(rst.getDouble(6));
        cep.setMoneda(rst.getString(7));
        cep.setTotal(rst.getDouble(8));
        cep.setTipoDeComprobante(rst.getString(9));
        cep.setLugarExpedicion(rst.getString(10));
        cep.setXmlnsPago10(rst.getString(11));
        cep.setRfcEmisor(rst.getString(12));
        cep.setNombreEmisor(rst.getString(13));
        cep.setRegimenFiscalEmisor(rst.getString(14));
        cep.setRfcReceptor(rst.getString(15));
        cep.setNombreReceptor(rst.getString(16));
        cep.setUsoCFDIReceptor(rst.getString(17));
        cep.setRfcProvCertif(rst.getString(18));
        cep.setVersionTimbreFiscal(rst.getString(19));
        cep.setFechaTimbrado(rst.getDate(20));
        cep.setNoCertificadoSAT(rst.getString(21));
        cep.setVersionPagos(rst.getString(22));
        cep.setIdentificador(rst.getString(23));
        return cep;
    }

    @Override
    public boolean existeUUID(String uuid) throws GeDocDAOException {
        String sql = "SELECT COUNT(uuid) FROM cep WHERE uuid = ";
        sql = sql.concat(UtilDAO.coverSimpleTildes(uuid));
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                int count = rst.getInt(1);
                return count>0;
            } else {
                return false;
            }
        }catch(SQLException e){
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try{
                if (conexion!=null){
                    conexion.close();
                }
            }catch(SQLException e){}
        }
    }

}
