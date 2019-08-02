package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

public class EventoLlamadaFinalizada extends EventObject {
	private static final long serialVersionUID = 7659583696572151361L;
	private String numeroTelefonico;
	
    public EventoLlamadaFinalizada(Object remitente, String numeroTelefonico) {
        super(remitente);
        this.numeroTelefonico = numeroTelefonico;
    }
    
    public String obtenerNumeroTelefonico() {
        return numeroTelefonico;
    }
}
