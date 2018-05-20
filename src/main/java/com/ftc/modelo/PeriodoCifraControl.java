package com.ftc.modelo;

public class PeriodoCifraControl {
    private int registros;
    private double monto;
    private boolean pendienteAutorizar;
    
    public PeriodoCifraControl(){}

    public int getRegistros() {
        return registros;
    }

    public void setRegistros(int registros) {
        this.registros = registros;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public boolean isPendienteAutorizar() {
        return pendienteAutorizar;
    }

    public void setPendienteAutorizar(boolean pendienteAutorizar) {
        this.pendienteAutorizar = pendienteAutorizar;
    }
    
    
}
