package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

public class EventoTiempoEsperaTerminado extends EventObject {
	private static final long serialVersionUID = 3722044759117007283L;
	private String mensaje;
    
    public EventoTiempoEsperaTerminado(Object remitente, String mensaje) {
        super(remitente);
        this.mensaje = mensaje;
    }
    
    public String obtenerMensaje() {
        return mensaje;
    }
}
