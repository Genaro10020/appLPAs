package com.auditorias.applpas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Auditando extends AppCompatActivity {
    String id_proceso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditando);
        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);

        Intent intent = getIntent();
        id_proceso = intent.getStringExtra("ID_PROCESO");

        Log.e("","ID_PROCESO"+id_proceso);

        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        layoutBtnDos.setVisibility(View.GONE);

        Button btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);

        TextView textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");

        titulo.setText("Auditando");
        consultarPreguntas();
    }


    private void consultarPreguntas(){
        String url = "https://vvnorth.com/lpa/app/consultarPreguntas.php";

        StringRequest respuesta = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("","Respuesta"+response);
                try {
                    JSONArray arrayPreguntas = new JSONArray(response);

                    for (int i=0; i < arrayPreguntas.length(); i++){
                        JSONObject jsonObjectPreguntas = arrayPreguntas.getJSONObject(i);

                        String pregunta =jsonObjectPreguntas.getString("pregunta");
                        String tipo_boton =jsonObjectPreguntas.getString("tipo_boton");

                        LinearLayout linearLayoutPadre = findViewById(R.id.layout_padre);
                        linearLayoutPadre.setPadding(10,10,10,0);


                        LinearLayout linearLayoutHijo = new LinearLayout(getApplicationContext());
                        linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);



                        // Agregamos el fondo drawable al LinearLayout hijo
                        linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_preguntas));


                        // Crea una nueva instancia de TextView y establece el texto
                        TextView textViewPregunta = new TextView(getApplicationContext());
                        textViewPregunta.setText(i+1+".-"+pregunta);

                        // Configura cualquier otro atributo deseado
                        textViewPregunta.setTextSize(14);


                        // Agrega el TextView al LinearLayout
                        linearLayoutHijo.addView(textViewPregunta);
                        linearLayoutHijo.setPadding(50, 10, 0, 10);




                       if (tipo_boton.equals("Si No y Na")){

                            Button boton1 = new Button(getApplicationContext());
                            boton1.setText("SI");
                            Button boton2 = new Button(getApplicationContext());
                            boton2.setText("NO");
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText("Detener la operación y solicitar que realice de acuerdo al estandar E-MI-CC");
                            textView.setBackground(getResources().getDrawable(R.drawable.fondo_detener_operacion));
                            textView.setPadding(20,0,0,0);

                            EditText editNo = new EditText(getApplicationContext());
                            editNo.setTextSize(12);
                            editNo.setBackground(getResources().getDrawable(R.drawable.fondo_edits));
                            editNo.setHint("Coloque la respuesta");


                            Button boton3 = new Button(getApplicationContext());
                            boton3.setText("N/A");



                            // Agrega los tres botones al LinearLayout hijo
                            linearLayoutHijo.addView(boton1);
                            linearLayoutHijo.addView(boton2);
                            linearLayoutHijo.addView(textView);
                            linearLayoutHijo.addView(editNo);
                            linearLayoutHijo.addView(boton3);


                        } else if(tipo_boton.equals("Si y No")){
                            Button boton1 = new Button(getApplicationContext());
                            boton1.setText("SI");

                            Button boton2 = new Button(getApplicationContext());
                            boton2.setText("NO");

                            TextView textView = new TextView(getApplicationContext());
                            textView.setText("Detener la operación y solicitar que realice de acuerdo al estandar E-MI-CC");

                            linearLayoutHijo.addView(boton1);
                            linearLayoutHijo.addView(boton2);
                            linearLayoutHijo.addView(textView);

                        }else if(tipo_boton.equals("Pregunta abierta")){
                            EditText edit = new EditText(getApplicationContext());
                            linearLayoutHijo.addView(edit);
                        }

                        // Agregamos el LinearLayout hijo al LinearLayout padre
                        // Creamos un TextView de separación
                        TextView separador = new TextView(getApplicationContext());
                        separador.setLayoutParams(new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.MATCH_PARENT));
                        linearLayoutPadre.addView(separador);
                        linearLayoutPadre.addView(linearLayoutHijo);








                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("","Error"+error);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("id_proceso", id_proceso);
                return params;
            }
        };

        RequestQueue solicitud = Volley.newRequestQueue(this);
        solicitud.add(respuesta);


    }
}
