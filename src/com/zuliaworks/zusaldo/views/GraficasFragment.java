package com.zuliaworks.zusaldo.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.zuliaworks.zusaldo.Comunes;
import com.zuliaworks.zusaldo.Constantes;
import com.zuliaworks.zusaldo.MainActivity;
import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.Constantes.SALDO;
import com.zuliaworks.zusaldo.eventos.EscuchaEventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.eventos.EventoMenuContextualItemSeleccionado;
import com.zuliaworks.zusaldo.models.Saldo;
import com.zuliaworks.zusaldo.models.SaldoDataSource;

public class GraficasFragment extends SherlockFragment implements EscuchaEventoMenuContextualItemSeleccionado {
    // Variables
    private static enum EJE { IZQUIERDO, DERECHO };
    private SaldoDataSource fuenteDeDatos;
    private GraphicalView grafica;
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderizador;
    private Map<SALDO, Pair<Boolean, XYSeriesRenderer>> series;
    private SALDO dibujado;

    // Propiedades
    public synchronized Boolean obtenerSerieDibujada(SALDO valor) {
        return (series.containsKey(valor)) 
                ? series.get(valor).getValue0() 
                : null;
    }
    
    private synchronized void establecerSerieDibujada(SALDO entrada, 
                                                      Boolean valor) {
        if (series.containsKey(entrada)) {
            Pair<Boolean, XYSeriesRenderer> par = 
                series.get(entrada);
            series.put(entrada, par.setAt0(valor));
        }
    }
    
    public synchronized XYSeriesRenderer obtenerDibujanteSerie(SALDO valor) {
        return (series.containsKey(valor)) 
                ? series.get(valor).getValue1() 
                : null;
    }
    
    private synchronized void establecerDibujanteSerie(SALDO entrada, 
                                                       XYSeriesRenderer valor) {
        if (series.containsKey(entrada)) {
            Pair<Boolean, XYSeriesRenderer> par = 
                series.get(entrada);
            series.put(entrada, par.setAt1(valor));
        }
    }
    
    // Funciones
    private void liberarEje(SALDO serie) {
        if (dibujado == null)
            return;

        dataset.removeSeries(EJE.IZQUIERDO.ordinal());
        XYSeriesRenderer x = obtenerDibujanteSerie(dibujado);
        renderizador.removeSeriesRenderer(x);
        establecerDibujanteSerie(dibujado, null);
        establecerSerieDibujada(dibujado, false);
        dibujado = null;
    }
    
    private void inicializarGrafica() {
        renderizador.setAxisTitleTextSize(20);
        renderizador.setChartTitleTextSize(24);
        renderizador.setLabelsTextSize(18);
        renderizador.setPointSize(5f);
        
        renderizador.setChartTitle("Saldo");
        renderizador.setXTitle("Tiempo");
        
        renderizador.setAxesColor(Color.DKGRAY);
        renderizador.setLabelsColor(Color.DKGRAY);
        renderizador.setShowLegend(false);
        
        renderizador.setApplyBackgroundColor(true);
        renderizador.setBackgroundColor(0x00FFFFFF);
        renderizador.setMarginsColor(0x00FFFFFF);
        
        renderizador.setXLabels(5);
        renderizador.setYLabels(10);
        renderizador.setShowGrid(true);
        renderizador.setXLabelsAlign(Align.RIGHT);
        renderizador.setYLabelsAlign(Align.RIGHT);
        renderizador.setZoomButtonsVisible(false);
        renderizador.setMargins(new int[] {70, 70, 10, 10});
        //renderizador.setPanLimits(new double[] { -10, 20, -10, 40 });
        //renderizador.setZoomLimits(new double[] { -10, 20, -10, 40 });
    }

