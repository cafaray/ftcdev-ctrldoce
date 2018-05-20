package com.ftc.modelo;

import java.util.Date;

public class Documento {

    private String identificador;
    private String titulo;
    private String observaciones;
    private String archivos;
    private String persona;
    private String empresa;
    private Date fecha;
    private String estatus;

    public Documento() {
    }

    public Documento(String titulo, String observaciones, String archivos, String persona) {
        this.titulo = titulo;
        this.observaciones = observaciones;
        this.archivos = archivos;
        this.persona = persona;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
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

    public String[] archivos() {
        String[] tmp = archivos.split(",");
        return tmp;
    }

    public void setArchivos(String archivos) {
        this.archivos = archivos;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setEstatus(String estatus){
        this.estatus = estatus;
    }

    public String getEstatus() {
        return this.estatus;
    }

    public String getDsEstatus() {
        return DocumentoEstatus.Estatus.getEstatus(this.estatus.charAt(0)).getDescripcion();
    }

}
