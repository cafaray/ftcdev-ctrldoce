package com.ftc.gedoc.bo.impl;

import com.ftc.aq.Comunes;
import com.ftc.gedoc.bo.PeriodoBo;
import com.ftc.gedoc.dao.PeriodoDAO;
import com.ftc.gedoc.dao.impl.PeriodoDAOImpl;
import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.CifraControl;
import com.ftc.modelo.CifraControlAjuste;
import com.ftc.modelo.Periodo;
import com.ftc.modelo.PeriodoCabecera;
import com.ftc.modelo.PeriodoCifraControl;
import com.ftc.modelo.PeriodoRegistro;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PeriodoBOImpl implements PeriodoBo {

    PeriodoDAO dao;

    public PeriodoBOImpl() {
        dao = new PeriodoDAOImpl();
    }

    public Periodo cerrarPeriodo(Periodo periodo) throws GeDocBOException {
        try {
            Periodo activo = dao.activo();
            if (activo.equals(periodo)){
                
                
            } else {
                throw new GeDocBOException(
                        "Al parecer el periodo indicado no esta disponible para ser cerrado, ya que no es el periodo activo "
                                + periodo.getIdentificador() + " != " + activo.getIdentificador());
            }
            return dao.activo();
        } catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
    @Override
    public Periodo abrirPeriodo() throws GeDocBOException{
        try {
            Calendar calendar = Calendar.getInstance();
            int any = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            Periodo actual = new Periodo();
            actual.setAny(any);
            actual.setPeriodo(mes);
            actual = dao.abrir(actual);
            return actual;
        } catch(GeDocDAOException e){
            throw new GeDocBOException(e.getMessage(), e);
        }
    }
    
    @Override
    @Deprecated
    public Periodo cerrarPeriodo() throws GeDocBOException {
        try {
            Periodo activo = dao.activo();
            if (activo != null && activo.getIdentificador()!=null && !activo.getIdentificador().isEmpty()) {
                int iRegistros = 0;
                double monto = 0d;
                //obtiene los registros para calcular las cifras control.
                List<PeriodoRegistro> registros = dao.listaRegistrosPeriodo(activo);
                if (registros.size() > 0) {
                    PeriodoCifraControl cifraControl = cifraControl(registros);
                    if(cifraControl.isPendienteAutorizar()){
                        throw new GeDocBOException("Hay gastos pendientes de autorizar, es necesario marcar el estatus para poder cerrar el período.");
                    }
                    dao.actualizaCifraControl(activo.getIdentificador(), cifraControl);
                }
                //cierra el periodo cambiando el estatus
                dao.cerrar(activo);
                //Se genera el nuevo periodo
                Periodo nuevo = new Periodo();
                if (activo.getPeriodo() == 12) {
                    nuevo.setPeriodo(1);
                    nuevo.setAny(activo.getAny() + 1);
                } else {
                    nuevo.setPeriodo(activo.getPeriodo() + 1);
                    nuevo.setAny(activo.getAny());
                }
                dao.abrir(nuevo);
                return nuevo;
            } else {
                throw new GeDocBOException("El periodo no se ha cerrado debido a que no encontro uno abierto actualmente.");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e);
        }
    }

    @Override
    public Periodo actual() throws GeDocBOException {
        try {
            return dao.activo();
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("Algo ocurrió al recuperar el período actual.", e);
        }
    }

    @Override
    public Periodo obtenerPeriodoPorId(String id) throws GeDocBOException{
        try {
            return dao.encuentraPorId(id);
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("No se localizo el período actual.", e);
        }
    }
    
    @Override
    public Periodo obtenerPeriodoPorFecha(String valor) throws GeDocBOException {
        try{
            return dao.encuentraPorFecha(valor);
        }catch(GeDocDAOException e){
            throw new GeDocBOException("No se localizo el período "+valor, e);
        }
    }
    
    @Override
    public Periodo obtenerPeriodoCompleto(String id, String tipoGasto) throws GeDocBOException {
        try {
            Periodo periodo = dao.encuentraPorId(id);
            List<PeriodoCabecera> cabeceras = dao.listaCabeceras(id, tipoGasto);
            periodo.setCabeceras(cabeceras);
            //           periodo.setCifraControl(cifraControl(cabeceras));
            return periodo;
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("No se lograron recuperar los elementos del periodo.", e);
        }
    }

    @Override
    public Periodo actualizaRegistros(List<PeriodoRegistro> registros) throws GeDocBOException {
        try {
            Periodo periodo = dao.activo();
            dao.eliminaRegistros(periodo.getIdentificador());
            List<PeriodoRegistro> nuevos = dao.insertaRegistros(periodo.getIdentificador(), registros);
//            periodo.setRegistro(nuevos);
            periodo.setCifraControl(cifraControl(nuevos));
            dao.actualizaCifraControl(periodo.getIdentificador(), periodo.getCifraControl());
            return periodo;
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("No se lograron actualizar los elementos del periodo.", e);
        }
    }

    @Override
    @Deprecated
    public Periodo abrirPeriodoAnterior() throws GeDocBOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PeriodoCifraControl cifraControl(List<PeriodoRegistro> registros) throws GeDocBOException {
        double monto = 0d;
        int iRegistros = registros.size();
        boolean noHayPendienteAutorizar = true;
        for (PeriodoRegistro periodoRegistro : registros) {
            monto += periodoRegistro.getImporte();
            noHayPendienteAutorizar &= periodoRegistro.getAutoriza()!=null&&!periodoRegistro.getAutoriza().isEmpty()&&!periodoRegistro.getAutoriza().equalsIgnoreCase("NO");
        }
        PeriodoCifraControl cifraControl = new PeriodoCifraControl();
        cifraControl.setRegistros(iRegistros);
        cifraControl.setMonto(monto);
        cifraControl.setPendienteAutorizar(!noHayPendienteAutorizar);
        return cifraControl;
    }

    @Override
    public List<Periodo> listado() throws GeDocBOException {
        try {
            return dao.listado();
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("Error al listar los periodos.", e);
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabeceras(String id, String tipoGasto) throws GeDocBOException {
        try {
            Periodo periodo = dao.encuentraPorId(id);
            if (periodo != null) {
                return dao.listaCabeceras(id, tipoGasto);
            } else {
                throw new GeDocBOException("No se localizo el periodo soliciado [" + id + "].");
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrio un error al listar las cabeceras de registro de gasto.", e);
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabeceras(String id) throws GeDocBOException {
        try {
            Periodo periodo = dao.encuentraPorId(id);
            if (periodo != null) {
                return dao.listaCabeceras(id);
            } else {
                throw new GeDocBOException("No se localizo el periodo soliciado [" + id + "].");
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrio un error al listar las cabeceras de registro de gasto.", e);
        }
    }

    
    
    @Override
    public List<PeriodoCabecera> listaCabecerasCompletas(String id, String tipoGasto, String... params) throws GeDocBOException {
        /*
        if(params.length==1 && params[0].startsWith("asignadoA")){
            return listaCabecerasCompletas(id, tipoGasto, params[0].substring(params[0].indexOf(":")+1));
        }
                */
        try {
            Periodo periodo = dao.encuentraPorId(id);
            if (periodo != null) {
                return dao.listaCabecerasConImporte(id, tipoGasto, params);
            } else {
                throw new GeDocBOException("No se localizo el periodo soliciado [" + id + "].");
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrio un error al listar las cabeceras de registro de gasto.", e);
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabecerasCompletas(String id) throws GeDocBOException {
        try {
            List<PeriodoCabecera> cabeceras = listaCabeceras(id);
            if (cabeceras.size() > 0) {
                for (PeriodoCabecera cabecera : cabeceras) {
                    List<PeriodoRegistro> registros = dao.listaRegistros(cabecera.getIdentificador());
                    cabecera.setRegistros(registros);
                }
            }
            return cabeceras;
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("Ocurrió una excepción al listar las cabeceras con todos sus datos.", e);
        }
    }

    
    @Override
    public Periodo obtenerPeriodoPorFecha(Date fecha) throws GeDocBOException{
        try{            
            int any, mes;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            any = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH) + 1;
            PeriodoDAO dao = new PeriodoDAOImpl();
            Periodo periodo = dao.getPeriodo(any, mes);
            if (periodo!=null){
                return periodo;
            }
            throw new GeDocBOException("Al parecer el periodo no se encuentra registrado aun. Verifique con el administrador de periodos");
        } catch (GeDocDAOException e) {            
            throw new GeDocBOException("Ocurrió una excepción al obtener el periodo con los valores dador " + Comunes.date2String(fecha, 1), e);
        }
    }
    
    @Override
    public PeriodoCabecera insertaCabecera(String id, PeriodoCabecera periodoCabecera) throws GeDocBOException {
        try {
            Periodo periodo = dao.encuentraPorId(id);           
            // --> cafara 281217: se incluyen validaciones y especificacion de periodo para registros fuera de periodo actual
            // validar si viene el parametro de fecha especificado:
            if (periodoCabecera.getFecha()!=null){
                Periodo periodoRegistro = obtenerPeriodoPorFecha(periodoCabecera.getFecha());
                // el especificado y el actual son el mismo:
                if (!(periodoRegistro.getAny() == periodo.getAny() && periodoRegistro.getPeriodo() == periodo.getPeriodo())){
                    periodo = periodoRegistro;
                }
            }
            
            if (periodo != null) {
//                Se invalida esta seccion debido a que se requiere m�s de un registro por periodo:              
//                List<PeriodoCabecera> cabeceras = listaCabeceras(periodo.getIdentificador());
//                for(PeriodoCabecera cabecera : cabeceras){
//                    if(cabecera.getAsociadoA().equals(periodoCabecera.getAsociadoA())){
//                        throw new GeDocBOException("Ya existe un registro para esta persona, no se puede registrar otro en el mismo periodo.");
//                    }
//                }
                // -> cfa:121115 a cajon se coloca la fecha de operaci�n
                periodoCabecera.setFecha(new Date());
                // <-
                return dao.registraCabecera(periodo.getIdentificador(), periodoCabecera);
            } else {
                throw new GeDocBOException("No se localizo el período especificado para registrar la cabcera [" + id + "].");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException("Ocurrió una excepción al insertar una cabecera dentro del periodo.", e);
        }
    }

    @Override
    public void eliminaCabecera(PeriodoCabecera periodoCabecera) throws GeDocBOException {
        try {
            if(validaEstatusCabecera(periodoCabecera)){
                dao.eliminaCabecera(periodoCabecera);
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al eliminar la información de cabecera de gasto [" + periodoCabecera.getIdentificador() + "]", e);
        }
    }

    @Override
    public void eliminaCabecera(String idCabecera) throws GeDocBOException {
        try {
            PeriodoCabecera pc = encuentraCabeceraPorId(idCabecera);
            if(validaEstatusCabecera(pc)){
                dao.eliminaCabecera(idCabecera);
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al eliminar la información de cabecera de gasto [" + idCabecera + "]", e);
        }
    }

    @Override
    public void eliminaCabeceras(String id) throws GeDocBOException {
        List<PeriodoCabecera> cabeceras = listaCabeceras(id);
        for(PeriodoCabecera cabecera:cabeceras){
            eliminaCabecera(cabecera);
        }
    }

    @Override
    public List<PeriodoRegistro> listaRegistros(String idCabecera) throws GeDocBOException {
        try {
            return dao.listaRegistros(idCabecera);
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al listar los registros de la cabeceras de gasto [" + idCabecera + "]", e);
        }
    }

    @Override
    public PeriodoRegistro encuentraRegistroPorId(String idRegistro) throws GeDocBOException {
        try {
            PeriodoRegistro periodoRegistro = dao.encuentraRegistroPorId(idRegistro);
            return periodoRegistro;
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(String.format("Hubo un error al localizar el registro [%s] en la unidad. Revise el log para más detalles.", idRegistro), e);
        }
    }

    @Override
    public PeriodoRegistro insertaRegistro(String idCabecera, PeriodoRegistro periodoRegistro) throws GeDocBOException {
        try {
            //valida que exista la cabecera.
            PeriodoCabecera pc = dao.encuentraCabeceraPorId(idCabecera);
            if (validaEstatusCabecera(pc)) {
                periodoRegistro = dao.insertaRegistro(idCabecera, periodoRegistro);
            } else {
                throw new GeDocBOException("No se localizo la cabecera [" + idCabecera + "] para asociarla al registro.");
            }
            return periodoRegistro;
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al insertar el registro de la cabecera de gasto [" + idCabecera + "]", e);
        }
    }

    @Override
    public List<PeriodoRegistro> insertaRegistros(String idCabecera, List<PeriodoRegistro> periodoRegistros) throws GeDocBOException {
        try {
            //valida que exista la cabecera.
            PeriodoCabecera pc = dao.encuentraCabeceraPorId(idCabecera);
            if (validaEstatusCabecera(pc)) {
                for (PeriodoRegistro periodoRegistro : periodoRegistros) {
                    periodoRegistro = dao.insertaRegistro(idCabecera, periodoRegistro);
                }
            } else {
                throw new GeDocBOException("No se localizo la cabecera [" + idCabecera + "] para asociarla al registro.");
            }
            return periodoRegistros;
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al insertar el registro de la cabecera de gasto [" + idCabecera + "]", e);
        }
    }

    @Override
    public void eliminaRegistros(String idCabecera) throws GeDocBOException {
        try {
            //valida que exista la cabecera.
            PeriodoCabecera pc = dao.encuentraCabeceraPorId(idCabecera);
            if (validaEstatusCabecera(pc)) {
                List<PeriodoRegistro> registros = listaRegistros(idCabecera);
                for (PeriodoRegistro registro : registros) {
                    eliminaRegistro(registro);
                }
            } else {
                throw new GeDocBOException("No se localizo la cabecera [" + idCabecera + "] para asociarla al registro.");
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al eliminar los registros de la cabecera de gasto [" + idCabecera + "]", e);
        }
    }

    @Override
    public void eliminaRegistro(PeriodoRegistro periodoRegistro) throws GeDocBOException {
        try {
            PeriodoCabecera pc = dao.encuentraPorRegisro(periodoRegistro.getRegistro());
            if(validaEstatusCabecera(pc)){
                dao.eliminaRegistro(periodoRegistro);
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al eliminar el registro", e);
        }
    }

    @Override
    public void eliminaRegistro(String idRegistro) throws GeDocBOException {
        try {
            PeriodoRegistro pr = dao.encuentraRegistroPorId(idRegistro);
            if (pr != null) {
                eliminaRegistro(pr);
            }
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al insertar el registro de la cabecera de gasto [" + idRegistro + "]", e);
        }
    }

    @Override
    public List<String> filtraPersonasAsociadas(String filtro) throws GeDocBOException {
        try {
            List<String> asignados = dao.listaAsignados();
            List<String> filtrados = new ArrayList<String>();
            for (String asignado : asignados) {
                if (asignado.contains(filtro)) {
                    filtrados.add(asignado);
                }
            }
            return filtrados;
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al consultar las personas asignadas a gastos.", e);
        }
    }

    @Override
    public PeriodoCabecera encuentraCabeceraPorId(String idCabecera) throws GeDocBOException {
        try {
            PeriodoCabecera pc = dao.encuentraCabeceraPorId(idCabecera);
            return pc;
        } catch (GeDocDAOException e) {
            e.printStackTrace(System.out);
            throw new GeDocBOException("Ocurrió una excepción al localizar la cabecera [" + idCabecera + "].", e);
        }
    }

    @Override
    public Map<String, String> listadoTipoComprobante(String tipoGasto) throws GeDocBOException {
        try {
            return dao.listadoTipoComprobante(tipoGasto);
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e.getMessage());
        }
    }

    @Override
    public List<PeriodoRegistro> pendientesAprobacion(String idCabecera) throws GeDocBOException {
        List<PeriodoRegistro> listado = listaRegistros(idCabecera);
        List<PeriodoRegistro> registrosPendientes = new ArrayList<PeriodoRegistro>();
        if (listado.size() > 0) {
            for (PeriodoRegistro pr : listado) {
                if (!(pr.getTipo()!= null && !pr.getTipo().isEmpty() && !pr.getTipo().equals("-1"))) {
                    registrosPendientes.add(pr);
                }
            }
            return registrosPendientes;
        } else {
            return listado;
        }
    }

    private boolean validaEstatusCabecera(PeriodoCabecera periodoCabecera) throws GeDocBOException {
            if(periodoCabecera!=null && periodoCabecera.getEstatus().equals("A")){
                return true;
            } else {
                if(periodoCabecera==null){
                    throw new GeDocBOException("No se ha localizado el identificador de control de registro de gasto. Imposible actualizar.");
                }else{
                    throw new GeDocBOException("El identificador de control de registro de gasto esta cerrado, no se puede aplicar la actualización.");
                }
            }
    }
    
    @Override
    public PeriodoRegistro actualizaTipoComprobante(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException {
        try {
            PeriodoCabecera pc = dao.encuentraPorRegisro(periodoRegistro.getRegistro());
            if(validaEstatusCabecera(pc)){
                return dao.actualizaTipoComprobanteRegistro(periodoRegistro, valor);
            }else{
                throw new GeDocBOException("Esta linea nunca debe de ejecutarse.");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e.getMessage());
        }
    }

    @Override
    public PeriodoRegistro actualizaAutoriza(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException {
        try {
            PeriodoCabecera pc = dao.encuentraPorRegisro(periodoRegistro.getRegistro());
            if(validaEstatusCabecera(pc)){
                return dao.actualizaAutorizaRegistro(periodoRegistro, valor);
            }else{
                throw new GeDocBOException("Esta linea nunca debe de ejecutarse.");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e.getMessage());
        }
    }

    @Override
    public PeriodoRegistro actualizaEstado(PeriodoRegistro periodoRegistro, String valor) throws GeDocBOException {
        try {
            PeriodoCabecera pc = dao.encuentraPorRegisro(periodoRegistro.getRegistro());
            if(validaEstatusCabecera(pc)){
                return dao.actualizaEstadoRegistro(periodoRegistro, valor);
            }else{
                throw new GeDocBOException("Esta linea nunca debe de ejecutarse.");
            }
        } catch (GeDocDAOException e) {
            throw new GeDocBOException(e.getMessage());
        }
    }

    @Override
    public double importePeriodoPorTipoGasto(Periodo periodo, String tipo) throws GeDocBOException {
        if (periodo != null) {
            List<PeriodoCabecera> cabeceras = listaCabecerasCompletas(periodo.getIdentificador());
            double total = 0d;
            for (PeriodoCabecera cabecera : cabeceras) {
                if (cabecera.getTipo().equals(tipo)) {
                    total += totalRegistros(cabecera.getRegistros());
                }
            }
            return total;
        } else {
            throw new GeDocBOException("El periodo indicado no existe en la unidad.");
        }
    }

    @Override
    public double importePeriodo(Periodo periodo) throws GeDocBOException {
        if (periodo != null) {
            List<PeriodoCabecera> cabeceras = listaCabecerasCompletas(periodo.getIdentificador());
            double total = 0d;
            for (PeriodoCabecera cabecera : cabeceras) {
                total += totalRegistros(cabecera.getRegistros());
            }
            return total;
        } else {
            throw new GeDocBOException("El periodo indicado no existe en la unidad.");
        }
    }
    
    @Override
    public double totalRegistros(List<PeriodoRegistro> registros) throws GeDocBOException {
        double total = 0d;
        try {
            for (PeriodoRegistro registro : registros) {
                total += registro.getImporte();
            }
            return total;
        } catch (NullPointerException e) {
            throw new GeDocBOException("La lista de registros del periodo no existe.");
        }
    }

    @Override
    public double totalAutorizado(List<PeriodoRegistro> registros) throws GeDocBOException {
        double total = 0d;
        try {
            for (PeriodoRegistro registro : registros) {
                if(registro.getAutoriza()!=null && !registro.getAutoriza().isEmpty() && !registro.getAutoriza().equalsIgnoreCase("NO")){
                    total += registro.getImporte();
                }
            }
            return total;
        } catch (NullPointerException e) {
            throw new GeDocBOException("La lista de registros del periodo no existe.");
        }
    }

    
    public List<PeriodoCabecera> listaCabecerasCompletas(String id, String tipoGasto, String asignado) throws GeDocBOException {
        List<PeriodoCabecera> cabeceras = listaCabecerasCompletas(id, tipoGasto);
        List<PeriodoCabecera> filtrados = new ArrayList<PeriodoCabecera>();
        for(PeriodoCabecera cabecera:cabeceras){
            if(cabecera.getAsociadoA().equals(asignado)){
                filtrados.add(cabecera);
            }
        }
        return filtrados;
    }

    @Override
    public PeriodoCabecera cierraCabcera(String idCabecera) throws GeDocBOException {
        try{
            PeriodoCabecera pc = dao.cierraCabecera(idCabecera);
            return pc;
        }catch(GeDocDAOException e){
            e.getCause().printStackTrace(System.out);
            throw new GeDocBOException("La actualización no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + e.getMessage());
        }
    }

    @Override
    public PeriodoCabecera cierraCabceraAjuste(String idCabecera) throws GeDocBOException {
        try{
            PeriodoCabecera pc = dao.cierraCabeceraAjuste(idCabecera);
            return pc;
        }catch(GeDocDAOException e){
            e.getCause().printStackTrace(System.out);
            throw new GeDocBOException("La actualización no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + e.getMessage());
        }
    }
    
    @Override
    public CifraControl getCifraControl(Periodo periodo) throws GeDocBOException {                
        try {
            CifraControl cifraControl = new CifraControl();
            cifraControl.setPeriodo(periodo);
            cifraControl = dao.getCifraControl(periodo.getIdentificador());
            return cifraControl;
        } catch (GeDocDAOException ex) {
            ex.getCause().printStackTrace(System.out);
            throw new GeDocBOException("La actualización no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + ex.getMessage());
        }
    }
    
    @Override
    public List<CifraControlAjuste> getCifraControlAjuste(Periodo periodo) throws GeDocBOException {                
        try {
            List<CifraControlAjuste> ajustes = new ArrayList<CifraControlAjuste>();
            //CifraControlAjuste cifraControl = new CifraControlAjuste();
            //cifraControl.setPeriodo(periodo);
            
            ajustes = dao.getCifraControlAjuste(periodo.getIdentificador());
            for (CifraControlAjuste ajuste : ajustes){
                ajuste.setPeriodo(periodo);
            }
            return ajustes;
        } catch (GeDocDAOException ex) {
            ex.getCause().printStackTrace(System.out);
            throw new GeDocBOException("La actualización no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + ex.getMessage());
        }
    }
    
    @Override
    public CifraControl cierraPeriodo(Periodo periodo) throws GeDocBOException{
        try {
            CifraControl cifraControl = new CifraControl();
            cifraControl.setPeriodo(periodo);
            cifraControl = dao.cierreCifraControl(periodo);
            return cifraControl;
        } catch (GeDocDAOException ex) {
            ex.getCause().printStackTrace(System.out);
            throw new GeDocBOException("El cierre de periodo no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + ex.getMessage());
        }
    }
    
    @Override
    public String cierraPeriodoAjuste(Periodo periodo) throws GeDocBOException{
        try {            
            String ajuste = dao.cierreCifraControlAjuste(periodo);
            return ajuste;
        } catch (GeDocDAOException ex) {
            //ex.getCause().printStackTrace(System.out);
            throw new GeDocBOException("El cierre de periodo no se llevo a cabo debido a que no se logro actualizar el valor. El mensaje devuelto es: " + ex.getMessage());
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabecerasAgrupadas(String idPeriodo) throws GeDocBOException {
        try{
            return dao.getCabecerasAgrupadasPorAsociado(idPeriodo);
        } catch(GeDocDAOException ex) {
            throw new GeDocBOException("La consulta de cabeceras ha devuelto una excepción, revise el log para más detalles: " + ex.getMessage());
        } catch(NullPointerException ex) {
            throw new GeDocBOException("La consulta de cabeceras ha devuelto un valor null, revise el log para más detalles: " + ex.getMessage());
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabecerasPeriodoAsociado(String idPeriodo, String asociado) throws GeDocBOException {
        try{
            return dao.getCabecerasPorAsociado(idPeriodo, asociado);
        } catch(GeDocDAOException ex) {
            throw new GeDocBOException("La consulta de cabeceras por periodo y asociado ha devuelto una excepción: " + ex.getMessage());
        } catch(NullPointerException ex) {
            throw new GeDocBOException("La consulta de cabeceras ha devuelto un valor null, revise el log para más detalles: " + ex.getMessage());
        }
    }
    
}
