package com.ftc.gedoc.bo;

import com.ftc.gedoc.exceptions.GeDocBOException;
import com.ftc.modelo.Notificacion;
import java.util.List;

public interface NotificacionBO {
    
    /***
     * registra una nueva notificacion dentro del área de persistencia de datos
     * @param notificacion Objeto que será persistido
     * @return Notificacion Objeto con el identificador que se asigno
     * @throws GeDocBOException 
     */
    Notificacion registrar(Notificacion notificacion) throws GeDocBOException;
    
    /***
     * cancela una notificacion a traves de su codigo de identificacion dentro del campo de persistencia
     * @param notificacion Objeto de notificacion que se cancelara
     * @return Notificacion con el nuevo estatus de cancelacion
     * @throws GeDocBOException 
     */
    Notificacion cancelarNotificacion(Notificacion notificacion) throws GeDocBOException;
    
    /***
     * cancela varias notificaciones a la vez, se hace una iteracion sobre el listado de notificaciones enviadas y se va llamando a cancelaNotificacion
     * @param notificaciones Listado con las notificaciones a cancelar
     * @return valor entero que indica cuantas notificaciones se cancelaron con exito
     * @throws GeDocBOException 
     */
    int cancelarNotificaciones(List<Notificacion> notificaciones) throws GeDocBOException;
    
    /***
     * listado de todas las notificaciones con estatus de pendiente dentro del area de persistencia 
     * @param  empresa Empresa de la que se recuperaran las notificaciones
     * @return java.util.List<Notificacion> 
     * @throws GeDocBOException 
     */
    List<Notificacion> notificaciones(String empresa) throws GeDocBOException;
    
    /***
     * listado de todas las notificaciones con estatus de pendiente dentro del �rea de persistencia por tipo de notificacion
     * @param  empresa Empresa de la que se recuperaran las notificaciones
     * @param estatus Se filtra por el estatus que se pide en el listado
     * @return
     * @throws GeDocBOException 
     */
    List<Notificacion> notificaciones(String empresa, String estatus) throws GeDocBOException;
    
}
