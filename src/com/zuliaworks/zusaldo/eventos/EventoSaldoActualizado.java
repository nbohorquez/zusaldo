package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;
import java.util.Map;

import com.zuliaworks.zusaldo.Constantes.SALDO;

public class EventoSaldoActualizado extends EventObject {
	private static final long serialVersionUID = -8844249403012939584L;
	private Map<SALDO, String> saldo;
    private Boolean ultimoMensaje;
    
    public EventoSaldoActualizado(Object remitente, Map<SALDO, String> saldo,
                                  Boolean ultimoMensaje) {
        super(remitente);
        this.saldo = saldo;
        this.ultimoMensaje = ultimoMensaje;
    }
    
    public Map<SALDO, String> obtenerSaldo() {
        return saldo;
    }
    
    public Boolean obtenerUltimoMensaje() {
        return ultimoMensaje;
    }
}
