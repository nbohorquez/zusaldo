package com.zuliaworks.zusaldo.views;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.models.Mensaje;

public class MensajesArrayAdapter extends ArrayAdapter<Mensaje> {
    // Variables
    private final LayoutInflater inflador;
 
    // Propiedades
    public void establecerDatos(List<Mensaje> data) {
        clear();
        if (data != null) {
            for (Mensaje dato : data) {
                add(dato);
            }
        }
        notifyDataSetChanged();
    }
    
    // Constructores
    public MensajesArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        inflador = (LayoutInflater)context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        );
    }
 
    // Implementacion de interfaces
    /**
     * Populate new items in the list.
     */
    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista;

        if (convertView == null) {
            vista = inflador.inflate(R.layout.fragment_recibido, parent, false);
        } else {
            vista = convertView;
        }
 
        Mensaje item = getItem(position);
        String texto = item.obtenerTexto();
        String remitente = item.obtenerRemitente();
        DateTime fecha = Comunes.formatoFechaParaSQLite.parseDateTime(
            item.obtenerFecha()
        );
        DateTime ahora = new DateTime();
        LocalDate hoy = ahora.toLocalDate();
        LocalDate manana = hoy.plusDays(1);
        DateTime comienzoDeHoy = hoy.toDateTimeAtStartOfDay(ahora.getZone());
        DateTime comienzoDeManana = manana.toDateTimeAtStartOfDay(
            ahora.getZone()
        );
        String fechaAMostrar = null;
        
        if (fecha.isAfter(comienzoDeHoy.toDate().getTime()) 
            && fecha.isBefore(comienzoDeManana.toDate().getTime())) {
            fechaAMostrar = fecha.toLocalTime().toString(
                Comunes.formatoFechaParaDataDeHoy
            );
        } else {
            fechaAMostrar = fecha.toLocalDate().toString(
                Comunes.formatoFechaParaDataDeAntes
            );
        }
        
        TextView mensajeRemitente = (TextView)vista.findViewById(
            R.id.mensaje_remitente
        ); 
        TextoOcultoView mensajeTexto = (TextoOcultoView)vista.findViewById(
            R.id.mensaje_texto
        );
        TextView mensajeFecha = (TextView)vista.findViewById(
            R.id.mensaje_fecha
        );
        
        mensajeRemitente.setText(
            remitente.substring(0, Math.min(remitente.length(), 30))
        );
        mensajeTexto.establecerTextoOculto(texto);
        mensajeTexto.setText(
            texto.substring(0, Math.min(texto.length(), 120)) + "..."
        );
        mensajeFecha.setText(fechaAMostrar);

        return vista;
    }
}