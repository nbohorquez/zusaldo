package com.zuliaworks.zusaldo.servicios;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.R;

public class SuperponerPantalla extends Service {
    // Variables
    private final IBinder atadura = new LocalBinder();
    private View pantallaActualizando;
        
    // Funcion
    public void mostrarPantallaActualizando(final Activity actividad) {
        actividad.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // http://stackoverflow.com/questions/4481226/creating-a-system-overlay-always-on-top-button-in-android
                    WindowManager.LayoutParams params = 
                        new WindowManager.LayoutParams();
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = WindowManager.LayoutParams.MATCH_PARENT;
                    params.type = 
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                    params.flags = 
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    params.format = PixelFormat.TRANSLUCENT;
                    
                    WindowManager wm = (WindowManager)getSystemService(
                        Context.WINDOW_SERVICE
                    );
                    
                    LayoutInflater inflater = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    );
                    
                    pantallaActualizando = inflater.inflate(
                        R.layout.overlay_actualizando, null
                    );
                    wm.addView(pantallaActualizando, params);
                } catch (Exception e) {
                    Comunes.mostrarEnToast(actividad, e.getMessage());
                }
            }
        });
    }
    
    public void mostrarPantallaActualizando(final Activity actividad, 
                                            int retardo) {
        Comunes.worker.schedule(new Runnable() {
            public void run() {
                mostrarPantallaActualizando(actividad);
            }
        }, retardo, TimeUnit.SECONDS);
    }
    
    public void borrarPantallaActualizando(final Activity actividad) {
        actividad.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pantallaActualizando != null) {
                        ((WindowManager) getSystemService(
                            Context.WINDOW_SERVICE
                        )).removeView(pantallaActualizando);
                        pantallaActualizando = null;
                    }
                } catch (Exception e) {
                    Comunes.mostrarEnToast(actividad, e.getMessage());
                }
            }
        });
    }
    
    public void borrarPantallaActualizando(final Activity actividad, int retardo) {
        Comunes.worker.schedule(new Runnable() {
            public void run() {
                borrarPantallaActualizando(actividad);
            }
        }, retardo, TimeUnit.SECONDS);
    }
    
    // Implementacion de interfaces
    @Override
    public IBinder onBind(Intent intent) {
        return atadura;
    }
    
    // Tipos anidados
    public class LocalBinder extends Binder {
        public SuperponerPantalla getService() {
            return SuperponerPantalla.this;
        }
    }
}