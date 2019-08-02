package com.zuliaworks.zusaldo.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.javatuples.Pair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.Constantes;
import com.zuliaworks.zusaldo.MainActivity;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.SuscriptorMovil;
import com.zuliaworks.zusaldo.Constantes.SALDO;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoSaldoActualizado;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoTiempoEsperaTerminado;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoViewInflado;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EventoSaldoActualizado;
import com.zuliaworks.zusaldo.eventos.EventoTiempoEsperaTerminado;
import com.zuliaworks.zusaldo.eventos.EventoViewInflado;

public class ResumenFragment extends SherlockFragment implements EscuchaEventoLlamadaIniciada,
                                                                 EscuchaEventoViewInflado,
                                                                 EscuchaEventoSaldoActualizado,
                                                                 EscuchaEventoTiempoEsperaTerminado {
    // Variables
    private SuscriptorMovil cliente;
    private Map<SALDO, String> saldo;
    private List<SALDO> actualizados;
    private Map<SALDO, Pair<Boolean, Integer[]>> cartas;
    
    // Propiedades
    public synchronized Map<SALDO, String> obtenerSaldo() {
        return saldo;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerSaldo(Map<SALDO, String> valor) {
        saldo = valor;
    }
    
    private synchronized void combinarSaldo(Map<SALDO, String> valor) {
        for (SALDO i : valor.keySet()) {
            saldo.put(i, valor.get(i));
        }
    }
    
    private synchronized void agregarActualizado(SALDO valor) {
        actualizados.add(valor);
    }
    
    @SuppressWarnings("unused")
    private synchronized void quitarActualizado(SALDO valor) {
        actualizados.remove(valor);
    }
    
    private synchronized void reiniciarActualizados() {
        actualizados.clear();
    }
    
    public synchronized Boolean obtenerCartaVolteada(SALDO valor) {
        return (cartas.containsKey(valor)) 
                ? cartas.get(valor).getValue0() 
                : null;
    }
    
    private synchronized void establecerCartaVolteada(SALDO entrada, 
                                                      Boolean valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            cartas.put(entrada, par.setAt0(valor));
        }
    }
    
    public synchronized TextView obtenerViewCartaAnverso(SALDO entrada) {
        return (cartas.containsKey(entrada)) 
            ? (TextView)getActivity().findViewById(cartas.get(entrada).getValue1()[0]) 
            : null;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerViewCartaAnverso(SALDO entrada, 
                                                         TextView valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            Integer[] lista = par.getValue1();
            lista[0] = valor.getId();
            cartas.put(entrada, par.setAt1(lista));
        }
    }
    
    public synchronized TextView obtenerViewCartaReverso(SALDO entrada) {
        return (cartas.containsKey(entrada)) 
            ? (TextView)getActivity().findViewById(cartas.get(entrada).getValue1()[1])
            : null;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerViewCartaReverso(SALDO entrada, 
                                                         TextView valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            Integer[] lista = par.getValue1();
            lista[1] = valor.getId();
            cartas.put(entrada, par.setAt1(lista));
        }
    }
    
    public synchronized Integer obtenerCuadroContenedor(SALDO entrada) {
        return (cartas.containsKey(entrada)) 
            ? cartas.get(entrada).getValue1()[2] 
            : -1;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerCuadroContenedor(SALDO entrada, 
                                                         Integer valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            Integer[] lista = par.getValue1();
            lista[2] = valor;
            cartas.put(entrada, par.setAt1(lista));
        }
    }
    
    public synchronized Integer obtenerLayoutCartaAnverso(SALDO entrada) {
        return (cartas.containsKey(entrada)) 
            ? cartas.get(entrada).getValue1()[3] 
            : -1;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerLayoutCartaAnverso(SALDO entrada, 
                                                           Integer valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            Integer[] lista = par.getValue1();
            lista[3] = valor;
            cartas.put(entrada, par.setAt1(lista));
        }
    }
    
    public synchronized Integer obtenerLayoutCartaReverso(SALDO entrada) {
        return (cartas.containsKey(entrada)) 
               ? cartas.get(entrada).getValue1()[4] 
               : -1;
    }
    
    @SuppressWarnings("unused")
    private synchronized void establecerLayoutCartaReverso(SALDO entrada, 
                                                           Integer valor) {
        if (cartas.containsKey(entrada)) {
            Pair<Boolean, Integer[]> par = cartas.get(entrada);
            Integer[] lista = par.getValue1();
            lista[4] = valor;
            cartas.put(entrada, par.setAt1(lista));
        }
    }
    
    private void mostrarCarta(SALDO carta, Boolean volteada) {
        LadoCartaFragment lado = new LadoCartaFragment();
        lado.agregarEscuchaEventoViewInflado(this);
        Bundle args = new Bundle(1);
        args.putInt(
            Constantes.EXTRA_ID, 
            (volteada) ? obtenerLayoutCartaReverso(carta) 
                       : obtenerLayoutCartaAnverso(carta)
        );
        lado.setArguments(args);
        
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .add(obtenerCuadroContenedor(carta), lado)
            .commit();
    }
    
    private void chequearCartasNuevas() {
        Set<SALDO> set = cliente.obtenerDatosEntregablesPorOperadora();
        for (SALDO i: set) {
            CuadroMovibleLayout cuadro = (CuadroMovibleLayout)
                getActivity().findViewById(obtenerCuadroContenedor(i));
            cuadro.superponerCartaOcupado(getActivity());
        }
        refrescarPantalla();
    }
    
    private void chequearCartasRezagadas() {
        Set<SALDO> set = cliente.obtenerDatosEntregablesPorOperadora();
        Map<SALDO, String> tmp = new HashMap<SALDO, String>();
        for (SALDO i: set)
            if (!actualizados.contains(i))
                tmp.put(i, "?");
        
        combinarSaldo(tmp);
        refrescarPantalla(tmp);
    }
    
    public void voltearCarta(View view) {
        try {
            SALDO id = null;
            for (SALDO i : cartas.keySet())
                if (obtenerCuadroContenedor(i) == view.getId())
                    id = i;
            
            if (id == null)
                return;

            LadoCartaFragment lado = new LadoCartaFragment();
            lado.agregarEscuchaEventoViewInflado(this);
            Bundle args = new Bundle(1);
            
            if (obtenerCartaVolteada(id)) {
                args.putInt(Constantes.EXTRA_ID, obtenerLayoutCartaAnverso(id));
                lado.setArguments(args);
                
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                        R.animator.card_flip_left_in, 
                        R.animator.card_flip_left_out,
                        R.animator.card_flip_right_in, 
                        R.animator.card_flip_right_out
                    )
                    .replace(view.getId(), lado)
                    .commit();
                establecerCartaVolteada(id, false);
            } else {
                args.putInt(Constantes.EXTRA_ID, obtenerLayoutCartaReverso(id));
                lado.setArguments(args);
                
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                        R.animator.card_flip_right_in, 
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, 
                        R.animator.card_flip_left_out
                    )
                    .replace(view.getId(), lado)
                    .commit();
                establecerCartaVolteada(id, true);
            }
        } catch (Exception e) {
            Comunes.mostrarEnToast(getActivity(), e.getMessage());
        }
    }
    
    public void refrescarPantalla() {
        refrescarPantalla(obtenerSaldo(), false);
    }
    
    public void refrescarPantalla(Map<SALDO, String> valores) {
        refrescarPantalla(valores, true);
    }
    
    public void refrescarPantalla(Map<SALDO, String> valores, 
                                  Boolean actualizar) {
        getActivity().runOnUiThread(new Runnable() {
            private Map<SALDO, String> saldo;
            private Activity actividadPadre;
            private Boolean actualizar;

            public Runnable iniciar(Map<SALDO, String> saldo, 
                                    Activity actividadPadre,
                                    Boolean actualizar) {
                this.saldo = saldo;
                this.actividadPadre = actividadPadre;
                this.actualizar = actualizar;
                return this;
            }

            @Override
            public void run() {
                for (SALDO i : saldo.keySet()) {
                    TextView a = obtenerViewCartaAnverso(i);
                    TextView b = obtenerViewCartaReverso(i);
                    String valor = saldo.get(i);

                    if (a != null) {
                        a.setText(valor);
                    } if (b != null) {
                        b.setText(valor);
                    }

                    if (actualizar) {
                        CuadroMovibleLayout cuadro = (CuadroMovibleLayout)
                            getActivity().findViewById(
                                obtenerCuadroContenedor(i)
                            );
                        cuadro.quitarCartaOcupado(actividadPadre);
                        cuadro.resaltarCarta();
                    }
                }
            }
        }.iniciar(valores, getActivity(), actualizar));
    }
    
    private HashMap<SALDO, Pair<Boolean, Integer[]>> iniciarCartas() {
        return new HashMap<SALDO, Pair<Boolean, Integer[]>>() {
            private static final long serialVersionUID = -1994283677890080088L;
            {
                put(SALDO.SMS, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_sms,
                            R.id.saldo_sms_reverso,
                            R.id.cuadro_saldo_sms,
                            R.layout.carta_anverso_saldo_sms, 
                            R.layout.carta_reverso_saldo_sms
                        }
                    )
                );
                put(SALDO.MB, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_mb,
                            R.id.saldo_mb_reverso,
                            R.id.cuadro_saldo_mb,
                            R.layout.carta_anverso_saldo_mb, 
                            R.layout.carta_reverso_saldo_mb
                        }
                    )
                );
                put(SALDO.MMS, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_mms,
                            R.id.saldo_mms_reverso,
                            R.id.cuadro_saldo_mms,
                            R.layout.carta_anverso_saldo_mms, 
                            R.layout.carta_reverso_saldo_mms
                        }
                    )
                );
                put(SALDO.LLAMADAS_MISMA_OPERADORA, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_llamadas1,
                            R.id.saldo_llamadas1_reverso,
                            R.id.cuadro_saldo_llamadas1,
                            R.layout.carta_anverso_saldo_llamadas1, 
                            R.layout.carta_reverso_saldo_llamadas1
                        }
                    )
                );
                put(SALDO.LLAMADAS_OTRAS_OPERADORAS, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_llamadas2,
                            R.id.saldo_llamadas2_reverso,
                            R.id.cuadro_saldo_llamadas2,
                            R.layout.carta_anverso_saldo_llamadas2, 
                            R.layout.carta_reverso_saldo_llamadas2
                        }
                    )
                );
                put(SALDO.LLAMADAS_FIJOS, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_llamadas3,
                            R.id.saldo_llamadas3_reverso,
                            R.id.cuadro_saldo_llamadas3,
                            R.layout.carta_anverso_saldo_llamadas3, 
                            R.layout.carta_reverso_saldo_llamadas3
                        }
                    )
                );
                put(SALDO.SALDO, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_saldo,
                            R.id.saldo_saldo_reverso,
                            R.id.cuadro_saldo_saldo,
                            R.layout.carta_anverso_saldo_saldo, 
                            R.layout.carta_reverso_saldo_saldo
                        }
                    )
                );
                put(SALDO.RENTA, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_renta,
                            R.id.saldo_renta_reverso,
                            R.id.cuadro_saldo_renta,
                            R.layout.carta_anverso_saldo_renta, 
                            R.layout.carta_reverso_saldo_renta
                        }
                    )
                );
                put(SALDO.VENCIMIENTO, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_vencimiento,
                            R.id.saldo_vencimiento_reverso,
                            R.id.cuadro_saldo_vencimiento,
                            R.layout.carta_anverso_saldo_vencimiento, 
                            R.layout.carta_reverso_saldo_vencimiento
                        }
                    )
                );
                put(SALDO.MENSAJE, 
                    Pair.with(
                        false, new Integer[] {
                            R.id.saldo_mensaje,
                            R.id.saldo_mensaje_reverso,
                            R.id.cuadro_saldo_mensaje,
                            R.layout.carta_anverso_saldo_mensaje, 
                            R.layout.carta_reverso_saldo_mensaje
                        }
                    )
                );
            }
        };
    }
    
    // Implementacion de interfaces
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saldo = new HashMap<SALDO, String>();
        actualizados = new ArrayList<SALDO>();
        // Inicializamos las variables de los cartas de UI
        cartas = iniciarCartas();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cartas, container, false);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first

        // Nos despegamos del cliente
        cliente.quitarEscuchaEventoSaldoActualizado(this);
        cliente.quitarEscuchaEventoTiempoEsperaTerminado(this);
        cliente.quitarEscuchaEventoLlamadaIniciada(this);
        cliente = null;
        
        // Guardamos los datos en un archivo
        SharedPreferences sharedPref = getActivity().getPreferences(
            Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPref.edit();
        for (SALDO s : saldo.keySet())
            editor.putString(s.name(), saldo.get(s));
        editor.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onActivityCreated(savedInstanceState);
        
        // Nos pegamos a cliente
        // ESTO ES LA COSA MAS HORRIBLE QUE HE VISTO EN MI VIDA!!!!
        cliente = ((MainActivity)getActivity()).obtenerCliente();
        cliente.agregarEscuchaEventoSaldoActualizado(this);
        cliente.agregarEscuchaEventoTiempoEsperaTerminado(this);
        cliente.agregarEscuchaEventoLlamadaIniciada(this);
        
        // Obtenemos los ultimos datos desde el archivo de preferencias
        SharedPreferences sharedPref = getActivity().getPreferences(
            Context.MODE_PRIVATE
        );
        Set<SALDO> set = cliente.obtenerDatosEntregablesPorOperadora();
        for (SALDO s : SALDO.values()) {
            // Tomamos los datos guardados en archivo
            saldo.put(
                s, sharedPref.getString(s.name(), set.contains(s)? "?" : "ND")
            );
        }
        
        if (savedInstanceState == null) {
            // Si es la primera vez que se abre la aplicacion entonces cargamos
            // la interfaz por defecto
            for (SALDO s : cartas.keySet())
                mostrarCarta(s, false);
            return;
        } else {
            for (SALDO s : cartas.keySet())
                if (savedInstanceState.containsKey(s.name())) {
                    Bundle dato = savedInstanceState.getBundle(s.name());
                    saldo.put(s, dato.getString(Constantes.EXTRA_VALOR));
                    mostrarCarta(s, dato.getBoolean(Constantes.EXTRA_VOLTEADO));
                }
        }
        
        refrescarPantalla();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        for (SALDO s : cartas.keySet()) {
            Bundle dato = new Bundle();
            dato.putString(Constantes.EXTRA_VALOR, saldo.get(s));
            dato.putBoolean(Constantes.EXTRA_VOLTEADO, obtenerCartaVolteada(s));
            savedInstanceState.putBundle(s.name(), dato);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        // Este codigo tiene que estar aqui por esto:
    }
    
    @Override
    public void llamadaIniciada(EventoLlamadaIniciada e) {
        try {
            // Reiniciamos la informacion de las cartas actualizadas
            reiniciarActualizados();
            
            // Coloca la "carta ocupado" sobre los cuadros que se van a 
            // actualizar
            chequearCartasNuevas();
            
            // Programamos la 'quitada' de las 'carta ocupado' para
            // aquellas cartas que no se actualizaron
            Comunes.worker.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        chequearCartasRezagadas();
                    }
                }, 
                Constantes.MAX_TIEMPO_OCUPADO, 
                TimeUnit.SECONDS
            );
        } catch(Exception ex) {
            Comunes.mostrarEnToast(getActivity(), ex.getMessage());
        }
    }
    
    @Override
    public void viewInflado(EventoViewInflado e) {
        try {
            refrescarPantalla();
        } catch(Exception ex) {
            /*
            return (operadoras.containsKey(operadoraRed)) 
                   ? new String(operadoras.get(operadoraRed).getValue0()) 
                   : null;
            */
            Comunes.mostrarEnToast(getActivity(), ex.getMessage());
        }
    }
    
    @Override
    public void saldoActualizado(EventoSaldoActualizado e) {
        try {
            // Llevamos la cuenta de cuales cartas se han actualizado
            Map<SALDO, String> tmp = e.obtenerSaldo();
            for (SALDO i : tmp.keySet())
                agregarActualizado(i);
            
            // Agregamos los valores de las cartas actualizadas a 'saldo'
            // ya existente
            combinarSaldo(tmp);
            refrescarPantalla(tmp);
            
            if (e.obtenerUltimoMensaje())
                chequearCartasRezagadas();
        } catch(Exception ex) {
            Comunes.mostrarEnToast(getActivity(), ex.getMessage());
        }
    }
    
    @Override
    public void tiempoEsperaTerminado(final EventoTiempoEsperaTerminado e) {
        try {
            chequearCartasRezagadas();
            Map<SALDO, String> tmp = new HashMap<SALDO, String>() {
                private static final long serialVersionUID = -1333097678661610775L;
                {
                    put(SALDO.MENSAJE, e.obtenerMensaje());
                }
            };
            combinarSaldo(tmp);
            refrescarPantalla(tmp);
            Comunes.mostrarEnToast(
                getActivity(), 
                cliente.obtenerNombreOperadora() + " se tardó demasiado en responder"
            );
        } catch (Exception ex) {
            Comunes.mostrarEnToast(getActivity(), ex.getMessage());
        }
    }
}