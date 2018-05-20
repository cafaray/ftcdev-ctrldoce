package com.ftc.gedoc.dao;

import com.ftc.modelo.FacturaCabecera;
import java.sql.SQLException;

public interface FacturaCabeceraDAO {
    String insertar(FacturaCabecera facturaCabecera, String sesion) throws SQLException;
    int eliminar(FacturaCabecera facturaCabecera) throws SQLException;
    FacturaCabecera findById(String cdfile) throws SQLException;
    FacturaCabecera removeById(String cdfile) throws SQLException;
    int actualizar(FacturaCabecera facturaCabecera) throws SQLException;
}
