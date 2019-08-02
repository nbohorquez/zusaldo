package com.zuliaworks.zusaldo.models;

public class Mensaje {
    // Variables
    private Long id;
    private String remitente;
    private String texto;
    private String fecha;

    // Propiedades
    public Long obtenerId() {
        return id;
    }

    public void establecerId(Long valor) {
        this.id = valor;
    }

    public String obtenerRemitente() {
        return remitente;
    }

    public void establecerRemitente(String valor) {
        this.remitente = valor;
    }
    
    public String obtenerTexto() {
        return texto;
    }

    public void establecerTexto(String valor) {
        this.texto = valor;
    }
    
    public String obtenerFecha() {
        return fecha;
    }

    public void establecerFecha(String valor) {
        this.fecha = valor;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return texto;
    }
}