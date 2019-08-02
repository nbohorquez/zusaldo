package com.zuliaworks.zusaldo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.widget.Toast;

public class Comunes {
    // Constantes
    public static final ExecutorService ejecutor = 
        Executors.newFixedThreadPool(Constantes.MAX_HILOS);
    public static final ScheduledExecutorService worker = 
        Executors.newSingleThreadScheduledExecutor();
    public static final DateTimeFormatter formatoFechaParaResumen = 
        DateTimeFormat.forPattern("dd 'de' MMMMM hh:mm a");
    public static final DateTimeFormatter formatoFechaParaSQLite = 
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final DateTimeFormatter formatoFechaParaDataDeHoy = 
        DateTimeFormat.forPattern("hh:mm a");
    public static final DateTimeFormatter formatoFechaParaDataDeAntes = 
        DateTimeFormat.forPattern("dd 'de' MMMMM");
    private static final NumberFormat formatoDecimal = 
        DecimalFormat.getInstance();
    
    public static Number String2Number(String valor) {
        Number numero;
        try {
            numero = formatoDecimal.parse(valor);
        } catch (ParseException e1) {
            numero = 0;
        }
        return numero;
    }
    
    public static void mostrarEnToast(final Activity actividad, final String mensaje) {
        actividad.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    actividad.getApplicationContext(), mensaje, Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}