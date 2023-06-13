package com.auditorias.applpas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HistorialAuditoriasRealizadas extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_auditoria);

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Historial");




    }
}
