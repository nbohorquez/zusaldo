package com.zuliaworks.zusaldo.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.zuliaworks.zusaldo.Constantes;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoViewInflado;
import com.zuliaworks.zusaldo.eventos.EventoViewInflado;

public class LadoCartaFragment extends SherlockFragment {
    // Variables
    private List<EscuchaEventoViewInflado> escuchasViewInflado;
    
    // Constructores
    public LadoCartaFragment() {
        escuchasViewInflado = new ArrayList<EscuchaEventoViewInflado>();
    }
    
    // Propiedades
    public synchronized void agregarEscuchaEventoViewInflado(EscuchaEventoViewInflado e) {
        escuchasViewInflado.add(e);
    }

    public synchronized void quitarEscuchaEventoViewInflado(EscuchaEventoViewInflado e) {
        escuchasViewInflado.remove(e);
    }
    
    // Funciones
    protected void dispararViewInflado(EventoViewInflado e) {
        for (EscuchaEventoViewInflado esc : escuchasViewInflado)
            Executors.newSingleThreadScheduledExecutor().execute(
                new Runnable() {
                    private EscuchaEventoViewInflado escucha;
                    private EventoViewInflado evento;
                    
                    public Runnable iniciar(EscuchaEventoViewInflado escucha,
                                            EventoViewInflado evento) {
                        this.escucha = escucha;
                        this.evento = evento;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        escucha.viewInflado(evento);
                    }
                }.iniciar(esc, e)
            );
    }

    // Implementacion de interfaces
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Integer ladoId = getArguments().getInt(Constantes.EXTRA_ID);
        View vista = inflater.inflate(ladoId, container, false);
        dispararViewInflado(new EventoViewInflado(this, vista, container));
        return vista;
    }
}