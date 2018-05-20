package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Periodo;
import com.ftc.modelo.PeriodoCabecera;
import com.ftc.modelo.PeriodoRegistro;
import com.ftc.modelo.CifraControl;
import com.ftc.modelo.CifraControlAjuste;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PeriodoBo {
    
    Periodo abrirPeriodo() throws GeDocBOException;
    
    /***
     * Realiza el cierre del periodo, cerrando las cifra control y abriendo el nuevo periodo
     * @return Periodo abierto, luego de cerrar el anterior.
     * @throws GeDocBOException 
     */
    Periodo cerrarPeriodo() throws GeDocBOException;
    
    /***
     * Determina el periodo abierto, solo puede existir uno
     * @return Periodo abierto a la fecha
     * @throws GeDocBOException 
     */
    Periodo actual() throws GeDocBOException;

    
        /***
     * Obtiene los datos del periodo, incluye todos sus objetos
     * @param id identificador de periodo que se quiere actualizar
     * @return Periodo
     * @throws GeDocBOException 
     */
    Periodo obtenerPeriodoPorId(String id) throws GeDocBOException;
    
    /***
     * Obtiene los datos del periodo, incluye todos sus objetos
     * @param id identificador de periodo que se quiere actualizar
     * @param tipoGasto identificador del tipo de gasto
     * @return Periodo
     * @throws GeDocBOException 
     */
    Periodo obtenerPeriodoCompleto(String id, String tipoGasto) throws GeDocBOException;
    
    /***
     * Actualiza los registros del periodo
     * @param registros listado de registros a actualizar
     * @return Periodo actualizado
     * @throws GeDocBOException 
     */
    Periodo actualizaRegistros(List<PeriodoRegistro> registros) throws GeDocBOException;
    
    /***
     * Abre el per�odo anterior al actual, se eliminan los elementos registrados al per�odo actual
     * @return Periodo periodo abierto actual
     * @throws GeDocBOException 
     */
    Periodo abrirPeriodoAnterior() throws GeDocBOException;
    
    /***
     * Listado de per�odos existentes
     * @return List<Periodo> 
     * @throws GeDocBOException 
     */
    List<Periodo> listado()throws GeDocBOException;
    
    /***
     * Listado de las cabeceras existentes en un periodo
     * @param id Identificador de periodo
     * @param tipoGasto identificador del tipo de gasto
     * @return List<PeriodoCabecera> 
     * @throws GeDocBOException 
     */
    List<PeriodoCabecera> listaCabeceras(String id, String tipoGasto) throws GeDocBOException;

    /***
     * Listado de las cabeceras existentes en un periodo
     * @param id Identificador de periodo
     * @return List<PeriodoCabecera> 
     * @throws GeDocBOException 
     */
    List<PeriodoCabecera> listaCabeceras(String id) throws GeDocBOException;
    
    /***
     * Listado de las cabeceras completas (eager) existentes en un periodo
     * @param id Identificador de periodo
     * @param tipoGasto identificador del tipo de gasto
     * @return List<PeriodoCabecera> 
     * @throws GeDocBOException 
     */
    List<PeriodoCabecera> listaCabecerasCompletas(String id, String tipoGasto, String...prams) throws GeDocBOException;
    
    /***
     * Listado de las cabeceras completas (eager) existentes en un periodo
     * @param id Identificador de periodo
     * @param tipoGasto identificador del tipo de gasto
     * @param asignado filtra por el campo de asignado
     * @return List<PeriodoCabecera> 
     * @throws GeDocBOException 
     */
//    List<PeriodoCabecera> listaCabecerasCompletas(String id, String tipoGasto, String...params) throws GeDocBOException;    

    /***
     * Listado de las cabeceras completas (eager) existentes en un periodo
     * @param id Identificador de periodo
     * @return List<PeriodoCabecera> 
     * @throws GeDocBOException 
     */
    List<PeriodoCabecera> listaCabecerasCompletas(String id) throws GeDocBOException;
    
    
    /***
     * Registra un elemento de cabecera dentro de un periodo
     * @param id Identificador de periodo
     * @param periodoCabecera elemento de cabecera
     * @return PeriodoCabecera 
     * @throws GeDocBOException 
     */
    PeriodoCabecera insertaCabecera(String id, PeriodoCabecera periodoCabecera) throws GeDocBOException;

    /***
     * Cierra el periodo de registro de detalle de gastos para una cabecera. Se entiende como cerrar el presupuesto asignado a una persona/caja. Una vez realizado no se pueden hacer actualizaciones a sus registros.
     * @param idCabecera identificador de la cabecera que ser&aacute; cerrada
     * @return PeriodoCabecera con el nuevo estatus luego de la actualizaci&oacute;n
     * @throws GeDocBOException 
     */
    PeriodoCabecera cierraCabcera(String idCabecera) throws GeDocBOException;
    
    /***
     * Elimina cabecera a trav�s del elemento propio
     * @param periodoCabecera elemento de cabecera que ser� eliminado
     * @throws GeDocBOException 
     */
    void eliminaCabecera(PeriodoCabecera periodoCabecera) throws GeDocBOException;

    /***
     * Elimina cabecera a trav�s del identificador del elemento de cabecera
     * @param idCabecera Identificador de la cabecera
     * @throws GeDocBOException 
     */
    void eliminaCabecera(String idCabecera) throws GeDocBOException;
    
    
    /***
     * Elimina las cabeceras a trav�s del identificador de periodo
     * @param id Identificador de periodo
     * @throws GeDocBOException 
     */
    void eliminaCabeceras(String id) throws GeDocBOException;    
    
    /***
     * Listado de registros asociados a una cabecera
     * @param idCabecera identificador de la cabecera
     * @return List<PeriodoRegistro> Listado de registros asociados a la cabecera
     * @throws GeDocBOException 
     */    
    List<PeriodoRegistro> listaRegistros(String idCabecera) throws GeDocBOException;
    
    /***
     * Inserta un registro asociado a la cabecera
     * @param idCabecera identificador de la cabecera
     * @param periodoRegistro elemento que sera registrado
     * @return PeriodoRegistro registro resultado de la inserci�n del elemento asociados a la cabecera
     * @throws GeDocBOException 
     */
    PeriodoRegistro insertaRegistro(String idCabecera, PeriodoRegistro periodoRegistro) throws GeDocBOException;
        
    /***
     * Inserta varios registros 
     * @param idCabecera identificador de la cabecera a la que se asocian los registros
     * @param periodoRegistros lista de los registros que se van a insertar
     * @return List<PeriodoRegistro> listado actualizdo de los registros
     * @throws GeDocBOException 
     */
    List<PeriodoRegistro> insertaRegistros(String idCabecera, List<PeriodoRegistro> periodoRegistros) throws GeDocBOException;
    
    /***
     * Elimina un registro asociado a la cabecera a trav�s de su identificador
     * @param idCabecera identificador de la cabecera
     * @throws GeDocBOException 
     */
    void eliminaRegistros(String idCabecera) throws GeDocBOException;
    void eliminaRegistro(PeriodoRegistro periodoRegistro) throws GeDocBOException;
    /***
     * Elimina un registro asociado a la cabecera a trav�s de su identificador
     * @param idRegistro identificador de la cabecera
     * @throws GeDocBOException 
     */
    void eliminaRegistro(String idRegistro) throws GeDocBOException;
    
    /***
     * Servicio para el autocomplete que devuelve un listado de las personas asociadas a gastos
     * @param filtro los primeros caracteres escritos y que aplican al filtro sobre el contenido del texto
     * @return List<String> valores que coincidieron con el filtro
     * @throws GeDocBOException 
     */
    List<String> filtraPersonasAsociadas(String filtro) throws GeDocBOException;
    
    /***
     * Encuentra la cabecera a trav�s de su identificador
     * @param idCabecera identificador de la cabecera
     * @return PeriodoCabecera Objeto encontrado
     * @throws GeDocBOException 
     */
    PeriodoCabecera encuentraCabeceraPorId(String idCabecera) throws GeDocBOException;
    
    
    /***
     * Encuentra el registro a trav�s de su identificador
     * @param idRegistro identificador del registro
     * @return PeriodoRegistro Objeto encontrado
     * @throws GeDocBOException 
     */
    PeriodoRegistro encuentraRegistroPorId(String idRegistro) throws GeDocBOException;
    
    /***
     * Tipos de comprobante usados para el reporte de gastos
     * @param tipoGasto el tipo de gasto para filtrar los tipos de comprobante
     * @return Map<String, String> mapa de elementos asociados al tipo de gasto
     * @throws GeDocBOException 
     */
    Map<String, String> listadoTipoComprobante(String tipoGasto) throws GeDocBOException;
 
    /***
     * Listado con los registros de gastos pendientes de aprobar
     * @param idCabecera identificador de la cabecera de la que se devolveran el total de registros
     * @return List<PeriodoRegistro> listado de elementos PeriodoRegistro que no tienen valor en el campo Autoriza
     * @throws GeDocBOException 
     */
    List<PeriodoRegistro> pendientesAprobacion(String idCabecera)throws GeDocBOException;
    
    /***
     * Registro con el valor actualizado
     * @param periodoRegistro identificador del registro
     * @param valor valor que se asignar� al campo
     * @return
     * @throws GeDocBOException 
     */
    PeriodoRegistro actualizaTipoComprobante(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException;
    
    /***
     * Registro con el valor actualizado
     * @param periodoRegistro identificador del registro
     * @param valor valor que se asignar� al campo
     * @return
     * @throws GeDocBOException 
     */
    PeriodoRegistro actualizaAutoriza(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException;

    /***
     * Registro con el valor actualizado
     * @param periodoRegistro identificador del registro
     * @param valor valor que se asignar� al campo
     * @return
     * @throws GeDocBOException 
     */
    PeriodoRegistro actualizaEstado(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException;
    
    double importePeriodoPorTipoGasto(Periodo periodo, String tipo) throws GeDocBOException;
    double importePeriodo(Periodo periodo) throws GeDocBOException;
    double totalRegistros(List<PeriodoRegistro> registros) throws GeDocBOException;
    double totalAutorizado(List<PeriodoRegistro> registros) throws GeDocBOException;
    
    CifraControl getCifraControl(Periodo periodo) throws GeDocBOException;    
 
    Periodo obtenerPeriodoPorFecha(String valor) throws GeDocBOException;
    
    CifraControl cierraPeriodo(Periodo periodo) throws GeDocBOException;
    
    String cierraPeriodoAjuste(Periodo periodo) throws GeDocBOException;
    
    List<CifraControlAjuste> getCifraControlAjuste(Periodo periodo) throws GeDocBOException;
    
    PeriodoCabecera cierraCabceraAjuste(String idCabecera) throws GeDocBOException;
 
    Periodo obtenerPeriodoPorFecha(Date fecha) throws GeDocBOException;
    
    List<PeriodoCabecera> listaCabecerasAgrupadas(String idPeriodo) throws GeDocBOException;
    
    List<PeriodoCabecera> listaCabecerasPeriodoAsociado(String idPeriodo, String asociado) throws GeDocBOException;
    
}
