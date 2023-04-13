package com.auditorias.applpas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Auditorias extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView titulo_toolbar =(TextView)findViewById(R.id.titulo_toolbar);
        titulo_toolbar.setText("Auditorías");


    }
}
