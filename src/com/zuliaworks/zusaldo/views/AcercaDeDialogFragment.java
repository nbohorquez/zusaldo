package com.zuliaworks.zusaldo.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.zuliaworks.zusaldo.R;

public class AcercaDeDialogFragment extends SherlockDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity actividad = getActivity();
        Context contexto = actividad.getApplicationContext();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        LayoutInflater factory = LayoutInflater.from(contexto);
        final View acerca_de = factory.inflate(
            R.layout.fragment_acerca_de, null
        );
        
        TableRow fila = (TableRow)((TableLayout)acerca_de).getChildAt(0);
        TextView txt = (TextView)fila.getChildAt(1);
        txt.setText(Html.fromHtml(getString(R.string.texto_acerca_de)));
        Linkify.addLinks(txt, Linkify.ALL);
        
        fila = (TableRow)((TableLayout)acerca_de).getChildAt(1);
        txt = (TextView)fila.getChildAt(0);
        txt.setText(Html.fromHtml(getString(R.string.texto_legal)));
        txt.setMovementMethod(new ScrollingMovementMethod());
        
        builder.setView(acerca_de)
               .setTitle(R.string.menu_acerca_de)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        return builder.create();
    }
}