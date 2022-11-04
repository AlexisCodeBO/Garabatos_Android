package com.example.garabatos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class MainActivityFragment extends Fragment {


    private VistaGarabato vistaGarabato;
    private float aceleracion;       //aceleracion calculada
    private float aceleracionActual; //aceleracion del movimiento actual
    private float aceleracionAnterior ; //aceleracion del movimiento previo
    private boolean dialogoEnPantalla;  //controla si ya hay un dialogo mostrándose

    View view;

    //Constante para determinar  si el usuario sacudió el dispositivo para borrar
    private static final int UMBRAL_ACELERACION=100000;

    //Constante para identificar el permiso
    private static final int CODIGO_PETICION_PERMISO_GUARDAR_IMAGEN=1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista= inflater.inflate(R.layout.fragment_main_activity, container, false);

        setHasOptionsMenu(true); // se especifica que el fragmento debe tener un menú

        vistaGarabato= vista.findViewById(R.id.vistaGarabato); //obtener la referencia a VistaGarabato

        aceleracion=0.00f;
        aceleracionActual= SensorManager.GRAVITY_EARTH;
        aceleracionAnterior= SensorManager.GRAVITY_EARTH;

        return vista;


    }// onCreateView

    //En este metodo se habilita el oyente del acelerometro
    @Override
    public void onResume() {
        super.onResume();

        habilitarEscucharAcelerometro();

    }// onResume

    private void habilitarEscucharAcelerometro(){
        //Se obtiene el servicio de sensores
        SensorManager adminSensores=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Se registran el oyente de eventos del acelerómetro
        adminSensores.registerListener(oyenteEventoSensor, adminSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }//habilitarEscucharAcelerometro

    @Override
    public void onPause() {
        super.onPause();

        deshabilitarEscucharAcelerometro();
    }//onPause

    private void deshabilitarEscucharAcelerometro(){
        //Se obtiene el servicio de sensores
        SensorManager adminSensores=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Se registran el oyente de eventos del acelerómetro
        adminSensores.unregisterListener(oyenteEventoSensor, adminSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    } //deshabilitarEscucharAcelerometro

    //Clase anónima interior para escuchar los eventos del acelerómetro
    SensorEventListener oyenteEventoSensor=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            //Si no hay diálogo en la pantalla
            if (!dialogoEnPantalla){
                float x=event.values[0];
                float y=event.values[1];
                float z=event.values[2];

                //se guarda la aceleración anterior
                aceleracionAnterior=aceleracionActual;

                //se calcula la aceleración actual
                aceleracionActual=x*x+ y*y+ z*z;

                //Se calcula la aceleración
                aceleracion=aceleracionActual*(aceleracionActual- aceleracionAnterior);

                //Si la aceleración sobrepasa el umbral
                if(aceleracion>UMBRAL_ACELERACION)
                    confirmarBorrar();  //se le pregunta al usuario si quiere guardar su dibujo
            }


        }//onSensorChanged

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }//onAccuracyChanged


    }; //oyenteEventosSensor

    private void confirmarBorrar(){
        FragmentoDialogoBorrarImagen fragmento= new FragmentoDialogoBorrarImagen();
        fragmento.show(getFragmentManager(), "diálogo borrar");

    }//confirmarBorrar()

    //Este metodo infla el menú utilizando un archivo XML de menú
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Este metodo infla el menú utilizando un archivo XML de menú
        inflater.inflate(R.menu.garabatos_fragmento_menu, menu);
    }//onCreateOptionsMenu

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Es necesario hacer esto para la primera vez que se otorgue el permiso
        switch(requestCode){
            case  CODIGO_PETICION_PERMISO_GUARDAR_IMAGEN:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                    vistaGarabato.guardarImagen();
                return;
        }


    }//onRequestPermissionResult

    //Devuelve la VistaGarabato que está en uso
    public VistaGarabato obtenerVistaGarabato(){
        return vistaGarabato;

    }//obtenerVistaGarabato

    //Indica si hay un diálogo mostrándose en pantalla
    public void establecerDialogoEnPantall(boolean visible){
        dialogoEnPantalla=visible;

    }

    //Controla qué opción
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { ;

        switch (item.getItemId()) {

            case R.id.color:
                DialogoColor dialogoColor = new DialogoColor();
                dialogoColor.show(getFragmentManager(), "diálogo color");
                return true;  //indica que se ha seleccionado un elemento del menú
            case R.id.grosor_linea:
                FragmentoDialogoGrosorLinea dialogoGrosor = new FragmentoDialogoGrosorLinea();
                dialogoGrosor.show(getFragmentManager(), "diálogo grosor");
                return true;
            case R.id.borrar:
                confirmarBorrar();
                return true;
            case R.id.guardar:
                guardarImagen();
                return true;
            case R.id.imprimir:
                vistaGarabato.imprimirImagen();
                return true;




        }
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

        //Permiso adecuado para guardar la imagen o la guarda guarda directamente si la app
        //ya obtuvo el permiso previamente
        private void guardarImagen () {

            final int  version= Build.VERSION.SDK_INT;
            if(version <Build.VERSION_CODES.M){

                vistaGarabato.guardarImagen();
            }else{
                if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //mostrar un mensaje que explique por qué se necesita el permiso

                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Se tiene que enviar al constructor del diálogo la actividad actual
                        AlertDialog.Builder constructor = new AlertDialog.Builder(getActivity());

                        constructor.setMessage(R.string.explicación_permiso);

                        //Se configura el botón OK con su oyente de click
                        constructor.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] permisosRequeridos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                //Se piden los permisos requeridos
                                requestPermissions(permisosRequeridos, CODIGO_PETICION_PERMISO_GUARDAR_IMAGEN);

                            }// onClick
                        });

                        constructor.create().show();  //se crea y se muestra el cuadro de diálogo

                    } else { //pedir permiso
                        String[] permisosRequeridos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permisosRequeridos, CODIGO_PETICION_PERMISO_GUARDAR_IMAGEN);

                    }
                } else {   //si la app ya tiene permiso para guardar la imagen
                    vistaGarabato.guardarImagen();

                }

            }



        }//guardarImagen

}// MainActivityFragment
