package com.zuliaworks.zusaldo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualDestruido;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EventoMenuContextualDestruido;
import com.zuliaworks.zusaldo.models.MensajeDataSource;
import com.zuliaworks.zusaldo.models.SaldoDataSource;
import com.zuliaworks.zusaldo.servicios.Llamadas;
import com.zuliaworks.zusaldo.servicios.Mensajeria;
import com.zuliaworks.zusaldo.servicios.SuperponerPantalla;
import com.zuliaworks.zusaldo.servicios.SuperponerPantalla.LocalBinder;
import com.zuliaworks.zusaldo.views.AcercaDeDialogFragment;
import com.zuliaworks.zusaldo.views.GraficasFragment;
import com.zuliaworks.zusaldo.views.MensajesFragment;
import com.zuliaworks.zusaldo.views.MenuContextual;
import com.zuliaworks.zusaldo.views.ResumenFragment;

public class MainActivity extends SherlockFragmentActivity  
                          implements EscuchaEventoLlamadaIniciada,
                                     EscuchaEventoLlamadaFinalizada,
                                     EscuchaEventoMenuContextualDestruido {
    // Variables
    private ActionMode menuContextual;
    private MensajeDataSource tablaDeMensajes;
    private SaldoDataSource tablaDeSaldos;
    private Mensajeria mensajeria;
    private Llamadas llamadas;
    private SuscriptorMovil cliente;
    private SuperponerPantalla servicio;
    private boolean atadoAServicio = false;
    private ServiceConnection conexionServicio = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, 
                                       IBinder service) {
            LocalBinder atadura = (LocalBinder) service;
            servicio = atadura.getService();
            establecerAtadoAServicio(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            establecerAtadoAServicio(false);
        }
    };
    private ViewPager paginador;
    private Pestanas pestanas;

    // Propiedades
    public synchronized Boolean obtenerAtadoAServicio() {
        return atadoAServicio;
    }
    
    private synchronized void establecerAtadoAServicio(Boolean valor) {
        atadoAServicio = valor;
    }
    
    public synchronized SuscriptorMovil obtenerCliente() {
        return cliente;
    }
    
    public synchronized MensajeDataSource obtenerTablaDeMensajes() {
        return tablaDeMensajes;
    }
    
    public synchronized SaldoDataSource obtenerTablaDeSaldos() {
        return tablaDeSaldos;
    }
    
    // Funciones
    // QUE VERGA MAS HORRIBLE MI HERMANO!!!!
    // EL FRAGMENTO DEBERIA RECIBIR ESTE LLAMADO DE UNA VEZ
    public void voltearCarta(View view) {
        ((ResumenFragment)pestanas.obtenerFragmento(1)).voltearCarta(view);
    }
    
    public void mostrarMenuContextual(Fragment demandante) {
        if (menuContextual == null) {
            MenuContextual menu = new MenuContextual();
            menu.agregarEscuchaEventoMenuContextualDestruido(this);
            menu.agregarEscuchaEventoMenuContextualItemSeleccionado(
                (EscuchaEventoMenuContextualItemSeleccionado)demandante
            );
            menuContextual = startActionMode(menu);
        }
    }
    
    public void quitarMenuContextual() {
        if (menuContextual != null) {
            menuContextual.finish();
            menuContextual = null;
        }
    }
    
    public void actualizarSaldo(View view) {
        String mensaje = null;

        try {
            mensaje = cliente.actualizarSaldo();
        } catch (Exception e) {
            mensaje = e.getMessage();
        }
        
        if (mensaje != null) {
            Comunes.mostrarEnToast(this, mensaje);
        }
    }
    
    private void enmudecerLlamada(Boolean enmudecer) {
        AudioManager audio = (AudioManager)getSystemService(
            Context.AUDIO_SERVICE
        );
        audio.setStreamMute(AudioManager.STREAM_VOICE_CALL, enmudecer);
    }
    
    // Implementacion de interfaces
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        paginador = (ViewPager)findViewById(R.id.paginador);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);

        pestanas = new Pestanas(this, paginador);
        pestanas.agregarPestana(
            getResources().getString(R.string.tab_graficas), 
            GraficasFragment.class, null
        );
        pestanas.agregarPestana(
            getResources().getString(R.string.tab_resumen), 
            ResumenFragment.class, null
        );
        pestanas.agregarPestana(
            getResources().getString(R.string.tab_mensajes), 
            MensajesFragment.class, null
        );
        TitlePageIndicator indicador = (TitlePageIndicator)findViewById(
            R.id.indicador_de_pagina
        );
        indicador.setViewPager(paginador, 1);
        indicador.setOnPageChangeListener(pestanas);
        
        // Abrimos la base de datos
        tablaDeMensajes = new MensajeDataSource(this);
        tablaDeSaldos = new SaldoDataSource(this);
        tablaDeMensajes.abrir();
        tablaDeSaldos.abrir();
        
        // Ajustamos el escucha de mensajes
        mensajeria = new Mensajeria();
        IntentFilter mensajeria_filtro = new IntentFilter();
        mensajeria_filtro.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mensajeria_filtro.addAction("android.provider.Telephony.SMS_RECEIVED");
        mensajeria_filtro.addAction("android.provider.Telephony.DATA_SMS_RECEIVED");
        registerReceiver(mensajeria, mensajeria_filtro);
        
        // Ajustamos el escucha de llamadas
        llamadas = new Llamadas();
        IntentFilter llamadas_filtro = new IntentFilter();
        llamadas_filtro.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        llamadas_filtro.addAction("android.intent.action.NEW_OUTGOING_CALL");
        llamadas_filtro.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(llamadas, llamadas_filtro);
        
        // Nos pegamos al servicio SuperponerPantalla
        Intent intent = new Intent(this, SuperponerPantalla.class);
        getApplicationContext().bindService(
            intent, conexionServicio, Context.BIND_AUTO_CREATE
        );
        
        // Creamos el suscriptor movil
        cliente = new SuscriptorMovil(
            this, tablaDeMensajes, tablaDeSaldos, llamadas, mensajeria
        );
        cliente.agregarEscuchaEventoLlamadaIniciada(this);
        cliente.agregarEscuchaEventoLlamadaFinalizada(this);
    }
    
    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            unregisterReceiver(mensajeria);
            unregisterReceiver(llamadas);
            tablaDeMensajes.cerrar();
            tablaDeSaldos.cerrar();
            
            if (obtenerAtadoAServicio()) {
                getApplicationContext().unbindService(conexionServicio);
                establecerAtadoAServicio(false);
            }
        } catch(Exception e) {
            Comunes.mostrarEnToast(this, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_actualizar:
                actualizarSaldo(new View(getApplicationContext()));
                return true;
            case R.id.menu_acerca_de:
                AcercaDeDialogFragment f = new AcercaDeDialogFragment();
                f.show(getSupportFragmentManager(), "acerca_de");
                return true;
            /*
            case R.id.menu_configuracion:
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void llamadaIniciada(EventoLlamadaIniciada e) {
        try {
            // Le quitamos el volumen a la llamada para que el usuario
            // no escuche la voz de la operadora
            enmudecerLlamada(true);
            
            // Muestra la pantalla de "Actualizando"
            if (obtenerAtadoAServicio()) {
                servicio.mostrarPantallaActualizando(this);
                servicio.borrarPantallaActualizando(
                    this, Constantes.MAX_TIEMPO_PANTALLA_ACTUALIZANDO
                );
            }
        } catch(Exception ex) {
            Comunes.mostrarEnToast(this, ex.getMessage());
        }
    }
    
    @Override
    public void llamadaFinalizada(EventoLlamadaFinalizada e) {
        try {
            // Subimos nuevamente el volumen al telefono celular
            enmudecerLlamada(false);
                
            // Borramos la pantalla de "Actualizando"
            if (obtenerAtadoAServicio()) {
                servicio.borrarPantallaActualizando(
                    this, Constantes.TIEMPO_RETARDO_PANTALLA_ACTUALIZANDO
                );
            }
        } catch(Exception ex) {
            Comunes.mostrarEnToast(this, ex.getMessage());
        }
    }
    
    @Override
    public void menuContextualDestruido(EventoMenuContextualDestruido e) {
        menuContextual = null;
    }
}