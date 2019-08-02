package com.zuliaworks.zusaldo.servicios;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaIniciada;

public class Llamadas extends BroadcastReceiver {
    // Variables y constantes
    private static enum LLAMADA { SALIENTE, ENTRANTE, TERMINADA };
    private List<String> destinatariosPretendidos;
    private List<EscuchaEventoLlamadaFinalizada> escuchasLlamadaFinalizada;
    private List<EscuchaEventoLlamadaIniciada> escuchasLlamadaIniciada;
    private boolean escuchando;
    private boolean llamadaActiva;
    private String numeroLlamada;
    private LLAMADA tipoLlamada;
    
    // Contructores
    public Llamadas() {
        destinatariosPretendidos = new ArrayList<String>();
        escuchasLlamadaFinalizada = new ArrayList<EscuchaEventoLlamadaFinalizada>();
        escuchasLlamadaIniciada = new ArrayList<EscuchaEventoLlamadaIniciada>();
        escuchando = false;
        llamadaActiva = false;
        numeroLlamada = null;
        tipoLlamada = LLAMADA.TERMINADA;
    }
    
    // Propiedades
    public synchronized void agregarDestinatarioPretendido(String remitente) {
        destinatariosPretendidos.add(remitente);
    }
    
    public synchronized void quitarDestinatarioPretendido(String remitente) {
        destinatariosPretendidos.remove(remitente);
    }
    
    public synchronized void agregarEscuchaEventoLlamadaIniciada(EscuchaEventoLlamadaIniciada e) {
        escuchasLlamadaIniciada.add(e);
    }

    public synchronized void quitarEscuchaEventoLlamadaIniciada(EscuchaEventoLlamadaIniciada e) {
        escuchasLlamadaIniciada.remove(e);
    }
    
    public synchronized void agregarEscuchaEventoLlamadaFinalizada(EscuchaEventoLlamadaFinalizada e) {
        escuchasLlamadaFinalizada.add(e);
    }

    public synchronized void quitarEscuchaEventoLlamadaFinalizada(EscuchaEventoLlamadaFinalizada e) {
        escuchasLlamadaFinalizada.remove(e);
    }

    public synchronized boolean obtenerEscuchando() {
        return escuchando;
    }
    
    public synchronized void establecerEscuchando(boolean valor) {
        escuchando = valor;
    }
    
    public synchronized boolean obtenerLlamadaActiva() {
        return llamadaActiva;
    }
    
    private synchronized void establecerLlamadaActiva(boolean valor) {
        llamadaActiva = valor;
    }
    
    public synchronized String obtenerNumeroLlamada() {
        return numeroLlamada;
    }
    
    private synchronized void establecerNumeroLlamada(String valor) {
        numeroLlamada = valor;
    }
    
    public synchronized LLAMADA obtenerTipoLlamada() {
        return tipoLlamada;
    }
    
    private synchronized void establecerTipoLlamada(LLAMADA valor) {
        tipoLlamada = valor;
    }
    
    // Funciones
    protected void dispararLlamadaIniciada(EventoLlamadaIniciada e) {
        for (EscuchaEventoLlamadaIniciada esc : escuchasLlamadaIniciada) {
            Runnable r = new Runnable() {
                private EscuchaEventoLlamadaIniciada escucha;
                private EventoLlamadaIniciada evento;
                
                public Runnable iniciar(EscuchaEventoLlamadaIniciada escucha, 
                                        EventoLlamadaIniciada evento) {
                    this.escucha = escucha;
                    this.evento = evento;
                    return this;
                }
                
                @Override
                public void run() {
                    escucha.llamadaIniciada(evento);
                }
            }.iniciar(esc, e);
            Comunes.ejecutor.submit(r);
        }
    }
    
    protected void dispararLlamadaFinalizada(EventoLlamadaFinalizada e) {
        for (EscuchaEventoLlamadaFinalizada esc : escuchasLlamadaFinalizada) {
            Runnable r = new Runnable() {
                private EscuchaEventoLlamadaFinalizada escucha;
                private EventoLlamadaFinalizada evento;
                
                public Runnable iniciar(EscuchaEventoLlamadaFinalizada escucha, 
                                        EventoLlamadaFinalizada evento) {
                    this.escucha = escucha;
                    this.evento = evento;
                    return this;
                }
                
                @Override
                public void run() {
                    escucha.llamadaFinalizada(evento);
                }
            }.iniciar(esc, e);
            Comunes.ejecutor.submit(r);
        }
    }

    public void finalizarLlamada() {
        establecerLlamadaActiva(false);
        establecerTipoLlamada(LLAMADA.TERMINADA);
        establecerNumeroLlamada(null);
    }
    
    // Implementacion de interfaces
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!obtenerEscuchando())
            return;
        
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        
        String estadoTelefono = extras.getString(TelephonyManager.EXTRA_STATE);
        String numeroSaliente = extras.getString(Intent.EXTRA_PHONE_NUMBER);
        
        if (numeroSaliente != null) {
            // Estamos haciendo una llamada
            establecerTipoLlamada(LLAMADA.SALIENTE);
            establecerNumeroLlamada(numeroSaliente);
        } else {
            if (estadoTelefono.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Estamos recibiendo una llamada
                establecerTipoLlamada(LLAMADA.ENTRANTE);
                establecerNumeroLlamada(
                    extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                );
            } else if (estadoTelefono.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                String numero = obtenerNumeroLlamada();
                // La llamada (entrante o saliente) esta activa
                if (obtenerTipoLlamada() == LLAMADA.SALIENTE 
                    && destinatariosPretendidos.contains(numero)) {
                    dispararLlamadaIniciada(
                        new EventoLlamadaIniciada(this, numero)
                    );
                    establecerLlamadaActiva(true);
                }
            } else if (estadoTelefono.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // La llamada ha terminado
                if (obtenerLlamadaActiva()) {
                    dispararLlamadaFinalizada(
                        new EventoLlamadaFinalizada(this, obtenerNumeroLlamada())
                    );
                    finalizarLlamada();
                }
            }
        }
    }
}