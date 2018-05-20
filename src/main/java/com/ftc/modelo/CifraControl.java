package com.ftc.modelo;

public class CifraControl {
    private Periodo periodo;
    private double monto;
    private int registrosTipo;
    private int registrosAsociado;
    private double maxTipo;
    private double maxAsociado;
    private double minTipo;
    private double minAsociado;
    private int ajustes;

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getRegistrosTipo() {
        return registrosTipo;
    }

    public void setRegistrosTipo(int registrosTipo) {
        this.registrosTipo = registrosTipo;
    }

    public int getRegistrosAsociado() {
        return registrosAsociado;
    }

    public void setRegistrosAsociado(int registrosAsociado) {
        this.registrosAsociado = registrosAsociado;
    }

    public double getMaxTipo() {
        return maxTipo;
    }

    public void setMaxTipo(double maxTipo) {
        this.maxTipo = maxTipo;
    }

    public double getMaxAsociado() {
        return maxAsociado;
    }

    public void setMaxAsociado(double maxAsociado) {
        this.maxAsociado = maxAsociado;
    }

    public double getMinTipo() {
        return minTipo;
    }

    public void setMinTipo(double minTipo) {
        this.minTipo = minTipo;
    }

    public double getMinAsociado() {
        return minAsociado;
    }

    public void setMinAsociado(double minAsociado) {
        this.minAsociado = minAsociado;
    }
    
    public int getAjustes() {
        return this.ajustes;
    }
    
    public void setAjustes(int ajustes){
        this.ajustes = ajustes;
    }
    
}
