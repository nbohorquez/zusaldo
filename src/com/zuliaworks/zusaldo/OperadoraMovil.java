package com.zuliaworks.zusaldo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Triplet;

import android.telephony.TelephonyManager;

import com.zuliaworks.zusaldo.Constantes.SALDO;

public class OperadoraMovil {
    // Variables y constantes
    public static String DIGITEL = "Digitel";
    public static String MOVISTAR = "Movistar";
    public static String MOVILNET = "Movilnet";
    private Map<SALDO, Pattern[]> regex_digitel = new HashMap<SALDO, Pattern[]>() {
        private static final long serialVersionUID = -343684690983611493L;
        {
            put(
                SALDO.SALDO, 
                new Pattern[] {
                    Pattern.compile("^Bs \\d+,\\d{2}", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.LLAMADAS_MISMA_OPERADORA,
                new Pattern[] {
                    Pattern.compile("Min Dig:\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.LLAMADAS_OTRAS_OPERADORAS,
                new Pattern[] {
                    Pattern.compile("Min Otras:\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.LLAMADAS_FIJOS,
                new Pattern[] {
                    Pattern.compile("Min Otras:\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.SMS,
                new Pattern[] {
                    Pattern.compile("SMS:\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.MB,
                new Pattern[] {
                    Pattern.compile("MB:\\d+,\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.MMS, 
                new Pattern[] {
                    Pattern.compile("MMS:\\d+", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.VENCIMIENTO,
                new Pattern[] {
                    Pattern.compile(
                        "Prox Pago (0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])", 
                        Pattern.CASE_INSENSITIVE
                    )
                }
            );
        }
    };
    private Map<SALDO, Pattern[]> regex_movilnet = new HashMap<SALDO, Pattern[]>() {
        private static final long serialVersionUID = -7784702406147825388L;
        {
            put(
                SALDO.SALDO, 
                new Pattern[] {
                    Pattern.compile(
                        "Tu saldo es Bs. (-)?\\d+,\\d{5}", Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.LLAMADAS_MISMA_OPERADORA,
                new Pattern[] {
                    Pattern.compile(
                        "tienes \\d+.\\d+ segundos libres", Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.LLAMADAS_OTRAS_OPERADORAS,
                new Pattern[] {
                    Pattern.compile(
                        "tienes \\d+.\\d+ segundos libres", Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.LLAMADAS_FIJOS,
                new Pattern[] {
                    Pattern.compile(
                        "tienes \\d+.\\d+ segundos libres", Pattern.CASE_INSENSITIVE
                    )
                }
            );
        }
    };
    private Map<SALDO, Pattern[]> regex_movistar = new HashMap<SALDO, Pattern[]>() {
        private static final long serialVersionUID = 1319801829778781905L;
        {
            put(
                SALDO.SECUENCIA,
                new Pattern[] {
                    Pattern.compile("\\(\\d+/\\d+\\)", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.SALDO,
                new Pattern[] {
                    Pattern.compile(
                        "Su saldo es Bs.F. \\d,\\d{2}", Pattern.CASE_INSENSITIVE
                    ),
                    Pattern.compile(
                        "Su (cupo|bono) es de Bs.F. \\d,\\d{2}", 
                        Pattern.CASE_INSENSITIVE
                    ),
                    Pattern.compile(
                        "Su deuda total es de Bs.F. \\d,\\d{2}", 
                        Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.MB,
                new Pattern[] {
                    Pattern.compile("\\d+ Mb", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.LLAMADAS_MISMA_OPERADORA,
                new Pattern[] {
                    Pattern.compile("mov mov \\d+ Min", Pattern.CASE_INSENSITIVE),
                    Pattern.compile(
                        "mov fij(o)?( movis)? \\d+ Min", Pattern.CASE_INSENSITIVE
                    ),
                    Pattern.compile(
                        "cualquier op.* \\d+ Min", Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.LLAMADAS_FIJOS , 
                new Pattern[] {
                    Pattern.compile(
                        "mov fij(o)?( movis)? \\d+ Min", Pattern.CASE_INSENSITIVE
                    ),
                    Pattern.compile(
                        "cualquier op.* \\d+ Min", Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.LLAMADAS_OTRAS_OPERADORAS,
                new Pattern[] {
                    Pattern.compile("mov op \\d+ Min", Pattern.CASE_INSENSITIVE),
                    Pattern.compile(
                        "cualquier op.* \\d+ Min", Pattern.CASE_INSENSITIVE
                    ),
                    Pattern.compile("op movil \\d+ Min", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.SMS, 
                new Pattern[] {
                    Pattern.compile("\\d+ Msj", Pattern.CASE_INSENSITIVE)
                }
            );
            put(
                SALDO.RENTA,
                new Pattern[] {
                    Pattern.compile(
                        "monto mensual de su RB y/o servicios es de Bs.F. \\d+,\\d{2}", 
                        Pattern.CASE_INSENSITIVE
                    )
                }
            );
            put(
                SALDO.VENCIMIENTO,
                new Pattern[] {
                    Pattern.compile(
                        "fecha de vencimiento de su renta es el " +
                        "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](\\d{2})", 
                        Pattern.CASE_INSENSITIVE
                    )
                }
            );
        }
    };
    private Map<String, Triplet<String, String[], Map<SALDO, Pattern[]>>> operadoras = 
        new HashMap<String, Triplet<String, String[], Map<SALDO, Pattern[]>>>() {
            private static final long serialVersionUID = -2435788726384689286L;
            {
                put(DIGITEL, 
                    Triplet.with(
                        "123", 
                        new String[] {
                            "SU SALDO ES", "787253637", "+787253637", "787-253637", 
                            "+787-253637"
                        }, 
                        regex_digitel
                    )
                );
                put(MOVILNET, 
                    Triplet.with(
                        "*55", new String[] {"9997", "+9997"}, 
                        regex_movilnet
                    )
                );
                put(MOVISTAR, 
                    Triplet.with(
                        "*88", new String[] {"+811", "0811", "+0811", "At.Cliente"}, 
                        regex_movistar
                    )
                );
            }
        };
    private String operadoraRed;

    // Constructores
    public OperadoraMovil(TelephonyManager telefonia) {
        operadoraRed = telefonia.getNetworkOperatorName();
        String[] operadoras = new String[] { MOVISTAR, DIGITEL, MOVILNET };
        
        for (String op : operadoras) {
            Pattern p = Pattern.compile(op, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(operadoraRed);
            if (m.find()) {
                operadoraRed = op;
                break;
            }
        }
        
    }
    
    // Propiedades
    public synchronized String obtenerNombreOperadora() {
        return operadoraRed;
    }
    
    public synchronized String obtenerTelefonoPedirSaldo() {
        return (operadoras.containsKey(operadoraRed)) 
               ? new String(operadoras.get(operadoraRed).getValue0()) 
               : null;
    }
    
    public synchronized String[] obtenerTelefonoRecibirSaldo() {
        return (operadoras.containsKey(operadoraRed)) 
               ? operadoras.get(operadoraRed).getValue1().clone() 
               : new String[] {};
    }

    public synchronized Map<SALDO, Pattern[]> obtenerRegexParsearSaldo() {
        return (operadoras.containsKey(operadoraRed)) 
               ? new HashMap<SALDO, Pattern[]>(operadoras.get(operadoraRed).getValue2()) 
               : new HashMap<SALDO, Pattern[]>();
    }
}