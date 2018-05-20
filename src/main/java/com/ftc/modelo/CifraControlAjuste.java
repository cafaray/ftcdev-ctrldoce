package com.ftc.modelo;

import com.ftc.modelo.CifraControl;

import java.util.Date;

public class CifraControlAjuste extends CifraControl {
    public String ajuste;
    public Date fechaAjuste;
    
    
    public void setAjuste(String ajuste){
        this.ajuste = ajuste;
    }
    
    public String getAjuste(){
        return this.ajuste;
    }
    
    public void setFechaAjuste(Date fechaAjuste){
        this.fechaAjuste = fechaAjuste;
    }
    
    public Date getFechaAjuste(){
        return this.fechaAjuste;
    }
    
}
