package com.ftc.gedoc.utiles;

import com.ftc.gedoc.exceptions.GeDocDAOException;
import java.util.Map;
import java.util.TreeMap;

public class TipoComprobante {
    
    private String tipoGasto;
    private String codigo;
    private String descripcion;
    
    private final static String[][] VENDEDORES = {{"VH","Hospedaje"}, {"VA","Alimentos"}, {"VP","Peajes"}, {"VN","No deducibles"}, {"VT","Transporte local"},{"VD","Diversos"}};
    private final static String[][] CAJA = {{"CF","Fletes"}, {"CP","Papelería"}, {"CQ","Pquetería"}, {"CS","Despensa"},
        {"CN","No deducibles"},{"CM","Manto bodega"},{"CT","Manto eq transp"},{"CA","Alimentos"},{"CH","Hospedaje"},{"CJ","Peaje"},{"CD","Diversos"}};
    private final static String[][] AGENTES = {{"AH","Honorarios"}, {"AA","Almacenaje"}, {"AM","Maniobras"}, {"AF","Fumigaciones"}, 
        {"AC","Fletes y casetas"},{"AT","DTA"},{"AP","PRV"},{"AV","Reconocimiento previo"},{"AS","Servicios complementarios"},
        {"AE","Procesamiento electrónico"},{"AD","Otros gastos"}};
    
    private TipoComprobante() {}
    
    public final static Map<String, String> listaTipoComprobante(String tipoGasto) throws GeDocDAOException{
        Map<String, String> mapa = new TreeMap<String, String>();
        if (tipoGasto.equals("h")){
            for(String[] elemento:CAJA){
                mapa.put(elemento[0], elemento[1]);
            }
            return mapa;
        } else if(tipoGasto.equals("t")){
            for(String[] elemento:VENDEDORES){
                mapa.put(elemento[0], elemento[1]);
            }
            return mapa;
        } else if(tipoGasto.equals("a")){
            for(String[] elemento:AGENTES){
                mapa.put(elemento[0], elemento[1]);
            }
            return mapa;
        } else {
            throw new GeDocDAOException(String.format("El tipo de gasto %s no se identifico, por lo que no hay listado.", tipoGasto));
        }
    }
    
    public final static String getDescripcion(String codigo){
        for(String[] tipo:VENDEDORES){
            if(tipo[0].equals(codigo)){
                return tipo[1];
            }
        }
        for(String[] tipo:CAJA){
            if(tipo[0].equals(codigo)){
                return tipo[1];
            }
        }
        for(String[] tipo:AGENTES){
            if(tipo[0].equals(codigo)){
                return tipo[1];
            }
        }
        return "";
    }
    
    public String getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescripcion(){
        return this.descripcion;
    }
    
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
