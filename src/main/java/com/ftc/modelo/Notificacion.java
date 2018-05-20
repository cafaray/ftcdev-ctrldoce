package com.ftc.modelo;

import java.sql.Timestamp;
import java.util.Date;

public class Notificacion {
    
    private String persona;
    private String documentoElectronico;
    private int identificador;
    private String mensaje;
    private Date fecha;
    private String estatus;
    private String conCopia;
    private Timestamp fechaRegistro;
    
    public Notificacion(){}

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getDocumentoElectronico() {
        return documentoElectronico;
    }

    public void setDocumentoElectronico(String documentoElectronico) {
        this.documentoElectronico = documentoElectronico;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getConCopia() {
        return conCopia;
    }

    public void setConCopia(String conCopia) {
        this.conCopia = conCopia;
    }
   public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Override
    public boolean equals(Object object){
        if(object instanceof Notificacion){
            Notificacion obj = (Notificacion)object;
            return (obj.getPersona().equals(this.persona) 
            && obj.getDocumentoElectronico().equals(this.getDocumentoElectronico())
            && obj.getIdentificador()==this.getIdentificador());
        }else{
            return false;
        }
    } 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.persona != null ? this.persona.hashCode() : 0);
        hash = 11 * hash + (this.documentoElectronico != null ? this.documentoElectronico.hashCode() : 0);
        hash = 11 * hash + this.identificador;
        return hash;
    }

 
}
