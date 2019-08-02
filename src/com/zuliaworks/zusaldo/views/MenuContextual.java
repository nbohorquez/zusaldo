package com.zuliaworks.zusaldo.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.Constantes.SALDO;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualDestruido;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.eventos.EventoMenuContextualDestruido;
import com.zuliaworks.zusaldo.eventos.EventoMenuContextualItemSeleccionado;

public class MenuContextual implements ActionMode.Callback {
    // Variables
    private List<EscuchaEventoMenuContextualDestruido> escuchasMenuContextualDestruido;
    private List<EscuchaEventoMenuContextualItemSeleccionado> escuchasMenuContextualItemSeleccionado;
    
    // Constructores
    public MenuContextual() {
        escuchasMenuContextualDestruido = 
            new ArrayList<EscuchaEventoMenuContextualDestruido>();
        escuchasMenuContextualItemSeleccionado = 
                new ArrayList<EscuchaEventoMenuContextualItemSeleccionado>();
    }
    
    // Propiedades
    public synchronized void agregarEscuchaEventoMenuContextualDestruido(EscuchaEventoMenuContextualDestruido e) {
        escuchasMenuContextualDestruido.add(e);
    }

    public synchronized void quitarEscuchaEventoMenuContextualDestruido(EscuchaEventoMenuContextualDestruido e) {
        escuchasMenuContextualDestruido.remove(e);
    }
    
    public synchronized void agregarEscuchaEventoMenuContextualItemSeleccionado(EscuchaEventoMenuContextualItemSeleccionado e) {
        escuchasMenuContextualItemSeleccionado.add(e);
    }

    public synchronized void quitarEscuchaEventoMenuContextualItemSeleccionado(EscuchaEventoMenuContextualItemSeleccionado e) {
        escuchasMenuContextualItemSeleccionado.remove(e);
    }
    
    // Funciones
    protected void dispararMenuContextualDestruido(EventoMenuContextualDestruido e) {
        for (EscuchaEventoMenuContextualDestruido esc : escuchasMenuContextualDestruido)
            Executors.newSingleThreadScheduledExecutor().execute(
                new Runnable() {
                    private EscuchaEventoMenuContextualDestruido escucha;
                    private EventoMenuContextualDestruido evento;
                    
                    public Runnable iniciar(EscuchaEventoMenuContextualDestruido escucha,
                                            EventoMenuContextualDestruido evento) {
                        this.escucha = escucha;
                        this.evento = evento;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        escucha.menuContextualDestruido(evento);
                    }
                }.iniciar(esc, e)
            );
    }
    
    protected void dispararMenuContextualItemSeleccionado(EventoMenuContextualItemSeleccionado e) {
        for (EscuchaEventoMenuContextualItemSeleccionado esc : escuchasMenuContextualItemSeleccionado)
            Executors.newSingleThreadScheduledExecutor().execute(
                new Runnable() {
                    private EscuchaEventoMenuContextualItemSeleccionado escucha;
                    private EventoMenuContextualItemSeleccionado evento;
                    
                    public Runnable iniciar(EscuchaEventoMenuContextualItemSeleccionado escucha,
                                            EventoMenuContextualItemSeleccionado evento) {
                        this.escucha = escucha;
                        this.evento = evento;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        escucha.menuContextualItemSeleccionado(evento);
                    }
                }.iniciar(esc, e)
            );
    }
    
    // Implementacion de interfaces
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.add(SALDO.SMS.toString())
            .setIcon(R.drawable.content_email)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(SALDO.MB.toString())
            .setIcon(R.drawable.location_web_site)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        /*
        menu.add("MMS")
            .setIcon()
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            */
        menu.add(SALDO.LLAMADAS_MISMA_OPERADORA.toString())
            .setIcon(R.drawable.device_access_call)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        /*
        menu.add("Saldo")
            .setIcon()
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Renta")
            .setIcon()
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Vencimiento")
            .setIcon()
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            */
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        dispararMenuContextualItemSeleccionado(
            new EventoMenuContextualItemSeleccionado(this, mode, item)
        );
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        dispararMenuContextualDestruido(
            new EventoMenuContextualDestruido(this)
        );
    }
}