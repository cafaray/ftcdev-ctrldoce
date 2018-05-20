package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.modelo.FacturaCabecera;

import com.ftc.gedoc.dao.FacturaCabeceraDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class FacturaCabeceraDAOImpl implements FacturaCabeceraDAO{

    Connection conexion;
    
    public FacturaCabeceraDAOImpl(){}

    @Override
    public String insertar(FacturaCabecera facturaCabecera, String sesion) throws SQLException {
        try{
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, facturaCabecera.getPersona()));
            params.add(new SpParam(2, Types.VARCHAR, facturaCabecera.getCdDocumento()));
            params.add(new SpParam(3, Types.VARCHAR, facturaCabecera.getLocacion()));
            params.add(new SpParam(4, Types.VARCHAR, facturaCabecera.getTipo()));
            params.add(new SpParam(5, Types.VARCHAR, facturaCabecera.getSerie()));
            params.add(new SpParam(6, Types.VARCHAR, facturaCabecera.getFolio()));
            params.add(new SpParam(7, Types.VARCHAR, facturaCabecera.getStrFecha()));
            params.add(new SpParam(8, Types.VARCHAR, facturaCabecera.getFormaDePago()));
            params.add(new SpParam(9, Types.DOUBLE, facturaCabecera.getSubTotal()));
            params.add(new SpParam(10, Types.DOUBLE, facturaCabecera.getDescuento()));
            params.add(new SpParam(11, Types.DOUBLE, Double.parseDouble(facturaCabecera.getTipoCambio())));
            params.add(new SpParam(12, Types.DOUBLE, facturaCabecera.getTotal()));
            params.add(new SpParam(13, Types.VARCHAR, facturaCabecera.getMoneda()));
            params.add(new SpParam(14, Types.VARCHAR, facturaCabecera.getMetodoDePago()));
            params.add(new SpParam(15, Types.VARCHAR, facturaCabecera.getLugarExpedicion()));
            params.add(new SpParam(16, Types.VARCHAR, facturaCabecera.getRfc()));
            params.add(new SpParam(17, Types.VARCHAR, facturaCabecera.getNombre()));
            params.add(new SpParam(18, Types.VARCHAR, facturaCabecera.getRfcReceptor()));
            params.add(new SpParam(19, Types.VARCHAR, facturaCabecera.getNombreReceptor()));
            params.add(new SpParam(20, Types.DOUBLE, facturaCabecera.getTotalImpuestosTrasladados()));
            params.add(new SpParam(21, Types.VARCHAR, facturaCabecera.getUuid()));
            params.add(new SpParam(22, Types.VARCHAR, facturaCabecera.getStrFechaTimbrado()));
            params.add(new SpParam(23, Types.VARCHAR, sesion));
            params.add(new SpParam(24, Types.VARCHAR, null, true)); //referencia
            params.add(new SpParam(25, Types.VARCHAR, null, true)); //error
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraCabeceraFactura", params);
            if (vuelta != null && vuelta.length == 2) {
                if (String.valueOf(vuelta[1]).length() > 0) {
                    return String.valueOf(vuelta[1]);
                } else {
                    return String.valueOf(vuelta[0]);
                }
            }else{
                throw new SQLException("No se logro ejecutar el procedimiento almacenado.", "20000", -5001);
            }
            
        }catch(SQLException e){
            e.printStackTrace(System.out);
            throw e;
        }
    }

    @Override
    public int eliminar(FacturaCabecera facturaCabecera) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FacturaCabecera findById(String cdfile) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FacturaCabecera removeById(String cdfile) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int actualizar(FacturaCabecera facturaCabecera) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
   
    
}
