package com.example.garabatos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;


public class FragmentoDialogoGrosorLinea extends DialogFragment {

    private ImageView grosorImageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder constructor= new AlertDialog.Builder(getContext());
        final View vistaDialogoGrosorLinea= getActivity().getLayoutInflater().inflate(R.layout.fragment_fragmento_dialogo_grosor_linea,
                null);
        constructor.setView(vistaDialogoGrosorLinea);//establecer como vista del diálogo
                                                    // fragmento_grosor_línea
        constructor.setTitle(R.string.título_diálogo_grosor_línea);

        grosorImageView=vistaDialogoGrosorLinea.findViewById(R.id.grosorImageView);

        final VistaGarabato vistaGarabato=obtenerFragmentoGarabato().obtenerVistaGarabato();
        final SeekBar grosorSeekBar= vistaDialogoGrosorLinea.findViewById(R.id.grosorSeekBar);
        //Establecer el progreso de acuerdo al grosor de la línea
        grosorSeekBar.setProgress(vistaGarabato.obtenerGrosorLinea());
        grosorSeekBar.setOnSeekBarChangeListener(cambioGrosorLinea);

        constructor.setPositiveButton(R.string.boton_establecer_grosor_línea, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vistaGarabato.establecerGrosorLinea(grosorSeekBar.getProgress());

            }//onClick
        });
        return super.onCreateDialog(savedInstanceState);
    }

    private MainActivityFragment obtenerFragmentoGarabato(){
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.garabatoFragment);
    }//obtenerFragmentoGarabato

    //Indicar a MainActivityFragment que hay un diálogo en pantalla
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Obtener una referencia al fragmento
        MainActivityFragment fragmento=obtenerFragmentoGarabato();

        if(fragmento != null){
            fragmento.establecerDialogoEnPantall(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //Obtener una referencia al fragmento
        MainActivityFragment fragmento = obtenerFragmentoGarabato();

        if (fragmento != null) //Si existe el fragmento
            fragmento.establecerDialogoEnPantall(false);

    }//onDetach

    private final SeekBar.OnSeekBarChangeListener cambioGrosorLinea= new SeekBar.OnSeekBarChangeListener() {

        final Bitmap mapaBits=Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888);
        final Canvas lienzo= new Canvas(mapaBits);


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Configurar el objeto paint de acuerdo al grosor especificado por el SeekBar
            Paint p=new Paint();
            p.setColor(obtenerFragmentoGarabato().obtenerVistaGarabato().obtenerColorLinea());
            p.setStrokeCap(Paint.Cap.SQUARE);
            p.setStrokeWidth(progress);

            final int version= Build.VERSION.SDK_INT;
            if(version < Build.VERSION_CODES.M) // Si la versión es menor a Marshmallow
                mapaBits.eraseColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            else
                mapaBits.eraseColor(getResources().getColor(android.R.color.transparent, getContext().getTheme()));

            //Dibujar una línea en el mapa de bits y mostrar el mapa de bits en un ImageView
            lienzo.drawLine(30,50,370,50, p);
            grosorImageView.setImageBitmap(mapaBits);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
