package com.auditorias.applpas;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Auditorias extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditorias);
        TextView titulo_toolbar =(TextView)findViewById(R.id.titulo_toolbar);
        titulo_toolbar.setText("Auditorías");

        // Obtenemos el LinearLayout padre
        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);


        // Creamos varios LinearLayouts hijos
        for (int i = 0; i < 5; i++) {
            // Creamos el LinearLayout hijo
            LinearLayout linearLayoutHijo = new LinearLayout(this);
            linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);


            // Agregamos el fondo drawable al LinearLayout hijo
            linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

            // Creamos las cuatro líneas de texto
            for (int j = 0; j < 4; j++) {
                TextView textView = new TextView(this);
                textView.setText("Texto de la línea " + (j + 1));
                linearLayoutHijo.addView(textView);
            }


            // Creamos un TextView de separación
            TextView separador = new TextView(this);
            separador.setLayoutParams(new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.MATCH_PARENT));


            // Agregamos el LinearLayout hijo al LinearLayout padre
            linearLayoutPadre.addView(linearLayoutHijo);
            linearLayoutPadre.addView(separador);
        }



    }
}
