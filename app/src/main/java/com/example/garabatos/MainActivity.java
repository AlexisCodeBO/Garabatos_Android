package com.example.garabatos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //determinar el tamaño de la pantalla
        int tamañoPantalla = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        //utilizar paisaje para tablets extra grandes; de lo contrario, utilizar retrato
        if (tamañoPantalla == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            //si la pantalla es muy grande
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//ejecutar la aplicacion en modo paisaje
        else                                                                   //caso contrario
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//ejecutar la aplicacion en modo retrato



    }// onCreate()
}// MainActivity
