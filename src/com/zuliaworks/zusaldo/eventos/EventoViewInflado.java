package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

import android.view.View;
import android.view.ViewGroup;

public class EventoViewInflado extends EventObject {
	private static final long serialVersionUID = -1411283623811497250L;
	private View vistaInflada;
    private ViewGroup vistaContenedora;
    
    public EventoViewInflado(Object remitente, View vistaInflada,
                             ViewGroup vistaContenedora) {
        super(remitente);
        this.vistaInflada = vistaInflada;
        this.vistaContenedora = vistaContenedora;
    }
    
    public View obtenerVistaInflada() {
        return vistaInflada;
    }
    
    public ViewGroup obtenerVistaContenedora() {
        return vistaContenedora;
    }
}
