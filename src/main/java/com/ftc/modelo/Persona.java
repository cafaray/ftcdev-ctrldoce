package com.ftc.modelo;

public class Persona {

    private String identificador;
    private String nombre;
    private String rfc;
    private char tipo;

    public Persona() {}

    public Persona(String nombre, String rfc, char tipo) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.tipo = tipo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public String getDescripcionTipo() {
        switch (tipo) {
            case 'P':
                return "Proveedor";
            case 'C':
                return "Clientes";
            case 'A':
                return "Administrador";
            default:
                return "";
        }
    }


}
