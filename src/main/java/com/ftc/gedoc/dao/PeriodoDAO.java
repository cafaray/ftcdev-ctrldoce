package com.ftc.gedoc.dao;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Periodo;
import com.ftc.modelo.PeriodoCabecera;
import com.ftc.modelo.PeriodoCifraControl;
import com.ftc.modelo.PeriodoRegistro;
import com.ftc.modelo.CifraControl;
import com.ftc.modelo.CifraControlAjuste;

import java.util.List;
import java.util.Map;

public interface PeriodoDAO {
    List<Periodo> listado() throws GeDocDAOException;
    Periodo abrir(Periodo periodo) throws GeDocDAOException;    
    Periodo cerrar(Periodo periodo) throws GeDocDAOException;
    Periodo encuentraPorId(String id) throws GeDocDAOException;
    Periodo activo()throws GeDocDAOException;
    List<PeriodoRegistro> listaRegistrosPeriodo(Periodo periodo)throws GeDocDAOException;
    
    PeriodoCifraControl obtieneCifraControl(String id) throws GeDocDAOException;
    PeriodoCifraControl insertaCifraControl(String id, PeriodoCifraControl cifraControl) throws GeDocDAOException;
    PeriodoCifraControl actualizaCifraControl(String id, PeriodoCifraControl cifraControl) throws GeDocDAOException;    
    
    List<PeriodoCabecera> listaCabeceras(String id) throws GeDocDAOException;
    List<PeriodoCabecera> listaCabeceras(String id, String tipoGasto) throws GeDocDAOException;
    //List<PeriodoCabecera> listaCabecerasConImporte(String id, String... params) throws GeDocDAOException;
    List<PeriodoCabecera> listaCabecerasConImporte(String id, String tipoGasto, String... params) throws GeDocDAOException;
    PeriodoCabecera registraCabecera(String id, PeriodoCabecera periodoCabecera) throws GeDocDAOException;
    void eliminaCabecera(PeriodoCabecera periodoCabecera) throws GeDocDAOException;
    void eliminaCabecera(String idCabecera) throws GeDocDAOException;
    void eliminaCabeceras(String id) throws GeDocDAOException;
    PeriodoCabecera encuentraCabeceraPorId(String idCabecera) throws GeDocDAOException;
    PeriodoCabecera cierraCabecera(String idCabecera) throws GeDocDAOException;
    PeriodoCabecera encuentraPorRegisro(String idRegistro) throws GeDocDAOException;
    
    List<PeriodoRegistro> listaRegistros(String id) throws GeDocDAOException;
    List<PeriodoRegistro> insertaRegistros(String id, List<PeriodoRegistro> periodoRegistros) throws GeDocDAOException;
    PeriodoRegistro insertaRegistro(String id, PeriodoRegistro periodoRegistro) throws GeDocDAOException;
    void eliminaRegistro(PeriodoRegistro periodoRegistro) throws GeDocDAOException;
    void eliminaRegistros(String id) throws GeDocDAOException;
    PeriodoRegistro encuentraRegistroPorId(String idRegistro) throws GeDocDAOException;
    List<PeriodoRegistro> pendientesAprobacion(String idCabecera) throws GeDocDAOException;
 
    List<String> listaAsignados() throws GeDocDAOException;
 
    Map<String, String> listadoTipoComprobante(String tipoGasto) throws GeDocDAOException;
    
    PeriodoRegistro actualizaTipoComprobanteRegistro(PeriodoRegistro periodoRegistro, String tipoComprobante) throws GeDocDAOException;
    
    PeriodoRegistro actualizaAutorizaRegistro(PeriodoRegistro periodoRegistro, String autoriza) throws GeDocDAOException;
    
    PeriodoRegistro actualizaEstadoRegistro(PeriodoRegistro periodoRegistro, String estado) throws GeDocDAOException;
    
    CifraControl getCifraControl(String idPeriodo) throws GeDocDAOException;
    
    Periodo encuentraPorFecha(String valor) throws GeDocDAOException;
    
    CifraControl cierreCifraControl(Periodo periodo) throws GeDocDAOException;
    
    String cierreCifraControlAjuste(Periodo periodo) throws GeDocDAOException;
    
    List<CifraControlAjuste> getCifraControlAjuste(String idPeriodo) throws GeDocDAOException;
 
    PeriodoCabecera cierraCabeceraAjuste(String idCabecera) throws GeDocDAOException;
    
    Periodo getPeriodo(int any, int mes) throws GeDocDAOException;
    
    List<PeriodoCabecera> getCabecerasAgrupadasPorAsociado(String idPeriodo) throws GeDocDAOException;
    
    List<PeriodoCabecera> getCabecerasPorAsociado(String idPeriodo, String asociado) throws GeDocDAOException;
    
}
