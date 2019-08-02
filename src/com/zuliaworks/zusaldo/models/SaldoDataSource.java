package com.zuliaworks.zusaldo.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.Constantes.SALDO;

public class SaldoDataSource extends DataSource {
    // Constructores
    public SaldoDataSource(Context context) {
        super(context);
        todasLasColumnas = new String[] { 
            BaseDeDatos.COLUMNA_ID, BaseDeDatos.COLUMNA_SMS, 
            BaseDeDatos.COLUMNA_MB, BaseDeDatos.COLUMNA_MMS,
            BaseDeDatos.COLUMNA_LLAMADAS_MISMA_OP, 
            BaseDeDatos.COLUMNA_LLAMADAS_OTRAS_OP,
            BaseDeDatos.COLUMNA_LLAMADAS_FIJOS,
            BaseDeDatos.COLUMNA_SALDO, BaseDeDatos.COLUMNA_RENTA,
            BaseDeDatos.COLUMNA_VENCIMIENTO,
            BaseDeDatos.COLUMNA_FECHA
        };
    }

    // Funciones
    private Saldo cursorASaldo(Cursor cursor) {
        Saldo saldo = new Saldo();
        saldo.establecerId(cursor.getLong(0));
        saldo.establecerSms(cursor.getInt(1));
        saldo.establecerMb(cursor.getDouble(2));
        saldo.establecerMms(cursor.getInt(3));
        saldo.establecerLlamadasMismaOp(cursor.getDouble(4));
        saldo.establecerLlamadasOtrasOp(cursor.getDouble(5));
        saldo.establecerLlamadasFijos(cursor.getDouble(6));
        saldo.establecerSaldo(cursor.getDouble(7));
        saldo.establecerRenta(cursor.getDouble(8));
        saldo.establecerVencimiento(cursor.getString(9));
        saldo.establecerFecha(cursor.getString(10));
        return saldo;
    }

    public Saldo crearSaldo(Map<SALDO, String> saldo) {
        String fecha = DateTime.now().toString(Comunes.formatoFechaParaSQLite);
        ContentValues valores = new ContentValues();
        valores.put(BaseDeDatos.COLUMNA_SMS, saldo.get(SALDO.SMS));
        valores.put(BaseDeDatos.COLUMNA_MB, saldo.get(SALDO.MB));
        valores.put(BaseDeDatos.COLUMNA_MMS, saldo.get(SALDO.MMS));
        valores.put(
            BaseDeDatos.COLUMNA_LLAMADAS_MISMA_OP, 
            saldo.get(SALDO.LLAMADAS_MISMA_OPERADORA)
        );
        valores.put(
            BaseDeDatos.COLUMNA_LLAMADAS_OTRAS_OP, 
            saldo.get(SALDO.LLAMADAS_OTRAS_OPERADORAS)
        );
        valores.put(
            BaseDeDatos.COLUMNA_LLAMADAS_FIJOS, 
            saldo.get(SALDO.LLAMADAS_FIJOS)
        );
        valores.put(BaseDeDatos.COLUMNA_SALDO, saldo.get(SALDO.SALDO));
        valores.put(BaseDeDatos.COLUMNA_RENTA, saldo.get(SALDO.RENTA));
        valores.put(
            BaseDeDatos.COLUMNA_VENCIMIENTO, saldo.get(SALDO.VENCIMIENTO)
        );
        valores.put(BaseDeDatos.COLUMNA_FECHA, fecha);
        
        long idNuevo = baseDeDatos.insert(
            BaseDeDatos.TABLA_SALDO, null, valores
        );
        Cursor cursor = baseDeDatos.query(
            BaseDeDatos.TABLA_SALDO, todasLasColumnas, 
            BaseDeDatos.COLUMNA_ID + " = " + idNuevo, null, null, null, null
        );
        cursor.moveToFirst();
        Saldo mensajeNuevo = cursorASaldo(cursor);
        cursor.close();
        return mensajeNuevo;
    }

    public void borrarSaldo(Saldo saldo) {
        Long id = saldo.obtenerId();
        baseDeDatos.delete(
            BaseDeDatos.TABLA_SALDO, 
            BaseDeDatos.COLUMNA_ID + " = " + id, null
        );
    }

    public List<Saldo> obtenerTodosLosSaldos() {
        List<Saldo> saldos = new ArrayList<Saldo>();

        Cursor cursor = baseDeDatos.query(
            BaseDeDatos.TABLA_SALDO, todasLasColumnas, null, null, null, null, 
            null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Saldo saldo = cursorASaldo(cursor);
            saldos.add(saldo);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        return saldos;
    }
}