package com.zuliaworks.zusaldo.servicios;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.zuliaworks.zusaldo.eventos.EscuchaEventoSmsRecibido;
import com.zuliaworks.zusaldo.eventos.EventoSmsRecibido;

public class Mensajeria extends BroadcastReceiver {
    // Variables y constantes
    private static final String EXTRA_SMS = "pdus";
    private List<String> remitentesEsperados;
    private List<EscuchaEventoSmsRecibido> escuchasSmsRecibido;
    private boolean escuchando;
    public enum SMS { REMITENTE, CONTENIDO };

    // Contructores
    public Mensajeria() {
        remitentesEsperados = new ArrayList<String>();
        escuchasSmsRecibido = new ArrayList<EscuchaEventoSmsRecibido>();
        escuchando = false;
    }
    
    // Propiedades
    public synchronized void agregarEscuchaEventoSmsRecibido(EscuchaEventoSmsRecibido e) {
        escuchasSmsRecibido.add(e);
    }

    public synchronized void quitarEscuchaEventoSmsRecibido(EscuchaEventoSmsRecibido e) {
        escuchasSmsRecibido.remove(e);
    }
    
    public synchronized void agregarRemitenteEsperado(String remitente) {
        remitentesEsperados.add(remitente);
    }
    
    public synchronized void quitarRemitenteEsperado(String remitente) {
        remitentesEsperados.remove(remitente);
    }

    public synchronized boolean obtenerEscuchando() {
        return escuchando;
    }
    
    public synchronized void establecerEscuchando(boolean valor) {
        escuchando = valor;
    }
    
    // Funciones
    protected void dispararSmsRecibido(EventoSmsRecibido e) {
        for (EscuchaEventoSmsRecibido esc : escuchasSmsRecibido)
            Executors.newSingleThreadScheduledExecutor().execute(
                new Runnable() {
                    private EscuchaEventoSmsRecibido escucha;
                    private EventoSmsRecibido evento;
                    
                    public Runnable iniciar(EscuchaEventoSmsRecibido escucha,
                                            EventoSmsRecibido evento) {
                        this.escucha = escucha;
                        this.evento = evento;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        escucha.smsRecibido(evento);
                    }
                }.iniciar(esc, e)
            );
    }
    
    // Implementacion de interfaces
    @Override
    public void onReceive(Context context, Intent intent) {
        // http://www.apriorit.com/our-company/dev-blog/227-handle-sms-on-android
        if (!obtenerEscuchando())
            return;
        
        Bundle extras = intent.getExtras();
        
        if (extras != null) {
            Object[] extraSms = (Object[]) extras.get(EXTRA_SMS);
            
            for (int i = 0, len_sms = extraSms.length; i < len_sms; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])extraSms[i]);

                Map<SMS, String> mensaje = new EnumMap<SMS, String>(SMS.class);
                mensaje.put(SMS.REMITENTE, sms.getOriginatingAddress());
                mensaje.put(SMS.CONTENIDO, sms.getMessageBody().toString());
                
                if (remitentesEsperados.contains(mensaje.get(SMS.REMITENTE))) {
                    dispararSmsRecibido(new EventoSmsRecibido(this, mensaje));
                    // WARNING!!! 
                    // The next line makes the received SMS not available in 
                    // incoming. Be careful!
                    abortBroadcast();
                }
            }
        }
    }
}