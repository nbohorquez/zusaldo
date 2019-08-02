package com.zuliaworks.zusaldo.views;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockListFragment;
import com.zuliaworks.zusaldo.Constantes;
import com.zuliaworks.zusaldo.MainActivity;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.SuscriptorMovil;
import com.zuliaworks.zusaldo.Constantes.SALDO;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.eventos.EventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.models.Mensaje;

public class MensajesFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<Mensaje>>,
                                                                      EscuchaEventoMenuContextualItemSeleccionado {
    // Variables
    private Map<SALDO, Pattern[]> regex;
    private MensajesArrayAdapter adaptador;
    private View vistaActual;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onActivityCreated(savedInstanceState);
        // Nos pegamos a cliente
        // ESTO ES LA COSA MAS HORRIBLE QUE HE VISTO EN MI VIDA!!!!
        SuscriptorMovil cliente = ((MainActivity)getActivity()).obtenerCliente();
        regex = cliente.obtenerRegexParsearSaldo();
        adaptador = new MensajesArrayAdapter(getActivity());
        setListAdapter(adaptador);
        
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Loader<List<Mensaje>> loader = getLoaderManager().getLoader(0);
        if (loader.isStarted())
            loader.forceLoad();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first
        getLoaderManager().destroyLoader(0);
        setListAdapter(null);
        adaptador = null;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        vistaActual = v;
        ((MainActivity)getActivity()).mostrarMenuContextual(this);
        
        int hijos = l.getChildCount();
        for (int i = 0; i < hijos; i++) {
            View fila = l.getChildAt(i);
            ToggleButton boton = (ToggleButton)fila.findViewById(R.id.checkBox);
            TextoOcultoView texto = (TextoOcultoView)fila.findViewById(
                R.id.mensaje_texto
            );
            String textoOculto = texto.obtenerTextoOculto();
            
            if (fila != v) {
                boton.setChecked(false);
                texto.setText(
                    textoOculto.substring(0, Math.min(textoOculto.length(), 120))
                    + "..."
                );
            } else {
                boton.setChecked(true);
                texto.setText(textoOculto);
            }
        }
    }

    @Override
    public Loader<List<Mensaje>> onCreateLoader(int id, Bundle args) {
        return new MensajesListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Mensaje>> loader, List<Mensaje> data) {
        adaptador.establecerDatos(data);
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Mensaje>> arg0) {
        adaptador.establecerDatos(null);
    }

    @Override
    public void menuContextualItemSeleccionado(final EventoMenuContextualItemSeleccionado e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tituloItem = e.obtenerItem().getTitle().toString();
                TextoOcultoView texto = (TextoOcultoView)
                    vistaActual.findViewById(R.id.mensaje_texto);
                
                texto.setText(texto.getText(), TextView.BufferType.SPANNABLE);
                Spannable textoResaltado = (Spannable) texto.getText();
                SALDO clave = null;

                for (SALDO s : SALDO.values()) {
                    if (tituloItem.equals(s.toString())) {
                        clave = s;
                        break;
                    }
                }
                
                Pattern[] patrones = regex.get(clave);
                for (Pattern patron : patrones) {
                    Matcher matcher = patron.matcher(texto.getText());
                    while (matcher.find()) {
                        textoResaltado.setSpan(
                            new BackgroundColorSpan(
                                Constantes.COLORES_MENSAJES.get(clave)[0]
                            ), matcher.start(), matcher.end(), 
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        textoResaltado.setSpan(
                            new ForegroundColorSpan(
                                Constantes.COLORES_MENSAJES.get(clave)[1]
                            ), matcher.start(), matcher.end(), 
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                }
            }
        });
    }
}