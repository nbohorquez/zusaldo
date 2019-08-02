package com.zuliaworks.zusaldo.models;

public class Saldo {
    // Variables
    private Long id;
    private Integer sms;
    private Double mb;
    private Integer mms;
    private Double llamadas_misma_op;
    private Double llamadas_otras_op;
    private Double llamadas_fijos;
    private Double saldo;
    private Double renta;
    private String vencimiento;
    private String fecha;

    // Propiedades
    public Long obtenerId() {
        return id;
    }

    public void establecerId(Long valor) {
        this.id = valor;
    }

    public Integer obtenerSms() {
        return sms;
    }

    public void establecerSms(Integer valor) {
        this.sms = valor;
    }
    
    public Double obtenerMb() {
        return mb;
    }

    public void establecerMb(Double valor) {
        this.mb = valor;
    }
    
    public Integer obtenerMms() {
        return mms;
    }

    public void establecerMms(Integer valor) {
        this.mms = valor;
    }

    public Double obtenerLlamadasMismaOp() {
        return llamadas_misma_op;
    }

    public void establecerLlamadasMismaOp(Double valor) {
        this.llamadas_misma_op = valor;
    }
    
    public Double obtenerLlamadasOtrasOp() {
        return llamadas_otras_op;
    }

    public void establecerLlamadasOtrasOp(Double valor) {
        this.llamadas_otras_op = valor;
    }
    
    public Double obtenerLlamadasFijos() {
        return llamadas_fijos;
    }

    public void establecerLlamadasFijos(Double valor) {
        this.llamadas_fijos = valor;
    }
    
    public Double obtenerSaldo() {
        return saldo;
    }

    public void establecerSaldo(Double valor) {
        this.saldo = valor;
    }
    
    public Double obtenerRenta() {
        return renta;
    }

    public void establecerRenta(Double valor) {
        this.renta = valor;
    }
    
    public String obtenerVencimiento() {
        return vencimiento;
    }

    public void establecerVencimiento(String valor) {
        this.vencimiento = valor;
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
        return "SMS:" + sms.toString() + "; MB:" + mb.toString() 
                + "; MMS: " + mms.toString() 
                + "; Llam. Misma: " + llamadas_misma_op.toString()
                + "; Llam. Otras: " + llamadas_otras_op.toString()
                + "; Llam. Fijos: " + llamadas_fijos.toString()
                + "; Saldo: " + saldo.toString() + "; Renta: " 
                + renta.toString() + "; Vence: " + vencimiento 
                + "; Fecha: " + fecha;
    }
}