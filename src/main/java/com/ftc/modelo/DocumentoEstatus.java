package com.ftc.modelo;

import java.util.Date;

public class DocumentoEstatus {
    private String persona;
    private String documentoElectronico;
    private int identificador;
    private Estatus estatus;
    private Date fecha;
    private String sesion;
    private String comentario;
    private String usuario;
    
    public DocumentoEstatus(){}

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

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSesion() {
        return sesion;
    }

    public void setSesion(String sesion) {
        this.sesion = sesion;
    }

    public String getComentario() {
        return comentario;
    }

    public String getUsuario(){
        return this.usuario;
    }
    
    public void setUsuario(String usuario){
        this.usuario = usuario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
            
    public enum Estatus{
        ESTATUS_DOCUMENTO_CARGA("L", "Carga"),
        ESTATUS_DOCUMENTO_DESCARGA("D", "Descarga"),
        ESTATUS_DOCUMENTO_REVISION("C", "Revisión"),
        ESTATUS_DOCUMENTO_APROBADA("V", "Aprobada"),
        ESTATUS_DOCUMENTO_RECHAZADA("R", "Rechazada"),
        ESTATUS_DOCUMENTO_PROGRAMADA("T", "Programada para pago"),
        ESTATUS_DOCUMENTO_PAGADA("P", "Pagada"),
        ESTATUS_DOCUMENTO_CANCELADA("X", "Cancelada"),
        ESTATUS_DOCUMENTO_PROCESADA("-","Procesada");
        
        Estatus(String indicador, String descripcion){
            this.indicador = indicador;
            this.descripcion = descripcion;
        }
        private final String indicador;
        private final String descripcion;
        
        public String getIndicador(){
            return this.indicador;
        }
        
        public String getDescripcion(){
            return this.descripcion;
        }
        
        public static Estatus getEstatus(char codigo){
            switch (codigo){
                case 'L':
                    return ESTATUS_DOCUMENTO_CARGA;
                case 'D':
                    return ESTATUS_DOCUMENTO_DESCARGA;
                case 'C':
                    return ESTATUS_DOCUMENTO_REVISION;
                case 'V':
                    return ESTATUS_DOCUMENTO_APROBADA;
                case 'R':
                    return ESTATUS_DOCUMENTO_RECHAZADA;
                case 'T':
                    return ESTATUS_DOCUMENTO_PROGRAMADA;
                case 'P':
                    return ESTATUS_DOCUMENTO_PAGADA;
                case 'X':
                    return ESTATUS_DOCUMENTO_CANCELADA;
                case '-':
                    return ESTATUS_DOCUMENTO_PROCESADA;
                default:
                    return null;
            }
        }
    }        
}
