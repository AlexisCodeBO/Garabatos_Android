package com.example.garabatos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentoDialogoBorrarImagen extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder constructor= new AlertDialog.Builder(getContext()) ;

        constructor.setMessage(R.string.mensaje_borrar);

        constructor.setPositiveButton(R.string.boton_borrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                obtenerFragmentoGarabato().obtenerVistaGarabato().limpiarPantalla();
            }//onClick
        });

        constructor.setNegativeButton(R.string.boton_cancelar, null);

        return constructor.create(); //Devolver el cuadro de diálogo creado
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
            fragmento.establecerDialogoEnPantall(false);//Hayun diálogo en pantalla
        }
    }//onAttach

    // Indicar a MainActiivityFragment que ya no hayun diálogo en pantalla
    @Override
    public void onDetach() {
        super.onDetach();

        //Obtener una referencia al fragmento
        MainActivityFragment fragmento = obtenerFragmentoGarabato();

        if (fragmento != null) //Si existe el fragmento
            fragmento.establecerDialogoEnPantall(false);

    }
}//FragmentoDialogoBorrarImagen
