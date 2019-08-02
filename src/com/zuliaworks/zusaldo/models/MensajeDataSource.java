package com.zuliaworks.zusaldo.models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zuliaworks.zusaldo.Comunes;

public class MensajeDataSource extends DataSource {
    // Constructores
    public MensajeDataSource(Context context) {
        super(context);
        todasLasColumnas = new String[] { 
            BaseDeDatos.COLUMNA_ID, BaseDeDatos.COLUMNA_REMITENTE, 
            BaseDeDatos.COLUMNA_TEXTO, BaseDeDatos.COLUMNA_FECHA
        };
    }

    // Funciones
    private Mensaje cursorAMensaje(Cursor cursor) {
        Mensaje mensaje = new Mensaje();
        mensaje.establecerId(cursor.getLong(0));
        mensaje.establecerRemitente(cursor.getString(1));
        mensaje.establecerTexto(cursor.getString(2));
        mensaje.establecerFecha(cursor.getString(3));
        return mensaje;
    }
    
    public Mensaje crearMensaje(String remitente, String texto) {
        String fecha = DateTime.now().toString(Comunes.formatoFechaParaSQLite);
        ContentValues valores = new ContentValues();
        valores.put(BaseDeDatos.COLUMNA_REMITENTE, remitente);
        valores.put(BaseDeDatos.COLUMNA_TEXTO, texto);
        valores.put(BaseDeDatos.COLUMNA_FECHA, fecha);
        
        long idNuevo = baseDeDatos.insert(
            BaseDeDatos.TABLA_MENSAJE, null, valores
        );
        Cursor cursor = baseDeDatos.query(
            BaseDeDatos.TABLA_MENSAJE, todasLasColumnas, 
            BaseDeDatos.COLUMNA_ID + " = " + idNuevo, null, null, null, null
        );
        cursor.moveToFirst();
        Mensaje mensajeNuevo = cursorAMensaje(cursor);
        cursor.close();
        return mensajeNuevo;
    }

    public void borrarMensaje(Mensaje mensaje) {
        long id = mensaje.obtenerId();
        baseDeDatos.delete(
            BaseDeDatos.TABLA_MENSAJE, 
            BaseDeDatos.COLUMNA_ID + " = " + id, null
        );
    }

    public List<Mensaje> obtenerTodosLosMensajes() {
        List<Mensaje> mensajes = new ArrayList<Mensaje>();

        Cursor cursor = baseDeDatos.query(
            BaseDeDatos.TABLA_MENSAJE, todasLasColumnas, null, null, null, null, 
            BaseDeDatos.COLUMNA_FECHA + " DESC"
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Mensaje mensaje = cursorAMensaje(cursor);
            mensajes.add(mensaje);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        return mensajes;
    }
}