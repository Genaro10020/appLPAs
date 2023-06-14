package com.auditorias.applpas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HallazgosResponsable extends AppCompatActivity {
    String id_usuario,nombre,tipo_usuario,num_nomina;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallazgos_responsables);

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Hallazgos");

        SharedPreferences miSession = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        num_nomina = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");

        TextView subtitulo = (TextView)findViewById(R.id.textUsuarioSession);
        subtitulo.setText("Responsable: "+nombre+" ("+num_nomina+")");

    }
}
