package com.zuliaworks.zusaldo.views;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.zuliaworks.zusaldo.MainActivity;
import com.zuliaworks.zusaldo.models.Mensaje;

public class MensajesListLoader extends AsyncTaskLoader<List<Mensaje>> {
    // Variables
    private List<Mensaje> mensajes;
    private Activity actividadPadre;

    // Constructores
    public MensajesListLoader(Context context) {
        super(context);
        actividadPadre = (Activity)context;
    }

    // Implementacion de interfaces
    @Override
    public List<Mensaje> loadInBackground() {
         // You should perform the heavy task of getting data from 
         // Internet or database or other source
        List<Mensaje> datos = ((MainActivity)actividadPadre)
            .obtenerTablaDeMensajes().obtenerTodosLosMensajes();
        return datos;
    }
     
    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override 
    public void deliverResult(List<Mensaje> listOfData) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (listOfData != null) {
                onReleaseResources(listOfData);
            }
        }
        List<Mensaje> oldApps = listOfData;
        mensajes = listOfData;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(listOfData);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override 
    protected void onStartLoading() {
        if (mensajes != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mensajes);
        }

        if (takeContentChanged() || mensajes == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override 
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override 
    public void onCanceled(List<Mensaje> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override 
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mensajes != null) {
            onReleaseResources(mensajes);
            mensajes = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Mensaje> apps) {
    }
}