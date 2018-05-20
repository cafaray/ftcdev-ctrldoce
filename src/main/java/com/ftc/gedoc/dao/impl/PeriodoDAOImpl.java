package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.dao.PeriodoDAO;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.gedoc.utiles.*;
import com.ftc.modelo.*;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeriodoDAOImpl implements PeriodoDAO {

    Connection conexion = null;

    public PeriodoDAOImpl() {
    }

    @Override
    public List<Periodo> listado() throws GeDocDAOException {
        List<Periodo> periodos = new ArrayList<Periodo>();
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT a.idnumper, inanyper, innumper, dtfecape, dtfeccie, instatus, dscoment, dbmonto, incuenta "
                    + "FROM jctm01t a LEFT JOIN jctm02t b ON a.idnumper = b.idnumper "
                    + "LEFT JOIN ("
                    + "      SELECT COUNT(A.idreggas) incuenta, idnumper FROM jctm10t A INNER JOIN jctm09t B ON A.idreggas = B.idreggas WHERE B.instatus = 'A' OR B.instatus = 'Q' GROUP BY idnumper "
                    + "    ) c ON a.idnumper = c.idnumper "
                    + " ORDER BY inanyper DESC, innumper DESC;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setAny(rst.getInt(2));
                    periodo.setApertura(rst.getDate(4));
                    periodo.setCierre(rst.getDate(5) != null ? rst.getDate(5) : null);
                    periodo.setComentario(rst.getString(7));
                    periodo.setEstatus(rst.getString(6));
                    periodo.setIdentificador(rst.getString(1));
                    periodo.setPeriodo(rst.getInt(3));
                    periodo.setMonto(rst.getDouble(8));
                    periodo.setCuenta(rst.getInt(9));
                    periodos.add(periodo);
                }
            }
            return periodos;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Periodo abrir(Periodo periodo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "abrirPeriodo";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.INTEGER, periodo.getAny()));
            params.add(new SpParam(2, Types.INTEGER, periodo.getPeriodo()));
            params.add(new SpParam(3, Types.VARCHAR, null, true)); // nuevo periodo
            params.add(new SpParam(4, Types.VARCHAR, null, true)); // fecha apertura
            params.add(new SpParam(5, Types.VARCHAR, null, true)); // estatus
            params.add(new SpParam(6, Types.VARCHAR, null, true)); // comentarios
            params.add(new SpParam(7, Types.VARCHAR, null, true)); // error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 5) {
                if (vuelta[4] != null && !((String) vuelta[4]).isEmpty()) {
                    throw new GeDocDAOException((String) vuelta[4]);
                } else {
                    System.out.println("Quedo abierto el nuevo periodo: " + (String) vuelta[0]);
                    periodo.setIdentificador((String) vuelta[0]);
                    String sFecha = (String) vuelta[1];
                    Date fecha;
                    try {
                        fecha = Comunes.toFechaSQL(sFecha, "-");
                    } catch (Exception e) {
                        System.out.println("=====> Error en la transformacion de fecha de aperetura del periodo." + e.getMessage());
                        e.printStackTrace(System.out);
                        //en caso de que no se logre tomar la fecha de la base de datos, colocamos la fecha del appServer
                        fecha = new Date(Calendar.getInstance().getTimeInMillis());
                    }
                    periodo.setApertura(fecha);
                    periodo.setEstatus((String) vuelta[2]);
                    periodo.setComentario((String) vuelta[3]);
                }
            } else {
                throw new GeDocDAOException("El procedimiento para abrir un nuevo periodo no retorno los parametros necesarios. Se esperaban 5 regreso " + vuelta.length);
            }
            return periodo;
        } catch (SQLException e) {
            throw new GeDocDAOException("La qpertura de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Periodo cerrar(Periodo periodo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "cerrarPeriodo";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, periodo.getIdentificador()));
            params.add(new SpParam(2, Types.VARCHAR, periodo.getComentario()));
            params.add(new SpParam(3, Types.DATE, null, true)); // fecha cierre
            params.add(new SpParam(4, Types.VARCHAR, null, true)); // estatus
            params.add(new SpParam(5, Types.VARCHAR, null, true)); // error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 3) {
                if (vuelta[2] != null && !((String) vuelta[2]).isEmpty()) {
                    throw new GeDocDAOException((String) vuelta[2]);
                } else {
                    System.out.println("Quedo cerrado el periodo: " + periodo.getIdentificador());
                    periodo.setCierre((Date) vuelta[0]);
                    periodo.setEstatus((String) vuelta[1]);
                }
            } else {
                throw new GeDocDAOException("El procedimiento para cerrar un nuevo periodo no retorno los parametros necesarios. Se esperaban 5 regreso " + vuelta.length);
            }
            return periodo;
        } catch (SQLException e) {
            throw new GeDocDAOException("El cierre de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Periodo activo() throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idnumper, inanyper, innumper, dtfecape, dtfeccie, instatus, dscoment ");
            sql.append("FROM jctm01t ");
            sql.append(" WHERE instatus = ?");
            sql.append("LIMIT 1;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, "A");
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setAny(rst.getInt(2));
                    periodo.setApertura(rst.getDate(4));
                    periodo.setCierre(rst.getDate(5) != null ? rst.getDate(5) : null);
                    periodo.setComentario(rst.getString(7));
                    periodo.setEstatus(rst.getString(6));
                    periodo.setIdentificador(rst.getString(1));
                    periodo.setPeriodo(rst.getInt(3));
                    return periodo;
                }
            }
            return new Periodo();
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Periodo encuentraPorId(String id) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idnumper, inanyper, innumper, dtfecape, dtfeccie, instatus, dscoment ");
            sql.append("FROM jctm01t ");
            sql.append(" WHERE idnumper = ?");
            sql.append(" ORDER BY inanyper DESC, innumper DESC;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setAny(rst.getInt(2));
                    periodo.setApertura(rst.getDate(4));
                    periodo.setCierre(rst.getDate(5) != null ? rst.getDate(5) : null);
                    periodo.setComentario(rst.getString(7));
                    periodo.setEstatus(rst.getString(6));
                    periodo.setIdentificador(rst.getString(1));
                    periodo.setPeriodo(rst.getInt(3));
                    return periodo;
                }
            }
            return new Periodo();
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void eliminaRegistros(String identificador) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, identificador));
            params.add(new SpParam(2, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "eliminaRegistro", params);
            if (vuelta != null && vuelta.length == 1) {
                if (((String) vuelta[0]).isEmpty()) {
                    System.out.println("Se elimin� correctamente el registro.");
                } else {
                    throw new GeDocDAOException("Fallo al ejecutar el procedimiento. " + (String) vuelta[0]);
                }
            } else {
                throw new GeDocDAOException("Fallo al ejecutar el procedimiento. No se devolvieron los parametros esperados, se esperaba 1 y se encontraron " + (vuelta != null ? vuelta.length : "null"));
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("Hubo un error al eliminar los datos de registro, la operaci�n no se completa.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabecerasConImporte(String id, String tipoGasto, String... params) throws GeDocDAOException {
        List<PeriodoCabecera> registros = new ArrayList<PeriodoCabecera>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT A.idreggas, dsasocia, idnumper, A.intipgas, A.instatus, fefecreg, dsdocto, dsrefdoc, total, cuenta ");
            sql.append("FROM jctm09t A LEFT JOIN (");
            sql.append("    SELECT idreggas, IFNULL(SUM(dbimpreg), 0) AS total, IFNULL(COUNT(idreggas), 0) cuenta FROM jctc10v GROUP BY idreggas ");
            sql.append(") C ON A.idreggas = C.idreggas ");
            sql.append(" WHERE (idnumper = ? AND A.intipgas = ? OR A.instatus = ?) ");
            if (params.length > 0) {
                String and = "";
                for (String param : params) {
                    if (param.startsWith("fechas:")) {
                        String[] periodo = param.substring(param.indexOf(":") + 1).split(",");
                        if (periodo.length == 2) {
                            Date fecha1 = Comunes.DMAtoFechaSQL(periodo[0]);
                            Date fecha2 = Comunes.DMAtoFechaSQL(periodo[1]);
                            and = String.format(" fefecreg BETWEEN '%s' AND '%s' ", Comunes.date2String(fecha1, -3), Comunes.date2String(fecha2, -3));
                        } else {
                            System.out.println("Parametro de fechas incorrecto: " + param);
                        }
                    } else if (param.startsWith("asignadoA:")) {
                        and = " dsasocia LIKE ('%" + param.substring(param.indexOf(":") + 1) + "%') ";
                    } else if (param.startsWith("documento:")) {
                        and = " dsdocto LIKE ('%" + param.substring(param.indexOf(":") + 1) + "%') ";
                    } else if (param.startsWith("referencia:")) {
                        and = " dsrefdoc LIKE ('%" + param.substring(param.indexOf(":") + 1) + "%') ";
                    } else if (param.startsWith("estatus:")) {
                        and = String.format(" instatus = '%s' ", param.substring(param.indexOf(":") + 1, param.indexOf(":") + 2));
                    } else {
                        System.out.println("Parametro no localizado: " + param);
                    }
                    sql.append(" AND").append(and);
                }
            }

            sql.append("ORDER BY dsasocia;");

            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            stm.setString(2, tipoGasto);
            stm.setString(3, "A");
            System.out.println("Consulta de gasto con importe: " + stm.toString());
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoCabecera pc = new PeriodoCabecera();
                    pc.setIdentificador(rst.getString(1));
                    pc.setAsociadoA(rst.getString(2));
                    pc.setTipo(rst.getString(4));
                    pc.setEstatus(rst.getString(5));
                    pc.setFecha(rst.getDate(6));
                    pc.setDocumento(rst.getString(7));
                    pc.setReferencia(rst.getString(8));
                    pc.setRegistros(new ArrayList<PeriodoRegistro>());
                    pc.setImporte(rst.getDouble(9));
                    pc.setCuentaFueraPeriodo(rst.getInt(10));
                    registros.add(pc);
                }
            }
            return registros;
        } catch (SQLException e) {
            System.out.println("Consulta de gasto con importe: " + e.getMessage() + " -> " + e.getCause());
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoCabecera> listaCabeceras(String id, String tipoGasto) throws GeDocDAOException {
        List<PeriodoCabecera> registros = new ArrayList<PeriodoCabecera>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idreggas, dsasocia, idnumper, intipgas, instatus, fefecreg, dsdocto, dsrefdoc ");
            sql.append("FROM jctm09t ");
            sql.append("WHERE idnumper = ? AND intipgas = ? ");
            sql.append("ORDER BY dsasocia;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            stm.setString(2, tipoGasto);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoCabecera pc = new PeriodoCabecera();
                    pc.setAsociadoA(rst.getString(2));
                    pc.setIdentificador(rst.getString(1));
                    pc.setTipo(rst.getString(4));
                    pc.setEstatus(rst.getString(5));
                    pc.setFecha(rst.getDate(6));
                    pc.setDocumento(rst.getString(7));
                    pc.setReferencia(rst.getString(8));
                    pc.setRegistros(new ArrayList<PeriodoRegistro>());
                    registros.add(pc);
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

//    @Override
//    public List<PeriodoCabecera> listaCabeceras(String id) throws GeDocDAOException {
//        List<PeriodoCabecera> registros = new ArrayList<PeriodoCabecera>();
//        try {
//            conexion = Conexion.getConexion();
//            StringBuilder sql = new StringBuilder();
//            sql.append("SELECT idreggas, dsasocia, idnumper, intipgas, instatus, fefecreg, dsdocto, dsrefdoc, ");
//            sql.append("(SELECT SUM(B.dbimpreg) AS total FROM jctc10v B WHERE B.idreggas = A.idreggas GROUP BY B.idreggas) AS importe ");
//            sql.append("FROM jctm09t A");
//            sql.append("WHERE idnumper = ? ");
//            sql.append("ORDER BY dsasocia;");
//            PreparedStatement stm = conexion.prepareStatement(sql.toString());
//            stm.setString(1, id);
//            ResultSet rst = stm.executeQuery();
//            if (rst != null) {
//                while (rst.next()) {
//                    PeriodoCabecera pc = new PeriodoCabecera();
//                    pc.setAsociadoA(rst.getString(2));
//                    pc.setIdentificador(rst.getString(1));
//                    pc.setTipo(rst.getString(4));
//                    pc.setEstatus(rst.getString(5));
//                    pc.setFecha(rst.getDate(6));
//                    pc.setDocumento(rst.getString(7));
//                    pc.setReferencia(rst.getString(8));
//                    pc.setRegistros(new ArrayList<PeriodoRegistro>());
//                    registros.add(pc);
//                }
//            }
//            return registros;
//        } catch (SQLException e) {
//            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
//        } finally {
//            try {
//                if (conexion != null & !conexion.isClosed()) {
//                    conexion.close();
//                }
//            } catch (SQLException e) {
//            }
//        }
//    }
    @Override
    public List<PeriodoCabecera> listaCabeceras(String id) throws GeDocDAOException {
        List<PeriodoCabecera> registros = new ArrayList<PeriodoCabecera>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idreggas, dsasocia, idnumper, intipgas, instatus, fefecreg, dsdocto, dsrefdoc ");
            sql.append("FROM jctm09t ");
            sql.append("WHERE idnumper = ? AND instatus = 'A' OR instatus = 'Q' "); // --> cafaray 221217: solo permite los pendientes de autorizar y los que tienen pendiente aplicar ajuste de periodo
            sql.append("ORDER BY dsasocia;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            // stm.setString(2, "A"); // --> cafaray 221217: solo permite los que est�n pendientes de autorizar
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoCabecera pc = new PeriodoCabecera();
                    pc.setAsociadoA(rst.getString(2));
                    pc.setIdentificador(rst.getString(1));
                    pc.setTipo(rst.getString(4));
                    pc.setEstatus(rst.getString(5));
                    pc.setFecha(rst.getDate(6));
                    pc.setDocumento(rst.getString(7));
                    pc.setReferencia(rst.getString(8));
                    pc.setRegistros(new ArrayList<PeriodoRegistro>());
                    registros.add(pc);
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoCabecera registraCabecera(String id, PeriodoCabecera periodoCabecera) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "registraCabecera";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, id));
            params.add(new SpParam(2, Types.VARCHAR, periodoCabecera.getAsociadoA()));
            params.add(new SpParam(3, Types.VARCHAR, periodoCabecera.getTipo()));
            // -> cfa:121115 se solicita la inclusi�n de tres campos de control para la cabecera de registro
            params.add(new SpParam(4, Types.DATE, new java.sql.Date(periodoCabecera.getFecha().getTime())));
            params.add(new SpParam(5, Types.VARCHAR, periodoCabecera.getDocumento()));
            params.add(new SpParam(6, Types.VARCHAR, periodoCabecera.getReferencia()));
            // <- cfa:121115
            params.add(new SpParam(7, Types.VARCHAR, null, true));
            params.add(new SpParam(8, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 2) {
                if (((String) vuelta[1]).isEmpty()) {
                    periodoCabecera.setIdentificador((String) vuelta[0]);
                    return periodoCabecera;
                } else {
                    throw new GeDocDAOException("Fallo al ejecutar el procedimiento: " + (String) vuelta[1]);
                }
            } else {
                throw new GeDocDAOException("Fallo al ejecutar el procedimiento. No se devolvieron los parametros esperados, se esperaba 2 y se encontraron " + (vuelta != null ? vuelta.length : "null"));
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("No se logr� crear la cabecera del registro de gasto: " + e.getSQLState(), e);
        } catch (NullPointerException e) {
            throw new GeDocDAOException("No se logr� crearla cabecera del registro de gasto. Se encontro un apuntador nulo " + e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(PeriodoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void eliminaCabecera(PeriodoCabecera periodoCabecera) throws GeDocDAOException {
        eliminaCabecera(periodoCabecera.getIdentificador());
    }

    @Override
    public void eliminaCabecera(String idCabecera) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM jctm09t WHERE idreggas = ?");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, idCabecera);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new GeDocDAOException("No se logr� eliminar la cabecera del registro de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(PeriodoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void eliminaCabeceras(String id) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM jctm09t WHERE idnumper = ?");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new GeDocDAOException("No se logr� eliminar la cabecera del registro de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(PeriodoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public PeriodoCabecera encuentraCabeceraPorId(String idCabecera) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idreggas, dsasocia, idnumper, intipgas, instatus, fefecreg, dsdocto, dsrefdoc ");
            sql.append(" FROM jctm09t ");
            sql.append(" WHERE idreggas = ? ORDER BY tmstmp, dsasocia;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, idCabecera);
            ResultSet rst = stm.executeQuery();
            PeriodoCabecera pc = new PeriodoCabecera();
            if (rst != null && rst.next()) {
                pc.setAsociadoA(rst.getString(2));
                pc.setIdentificador(rst.getString(1));
                pc.setTipo(rst.getString(4));
                pc.setEstatus(rst.getString(5));
                pc.setFecha(rst.getDate(6));
                pc.setDocumento(rst.getString(7));
                pc.setReferencia(rst.getString(8));
            } else {
                throw new GeDocDAOException("No se localizo la cabecera indicada [" + idCabecera + "].");
            }
            return pc;
        } catch (SQLException e) {
            throw new GeDocDAOException("No se logr� eliminar la cabecera del registro de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(PeriodoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public PeriodoRegistro encuentraRegistroPorId(String idRegistro) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idregper, dsregper, intipreg, dtfecreg, dbimpreg, dbimpues, instatus, dsnotreg, iddocele ");
            sql.append(" FROM jctc10v ");
            sql.append(" WHERE idregper = ? ");
            sql.append(" ORDER BY dtfecreg;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, idRegistro);
            ResultSet rst = stm.executeQuery();
            PeriodoRegistro pr = new PeriodoRegistro();
            if (rst != null) {
                if (rst.next()) {
                    pr.setEstatus(rst.getString(7));
                    DocumentoDAOImpl documento = new DocumentoDAOImpl();
                    pr.setEvidencia(rst.getString(9));
                    pr.setFecha(rst.getDate(4));
                    pr.setImporte(rst.getDouble(5));
                    pr.setImpuesto(rst.getDouble(6));
                    pr.setNota(rst.getString(2));
                    pr.setRegistro(rst.getString(1));
                    pr.setTipo(rst.getString(3));
                    pr.setDescripcion(rst.getString(8));
                }
            }
            return pr;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoRegistro> listaRegistrosPeriodo(Periodo periodo) throws GeDocDAOException {
        List<PeriodoRegistro> registros = new ArrayList<PeriodoRegistro>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idregper, intipreg, dtfecreg, dbimpreg, dbimpues, instatus, dsnotreg, iddocele, dsautori, dsregper ");
            sql.append(" FROM jctc10v ");
            sql.append("WHERE idreggas IN (SELECT idreggas FROM jctm09t WHERE idnumper = ?) ");
            sql.append("ORDER BY dtfecreg;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, periodo.getIdentificador());
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoRegistro pr = new PeriodoRegistro();
                    pr.setEstatus(rst.getString(6));
                    pr.setFecha(rst.getDate(3) != null ? new java.util.Date(rst.getDate(3).getTime()) : new java.util.Date());
                    pr.setImporte(rst.getDouble(4));
                    pr.setImpuesto(rst.getDouble(5));
                    pr.setNota(rst.getString(7));
                    pr.setRegistro(rst.getString(1));
                    pr.setTipo(rst.getString(2));
                    pr.setDescripcion(rst.getString(10));
                    pr.setAutoriza(rst.getString(9));
                    pr.setEvidencia(rst.getString(8));
                    registros.add(pr);
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public CifraControl cierreCifraControl(Periodo periodo) throws GeDocDAOException {
        try {

            conexion = Conexion.getConexion();
            String sp = "cifrasControl";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, periodo.getIdentificador()));
            params.add(new SpParam(2, Types.VARCHAR, null, true)); // manejador de error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 1) {
                String error = vuelta[0] == null ? "" : (String) vuelta[0];
                if (error.isEmpty()) {
                    return getCifraControl(periodo.getIdentificador());
                } else {
                    throw new GeDocDAOException("Fallo al ejecutar el procedimiento. " + error);
                }
            } else {
                throw new GeDocDAOException("Fallo al ejecutar el procedimiento. No se devolvieron los parametros esperados, se esperaba 1 y se encontraron " + (vuelta != null ? vuelta.length : "null"));
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("La ejecuci�n del procedimiento de cifras de control del periodo gener� una excepci�n, revise el log para m�s detalles.", e);
        } catch (NullPointerException e) {
            throw new GeDocDAOException("La ejecuci�n del procedimiento de cifras de control del periodo gener� una excepci�n, al parecer el valor es nulo en el retorno.", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public String cierreCifraControlAjuste(Periodo periodo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "cifrasControlAjuste";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, periodo.getIdentificador()));
            params.add(new SpParam(2, Types.VARCHAR, null, true)); // identificador de ajuste
            params.add(new SpParam(3, Types.VARCHAR, null, true)); // manejador de error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 2) {
                String idAjuste = (vuelta[0] == null ? "" : (String) vuelta[0]);
                String error = vuelta[1] == null ? "" : (String) vuelta[1];

                if (error.isEmpty()) {
                    if (!idAjuste.isEmpty()) {
                        return idAjuste;
                    } else {
                        throw new GeDocDAOException("El procedimiento se ha ejecutado correctamente, pero no se identifico el numero del ajuste.. " + idAjuste);
                    }
                } else {
                    throw new GeDocDAOException("Fallo al ejecutar el procedimiento. " + error);
                }
            } else {
                throw new GeDocDAOException("Fallo al ejecutar el procedimiento. No se devolvieron los parametros esperados, se esperaba 2 y se encontraron " + (vuelta != null ? vuelta.length : "null"));
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("La ejecuci�n del procedimiento de cifras de control de ajuste en el periodo gener� una excepci�n, revise el log para m�s detalles.", e);
        } catch (NullPointerException e) {
            throw new GeDocDAOException("La ejecuci�n del procedimiento de cifras de control de ajuste en el periodo gener� una excepci�n, al parecer el valor es nulo en el retorno.", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoRegistro> listaRegistros(String id) throws GeDocDAOException {
        List<PeriodoRegistro> registros = new ArrayList<PeriodoRegistro>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idregper, intipreg, dtfecreg, dbimpreg, dbimpues, instatus, dsnotreg, iddocele, dsautori, dsregper ");
            sql.append(" FROM jctc10v ");
            sql.append("WHERE idreggas = ? ");
            sql.append("ORDER BY dtfecreg;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoRegistro pr = new PeriodoRegistro();
                    pr.setEstatus(rst.getString(6));
                    DocumentoDAOImpl documento = new DocumentoDAOImpl();
                    pr.setFecha(rst.getDate(3) != null ? new java.util.Date(rst.getDate(3).getTime()) : new java.util.Date());
                    pr.setImporte(rst.getDouble(4));
                    pr.setImpuesto(rst.getDouble(5));
                    pr.setNota(rst.getString(7));
                    pr.setRegistro(rst.getString(1));
                    pr.setTipo(rst.getString(2));
                    pr.setDescripcion(rst.getString(10));
                    pr.setAutoriza(rst.getString(9));
                    pr.setEvidencia(rst.getString(8));
                    registros.add(pr);
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoCifraControl obtieneCifraControl(String id) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT innumreg, dbmonto FROM jctm02t WHERE idnumper = ?;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    PeriodoCifraControl cifraControl = new PeriodoCifraControl();
                    cifraControl.setRegistros(rst.getInt(1));
                    cifraControl.setMonto(rst.getDouble(2));
                    return cifraControl;
                }
            }
            throw new GeDocDAOException("El elemento de cifra control para el periodo no se encuentra registrado.");
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de registros en el periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoCifraControl insertaCifraControl(String id, PeriodoCifraControl cifraControl) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "registraCifraControl";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, id));
            params.add(new SpParam(2, Types.INTEGER, cifraControl.getRegistros()));
            params.add(new SpParam(3, Types.DOUBLE, cifraControl.getMonto()));
            params.add(new SpParam(4, Types.VARCHAR, null, true)); //error
            Object vuelta[] = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 1) {
                if (((String) vuelta[0]).isEmpty()) {
                    return cifraControl;
                } else {
                    throw new GeDocDAOException((String) vuelta[0]);
                }
            } else if (vuelta != null) {
                throw new GeDocDAOException("Los parametros de salida del procedimiento son inconrrectos, se esperaban 1 se tienen " + vuelta.length);
            } else {
                throw new GeDocDAOException("No se obtuevo respuesta desde la base de datos.");
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("La actualizacion de cifras control genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoCifraControl actualizaCifraControl(String id, PeriodoCifraControl cifraControl) throws GeDocDAOException {
        return insertaCifraControl(id, cifraControl);
    }

    @Override
    public List<PeriodoRegistro> insertaRegistros(String id, List<PeriodoRegistro> periodoRegistros) throws GeDocDAOException {
        for (PeriodoRegistro periodoRegistro : periodoRegistros) {
            insertaRegistro(id, periodoRegistro);
        }
        return periodoRegistros;
    }

    @Override
    public PeriodoRegistro insertaRegistro(String id, PeriodoRegistro periodoRegistro) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "insertaRegistro";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, id));
            params.add(new SpParam(2, Types.VARCHAR, periodoRegistro.getDescripcion()));
            params.add(new SpParam(3, Types.VARCHAR, periodoRegistro.getTipo()));
            params.add(new SpParam(4, Types.DATE, new java.sql.Date(periodoRegistro.getFecha().getTime())));
            params.add(new SpParam(5, Types.DOUBLE, periodoRegistro.getImporte()));
            params.add(new SpParam(6, Types.DOUBLE, periodoRegistro.getImpuesto()));
            params.add(new SpParam(7, Types.VARCHAR, periodoRegistro.getNota()));
            params.add(new SpParam(8, Types.VARCHAR, periodoRegistro.getAutoriza()));
            params.add(new SpParam(9, Types.VARCHAR, null, true)); // identificador de registro
            params.add(new SpParam(10, Types.VARCHAR, null, true)); // Error
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 2) {
                if (vuelta[1] != null && !((String) vuelta[1]).isEmpty()) {
                    throw new GeDocDAOException((String) vuelta[1]);
                } else {
                    periodoRegistro.setRegistro((String) vuelta[0]);
                    if (periodoRegistro.getEvidencia() != null) {
                        //registra la evidencia
                        params = new SpParams();
                        params.add(new SpParam(1, Types.VARCHAR, id));
                        params.add(new SpParam(2, Types.VARCHAR, periodoRegistro.getRegistro()));
                        params.add(new SpParam(3, Types.VARCHAR, periodoRegistro.getEvidencia()));
                        params.add(new SpParam(4, Types.VARCHAR, null, true)); // identificador de registro
                        params.add(new SpParam(5, Types.VARCHAR, null, true)); // Error
                        vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, "insertaEvidencia", params);
                        if (vuelta != null && vuelta.length == 2) {
                            if (vuelta[1] != null && !((String) vuelta[1]).isEmpty()) {
                                throw new GeDocDAOException((String) vuelta[1]);
                            }
                        }
                    }
                    return periodoRegistro;
                }
            } else if (vuelta != null) {
                throw new GeDocDAOException("El procedimiento de registros no devolvio los parametros esperados, se peraban 2 y se tienen " + vuelta.length);
            } else {
                throw new GeDocDAOException("El procedimiento de registros no devolvio resultados");
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("El procedimiento de registros devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void eliminaRegistro(PeriodoRegistro periodoRegistro) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sp = "eliminaRegistro";
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, periodoRegistro.getRegistro()));
            params.add(new SpParam(2, Types.VARCHAR, null, true));
            Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
            if (vuelta != null && vuelta.length == 1) {
                System.out.println("=====> Se ha eliminado el registro " + periodoRegistro.getRegistro() + " correctamente.");
            } else {
                throw new GeDocDAOException("El procedimiento de registros no devolvio los parametros esperados, se peraban 1 y se tienen " + (vuelta != null ? vuelta.length : "null"));
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("El procedimiento de registros devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<String> listaAsignados() throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            List<String> listado = new ArrayList<String>();
            String sql = "SELECT dsasocia FROM jctm09t ORDER BY dsasocia;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    listado.add(rst.getString(1));
                }
            }
            return listado;
        } catch (SQLException e) {
            throw new GeDocDAOException("El procedimiento de registros devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Map<String, String> listadoTipoComprobante(String tipoGasto) throws GeDocDAOException {
        Map<String, String> listado = TipoComprobante.listaTipoComprobante(tipoGasto);
        return listado;
    }

    @Override
    public List<PeriodoRegistro> pendientesAprobacion(String idCabecera) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            List<PeriodoRegistro> listado = new ArrayList<PeriodoRegistro>();
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(" idreggas,idregper,intipreg,dtfecreg,dbimpreg,dbimpues,dsautori,instatus,dsnotreg,dsfiles ");
            sql.append(" FROM jctc10v ");
            sql.append(" WHERE dsautori = '';");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoRegistro pr = new PeriodoRegistro();
                    pr.setAutoriza("");
                    pr.setDescripcion(rst.getString(9));
                    pr.setEstatus("8");
                    pr.setFecha(new java.util.Date(rst.getDate(4).getTime()));
                    pr.setImporte(rst.getDouble(5));
                    pr.setImpuesto(rst.getDouble(6));
                    pr.setNota(rst.getString(9));
                    pr.setRegistro(rst.getString(1));
                    pr.setTipo(rst.getString(3));
                    pr.setEvidencia(rst.getString(10));
                    listado.add(pr);
                }
            }
            return listado;

        } catch (SQLException e) {
            throw new GeDocDAOException("El procedimiento de registros devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoRegistro actualizaTipoComprobanteRegistro(PeriodoRegistro periodoRegistro, String tipoComprobante) throws GeDocDAOException {
        try {
            if (periodoRegistro != null) {
                conexion = Conexion.getConexion();
                StringBuilder sql = new StringBuilder("UPDATE jctm10t SET ");
                sql.append(" intipreg = ?");
                sql.append(" WHERE idregper = ? AND instatus = 'A';");
                PreparedStatement stm = conexion.prepareStatement(sql.toString());
                stm.setString(1, tipoComprobante);
                stm.setString(2, periodoRegistro.getRegistro());
                int x = stm.executeUpdate();
                if (x > 0) {
                    periodoRegistro.setTipo(tipoComprobante);
                }
            }
            return periodoRegistro;
        } catch (SQLException e) {
            throw new GeDocDAOException("La actualizaci�n del registro devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoRegistro actualizaAutorizaRegistro(PeriodoRegistro periodoRegistro, String autoriza) throws GeDocDAOException {
        try {
            if (periodoRegistro != null) {
                conexion = Conexion.getConexion();
                StringBuilder sql = new StringBuilder("UPDATE jctm10t SET ");
                sql.append(" dsautori = ?, instatus = 'A' ");
                sql.append(" WHERE idregper = ? AND instatus = 'P';");
                PreparedStatement stm = conexion.prepareStatement(sql.toString());
                stm.setString(1, autoriza);
                stm.setString(2, periodoRegistro.getRegistro());
                System.out.println("Ready to update Autoriza in records: " + stm.toString());
                int x = stm.executeUpdate();
                if (x > 0) {
                    periodoRegistro.setAutoriza(autoriza);
                }
            }
            return periodoRegistro;
        } catch (SQLException e) {
            throw new GeDocDAOException("La actualizaci�n del registro devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoRegistro actualizaEstadoRegistro(PeriodoRegistro periodoRegistro, String estado) throws GeDocDAOException {
        try {
            if (periodoRegistro != null) {
                conexion = Conexion.getConexion();
                StringBuilder sql = new StringBuilder("UPDATE jctm10t SET ");
                sql.append(" instatus = ?");
                sql.append(" WHERE idregper = ? AND instatus = 'A';");
                PreparedStatement stm = conexion.prepareStatement(sql.toString());
                stm.setString(1, estado);
                stm.setString(2, periodoRegistro.getRegistro());
                int x = stm.executeUpdate();
                if (x > 0) {
                    periodoRegistro.setEstatus(estado);
                }
            }
            return periodoRegistro;
        } catch (SQLException e) {
            throw new GeDocDAOException("La actualizaci�n del registro devolvio una excepcion.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public PeriodoCabecera cierraCabecera(String idCabecera) throws GeDocDAOException {
        try {
            PeriodoCabecera cabecera = encuentraCabeceraPorId(idCabecera);
            if (cabecera != null) {
                conexion = Conexion.getConexion();
                String sp = "cierraRegistroCabecera";
                SpParams params = new SpParams();
                params.add(new SpParam(1, Types.VARCHAR, cabecera.getIdentificador()));
                params.add(new SpParam(2, Types.VARCHAR, null, true)); //nuevo estatus
                params.add(new SpParam(3, Types.VARCHAR, null, true)); //error en caso de que ocurra
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
                if (vuelta != null && vuelta.length == 2) {
                    if (vuelta[1].toString().isEmpty()) {
                        cabecera.setEstatus((String) vuelta[0]);
                    } else {
                        throw new GeDocDAOException((String) vuelta[1]);
                    }
                } else {
                    throw new GeDocDAOException("El llamado al procedimiento genero una respuesta no esperada, se esperaban 2 y se tienen " + (vuelta != null ? vuelta.length : "null"));
                }
                return cabecera;
            } else {
                throw new GeDocDAOException("El registro de control de gasto no existe, verifique " + idCabecera);
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("Ocurrio un error al actualizar el estado del registro de control de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }

        }
    }

    @Override
    public PeriodoCabecera cierraCabeceraAjuste(String idCabecera) throws GeDocDAOException {
        try {
            PeriodoCabecera cabecera = encuentraCabeceraPorId(idCabecera);
            if (cabecera != null) {
                conexion = Conexion.getConexion();
                String sp = "cierraRegistroCabeceraAjuste";
                SpParams params = new SpParams();
                params.add(new SpParam(1, Types.VARCHAR, cabecera.getIdentificador()));
                params.add(new SpParam(2, Types.VARCHAR, null, true)); //nuevo estatus
                params.add(new SpParam(3, Types.VARCHAR, null, true)); //error en caso de que ocurra
                Object[] vuelta = Conexion.ejecutaStoreProcedureConSalida(conexion, sp, params);
                if (vuelta != null && vuelta.length == 2) {
                    if (vuelta[1].toString().isEmpty()) {
                        cabecera.setEstatus((String) vuelta[0]);
                    } else {
                        throw new GeDocDAOException((String) vuelta[1]);
                    }
                } else {
                    throw new GeDocDAOException("El llamado al procedimiento genero una respuesta no esperada, se esperaban 2 y se tienen " + (vuelta != null ? vuelta.length : "null"));
                }
                return cabecera;
            } else {
                throw new GeDocDAOException("El registro de control de gasto no existe, verifique " + idCabecera);
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("Ocurrio un error al actualizar el estado del registro de control de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }

        }
    }

    @Override
    public PeriodoCabecera encuentraPorRegisro(String idRegistro) throws GeDocDAOException {
        try {
            PeriodoRegistro registro = encuentraRegistroPorId(idRegistro);
            if (registro != null) {
                conexion = Conexion.getConexion();
                String sql = "SELECT idreggas, dsasocia, idnumper, intipgas, instatus FROM jctm09t WHERE idreggas = (SELECT idreggas FROM jctm10t WHERE idregper = ?);";
                PreparedStatement stm = conexion.prepareStatement(sql);
                stm.setString(1, idRegistro);
                ResultSet rst = stm.executeQuery();
                PeriodoCabecera cabecera = null;
                if (rst.next()) {
                    cabecera = new PeriodoCabecera();
                    cabecera.setAsociadoA(rst.getString(2));
                    cabecera.setEstatus(rst.getString(5));
                    cabecera.setIdentificador(rst.getString(1));
                    cabecera.setTipo(rst.getString(4));
                }
                return cabecera;
            } else {
                throw new GeDocDAOException("El registro con el detalle de control de gasto no existe, verifique " + idRegistro);
            }
        } catch (SQLException e) {
            throw new GeDocDAOException("Ocurrio un error al obtener el registro de control de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }

        }
    }

    @Override
    public CifraControl getCifraControl(String idPeriodo) throws GeDocDAOException {
        try {
            CifraControl cifraControl = new CifraControl();
            if (conexion != null && conexion.isClosed()) {
                conexion = Conexion.getConexion();
            }
            String sql = "SELECT idnumper, dbmonto, inregaso, inregtip, dbmaxaso, dbminaso, dbmaxtip, dbmintip, ajustes "
                    + " FROM jctm02t A LEFT JOIN ("
                    + "     SELECT COUNT(idajuste) ajustes, idnumper periodo FROM jctm03t GROUP BY idnumper "
                    + " ) B ON A.idnumper = B.periodo "
                    + " WHERE idnumper = ?;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            stm.setString(1, idPeriodo);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                cifraControl.setMonto(rst.getDouble(2));
                cifraControl.setRegistrosAsociado(rst.getInt(3));
                cifraControl.setRegistrosTipo(rst.getInt(4));
                cifraControl.setMaxAsociado(rst.getDouble(5));
                cifraControl.setMinAsociado(rst.getDouble(6));
                cifraControl.setMaxTipo(rst.getDouble(7));
                cifraControl.setMinTipo(rst.getDouble(8));
                cifraControl.setAjustes(rst.getInt(9));
            }
            return cifraControl;
        } catch (SQLException e) {
            throw new GeDocDAOException("Ocurrio un error al obtener las cifras control del registro de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public List<CifraControlAjuste> getCifraControlAjuste(String idPeriodo) throws GeDocDAOException {
        try {
            List<CifraControlAjuste> ajustes = new ArrayList<CifraControlAjuste>();
            if (conexion != null && conexion.isClosed()) {
                conexion = Conexion.getConexion();
            }
            String sql = "SELECT A.idajuste, B.dbmonto, B.inregaso, B.inregtip, B.dbmaxaso, B.dbminaso, B.dbmaxtip, B.dbmintip, A.fefecaju "
                    + " FROM jctm03t A INNER JOIN jctm3ct B "
                    + " ON A.idajuste = B.idajuste "
                    + " WHERE idnumper = ?;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            stm.setString(1, idPeriodo);
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                CifraControlAjuste cifraControl = new CifraControlAjuste();
                cifraControl.setAjuste(rst.getString(1));
                cifraControl.setMonto(rst.getDouble(2));
                cifraControl.setRegistrosAsociado(rst.getInt(3));
                cifraControl.setRegistrosTipo(rst.getInt(4));
                cifraControl.setMaxAsociado(rst.getDouble(5));
                cifraControl.setMinAsociado(rst.getDouble(6));
                cifraControl.setMaxTipo(rst.getDouble(7));
                cifraControl.setMinTipo(rst.getDouble(8));
                cifraControl.setFechaAjuste(rst.getDate(9));
                cifraControl.setAjustes(0);
                ajustes.add(cifraControl);
            }
            return ajustes;
        } catch (SQLException e) {
            throw new GeDocDAOException("Ocurrio un error al obtener el registro de cifras control del ajuste de gasto.", e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public Periodo encuentraPorFecha(String valor) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT idnumper, inanyper, innumper, dtfecape, dtfeccie, instatus, dscoment ");
            sql.append("FROM jctm01t ");
            sql.append(" WHERE inanyper = ? AND innumper = ? ");
            sql.append("ORDER BY inanyper DESC, innumper DESC;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            String anyper = valor.substring(0, 4);
            String numper = valor.substring(4);
            stm.setInt(1, Integer.valueOf(anyper));
            stm.setInt(2, Integer.valueOf(numper));
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setAny(rst.getInt(2));
                    periodo.setApertura(rst.getDate(4));
                    periodo.setCierre(rst.getDate(5) != null ? rst.getDate(5) : null);
                    periodo.setComentario(rst.getString(7));
                    periodo.setEstatus(rst.getString(6));
                    periodo.setIdentificador(rst.getString(1));
                    periodo.setPeriodo(rst.getInt(3));
                    return periodo;
                }
            }
            return new Periodo();
        } catch (NumberFormatException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, el valor de periodo no es valido " + valor, e);
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, revise el log para m�s detalles.", e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Periodo getPeriodo(int any, int mes) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT idnumper, inanyper, innumper, dtfecape, dtfeccie, instatus, dscoment FROM jctm01t WHERE inanyper = ? AND innumper = ?;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            stm.setInt(1, any);
            stm.setInt(2, mes);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setIdentificador(rst.getString(1));
                    periodo.setAny(any);
                    periodo.setPeriodo(mes);
                    periodo.setApertura(rst.getDate(4));
                    periodo.setCierre(rst.getDate(5));
                    periodo.setEstatus(rst.getString(6));
                    periodo.setComentario(rst.getString(7));
                    return periodo;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de datos de periodo genero una excepci�n, el valor de periodo no es valido " + String.valueOf(any) + String.valueOf(mes), e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoCabecera> getCabecerasAgrupadasPorAsociado(String idPeriodo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT dsasocia, SUM(importe) "
                    + " FROM jctm09t A LEFT JOIN ( "
                    + "     SELECT idreggas, dbimpreg importe FROM jctm10t "
                    + " ) B ON A.idreggas = B.idreggas "
                    + " WHERE idnumper = ? "
                    + " GROUP BY dsasocia;";
            PreparedStatement stm = conexion.prepareStatement(sql);
            stm.setString(1, idPeriodo);
            System.out.println("Consulta de cabeceras: " + stm.toString());
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                List<PeriodoCabecera> cabeceras = new ArrayList<PeriodoCabecera>();
                while (rst.next()) {
                    PeriodoCabecera pc = new PeriodoCabecera();
                    pc.setAsociadoA(rst.getString(1));
                    pc.setImporte(rst.getDouble(2));
                    cabeceras.add(pc);
                }
                return cabeceras;
            }
            return null;
        } catch (SQLException e) {
            throw new GeDocDAOException("La consulta de cabeceras en el periodo especificado genero una execepci�n " + idPeriodo + ", " + e.getSQLState(), e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public List<PeriodoCabecera> getCabecerasPorAsociado(String idPeriodo, String asociado) throws GeDocDAOException {
        List<PeriodoCabecera> cabeceras = new ArrayList<PeriodoCabecera>();
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT A.idreggas, dsasocia, idnumper, A.intipgas, A.instatus, fefecreg, dsdocto, dsrefdoc, total, cuenta ");
            sql.append("  FROM jctm09t A LEFT JOIN (");
            sql.append("    SELECT idreggas, IFNULL(SUM(dbimpreg), 0) AS total, IFNULL(COUNT(idreggas), 0) cuenta FROM jctc10v GROUP BY idreggas ");
            sql.append("  ) C ON A.idreggas = C.idreggas ");
            sql.append(" WHERE (idnumper = ? AND dsasocia LIKE(?));");

            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, idPeriodo);
            stm.setString(2, "%".concat(asociado).concat("%"));
            System.out.println("Consulta de cabeceras por asociado: " + stm.toString());
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                while (rst.next()) {
                    PeriodoCabecera pc = new PeriodoCabecera();
                    pc.setIdentificador(rst.getString(1));
                    pc.setAsociadoA(rst.getString(2));
                    pc.setTipo(rst.getString(4));
                    pc.setEstatus(rst.getString(5));
                    pc.setFecha(rst.getDate(6));
                    pc.setDocumento(rst.getString(7));
                    pc.setReferencia(rst.getString(8));
                    pc.setRegistros(new ArrayList<PeriodoRegistro>());
                    pc.setImporte(rst.getDouble(9));
                    pc.setCuentaFueraPeriodo(rst.getInt(10));
                    cabeceras.add(pc);
                }
            }
            return cabeceras;
        } catch (SQLException e) {
            System.out.println("Consulta de cabeceras de gasto: " + e.getMessage() + " -> " + e.getCause());
            throw new GeDocDAOException("Excepci�n al consultar registros de cabecera de gastos en el periodo: " + idPeriodo + ", " + asociado + " - " +e.getSQLState(), e);
        } finally {
            try {
                if (conexion != null & !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
            }
        }
    }

}
