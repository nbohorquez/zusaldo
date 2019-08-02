package com.zuliaworks.zusaldo;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

public class Constantes {
    public static enum SALDO { SALDO, LLAMADAS_MISMA_OPERADORA, 
                               LLAMADAS_FIJOS, LLAMADAS_OTRAS_OPERADORAS, SMS, 
                               MB, MMS, VENCIMIENTO, RENTA, SECUENCIA, MENSAJE };
    public static Map<SALDO, Integer> COLORES_GRAFICAS = 
        new HashMap<SALDO, Integer>() {
            private static final long serialVersionUID = -3726690242383568606L;
            {
                put(SALDO.SMS, 0x80B2182B);
                put(SALDO.MB, 0x80D6604D);
                put(SALDO.MMS, 0x80F4A582);
                put(SALDO.LLAMADAS_MISMA_OPERADORA, 0x80FDDBC7);
                put(SALDO.LLAMADAS_OTRAS_OPERADORAS, 0x80D1E5F0);
                put(SALDO.LLAMADAS_FIJOS, 0x8092C5DE);
                put(SALDO.SALDO, 0x804393C3);
                put(SALDO.RENTA, 0x802166AC);
            }
        };
    public static Map<SALDO, Integer[]> COLORES_MENSAJES =
        new HashMap<SALDO, Integer[]>() {
            private static final long serialVersionUID = 7742332816938650069L;
            {
                put(SALDO.SMS, new Integer[] { 0x80B2182B, Color.WHITE });
                put(SALDO.MB, new Integer[] { 0x80D6604D, Color.WHITE });
                put(SALDO.LLAMADAS_MISMA_OPERADORA, new Integer[] { 0x80FDDBC7, Color.BLACK });
            }
        };
    public static Integer MAX_HILOS = 5;
    public static Integer MAX_TIEMPO_ACTUALIZANDO_SALDO = 120;
    public static Integer MAX_TIEMPO_PANTALLA_ACTUALIZANDO = 10;
    public static Integer MAX_TIEMPO_OCUPADO = 180;
    public static Integer TIEMPO_RETARDO_PANTALLA_ACTUALIZANDO = 3;
    public static String EXTRA_ID = "ID";
    public static String EXTRA_VALOR = "VALOR";
    public static String EXTRA_VOLTEADO = "VOLTEADO";
}
