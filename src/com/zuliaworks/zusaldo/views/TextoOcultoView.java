package com.zuliaworks.zusaldo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextoOcultoView extends TextView {
    // Variables 
    private String textoOculto;
    
    // Constructores
    public TextoOcultoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    // Funciones
    public synchronized void establecerTextoOculto(String valor) {
        textoOculto = valor;
    }
    
    public synchronized String obtenerTextoOculto() {
        return textoOculto;
    }
}
