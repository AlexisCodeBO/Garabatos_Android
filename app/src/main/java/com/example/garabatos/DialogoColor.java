package com.example.garabatos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


public class DialogoColor extends DialogFragment {

     public SeekBar alfaSeekBar;
     public SeekBar rojoSeekBar;
     private SeekBar verdeSeekBar;
     private SeekBar azulSeekBar;
     private View colorView;
     private int color;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder constructor=new AlertDialog.Builder(getContext());
        //Se infla la interfaz para cambiar el color a partir de fragmento_color.xml
        View vistaDialogoColor= getActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);
        //La interfaz inflada se utiliza como interfaz del cuadro diálogo
        constructor.setView(vistaDialogoColor);
        constructor.setTitle(R.string.título_diálogo_color);

        alfaSeekBar=vistaDialogoColor.findViewById(R.id.alfaSeekBar);
        rojoSeekBar=vistaDialogoColor.findViewById(R.id.rojoSeekBar);
        verdeSeekBar=vistaDialogoColor.findViewById(R.id.verdeSeekBar);
        azulSeekBar=vistaDialogoColor.findViewById(R.id.azulSeekBar);
        colorView=vistaDialogoColor.findViewById(R.id.colorView);

        alfaSeekBar.setOnSeekBarChangeListener(oyenteCambioColor);
        rojoSeekBar.setOnSeekBarChangeListener(oyenteCambioColor);
        verdeSeekBar.setOnSeekBarChangeListener(oyenteCambioColor);
        azulSeekBar.setOnSeekBarChangeListener(oyenteCambioColor);

        //Obtener una referencia a la VistaGarabato del dibujo
        final VistaGarabato vistaGarabato= obtenerFragmentoGarabato().obtenerVistaGarabato();
        color= vistaGarabato.obtenerColorLinea();
        alfaSeekBar.setProgress(Color.alpha(color));
        rojoSeekBar.setProgress(Color.red(color));
        verdeSeekBar.setProgress(Color.green(color));
        azulSeekBar.setProgress(Color.blue(color));



        constructor.setPositiveButton(R.string.boton_establecer_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vistaGarabato.establecerColorLinea(color);
            }//onClick
        });

        return constructor.create();
    }//onCreateDialog

    //Obtener una referencia al fragmento del garabato
    private MainActivityFragment obtenerFragmentoGarabato(){
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.garabatoFragment);

    }//obtenerFragmentoGarabato

    //Indicar a MainActivityFragment que hay un diálogo en pantalla
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Obtener una referencia al fragmento
        MainActivityFragment fragmento=obtenerFragmentoGarabato();

        if(fragmento != null){
            fragmento.establecerDialogoEnPantall(true);//Hay una diálogo en pantalla
        }
    }

    // Indicar MainActiviityFragment que ya no hay un diálogo en pantalla
    @Override
    public void onDetach() {
        super.onDetach();

        //Obtener una referencia al fragmento
        MainActivityFragment fragmento = obtenerFragmentoGarabato();

        if (fragmento != null) //Si existe el fragmento
            fragmento.establecerDialogoEnPantall(false);

    }

    private final SeekBar.OnSeekBarChangeListener oyenteCambioColor= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) // Si el usuario  cambio el progreso de algun SeekBar
                color = Color.argb(alfaSeekBar.getProgress(), rojoSeekBar.getProgress(),
                        verdeSeekBar.getProgress(),azulSeekBar.getProgress());

            //Establecer el nuevo valor del color
        }//onProgressChanged

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }//onStartTrackingTouch

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }//onStopTrackingTouch
    };//oyenteCambioColor
}// DialogoColor
