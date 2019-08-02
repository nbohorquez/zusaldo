package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

public class EventoMenuContextualDestruido extends EventObject {
	private static final long serialVersionUID = -5074881217160618678L;

	public EventoMenuContextualDestruido(Object remitente) {
        super(remitente);
    }
}