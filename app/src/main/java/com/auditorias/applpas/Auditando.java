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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Auditando extends AppCompatActivity {
    JSONArray arrayPreguntas;
    int cantidad_preguntas;


    String id_proceso;
    Button[] arregloBtnSi;
    Button[] arregloBtnNo;
    Button[] arregloBtnNA;
    EditText[] arregloEditSi;
    EditText[] arregloEditNo;
    EditText[] arregloEditNA;
    EditText[] editPreguntaAbierta;

    Object[] recopilandoRespuestas;
    String contesto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditando);
        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);

        Intent intent = getIntent();
        id_proceso = intent.getStringExtra("ID_PROCESO");

        //Log.e("","ID_PROCESO"+id_proceso);

        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        layoutBtnDos.setVisibility(View.GONE);
        TextView textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");
        titulo.setText("Auditando");

                consultarPreguntas();

        Button btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);

        cantidad_preguntas = arrayPreguntas.length();
        recopilandoRespuestas = new Object[cantidad_preguntas];

                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        class ObjetoRespuesta {
                            String pregunta;
                            String respuesta;
                        }



                        ObjetoRespuesta[] recopilandoRespuestas = new ObjetoRespuesta[arrayPreguntas.length()];

                        for (int i =0; i < arrayPreguntas.length(); i++) {
                                try {
                                    JSONObject preguntas = arrayPreguntas.getJSONObject(i);
                                    String tipo_boton = preguntas.getString("tipo_boton");
                                    String pregunta_guardar = preguntas.getString("pregunta");


                                        if (tipo_boton.equals("Si No y Na")) {
                                            if(!arregloEditSi[i].getText().toString().isEmpty()){
                                                contesto = arregloEditSi[i].getText().toString();
                                            }else if(!arregloEditNo[i].getText().toString().isEmpty()){
                                                contesto = arregloEditNo[i].getText().toString();
                                            }else if(!arregloEditNA[i].getText().toString().isEmpty()){
                                                contesto = arregloEditNA[i].getText().toString();
                                            }else{
                                                contesto="";
                                                //Log.e("Si No y Na", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                            }
                                        }else if(tipo_boton.equals("Si y No")){
                                            if(!arregloEditSi[i].getText().toString().isEmpty()){
                                                contesto = arregloEditSi[i].getText().toString();
                                            }else if(!arregloEditNo[i].getText().toString().isEmpty()){
                                                contesto = arregloEditNo[i].getText().toString();
                                            }else{
                                                contesto="";
                                                //Log.e("Si y No", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                            }
                                        }else if(tipo_boton.equals("Pregunta abierta")){
                                            if(!editPreguntaAbierta[i].getText().toString().isEmpty()){
                                                contesto = editPreguntaAbierta[i].getText().toString();
                                            }else{
                                                contesto="";
                                               // Log.e("Pregunta abierta", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                            }
                                        }

                                    // Crear un objeto Pregunta con el tipo de botón y la respuesta, y agregarlo a la lista

                                    ObjetoRespuesta objRespuesta = new ObjetoRespuesta();
                                    objRespuesta.pregunta = pregunta_guardar;
                                    objRespuesta.respuesta = contesto;
                                    recopilandoRespuestas[i] = objRespuesta;
                                    Log.e("resultado",":"+recopilandoRespuestas[i]);
                                    //respuestasPreguntas.add(ObjRespuesta);
                                    //Log.e("Lista preguntas",":"+respuestasPreguntas);

                                    guardarRespuestas(i);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                        }


                        // para imprimir y verlo correctamente en consola.
                       /* for (ObjetoRespuesta respuesta : respuestasPreguntas) {
                            Log.e("Pregunta", respuesta.pregunta);
                            Log.e("Respuesta", respuesta.respuesta);
                        }*/
                    }
                });
    }


    private void guardarRespuestas(int index){
        String url = "https://vvnorth.com/lpa/app/guardandoRespuestas.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Maneja la respuesta del servidor si es necesario
                        Log.e("DESDE guardandoPHP", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja el error de la solicitud si es necesario
                        Log.e("error_php", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
               // params.put("pregunta", recopilandoRespuestas[index].pregunta);
               // params.put("respuesta", recopilandoRespuestas[index].respuesta);
                return params;
            }
        };
    }


    private void consultarPreguntas(){
        String url = "https://vvnorth.com/lpa/app/consultarPreguntas.php";

        StringRequest respuesta = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("","Respuesta"+response);
                try {
                    arrayPreguntas = new JSONArray(response);

                    cantidad_preguntas = arrayPreguntas.length();

                    arregloBtnSi = new Button[cantidad_preguntas];
                    arregloBtnNo = new Button[cantidad_preguntas];
                    arregloBtnNA = new Button[cantidad_preguntas];
                    arregloEditSi = new EditText[cantidad_preguntas];
                    arregloEditNo = new EditText[cantidad_preguntas];
                    arregloEditNA = new EditText[cantidad_preguntas];
                    editPreguntaAbierta = new EditText[cantidad_preguntas];


                   // Log.e("Largo del los arreglos","Cantidad"+cantidad_preguntas);
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
                        linearLayoutHijo.setPadding(50, 10, 50, 10);


                       if (tipo_boton.equals("Si No y Na")){
                                   arregloBtnSi[i] = new Button(getApplicationContext());
                                   arregloBtnSi[i].setText("SI");

                                       arregloEditSi[i] = new EditText(getApplicationContext());
                                       arregloEditSi[i].setTextSize(12);
                                       arregloEditSi[i].setPadding(10,0,10,0);
                                       LinearLayout.LayoutParams parametrosEditSi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                       parametrosEditSi.setMargins(0,10,0,0);
                                       arregloEditSi[i].setLayoutParams(parametrosEditSi);
                                       arregloEditSi[i].setBackground(getResources().getDrawable(R.drawable.fondo_edits_preguntas));
                                       arregloEditSi[i].setHint("Coloque la respuesta");
                                       arregloEditSi[i].setVisibility(View.GONE);


                                       TextView textView = new TextView(getApplicationContext());
                                       textView.setText("Detener la operación y solicitar que realice de acuerdo al estándar E-MI-CC");
                                       textView.setBackground(getResources().getDrawable(R.drawable.fondo_detener_operacion));
                                       textView.setPadding(20,0,0,0);
                                       textView.setVisibility(View.GONE);


                                   arregloBtnNo[i] = new Button(getApplicationContext());
                                   arregloBtnNo[i].setText("NO");


                                       arregloEditNo[i] = new EditText(getApplicationContext());
                                       arregloEditNo[i].setTextSize(12);
                                       arregloEditNo[i].setPadding(10,0,10,0);
                                       LinearLayout.LayoutParams parametrosEditNo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                       parametrosEditNo.setMargins(0,10,0,0);
                                       arregloEditNo[i].setLayoutParams(parametrosEditNo);
                                       arregloEditNo[i].setBackground(getResources().getDrawable(R.drawable.fondo_edits_preguntas));
                                       arregloEditNo[i].setHint("Coloque la respuesta");
                                       arregloEditNo[i].setVisibility(View.GONE);


                                   arregloBtnNA[i] = new Button(getApplicationContext());
                                   arregloBtnNA[i].setText("N/A");

                                       arregloEditNA[i] = new EditText(getApplicationContext());
                                       arregloEditNA[i].setTextSize(12);
                                       arregloEditNA[i].setPadding(10,0,10,0);
                                       LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                       parametros.setMargins(0,10,0,0);
                                       arregloEditNA[i].setLayoutParams(parametros);
                                       arregloEditNA[i].setBackground(getResources().getDrawable(R.drawable.fondo_edits_preguntas));
                                       arregloEditNA[i].setHint("Coloque la respuesta");
                                       arregloEditNA[i].setVisibility(View.GONE);

                                   int finalI = i;

                                   arregloBtnSi[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            arregloEditNo[finalI].setText("");
                                            arregloEditNA[finalI].setText("");
                                            arregloEditSi[finalI].setVisibility(View.VISIBLE);
                                            arregloEditNo[finalI].setVisibility(View.GONE);
                                            textView.setVisibility(View.GONE);
                                            arregloEditNA[finalI].setVisibility(View.GONE);
                                            respuesta(finalI);
                                        }
                                    });


                                   arregloBtnNo[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            arregloEditSi[finalI].setText("");
                                            arregloEditNA[finalI].setText("");
                                            arregloEditSi[finalI].setVisibility(View.GONE);
                                            textView.setVisibility(View.VISIBLE);
                                            arregloEditNA[finalI].setVisibility(View.GONE);
                                            arregloEditNo[finalI].setVisibility(View.VISIBLE);
                                            respuesta(finalI);
                                        }
                                    });


                                   arregloBtnNA[i].setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           arregloEditSi[finalI].setText("");
                                           arregloEditNo[finalI].setText("");
                                           arregloEditSi[finalI].setVisibility(View.GONE);
                                           arregloEditNo[finalI].setVisibility(View.GONE);
                                           textView.setVisibility(View.GONE);
                                           arregloEditNA[finalI].setVisibility(View.VISIBLE);
                                           respuesta(finalI);
                                       }
                                   });



                                    // Agrega los tres botones al LinearLayout hijo
                                    linearLayoutHijo.addView(arregloBtnSi[i]);
                                    linearLayoutHijo.addView(arregloEditSi[i]);
                                    linearLayoutHijo.addView(arregloBtnNo[i]);
                                    linearLayoutHijo.addView(textView);
                                    linearLayoutHijo.addView(arregloEditNo[i]);
                                    linearLayoutHijo.addView(arregloBtnNA[i]);
                                    linearLayoutHijo.addView(arregloEditNA[i]);
                                    //linearLayoutHijo.addView(boton3);

                        } else if(tipo_boton.equals("Si y No")){

                                   arregloBtnSi[i] = new Button(getApplicationContext());
                                   arregloBtnSi[i].setText("SI");

                                   arregloEditSi[i] = new EditText(getApplicationContext());
                                   arregloEditSi[i].setTextSize(12);
                                   arregloEditSi[i].setPadding(10,0,10,0);
                                   LinearLayout.LayoutParams parametrosEditSi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                   parametrosEditSi.setMargins(0,10,0,0);
                                   arregloEditSi[i].setLayoutParams(parametrosEditSi);
                                   arregloEditSi[i].setBackground(getResources().getDrawable(R.drawable.fondo_edits_preguntas));
                                   arregloEditSi[i].setHint("Coloque la respuesta");
                                   arregloEditSi[i].setVisibility(View.GONE);


                                   TextView textView = new TextView(getApplicationContext());
                                   textView.setText("Detener la operación y solicitar que realice de acuerdo al estandar E-MI-CC");
                                   textView.setBackground(getResources().getDrawable(R.drawable.fondo_detener_operacion));
                                   textView.setPadding(20,0,0,0);
                                   textView.setVisibility(View.GONE);


                                   arregloBtnNo[i] = new Button(getApplicationContext());
                                   arregloBtnNo[i].setText("NO");


                                   arregloEditNo[i] = new EditText(getApplicationContext());
                                   arregloEditNo[i].setTextSize(12);
                                   arregloEditNo[i].setPadding(10,0,10,0);
                                   LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                   parametros.setMargins(0,10,0,0);
                                   arregloEditNo[i].setLayoutParams(parametros);
                                   arregloEditNo[i].setBackground(getResources().getDrawable(R.drawable.fondo_edits_preguntas));
                                   arregloEditNo[i].setHint("Coloque la respuesta");
                                   arregloEditNo[i].setVisibility(View.GONE);

                                   int finalI = i;

                                   arregloBtnSi[i].setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           arregloEditSi[finalI].setVisibility(View.VISIBLE);
                                           textView.setVisibility(View.GONE);
                                           arregloEditNo[finalI].setVisibility(View.GONE);
                                           respuesta(finalI);
                                       }
                                   });

                                   arregloBtnNo[i].setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           arregloEditSi[finalI].setVisibility(View.GONE);
                                           textView.setVisibility(View.VISIBLE);
                                           arregloEditNo[finalI].setVisibility(View.VISIBLE);
                                           respuesta(finalI);
                                       }
                                   });

                                   linearLayoutHijo.addView(arregloBtnSi[i]);
                                   linearLayoutHijo.addView(arregloEditSi[i]);
                                   linearLayoutHijo.addView(arregloBtnNo[i]);
                                   linearLayoutHijo.addView(textView);
                                   linearLayoutHijo.addView(arregloEditNo[i]);


                        }else if(tipo_boton.equals("Pregunta abierta")){
                            editPreguntaAbierta[i] = new EditText(getApplicationContext());
                            linearLayoutHijo.addView(editPreguntaAbierta[i]);
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

    public void respuesta(int respuesta){
        Log.e("",""+respuesta);
    }
}
