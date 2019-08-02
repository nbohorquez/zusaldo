package com.zuliaworks.zusaldo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.zuliaworks.zusaldo.Constantes.SALDO;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoSaldoActualizado;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoSmsRecibido;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoTiempoEsperaTerminado;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaFinalizada;
import com.zuliaworks.zusaldo.eventos.EventoLlamadaIniciada;
import com.zuliaworks.zusaldo.eventos.EventoSaldoActualizado;
import com.zuliaworks.zusaldo.eventos.EventoSmsRecibido;
import com.zuliaworks.zusaldo.eventos.EventoTiempoEsperaTerminado;
import com.zuliaworks.zusaldo.models.MensajeDataSource;
import com.zuliaworks.zusaldo.models.SaldoDataSource;
import com.zuliaworks.zusaldo.servicios.Llamadas;
import com.zuliaworks.zusaldo.servicios.Mensajeria;
import com.zuliaworks.zusaldo.servicios.Mensajeria.SMS;

public class SuscriptorMovil implements EscuchaEventoSmsRecibido, 
                                        EscuchaEventoLlamadaIniciada,
                                        EscuchaEventoLlamadaFinalizada {
    // Variables y constantes
    private static final int OCUPADO = 1;
    private static final int LLAMANDO = 2;
    private Context contexto;
    private OperadoraMovil operadora;
    private MensajeDataSource tablaDeMensajes;
    private SaldoDataSource tablaDeSaldos;
    private Mensajeria mensajeria;
    private Llamadas llamadas;
    private List<EscuchaEventoSaldoActualizado> escuchasSaldoActualizado;
    private List<EscuchaEventoTiempoEsperaTerminado> escuchasTiempoEsperaTerminado;
    private Map<SALDO, String> saldo;
    private int estado;
    private String mensaje;
    private Integer gaussActual;
    private Integer mensajesTotales;
    
    // Constructores
    public SuscriptorMovil(Context contexto, MensajeDataSource tablaDeMensajes, 
                           SaldoDataSource tablaDeSaldos, Llamadas llamadas,
                           Mensajeria mensajeria) {
        // Con esta actividad realizamos las llamadas y recibimos los SMS
        this.tablaDeMensajes = tablaDeMensajes;
        this.tablaDeSaldos = tablaDeSaldos;
        this.contexto = contexto;
        this.mensajeria = mensajeria;
        this.llamadas = llamadas;
        
        // Iniciamos desocupados
        estado = 0;
        mensajesTotales = gaussActual = 0;
        escuchasSaldoActualizado = new ArrayList<EscuchaEventoSaldoActualizado>();
        escuchasTiempoEsperaTerminado = new ArrayList<EscuchaEventoTiempoEsperaTerminado>();
        
        TelephonyManager telefonia = (TelephonyManager)
            contexto.getSystemService(Context.TELEPHONY_SERVICE);
        
        // Si este no es un telefono entonces no hay nada que hacer
        if (telefonia.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
            return;
        
        // Detectamos la operadora movil
        operadora = new OperadoraMovil(telefonia);
        
        // Inicializamos saldo
        saldo = new HashMap<SALDO, String>();
        reiniciarSaldo();
        
        // Ajustamos el escucha de mensajes
        mensajeria.agregarEscuchaEventoSmsRecibido(this);
        for (String tel : operadora.obtenerTelefonoRecibirSaldo())
            mensajeria.agregarRemitenteEsperado(tel);
        
        // Ajustamos el escucha de llamadas 
        llamadas.agregarEscuchaEventoLlamadaIniciada(this);
        llamadas.agregarEscuchaEventoLlamadaFinalizada(this);
        llamadas.agregarDestinatarioPretendido(
            operadora.obtenerTelefonoPedirSaldo()
        );
    }
    
    // Propiedades
    public synchronized void agregarEscuchaEventoSaldoActualizado(EscuchaEventoSaldoActualizado e) {
        escuchasSaldoActualizado.add(e);
    }

    public synchronized void quitarEscuchaEventoSaldoActualizado(EscuchaEventoSaldoActualizado e) {
        escuchasSaldoActualizado.remove(e);
    }
    
    public synchronized void agregarEscuchaEventoTiempoEsperaTerminado(EscuchaEventoTiempoEsperaTerminado e) {
        escuchasTiempoEsperaTerminado.add(e);
    }

    public synchronized void quitarEscuchaEventoTiempoEsperaTerminado(EscuchaEventoTiempoEsperaTerminado e) {
        escuchasTiempoEsperaTerminado.remove(e);
    }
    
    public synchronized void agregarEscuchaEventoLlamadaIniciada(EscuchaEventoLlamadaIniciada e) {
        llamadas.agregarEscuchaEventoLlamadaIniciada(e);
    }

    public synchronized void quitarEscuchaEventoLlamadaIniciada(EscuchaEventoLlamadaIniciada e) {
        llamadas.quitarEscuchaEventoLlamadaIniciada(e);
    }
    
    public synchronized void agregarEscuchaEventoLlamadaFinalizada(EscuchaEventoLlamadaFinalizada e) {
        llamadas.agregarEscuchaEventoLlamadaFinalizada(e);
    }

    public synchronized void quitarEscuchaEventoLlamadaFinalizada(EscuchaEventoLlamadaFinalizada e) {
        llamadas.quitarEscuchaEventoLlamadaFinalizada(e);
    }
    
    private synchronized int obtenerEstado() {
        return estado;
    }
    
    private synchronized void establecerEstado(int valor) {
        estado = valor;
    }
    
    private synchronized Integer obtenerMensajesTotales() {
        return mensajesTotales;
    }
    
    private synchronized void establecerMensajesTotales(Integer valor) {
        mensajesTotales = valor;
    }
    
    private synchronized Integer obtenerGaussActual() {
        return gaussActual;
    }
    
    private synchronized void establecerGaussActual(Integer valor) {
        gaussActual = valor;
    }
    
    private synchronized Integer obtenerGaussTotal() {
        Integer mT = obtenerMensajesTotales();
        return (mT * (mT + 1 )) / 2;
    }

    private synchronized String obtenerEntradaSaldo(SALDO entrada) {
        return saldo.get(entrada);
    }
    
    private synchronized void establecerEntradaSaldo(SALDO entrada, 
                                                     String valor) {
        saldo.put(entrada, valor);
    }
    
    private synchronized Map<SALDO, String> obtenerSaldo() {
        return saldo;
    }
    
    private synchronized void reiniciarSaldo() {
        for (SALDO s : operadora.obtenerRegexParsearSaldo().keySet())
            establecerEntradaSaldo(s, null);
    }

    public String obtenerNombreOperadora() {
        return operadora == null ? "Operadora desconocida" 
                                 : operadora.obtenerNombreOperadora();
    }
    
    public Set<SALDO> obtenerDatosEntregablesPorOperadora() {
        Set<SALDO> set = operadora.obtenerRegexParsearSaldo().keySet();
        set.remove(SALDO.SECUENCIA);
        return set;
    }
    
    public Map<SALDO, Pattern[]> obtenerRegexParsearSaldo() {
        return new HashMap<SALDO, Pattern[]>(
            operadora.obtenerRegexParsearSaldo()
        );
    }
    
    private void establecerMensaje(String valor) {
        mensaje = valor;
    }
    
    private String obtenerMensaje() {
        return mensaje;
    }
    
    // Funciones
    private void activarWatchdog() {
        Comunes.worker.schedule(
            new Runnable() {
                @Override
                public void run() {
                    if (((obtenerEstado() & OCUPADO) == OCUPADO)
                        || ((obtenerEstado() & LLAMANDO) == LLAMANDO)) {
                        // Reiniciamos todos los estados
                        llamadas.finalizarLlamada();
                        llamadas.establecerEscuchando(false);
                        mensajeria.establecerEscuchando(false);
                        establecerEstado(obtenerEstado() & ~OCUPADO);
                        establecerEstado(obtenerEstado() & ~LLAMANDO);
                        
                        // Agregamos los datos que se tienen hasta ahorita
                        // a la base de datos
                        tablaDeMensajes.crearMensaje(
                            "Incompleto", obtenerMensaje()
                        );
                        tablaDeSaldos.crearSaldo(obtenerSaldo());
                        
                        // Avisamos a los escuchas que hubo un evento de
                        // tiempo de espera terminado
                        dispararTiempoEsperaTerminado(
                            new EventoTiempoEsperaTerminado(
                                this, 
                                "Error: Datos incompletos ("
                                + obtenerGaussActual() + "/"
                                + obtenerGaussTotal() + ") "
                                + DateTime.now().toString(
                                    Comunes.formatoFechaParaResumen
                                )
                            )
                        );
                        establecerMensajesTotales(0);
                        establecerGaussActual(0);
                    }
                }
            }, 
            Constantes.MAX_TIEMPO_ACTUALIZANDO_SALDO, 
            TimeUnit.SECONDS
        );
    }
    
    protected void dispararSaldoActualizado(EventoSaldoActualizado e) {
        for (EscuchaEventoSaldoActualizado esc : escuchasSaldoActualizado) {
            Runnable r = new Runnable() {
                private EscuchaEventoSaldoActualizado escucha;
                private EventoSaldoActualizado evento;
                
                public Runnable iniciar(EscuchaEventoSaldoActualizado escucha, 
                                        EventoSaldoActualizado evento) {
                    this.escucha = escucha;
                    this.evento = evento;
                    return this;
                }
                
                @Override
                public void run() {
                    escucha.saldoActualizado(evento);
                }
            }.iniciar(esc, e);
            Comunes.ejecutor.submit(r);
        }
    }
    
    protected void dispararTiempoEsperaTerminado(EventoTiempoEsperaTerminado e) {
        for (EscuchaEventoTiempoEsperaTerminado esc : 
             escuchasTiempoEsperaTerminado) {
            Runnable r = new Runnable() {
                private EscuchaEventoTiempoEsperaTerminado escucha;
                private EventoTiempoEsperaTerminado evento;
                
                public Runnable iniciar(EscuchaEventoTiempoEsperaTerminado escucha, 
                                        EventoTiempoEsperaTerminado evento) {
                    this.escucha = escucha;
                    this.evento = evento;
                    return this;
                }
                
                @Override
                public void run() {
                    escucha.tiempoEsperaTerminado(evento);
                }
            }.iniciar(esc, e);
            Comunes.ejecutor.submit(r);
        }
    }
    
    public String actualizarSaldo() {
        String resultado = null;
        String telefono = operadora.obtenerTelefonoPedirSaldo();

        if (operadora == null) {
            resultado = "Lo siento, este no es un dispositivo movil";
        } else if ((obtenerEstado() & OCUPADO) == OCUPADO) {
            resultado = "Actualizacion en curso, espere";
        } else if (llamadas.obtenerLlamadaActiva()) {
            resultado = "Hay una llamada en curso, espere";
        } else if (telefono == null) {
            resultado = "Operadora no reconocida";
        } else {
            // Ponemos a rodar la rueda
            establecerEstado(obtenerEstado() | OCUPADO);
            llamadas.establecerEscuchando(true);

            // Iniciamos la llamada
            Intent llamadaConsultaSaldo = new Intent(Intent.ACTION_CALL);
            llamadaConsultaSaldo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            llamadaConsultaSaldo.setData(Uri.parse("tel:" + telefono));
            contexto.startActivity(llamadaConsultaSaldo);
            
            // Colocamos un 'watchdog': si en determinado tiempo el proceso 
            // de actualizacion de saldo no ha concluido debemos terminarlo
            activarWatchdog();
            reiniciarSaldo();
        }

        return resultado;
    }
    
    // Implementacion de interfaces
    @Override
    public void llamadaIniciada(EventoLlamadaIniciada e) {
        try {
            mensajeria.establecerEscuchando(true);
            establecerEstado(obtenerEstado() | LLAMANDO);
            establecerMensaje("");
        } catch (Exception ex) {
            Comunes.mostrarEnToast((Activity)contexto, ex.getMessage());
        }
    }
    
    @Override
    public void llamadaFinalizada(EventoLlamadaFinalizada e) {
        try {
            if ((obtenerEstado() & LLAMANDO) == LLAMANDO) {
                establecerEstado(obtenerEstado() & ~LLAMANDO);
                llamadas.establecerEscuchando(false);
            }
        } catch (Exception ex) {
            Comunes.mostrarEnToast((Activity)contexto, ex.getMessage());
        }
    }

    @Override
    public void smsRecibido(EventoSmsRecibido e) {
        // Guardamos el mensaje en la base de datos
        String remitente = e.obtenerSms().get(SMS.REMITENTE);
        String texto = e.obtenerSms().get(SMS.CONTENIDO);
        
        Boolean ultimoMensaje = true;
        Map<SALDO, String> saldoTmp = new HashMap<SALDO, String>();
        Map<SALDO, Pattern[]> setPatrones = operadora.obtenerRegexParsearSaldo();
        
        for (Map.Entry<SALDO, Pattern[]> patrones : setPatrones.entrySet()) {
            SALDO clave = patrones.getKey();
            for (Pattern patron : patrones.getValue()) {
                Matcher matcher = patron.matcher(texto);
                // Si el patron de la expresion regular existe en el mensaje
                while (matcher.find()) {
                    if (clave == SALDO.SECUENCIA) {
                        // Si la operadora envia una secuencia de mensajes 
                        // entonces este no es el unico mensaje con datos
                        ultimoMensaje = false;
                        // Le quitamos los parentesis a la secuencia y separamos
                        // los valores de (n) numero actual de mensaje y (m) 
                        // numero total de mensajes. Ej: (n/m)
                        String[] secuencia = matcher.group().replaceAll(
                            "[\\(|\\))]", ""
                        ).split("/");
                        establecerGaussActual(
                            obtenerGaussActual() + Integer.valueOf(secuencia[0])
                        );
                        establecerMensajesTotales(Integer.valueOf(secuencia[1]));
                    } else {
                        /*
                        // Buscamos el primer caracter tipo decimal y borramos
                        // todo lo anterior a el.
                        Pattern p = Pattern.compile("\\d");
                        Matcher m = p.matcher(matcher.group());
                        int index = -1;
                        if (m.find())
                            index = m.start();
                        String valor = matcher.group().substring(
                            index, matcher.group().length()
                        );
                        */
                        // Tomamos la frase que cumplio con la regex y le quitamos
                        // todas las letras y espacios en blanco. De esta forma 
                        // queda solamente el valor que buscamos
                        String nuevo = matcher.group().replaceAll(
                            "[A-Z|a-z| |:|.]", ""
                        );
                        String viejo;
                        try {
                            viejo = obtenerEntradaSaldo(clave).replaceAll(
                                "[A-Z|a-z| |:]", ""
                            );
                        } catch (Exception ex) {
                            viejo = "?";
                        }
                        
                        String valor = "";
                        
                        Number nViejo = Comunes.String2Number(viejo);
                        Number nNuevo = Comunes.String2Number(nuevo);
                        
                        switch (clave) {
                            case SMS:
                            case MMS:
                                valor = ((Integer)(nNuevo.intValue() 
                                        + nViejo.intValue())).toString();
                                break;
                            case MB:
                                valor = ((Integer)(nNuevo.intValue() 
                                        + nViejo.intValue())).toString() 
                                        + " MB";
                                break;
                            case LLAMADAS_MISMA_OPERADORA:
                            case LLAMADAS_OTRAS_OPERADORAS:
                            case LLAMADAS_FIJOS:
                                String op = operadora.obtenerNombreOperadora();
                                Boolean usaMinutos = 
                                    op.equalsIgnoreCase(OperadoraMovil.DIGITEL) 
                                    || 
                                    op.equalsIgnoreCase(OperadoraMovil.MOVISTAR);
                                valor = ((Integer)(nNuevo.intValue() 
                                        + nViejo.intValue())).toString();
                                valor = valor + ((usaMinutos) ? " min" : " seg");
                                break;
                            case SALDO:
                            case RENTA:
                                valor = ((Float)(nNuevo.floatValue() 
                                        + nViejo.floatValue())).toString();
                                valor = "BsF " + valor;
                                break;
                            case VENCIMIENTO:
                                valor = nNuevo.toString();
                                break;
                            default:
                                valor = nNuevo.toString();
                                break;
                        }
                        
                        establecerEntradaSaldo(clave, valor);
                        saldoTmp.put(clave, valor);
                    }
                }
            }
        }
        
        Integer mT = obtenerMensajesTotales();
        Integer gA = obtenerGaussActual();
        Integer gT = obtenerGaussTotal();
        
        /* 
         * Usamos la sumatoria de Gauss para determinar si ya llegaron todos 
         * los mensajes. Ej: Si en total son 3 mensajes, movistar puede enviar 
         * los mensajes (1/3), (2/3) y (3/3) en cualquier secuencia. Cuando la 
         * sumatoria de los numeradores de los mensajes que van llegando (1 + 2 
         * + 3 = 6) sea igual a la sumatoria de Gauss del numero total de 
         * mensajes (3 * (3 + 1)) / 2 = 6) entonces puedo decir que ya han 
         * llegado todos los mensajes de la operadora, nuevamente sin 
         * importarme el orden en que lo hayan hecho.
         */
        
        establecerMensaje(
            obtenerMensaje() + ((obtenerMensaje() == "" ) ? "" : "\n") + texto
        );
        
        if (mT == 0 || (mT > 0 && (gT == gA))) {
            ultimoMensaje = true;
            // Una vez el saldo se actualiza por completo
            mensajeria.establecerEscuchando(false);
            establecerMensajesTotales(0);
            establecerGaussActual(0);
            establecerEstado(obtenerEstado() & ~OCUPADO);
            
            // Colocamos la fecha y hora de la actualizacion
            String fecha = DateTime.now().toString(
                Comunes.formatoFechaParaResumen
            );
            establecerEntradaSaldo(SALDO.MENSAJE, fecha);
            saldoTmp.put(SALDO.MENSAJE, fecha);
            
            tablaDeMensajes.crearMensaje(remitente, obtenerMensaje());
            tablaDeSaldos.crearSaldo(obtenerSaldo());
        }
        
        dispararSaldoActualizado(
            new EventoSaldoActualizado(this, saldoTmp, ultimoMensaje)
        );
    }
}