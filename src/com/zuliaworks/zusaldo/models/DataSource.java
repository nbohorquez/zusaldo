package com.zuliaworks.zusaldo.models;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DataSource {
    // Variables
    protected SQLiteDatabase baseDeDatos;
    protected BaseDeDatos ayudante;
    protected String[] todasLasColumnas;
    
    // Constructores
    public DataSource(Context context) {
        ayudante = new BaseDeDatos(context);
    }
    
    // Funciones
    public void abrir() throws SQLException {
        baseDeDatos = ayudante.getWritableDatabase();
    }

    public void cerrar() {
        ayudante.close();
    }
}
