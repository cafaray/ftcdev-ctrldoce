package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.gedoc.dao.CEPPagoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.gedoc.utiles.UtilDAO;
import com.ftc.modelo.CEPPago;
import com.ftc.modelo.CEPPagoDocumento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CEPPagoDAOImpl implements CEPPagoDAO{

    private final String uuid;
    private static final String SELECT = "SELECT A.uuid, A.documentoRelacionado, fechaPago, formaDePago, A.moneda, monto, rfcEmisorCtaOrd, ctaOrdenante, rfcEmisorCtaBen, ctaBeneficiario,"
            .concat(" B.partida, B.folio, B.serie, B.moneda AS monedaPago, B.metodoDePago, B.numParcialidad, B.saldoAnt, B.pagado, B.saldoInsoluto")
            .concat(" FROM cep_pago A INNER JOIN cep_documento B ")
            .concat("   ON A.uuid = B.uuid ")
            .concat("  AND A.documentoRelacionado = B.documentoRelacionado ");
    
    public CEPPagoDAOImpl(String uuid) {
        this.uuid = uuid;
    }
    public String getUuid(){
        return this.uuid;
    }

    @Override
    public CEPPago registraPago(CEPPago pago) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder("INSERT INTO cep_pago VALUES (");
        sql.append(UtilDAO.coverSimpleTildes(uuid)).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getIdDocumento())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(Comunes.formatoFecha(pago.getFechaPago(), -3))).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getFormaDePago())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getMoneda())).append(", ");
        sql.append(pago.getMonto()).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getRfcEmisorCtaOrd())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getCtaOrdenante())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getRfcEmisorCtaBen())).append(", ");
        sql.append(UtilDAO.coverSimpleTildes(pago.getCtaBeneficiario()));
        sql.append(");");
        
        StringBuilder sqlDoc = new StringBuilder("INSERT INTO cep_documento VALUES (");
        sqlDoc.append(UtilDAO.coverSimpleTildes(uuid)).append(", ");
        sqlDoc.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getIdDocumento())).append(", ");
        sqlDoc.append(pago.getDocumentoRelacionado().getPartida()).append(", ");
        sqlDoc.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getFolio())).append(", ");
        sqlDoc.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getSerie())).append(", ");
        sqlDoc.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getMonedaDR())).append(", ");
        sqlDoc.append(UtilDAO.coverSimpleTildes(pago.getDocumentoRelacionado().getMetodoDePagoDR())).append(", ");       
        sqlDoc.append(pago.getDocumentoRelacionado().getNumParcialidad()).append(", ");
        sqlDoc.append(pago.getDocumentoRelacionado().getSaldoAnt()).append(", ");
        sqlDoc.append(pago.getDocumentoRelacionado().getPagado()).append(", ");
        sqlDoc.append(pago.getDocumentoRelacionado().getSaldoInsoluto());        
        sqlDoc.append(");");
        
        Connection conexion = null;
        try {
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            int res = 0;
            System.out.println("SQL for Insert cep_pago:" + sql.toString());
            res = stm.executeUpdate();
            System.out.println("SQL for Insert cep_documento:" + sqlDoc.toString());
            res += stm.executeUpdate(sqlDoc.toString());
            return pago;
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
    public CEPPago actualizaPago(CEPPago pago) throws GeDocDAOException {
        this.eliminaPago(pago.getDocumentoRelacionado().getIdDocumento());
        return this.registraPago(pago);
    }
    
    @Override
    public CEPPago obtienePago(String documento) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder(SELECT);
        sql.append(" WHERE A.uuid = ").append(UtilDAO.coverSimpleTildes(uuid));
        sql.append(" AND  A.documentoRelacionado = ").append(UtilDAO.coverSimpleTildes(documento)).append(";");
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return mapPago(rst);
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
    public int eliminaPago(String documento) throws GeDocDAOException {
        String sqlPago = "DELETE FROM cep_pago WHERE ";
        sqlPago = sqlPago.concat("uuid = ").concat(UtilDAO.coverSimpleTildes(this.uuid));
        sqlPago = sqlPago.concat(" AND documentoRelacionado = ").concat(UtilDAO.coverSimpleTildes(documento));
        String sqlDoc = "DELETE FROM cep_documento WHERE ";
        sqlDoc = sqlDoc.concat("uuid = ").concat(UtilDAO.coverSimpleTildes(this.uuid));
        sqlDoc = sqlDoc.concat(" AND documentoRelacionado = ").concat(UtilDAO.coverSimpleTildes(documento));

        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sqlPago);
            int res = stm.executeUpdate();
            stm = conexion.prepareStatement(sqlDoc);
            res += stm.executeUpdate();
            return res;
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
    public List<CEPPago> listaPagos() throws GeDocDAOException {
        StringBuilder sql = new StringBuilder(SELECT);
        sql.append(" WHERE A.uuid = ").append(UtilDAO.coverSimpleTildes(uuid)).append(";");
        Connection conexion = null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            List<CEPPago> lista = new ArrayList<CEPPago>();
            while (rst.next()){
                CEPPago pago = new CEPPago();
                pago = mapPago(rst);
                lista.add(pago);
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
    
    private CEPPago mapPago(ResultSet rst) throws SQLException {
        CEPPago pago = new CEPPago();
        pago.setFechaPago(rst.getDate(3));
        pago.setFormaDePago(rst.getString(4));
        pago.setMoneda(rst.getString(5));
        pago.setMonto(rst.getDouble(6));
        pago.setRfcEmisorCtaOrd(rst.getString(7));
        pago.setCtaOrdenante(rst.getString(8));
        pago.setRfcEmisorCtaBen(rst.getString(9));
        pago.setCtaBeneficiario(rst.getString(10));
        CEPPagoDocumento documento = new CEPPagoDocumento();
        documento.setIdDocumento(rst.getString(2));
        documento.setPartida(rst.getInt(11));
        documento.setFolio(rst.getString(12));
        documento.setSerie(rst.getString(13));
        documento.setMonedaDR(rst.getString(14));
        documento.setMetodoDePagoDR(rst.getString(15));
        documento.setNumParcialidad(rst.getInt(16));
        documento.setSaldoAnt(rst.getDouble(17));
        documento.setPagado(rst.getDouble(18));
        documento.setSaldoInsoluto(rst.getDouble(19));
        pago.setDocumentoRelacionado(documento);
        return pago;
    }

    @Override
    public String obtieneAsociado(String uuid) throws GeDocDAOException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dsuuid, cddocele FROM jdem20t WHERE dsuuid = ").append(UtilDAO.coverSimpleTildes(uuid)).append(";");
        String docele = "";
        Connection conexion=null;
        try{
            conexion = Conexion.getConexion();
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst!=null && rst.next()){
                docele = rst.getString(2);
            }
            return docele;
        }catch(SQLException e){
            throw new GeDocDAOException("Fallo la consulta de datos al obtener el documento asociado " + uuid, e);
        }finally{
            if (conexion!=null){
                try{
                    conexion.close();
                } catch(SQLException ex){}
            }
        }                        
    }
}
