package com.zuliaworks.zusaldo.eventos;

import java.util.EventObject;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;

public class EventoMenuContextualItemSeleccionado extends EventObject {
	private static final long serialVersionUID = 6076078271017521198L;
	private ActionMode modo;
    private MenuItem item;
    
    public EventoMenuContextualItemSeleccionado(Object remitente, 
                                                ActionMode modo, 
                                                MenuItem item) {
        super(remitente);
        this.modo = modo;
        this.item = item;
    }
    
    public ActionMode obtenerModo() {
        return modo;
    }
    
    public MenuItem obtenerItem() {
        return item;
    }
}

