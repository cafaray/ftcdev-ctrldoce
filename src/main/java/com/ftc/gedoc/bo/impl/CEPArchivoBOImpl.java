package com.ftc.gedoc.bo.impl;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.CEPArchivoBO;
import com.ftc.gedoc.dao.CEPArchivoDAO;
import com.ftc.gedoc.dao.CEPCabeceraDAO;
import com.ftc.gedoc.dao.CEPConceptoDAO;
import com.ftc.gedoc.dao.CEPPagoDAO;
import com.ftc.gedoc.dao.impl.CEPArchivoDAOImpl;
import com.ftc.gedoc.dao.impl.CEPCabeceraDAOImpl;
import com.ftc.gedoc.dao.impl.CEPConceptoDAOImpl;
import com.ftc.gedoc.dao.impl.CEPPagoDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CEPArchivo;
import com.ftc.modelo.CEPCabecera;
import com.ftc.modelo.CEPConcepto;
import com.ftc.modelo.CEPPago;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CEPArchivoBOImpl implements CEPArchivoBO {    
    
    @Override
    public CEPArchivo registraCEP(CEPArchivo cep, CEPCabecera xml) throws GeDocBOException {
        try{        
        // 1. Insertar la cabecera
            CEPArchivoDAO dao = new CEPArchivoDAOImpl();
            cep.setIdentificador(Comunes.toMD5(cep.getArchivos().concat(String.valueOf(Calendar.getInstance().getTime()))));
            cep = dao.registraCEP(cep);
            if(cep==null){
                throw new GeDocBOException("No se ha logrado generar el registro del archivo. El resultado es nulo. ");
            }             
        // 2. Insertar el contenido XML
            CEPCabeceraDAO daoCabecera = new CEPCabeceraDAOImpl();
            xml.setIdentificador(cep.getIdentificador());
            xml = daoCabecera.registraCEP(xml);
            if (xml==null){
                dao.eliminaCEP(cep.getIdentificador());
                throw new GeDocBOException("No se ha logrado insertar la cabecera del CEP. El resultado fue nulo.".concat(cep.getIdentificador()));
            }
            CEPConceptoDAO daoConcepto = new CEPConceptoDAOImpl(xml.getUuid());
            for (CEPConcepto concepto: xml.getConceptos()){
                daoConcepto.registraCEPConcepto(concepto);
            }
            CEPPagoDAO daoPago = new CEPPagoDAOImpl(xml.getUuid());
            for (CEPPago pago: xml.getPagos()){
                daoPago.registraPago(pago);                
            }
            return cep;
        // 3. Exception si alguno falla        
        } catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }                
    }

    @Override
    public List<CEPArchivo> listaCEP(String persona) throws GeDocBOException {        
        try{
            CEPArchivoDAO dao = new CEPArchivoDAOImpl();
            List<CEPArchivo> archivos = dao.listar(persona);
            return archivos;
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public CEPArchivo obtieneCEP(String identificador) throws GeDocBOException {
        try{
            CEPArchivoDAO dao = new CEPArchivoDAOImpl();
            CEPArchivo archivo = dao.obtieneCEP(identificador);
            return archivo;
        }catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }

    @Override
    public List<CEPArchivo> listaCEP(String persona, String fechaInicio, String fechaFinal) throws GeDocBOException {
        try{
            CEPArchivoDAO dao = new CEPArchivoDAOImpl();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            List<CEPArchivo> cabeceras;
            if (fechaInicio!=null && fechaInicio.length()>0){
                Date fInicial = dateFormat.parse(fechaInicio);
                fechaInicio = Comunes.formatoFecha(fInicial, -3);
                Date fFinal = dateFormat.parse(fechaFinal);
                fechaFinal = Comunes.formatoFecha(fFinal, -3);
                cabeceras = dao.listar(persona, fechaInicio, fechaFinal);
            } else {
                cabeceras = dao.listar(persona);
            }            
            return cabeceras;
        }catch(GeDocDAOException e){
            throw new GeDocBOException("Ha ocurrido un fallo al listar los archivos CEP." + e.getMessage(), e);
        } catch(ParseException e){
            throw new GeDocBOException("Imposible listar los archivos CEP, lo se reconocen las fechas. " + fechaInicio + "," + fechaFinal, e);
        }
    }

    @Override
    public CEPArchivo actualizaCEP(String identificador, String estatus) throws GeDocBOException {
        try{
            CEPArchivoDAO dao = new CEPArchivoDAOImpl();
            CEPArchivo cep = dao.obtieneCEP(identificador);
            if (cep!=null){
                cep.setEstatus(estatus);
                cep = dao.actualizaCEP(cep);
                return cep;
            } else {
                throw new GeDocBOException("El CEP no se ha localizado, verifique. "+identificador);
            }
        }catch(GeDocDAOException e){
            throw new GeDocBOException("Ha ocurrido un fallo al listar los archivos CEP." + e.getMessage(), e);
        }
    }

}
