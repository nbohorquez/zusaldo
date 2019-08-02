package com.zuliaworks.zusaldo.views;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.R;

public class CuadroMovibleLayout extends FrameLayout {
    // Variables
    private View cartaOcupado;
    
    // Constructores
    public CuadroMovibleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Para agregarle el atributo onLongClick al XML mira este ejemplo:
        // http://stackoverflow.com/questions/5706038/long-press-definition-at-xml-layout-like-androidonclick-does
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iniciarArrastre(v);
                return true;
            }
        });
    }
    
    // Funciones
    private void iniciarArrastre(View v) {
    }
    
    public void superponerCartaOcupado(final Activity actividadPadre,
                                       int retardo) {
        Comunes.worker.schedule(
            new Runnable() {
                private CuadroMovibleLayout cuadro;
                private Activity actividad;
                
                public Runnable iniciar(CuadroMovibleLayout cuadro,
                                        Activity actividad) {
                    this.actividad = actividad;
                    this.cuadro = cuadro;
                    return this;
                }
                
                @Override
                public void run() {
                    cuadro.superponerCartaOcupado(actividad);
                }
            }.iniciar(this, actividadPadre), 
            retardo, 
            TimeUnit.SECONDS
        );
    }
    
    public void superponerCartaOcupado(final Activity actividadPadre) {
        actividadPadre.runOnUiThread(
            new Runnable() {
                private CuadroMovibleLayout cuadro;
                
                public Runnable iniciar(CuadroMovibleLayout cuadro) {
                    this.cuadro = cuadro;
                    return this;
                }
                
                @Override
                public void run() {
                    LayoutInflater factory = LayoutInflater.from(getContext());
                    cartaOcupado = factory.inflate(
                        R.layout.overlay_ocupado, null
                    );
                    
                    if (cuadro.indexOfChild(cartaOcupado) == -1)
                        cuadro.addView(cartaOcupado);
                    cuadro.bringChildToFront(cartaOcupado);
                }
            }.iniciar(this)
        );
    }
    
    public void quitarCartaOcupado(final Activity actividadPadre,
                                   int retardo) {
        Comunes.worker.schedule(
            new Runnable() {
                private CuadroMovibleLayout cuadro;
                private Activity actividad;

                public Runnable iniciar(CuadroMovibleLayout cuadro,
                                        Activity actividad) {
                    this.actividad = actividad;
                    this.cuadro = cuadro;
                    return this;
                }

                @Override
                public void run() {
                    cuadro.quitarCartaOcupado(actividad);
                }
            }.iniciar(this, actividadPadre), 
            retardo, 
            TimeUnit.SECONDS
        );
    }
    
    public void quitarCartaOcupado(final Activity actividadPadre) {
        actividadPadre.runOnUiThread(
            new Runnable() {
                private CuadroMovibleLayout cuadro;
                
                public Runnable iniciar(CuadroMovibleLayout cuadro) {
                    this.cuadro = cuadro;
                    return this;
                }
                
                @Override
                public void run() {
                    if (cuadro.indexOfChild(cartaOcupado) != -1) {
                        cuadro.removeView(cartaOcupado);
                        cartaOcupado = null;
                    }
                }
            }.iniciar(this)
        );
    }
    
    public void resaltarCarta() {
        TransitionDrawable drawable = (TransitionDrawable)this.getBackground();
        drawable.startTransition(500);
        drawable.reverseTransition(500);
    }
}