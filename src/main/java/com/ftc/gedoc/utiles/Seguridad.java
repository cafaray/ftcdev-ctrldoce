package com.ftc.gedoc.utiles;

import com.ftc.aq.Comunes;
import com.ftc.aq.Conexion;
import com.ftc.aq.SpParam;
import com.ftc.aq.SpParams;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public final class Seguridad implements Serializable{

    private int seguridad = 0;
    private String cadena = "";
    private String proveedor = "";
    private String cliente = "";
    private String gasto = "";
    private String admin = "";

    /*
     * (r)registro             (R)registro              (t)viaticos vendedores          (u)usuarios
     * (c)contacto             (C)contacto              (h)caja chica                   (*)cambiar passwd
     * (v)ver documento        (V)ver documento         (a)aduana                       (-)suspender/eliminar usuarios
     * (s)subir documento      (S)subir documento       (s)administrador                (+)activar usuarios
     * (n)notificacion         (N)notificacion                                          (g)Gestor de grupos 
     * (e)estado               (E)estado
     * (x)eliminar archivo     (X)eliminar archivo                  
     * (d)descargar doc        (D)descargar doc
     * 11001000110010001010000
     */

    public Seguridad(int seguridad) {
        this.seguridad = seguridad;
        this.cadena = Integer.toBinaryString(seguridad);
        try {
            cadena = Comunes.rellenaCeros(cadena, 25);
            proveedor = cadena.substring(0, 8);
            cliente = cadena.substring(8, 16);
            gasto = cadena.substring(16, 20);
            admin = cadena.substring(20);
        } catch (Exception e) {
            cadena = "";
            e.printStackTrace(System.out);
        }
    }

    public final String seguridadGastos() {
        char[] elemens = gasto.toCharArray();
        StringBuilder elemento = new StringBuilder("") ;
        elemento.append(elemens[0]=='1'?"t":"");
        elemento.append(elemens[1]=='1'?"h":"");
        elemento.append(elemens[2]=='1'?"a":"");
        elemento.append(elemens[3]=='1'?"s":"");
        return elemento.toString();        
    }
    
    public final String seguridadProveedor(){
        char[] elemens = proveedor.toCharArray();
        StringBuilder elemento = new StringBuilder("") ;
        elemento.append(elemens[0]=='1'?"r":"");
        elemento.append(elemens[1]=='1'?"c":"");
        elemento.append(elemens[2]=='1'?"v":"");
        elemento.append(elemens[3]=='1'?"s":"");
        elemento.append(elemens[4]=='1'?"n":"");
        elemento.append(elemens[5]=='1'?"e":"");
        elemento.append(elemens[6]=='1'?"x":"");
        elemento.append(elemens[7]=='1'?"d":"");
        return elemento.toString();
    }

    public final String seguridadCliente(){
        char[] elemens = cliente.toCharArray();
        StringBuilder elemento = new StringBuilder("") ;
        elemento.append(elemens[0]=='1'?"r":"");
        elemento.append(elemens[1]=='1'?"c":"");
        elemento.append(elemens[2]=='1'?"v":"");
        elemento.append(elemens[3]=='1'?"s":"");
        elemento.append(elemens[4]=='1'?"n":"");
        elemento.append(elemens[5]=='1'?"e":"");
        elemento.append(elemens[6]=='1'?"x":"");
        elemento.append(elemens[7]=='1'?"d":"");
        return elemento.toString();
    }

    public final String seguridadAdmin(){
        char[] elemens = admin.toCharArray();
        StringBuilder elemento = new StringBuilder("") ;
        elemento.append(elemens[0]=='1'?"u":"");
        elemento.append(elemens[1]=='1'?"*":"");
        elemento.append(elemens[2]=='1'?"-":"");
        elemento.append(elemens[3]=='1'?"+":"");
        elemento.append(elemens[4]=='1'?"g":"");
        return elemento.toString();
    }

    public final boolean esProveedor(){
        return proveedor.contains("1");
    }

    public final boolean esCliente(){
        return cliente.contains("1");
    }

    public final boolean esAdmin(){
        return admin.contains("1");
    }
    
    public final boolean esGastos(){
        return gasto.contains("1");
    }

    public static Map<String,String> listaGrupos(Connection conexion, String sesion)throws SQLException{
        try{
            if(conexion==null || conexion.isClosed()){
                conexion = Conexion.getConexion();
            }
            Map<String, String> listado = new HashMap<String, String>();
            SpParams params = new SpParams();
            params.add(new SpParam(1, Types.VARCHAR, sesion));
            ResultSet rst = Conexion.consultaStoreProcedure(conexion, "listaGrupos", params);
            while(rst.next()){
                listado.put(rst.getString(1), rst.getString(2));
            }
            return listado;
        }finally{
            if(conexion!=null || !conexion.isClosed()){
                conexion.close();
            }            
        }
    }

}
