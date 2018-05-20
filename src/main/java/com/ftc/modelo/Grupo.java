package com.ftc.modelo;

public class Grupo {
    private String identificador;
    private String grupo;
    private String descripcion;
    private long modo;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getModo() {
        return modo;
    }

    public void setModo(long modo) {
        this.modo = modo;
    }
    
}