    public void conmutarSerie(SALDO serie) {
        if (!obtenerSerieDibujada(serie)) {
            // La serie no esta presente en la grafica, la dibujamos
            liberarEje(serie);

            String nombreSerie = serie.toString().replace('_', ' ');
            XYSeries datos = new XYSeries(nombreSerie);
            XYSeriesRenderer dibujante = new XYSeriesRenderer();
            dibujante.setColor(Color.BLACK);
            dibujante.setPointStyle(PointStyle.CIRCLE);
            dibujante.setLineWidth(5);
            dibujante.setFillPoints(true);
            dibujante.setDisplayChartValues(true);
            
            List<Saldo> saldos = fuenteDeDatos.obtenerTodosLosSaldos();
            int saldosLength = saldos.size();

            DateTime fechaMasVieja = new DateTime();
            for (int j = 0; j < saldosLength; j++) {
                DateTime fecha = Comunes.formatoFechaParaSQLite.parseDateTime(
                    saldos.get(j).obtenerFecha()
                );
                
                if (fechaMasVieja.isAfter(fecha))
                    fechaMasVieja = fecha;
                
                switch (serie) {
                    case SMS:
                        datos.add(fecha.toDate().getTime(), saldos.get(j).obtenerSms());
                        break;
                    case MB:
                        datos.add(fecha.toDate().getTime(), saldos.get(j).obtenerMb());
                        break;
                    case MMS:
                        datos.add(fecha.toDate().getTime(), saldos.get(j).obtenerMms());
                        break;
                    case LLAMADAS_MISMA_OPERADORA:
                        datos.add(
                            fecha.toDate().getTime(), saldos.get(j).obtenerLlamadasMismaOp()
                        );
                        break;
                    case LLAMADAS_OTRAS_OPERADORAS:
                        datos.add(
                            fecha.toDate().getTime(), saldos.get(j).obtenerLlamadasOtrasOp()
                        );
                        break;
                    case LLAMADAS_FIJOS:
                        datos.add(
                            fecha.toDate().getTime(), saldos.get(j).obtenerLlamadasFijos()
                        );
                        break;
                    case SALDO:
                        datos.add(fecha.toDate().getTime(), saldos.get(j).obtenerSaldo());
                        break;
                    case RENTA:
                        datos.add(fecha.toDate().getTime(), saldos.get(j).obtenerRenta());
                        break;
                    default:
                        break;
                }
            }

            FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
            fill.setColor(Constantes.COLORES_GRAFICAS.get(serie));
            dibujante.addFillOutsideLine(fill);
            
            dataset.addSeries(datos);
            renderizador.addSeriesRenderer(dibujante);

            renderizador.setYAxisMax(0);
            renderizador.setYAxisMax(datos.getMaxY()*1.40);
            renderizador.setYAxisMin(Math.min(datos.getMinY(), 0));
            
            DateTime ahorita = new DateTime();
            int horas = Hours.hoursBetween(fechaMasVieja, ahorita).getHours();
            
            if (horas < 24) {
                renderizador.setXAxisMin(new DateTime().minusHours(horas+1).toDate().getTime());
            } else if (horas >= 24 && horas < 672){
                renderizador.setXAxisMin(new DateTime().minusDays((int)(horas / 24 + 1)).toDate().getTime());
            } else {
                renderizador.setXAxisMin(new DateTime().minusMonths(1).toDate().getTime());
            }
            
            renderizador.setXAxisMax(new DateTime().toDate().getTime());
            renderizador.setYTitle(nombreSerie);

            establecerDibujanteSerie(serie, dibujante);
            establecerSerieDibujada(serie, true);
            dibujado = serie;
        }
    }
    
    // Implementacion de interfaces
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dataset = new XYMultipleSeriesDataset();
        renderizador = new XYMultipleSeriesRenderer();
        series = new HashMap<SALDO, Pair<Boolean, XYSeriesRenderer>>();
        dibujado = null;
        
        for (SALDO s : SALDO.values())
            series.put(s, Pair.with(false, (XYSeriesRenderer)null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graficas, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onActivityCreated(savedInstanceState);
        fuenteDeDatos = ((MainActivity)getActivity()).obtenerTablaDeSaldos();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        fuenteDeDatos = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        
        if (grafica == null) {
            inicializarGrafica();
            conmutarSerie(SALDO.SMS);
            grafica = ChartFactory.getTimeChartView(
                getActivity(), dataset, renderizador, "dd/MM/yyyy"
            );
            LinearLayout layout = (LinearLayout) getActivity().findViewById(
                R.id.fragment_graficas
            );
            layout.addView(grafica);
        } else {
            grafica.repaint();
        }
    }

    @Override
    public void menuContextualItemSeleccionado(final EventoMenuContextualItemSeleccionado e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tituloItem = e.obtenerItem().getTitle().toString();
                SALDO i = null;
                
                for (SALDO s : SALDO.values())
                    if (tituloItem.equals(s.toString())) {
                        i = s;
                        break;
                    }
                
                conmutarSerie(i);
                grafica.repaint();
            }
        });
    }
}