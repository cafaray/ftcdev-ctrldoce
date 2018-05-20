package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CEPCabecera;
import java.util.Date;
import java.util.List;

public interface CEPCabeceraDAO {
    
    CEPCabecera registraCEP(CEPCabecera cep) throws GeDocDAOException;
    CEPCabecera actualizaCEP(CEPCabecera cep) throws GeDocDAOException;
    CEPCabecera obtieneCEP(String uuid) throws GeDocDAOException;
    void eliminaCEP(String uuid) throws GeDocDAOException;
    List<CEPCabecera> listaCEP(String proveedor) throws GeDocDAOException;
    boolean existeUUID(String uuid) throws GeDocDAOException;
    List<CEPCabecera> listaCEP(String proveedor, Date fechaInicial, Date fechaFinal) throws GeDocDAOException;
    
}
