package com.ftc.gedoc.dao.impl;

import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import com.ftc.gedoc.exceptions.GeDocDAOException;
import com.ftc.modelo.Grupo;
import com.ftc.gedoc.dao.GrupoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAOImpl implements GrupoDAO {

    protected Connection conexion;

    @Override
    public List<Grupo> listado() throws GeDocDAOException {
        try {
            List<Grupo> grupos = new ArrayList<Grupo>();
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdidegrp,dsidegrp,ingrpmod FROM jgrm01t ORDER BY dsidegrp;");
            ResultSet rst = Conexion.ejecutaConsulta(sql.toString(), conexion);
            if (rst != null) {
                while (rst.next()) {
                    Grupo grupo = new Grupo();
                    grupo.setIdentificador(rst.getString(1));
                    grupo.setGrupo(rst.getString(2));
                    grupo.setModo(rst.getLong(3));
                    grupos.add(grupo);
                }
            }
            return grupos;
        } catch (SQLException e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } catch (Exception e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    @Override
    public Grupo registrar(Grupo grupo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, grupo.getGrupo()));
            params.add(new SpParam(2, Types.VARCHAR, null, true));
            params.add(new SpParam(3, Types.VARCHAR, null, true));
            Object[] registro = Conexion.ejecutaStoreProcedureConSalida(conexion, "registraGrupo", params);
            if (registro != null && registro.length == 2) {
                if (registro[1] != null && !((String) registro[1]).isEmpty()) {
                    grupo.setIdentificador(null);
                    throw new GeDocDAOException((String) registro[1]);
                } else if (registro[0] != null && !((String) registro[0]).isEmpty()) {
                    grupo.setIdentificador((String) registro[0]);
                }
            } else {
                throw new GeDocDAOException("Imposible registrar el grupo, el procedimiento no coincide con la definicion de parametros.");
            }
            return grupo;
        } catch (SQLException e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    @Override
    public Grupo actualizaPermisosPorId(String id, long permisos) throws GeDocDAOException {
        Grupo grupo = encuentraPorId(id);
        if (grupo != null) {
            grupo.setModo(permisos);
            actualizar(grupo);
            return grupo;
        } else {
            throw new GeDocDAOException("No se localizo un grupo con este identificador");
        }
    }

    @Override
    public Grupo actualizar(Grupo grupo) throws GeDocDAOException {
        try {
            if (grupo != null && grupo.getIdentificador().length() > 1) {
                conexion = Conexion.getConexion();
                StringBuilder sql = new StringBuilder();
                SpParams params = new SpParams();
                params.add(new SpParam(1, Types.VARCHAR, grupo.getIdentificador()));
                params.add(new SpParam(2, Types.VARCHAR, grupo.getGrupo()));
                params.add(new SpParam(3, Types.VARCHAR, grupo.getModo()));
                params.add(new SpParam(4, Types.VARCHAR, null, true));
                Object[] registro = Conexion.ejecutaStoreProcedureConSalida(conexion, "actualizaGrupo", params);
                if (registro != null && registro.length == 1) {
                    if (registro[0] != null && !((String) registro[0]).isEmpty()) {
                        throw new GeDocDAOException((String) registro[0]);
                    }
                } else {
                    throw new GeDocDAOException("Imposible actualizar el grupo, el procedimiento no coincide con la definicion de parametros.");
                }

                return grupo;
            } else {
                throw new GeDocDAOException("Imposible actualizar un grupo predefinido.");
            }
        } catch (SQLException e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    @Override
    public void eliminar(Grupo grupo) throws GeDocDAOException {
        try {
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, grupo.getIdentificador()));
            params.add(new SpParam(2, Types.VARCHAR, null, true));
            Object[] registro = Conexion.ejecutaStoreProcedureConSalida(conexion, "eliminaGrupo", params);
            if (registro != null && registro.length == 1) {
                if (registro[0] != null && !((String) registro[0]).isEmpty()) {
                    throw new GeDocDAOException((String) registro[0]);
                }
            } else {
                throw new GeDocDAOException("Imposible eliminar el grupo, el procedimiento no coincide con la definicion de parametros.");
            }
        } catch (SQLException e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    @Override
    public void eliminarPorId(String id) throws GeDocDAOException {
        Grupo grupo = encuentraPorId(id);
        if (grupo != null) {
            eliminar(grupo);
        } else {
            throw new GeDocDAOException("No se localizo un grupo con este identificador");
        }
    }

    @Override
    public Grupo encuentraPorId(String id) throws GeDocDAOException {
        try {
            Grupo grupo = null;
            conexion = Conexion.getConexion();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cdidegrp,dsidegrp,ingrpmod FROM jgrm01t WHERE cdidegrp = ?;");
            PreparedStatement stm = conexion.prepareStatement(sql.toString());
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            if (rst != null) {
                if (rst.next()) {
                    grupo = new Grupo();
                    grupo.setIdentificador(rst.getString(1));
                    grupo.setGrupo(rst.getString(2));
                    grupo.setModo(rst.getLong(3));
                } else {
                    throw new GeDocDAOException("No existe un grupo con ese identificador");
                }
            }
            return grupo;
        } catch (SQLException e) {
            throw new GeDocDAOException(e.getMessage(), e);
        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

}
