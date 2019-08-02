package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

public class EventoLlamadaIniciada extends EventObject {
	private static final long serialVersionUID = -1588186128884604464L;
	private String numeroTelefonico;
    
    public EventoLlamadaIniciada(Object remitente, String numeroTelefonico) {
        super(remitente);
        this.numeroTelefonico = numeroTelefonico;
    }
    
    public String obtenerNumeroTelefonico() {
        return numeroTelefonico;
    }
}
