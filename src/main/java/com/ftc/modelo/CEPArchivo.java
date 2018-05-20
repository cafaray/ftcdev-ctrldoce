package com.ftc.modelo;

import java.util.Date;

public class CEPArchivo {

    private String identificador;
    private String persona;
    private String titulo;
    private String observaciones;
    private String archivos;
    private String uuid;
    private Date fecha;
    private String estatus;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getPersona(){
        return this.persona;                
    }
    
    public void setPersona(String persona){
        this.persona = persona;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getArchivos() {
        return archivos;
    }

    public void setArchivos(String archivos) {
        this.archivos = archivos;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    
    @Override
    public String toString() {
        return "CEPArchivo{" + "identificador=" + identificador + ", titulo=" + titulo + ", observaciones=" + observaciones + ", archivos=" + archivos + '}';
    }
        
}
