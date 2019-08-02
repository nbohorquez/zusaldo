package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;
import java.util.Map;

import com.zuliaworks.zusaldo.servicios.Mensajeria.SMS;

public class EventoSmsRecibido extends EventObject {
	private static final long serialVersionUID = -4406559571502586806L;
	private Map<SMS, String> sms;
    
    public EventoSmsRecibido(Object remitente, Map<SMS, String> sms) {
        super(remitente);
        this.sms = sms;
    }
    
    public Map<SMS, String> obtenerSms() {
        return sms;
    }
}
