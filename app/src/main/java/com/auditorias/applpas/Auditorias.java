package com.auditorias.applpas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Auditorias extends AppCompatActivity {
    RequestQueue requestQueue;
    String id_auditor;
    String auditor;
    String tipo_usuario;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditorias);
        TextView titulo_toolbar =(TextView)findViewById(R.id.titulo_toolbar);
        titulo_toolbar.setText("Auditorías");

        Intent intent = getIntent();
        id_auditor= intent.getStringExtra("ID_AUDITOR");
        auditor = intent.getStringExtra("NOMBRE_AUDITOR");
        tipo_usuario = intent.getStringExtra("TIPO_USUARIO");

        Log.e("","Auditor"+auditor+"id"+id_auditor+"tipo"+tipo_usuario);

        // Obtenemos el LinearLayout padre
        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);



        for (int i = 0; i < 10; i++) {
            // Creamos el LinearLayout hijo
            LinearLayout linearLayoutHijo = new LinearLayout(this);
            linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);


            // Agregamos el fondo drawable al LinearLayout hijo
            linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

            // Creamos las cuatro líneas de texto
            for (int j = 0; j < 4; j++) {
                TextView textView = new TextView(this);

                textView.setText("Texto de la línea " + (j + 1));
                if(j+1==1){
                    textView.setTextSize(18);
                }
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

    //METODO PARA VERIFICAR SI EXISTE EL USUARIO
    private void consultarAuditorias(String nomina, String clave){
        String url = "https://vvnorth.com/lpa/app/verificar.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("RESPUESTA",""+response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String id_auditor = jsonResponse.getString("id_auditor");
                        String nombre_auditor = jsonResponse.getString("auditor");
                        //enviarVistaAditorias(id_auditor,nombre_auditor);
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(),"No se encontraron auditorías.",Toast.LENGTH_LONG);
                        toast.show();
                    }


                }, error -> {
            Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Usuario", nomina);
                params.put("Contrasena", clave);
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
