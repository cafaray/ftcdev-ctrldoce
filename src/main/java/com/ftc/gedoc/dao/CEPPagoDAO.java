package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CEPPago;
import java.util.List;

public interface CEPPagoDAO {
    
    CEPPago registraPago(CEPPago pago) throws GeDocDAOException;
    CEPPago actualizaPago(CEPPago pago) throws GeDocDAOException;
    CEPPago obtienePago(String documento) throws GeDocDAOException;
    int eliminaPago(String documento) throws GeDocDAOException;
    List<CEPPago> listaPagos() throws GeDocDAOException;
    String obtieneAsociado(String uuid) throws GeDocDAOException;
}
