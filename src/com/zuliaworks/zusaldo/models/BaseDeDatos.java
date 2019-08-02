package com.zuliaworks.zusaldo.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {
    // Variables
    private static final int VERSION = 3;
    private static final String BASE_DE_DATOS = "com_zuliaworks_saldo.db";
    public static final String TABLA_MENSAJE = "mensaje";
    public static final String TABLA_SALDO = "saldo";
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_FECHA = "fecha";
    public static final String COLUMNA_REMITENTE = "remitente";
    public static final String COLUMNA_TEXTO = "texto";
    public static final String COLUMNA_SMS = "sms";
    public static final String COLUMNA_MB = "mb";
    public static final String COLUMNA_MMS = "mms";
    public static final String COLUMNA_LLAMADAS_MISMA_OP = "llamadas_misma_op";
    public static final String COLUMNA_LLAMADAS_OTRAS_OP = "llamadas_otras_op";
    public static final String COLUMNA_LLAMADAS_FIJOS = "llamadas_fijos";
    public static final String COLUMNA_SALDO = "saldo";
    public static final String COLUMNA_RENTA = "renta";
    public static final String COLUMNA_VENCIMIENTO = "vencimiento";
    private static final String CREAR_TABLA_SMS = "create table "
        + TABLA_MENSAJE + "(" 
        + COLUMNA_ID + " integer primary key autoincrement, " 
        + COLUMNA_REMITENTE + " text not null, " 
        + COLUMNA_TEXTO + " text not null, "
        + COLUMNA_FECHA + " text not null"
        + ");";
    private static final String CREAR_TABLA_SALDO = "create table "
        + TABLA_SALDO + "(" 
        + COLUMNA_ID + " integer primary key autoincrement, " 
        + COLUMNA_SMS + " integer, "
        + COLUMNA_MB + " real, "
        + COLUMNA_MMS + " integer, "
        + COLUMNA_LLAMADAS_MISMA_OP + " real, "
        + COLUMNA_LLAMADAS_OTRAS_OP + " real, "
        + COLUMNA_LLAMADAS_FIJOS + " real, "
        + COLUMNA_SALDO + " real, "
        + COLUMNA_RENTA + " real, "
        + COLUMNA_VENCIMIENTO + " text, "
        + COLUMNA_FECHA + " text not null"
        + ");";
  
    // Constructores
    public BaseDeDatos(Context context) {
        super(context, BASE_DE_DATOS, null, VERSION);
    }

    // Implementacion de interfaces
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_SMS);
        db.execSQL(CREAR_TABLA_SALDO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLA_MENSAJE);
        db.execSQL("drop table if exists " + TABLA_SALDO);
        onCreate(db);
    }
}