package com.ftc.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Periodo implements Serializable {
    private String identificador;
    private int any;
    private int periodo;
    private Date apertura;
    private Date cierre;
    private String estatus;
    private String comentario;
    private PeriodoCifraControl cifraControl;
    private List<PeriodoCabecera> cabeceras;
    private double monto;
    private int cuenta;
    
    public Periodo(){}

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getAny() {
        return any;
    }

    public void setAny(int any) {
        this.any = any;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public Date getApertura() {
        return apertura;
    }

    public void setApertura(Date apertura) {
        this.apertura = apertura;
    }

    public Date getCierre() {
        return cierre;
    }

    public void setCierre(Date cierre) {
        this.cierre = cierre;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getDsEstatus(){
        if(estatus.equals("A")){
            return "Abierto";
        } else if(estatus.equals("C")){
            return "Cerrado";
        } else {
            return "desconocido";
        }
    }
    
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public PeriodoCifraControl getCifraControl() {
        return cifraControl;
    }

    public void setCifraControl(PeriodoCifraControl cifraControl) {
        this.cifraControl = cifraControl;
    }
    
    public List<PeriodoCabecera> getCabeceras() {
        return cabeceras;
    }

    public void setCabeceras(List<PeriodoCabecera> cabeceras) {
        this.cabeceras = cabeceras;
    }
    
    public double getMonto(){
        return this.monto;
    }
    
    public void setMonto(double monto){
        this.monto = monto;
    }
    
    public int getCuenta(){
        return this.cuenta;
    }
    
    public void setCuenta(int cuenta){
        this.cuenta = cuenta;
    }

    @Override
    public String toString() {
        return "Periodo{" + "identificador=" + identificador + ", any=" + any + ", periodo=" + periodo + ", apertura=" + apertura + ", cierre=" + cierre + ", estatus=" + estatus + ", comentario=" + comentario + ", monto=" + monto + ", cuenta=" + cuenta + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.identificador != null ? this.identificador.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Periodo other = (Periodo) obj;
        if ((this.identificador == null) ? (other.identificador != null) : !this.identificador.equals(other.identificador)) {
            return false;
        }
        return true;
    }
    
    
    
}
