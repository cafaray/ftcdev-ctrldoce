package com.ftc.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PeriodoCabecera {
    private String identificador;
    private String asociadoA;
    private String tipo;
    private String estatus;
    private Date fecha;
    private String documento;
    private String referencia;
    private List<PeriodoRegistro> registros;
    // --> 210717: esta propiedad no le pertenece a PeriodoCabecera, es de paso para la suma del detalle
    private double importe;
    private int cuentaFueraPeriodo;
    // <--
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getAsociadoA() {
        return asociadoA;
    }

    public void setAsociadoA(String asociadoA) {
        this.asociadoA = asociadoA;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String obtieneFecha(){
        if(fecha!=null){
            return dateFormat.format(fecha);
        } else {
            return null;
        }
    }
    
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getReferencia() {
        return referencia;
    }
    
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public List<PeriodoRegistro> getRegistros() {
        return registros;
    }

    public void setRegistros(List<PeriodoRegistro> registros) {
        this.registros = registros;
    }
    
    public String getEstatus(){
        return this.estatus;
    }
    
    public String getDsEstatus(){
        if (estatus.equalsIgnoreCase("A")){
            return "Abierto";
        } else if(estatus.equalsIgnoreCase("C")){
            return "Cerrado";
        } else {
            return "No Definido";
        }
    }
    
    public void setEstatus(String estatus){
        this.estatus = estatus;
    }
    
    public void setImporte(double importe){
        this.importe = importe;
    }
    
    public double getImporte(){
        return this.importe;
    }
    
    public void setCuentaFueraPeriodo(int cuentaFueraPeriodo){
        this.cuentaFueraPeriodo = cuentaFueraPeriodo;
    }
    
    public int getCuentaFueraPeriodo(){
        return this.cuentaFueraPeriodo;
    }
}
