package com.ftc.gedoc.dao;


import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CEPConcepto;
import java.util.List;

public interface CEPConceptoDAO {
    
    CEPConcepto registraCEPConcepto(CEPConcepto cepConcepto) throws GeDocDAOException;
    CEPConcepto actualizaCEPConcepto(CEPConcepto cepConcepto) throws GeDocDAOException;
    int eliminaCEPConcepto(String claveProductoServicio) throws GeDocDAOException;
    CEPConcepto obtieneCEPConcepto(String claveProductoServicio) throws GeDocDAOException;
    List<CEPConcepto> listaCEPConcepto() throws GeDocDAOException;
    
}
