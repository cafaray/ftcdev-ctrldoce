package com.ftc.modelo;

public class Contacto {

    private String persona;
    private String razonSocial;
    private String identificador;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String movil;
    private String grupo;

    public Contacto() {
        this.persona = "";
        this.identificador = "";
        this.nombre = "";
        this.apellido = "";
        this.correo = "";
        this.telefono = "";
        this.movil = "";
        this.grupo = "";
    }

    public Contacto(String persona, String nombre, String apellido, String correo, String telefono, String movil, String grupo) {
        this.persona = persona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.movil = movil;
        this.grupo = grupo;
    }

    public Contacto(String nombre, String apellido, String correo, String telefono, String movil) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.movil = movil;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

}
