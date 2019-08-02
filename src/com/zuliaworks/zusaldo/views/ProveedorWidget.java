package com.zuliaworks.zusaldo.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.zuliaworks.zusaldo.R;
import com.zuliaworks.zusaldo.servicios.ServicioWidget;

public class ProveedorWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews vistasRemotas = new RemoteViews(
                context.getPackageName(), R.layout.widget
            );
            
            Intent proposito = new Intent(context, ServicioWidget.class);
            proposito.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            proposito.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds
            );
            
            PendingIntent propositoPendiente = PendingIntent.getBroadcast(
                context, 0, proposito, PendingIntent.FLAG_UPDATE_CURRENT
            );
            vistasRemotas.setOnClickPendingIntent(
                R.id.widget, propositoPendiente
            );
            appWidgetManager.updateAppWidget(widgetId, vistasRemotas);
        }
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}