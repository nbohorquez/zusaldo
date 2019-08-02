package com.zuliaworks.zusaldo.eventos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class DisparaEvento<T> {
    // Variables
    protected List<EscuchaEvento<T>> escuchasEvento;
    
    // Constructores
    public DisparaEvento() {
        escuchasEvento = new ArrayList<EscuchaEvento<T>>();
    }
    
    // Propiedades
    public synchronized void agregarEscuchaEvento(EscuchaEvento<T> e) {
        escuchasEvento.add(e);
    }

    public synchronized void quitarEscuchaEvento(EscuchaEvento<T> e) {
        escuchasEvento.remove(e);
    }
    
    // Funciones
    public void dispararEvento(T e) {
        for (EscuchaEvento<T> esc : escuchasEvento)
            Executors.newSingleThreadScheduledExecutor().execute(
                new Runnable() {
                    private EscuchaEvento<T> escucha;
                    private T evento;
                    
                    public Runnable iniciar(T evento, EscuchaEvento<T> escucha) {
                        this.evento = evento;
                        this.escucha = escucha;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        escucha.eventoDisparado(evento);
                    }
                }.iniciar(e, esc)
            );
    }
}