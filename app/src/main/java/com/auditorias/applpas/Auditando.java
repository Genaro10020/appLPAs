package com.auditorias.applpas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Auditando extends AppCompatActivity {
    JSONArray arrayPreguntas;
    int cantidad_preguntas=0;
     double CalificacionFinal= 0.0;

    TextView textUno;
    String id_proceso,Codigo,Nombre_Auditor,Titulo,Proceso,Responsable,Fecha_Programada,Descripcion;
    String[] contableSINO;
    Button[] arregloBtnSi;
    Button[] arregloBtnNo;
    Button[] arregloBtnNA;
    EditText[] arregloEditSi;
    EditText[] arregloEditNo;
    EditText[] arregloEditNA;
    EditText[] editPreguntaAbierta;
    String[] arregloBtnSeleccionado;

    ObjetoRespuesta[] recopilandoRespuestas;
    String contesto;
    RequestQueue queue;
    Button btnGuardar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditando);
        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);

        Intent intent = getIntent();
        Nombre_Auditor = intent.getStringExtra("NOMBRE_AUDITOR");
        id_proceso = intent.getStringExtra("ID_PROCESO");
        Codigo = intent.getStringExtra("CODIGO");
        Titulo = intent.getStringExtra("TITULO");
        Proceso = intent.getStringExtra("PROCESO");
        Responsable = intent.getStringExtra("RESPONSABLE");
        Fecha_Programada = intent.getStringExtra("FECHA_PROGRAMADA");
        Descripcion = intent.getStringExtra("DESCRIPCION");

        ;


        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        layoutBtnDos.setVisibility(View.GONE);
        textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");
        titulo.setText("Auditando");

                consultarPreguntas();

        btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);

        queue = Volley.newRequestQueue(getApplicationContext());



        btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int CantidadPreguntasContables=0;
                        int CantidadRespuestasSI=0;
                        int CantidadRespuestasNO=0;
                        int CantidadRespuestasNA=0;
                        if (verificarConexionInternet()) {
                                    int sumaVacias =0;
                                    for (int i =0; i < cantidad_preguntas; i++) {
                                            try {
                                                JSONObject preguntas = arrayPreguntas.getJSONObject(i);
                                                String tipo_boton = preguntas.getString("tipo_boton");
                                                String pregunta_guardar = preguntas.getString("pregunta");




                                                    if (tipo_boton.equals("Si No y Na")) {

                                                        if(contableSINO[i].equals("Si")){//Contabilizo cantidad preguntas contables
                                                            CantidadPreguntasContables++;
                                                        }

                                                        if(!arregloEditSi[i].getText().toString().isEmpty()){
                                                            CantidadRespuestasSI++;
                                                            contesto = arregloEditSi[i].getText().toString();
                                                        }else if(!arregloEditNo[i].getText().toString().isEmpty()){
                                                            CantidadRespuestasNO++;
                                                            contesto = arregloEditNo[i].getText().toString();
                                                        }else if(!arregloEditNA[i].getText().toString().isEmpty()){
                                                            contesto = arregloEditNA[i].getText().toString();
                                                            CantidadRespuestasNA++;
                                                        }else{
                                                            contesto="";
                                                            sumaVacias++;
                                                            //Log.e("Si No y Na", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                                        }
                                                    }else if(tipo_boton.equals("Si y No")){

                                                        if(contableSINO[i].equals("Si")){//Contabilizo cantidad preguntas contables
                                                            CantidadPreguntasContables++;
                                                        }

                                                        if(!arregloEditSi[i].getText().toString().isEmpty()){
                                                            CantidadRespuestasSI++;
                                                            contesto = arregloEditSi[i].getText().toString();
                                                        }else if(!arregloEditNo[i].getText().toString().isEmpty()){
                                                            CantidadRespuestasNO++;
                                                            contesto = arregloEditNo[i].getText().toString();
                                                        }else{
                                                            contesto="";
                                                            sumaVacias++;
                                                            //Log.e("Si y No", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                                        }
                                                    }else if(tipo_boton.equals("Pregunta abierta")){
                                                        if(!editPreguntaAbierta[i].getText().toString().isEmpty()){
                                                            contesto = editPreguntaAbierta[i].getText().toString();
                                                        }else{
                                                            contesto="";
                                                            sumaVacias++;
                                                           // Log.e("Pregunta abierta", "FAVOR CONTESTE LA PREGUNTA"+(i+1));
                                                        }
                                                    }

                                                // Crear un objeto Pregunta con el tipo de botón y la respuesta, y agregarlo a la lista

                                                ObjetoRespuesta objRespuesta = new ObjetoRespuesta();
                                                objRespuesta.pregunta = pregunta_guardar;
                                                objRespuesta.respuesta = contesto;
                                                recopilandoRespuestas[i] = objRespuesta;
                                                //Log.e("resultado",":"+recopilandoRespuestas[i]);
                                                //respuestasPreguntas.add(ObjRespuesta);
                                                //Log.e("Lista preguntas",":"+respuestasPreguntas);




                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                    }


                                    if (sumaVacias==0){

                                       int SumaContables = CantidadPreguntasContables-CantidadRespuestasNA;
                                       CalificacionFinal = ((double) CantidadRespuestasSI * 100) / (double) SumaContables;

                                       //Log.e("Calificacion",":"+CalificacionFinal+"Contable"+CantidadPreguntasContables);


                                        btnGuardar.setVisibility(View.GONE);
                                        textUno.setText("Guardando Auditoria.....espere");
                                            for (int i =0; i < cantidad_preguntas; i++) {
                                                JSONObject preguntas = null;
                                                String tipo_boton = "";
                                                        try {
                                                            preguntas = arrayPreguntas.getJSONObject(i);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        try {
                                                            tipo_boton = preguntas.getString("tipo_boton");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                                guardarRespuestas(i,tipo_boton);
                                            }
                                    }else{
                                        Toast toas = Toast.makeText(getApplicationContext(),"Conteste todas las preguntas",Toast.LENGTH_LONG);
                                        toas.show();
                                    }
                        } else {
                            Toast toas = Toast.makeText(getApplicationContext(),"No hay internet, verifica.",Toast.LENGTH_LONG);
                            toas.show();
                        }
                    }
                });
    }

    class ObjetoRespuesta {
        String pregunta;
        String respuesta;
    }


    private boolean verificarConexionInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    private void guardarRespuestas(int index, String tipo_boton){
        String url = "https://vvnorth.com/lpa/app/guardandoAuditoria.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Maneja la respuesta del servidor si es necesario

                        try {
                            JSONObject jsonObject  = new JSONObject(response);
                            String mensaje = jsonObject.getString("mensaje");
                            Log.e("DESDE guardandoPHP", response);

                            if (mensaje.equals("Guardado Completo")){
                                btnGuardar.setVisibility(View.VISIBLE);
                                textUno.setText("Guardar");
                                Intent intent = new Intent (getApplicationContext(), ResultadoAuditoria.class);
                                intent.putExtra("CODIGO",Codigo);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            btnGuardar.setVisibility(View.VISIBLE);
                            textUno.setText("Guardar");
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnGuardar.setVisibility(View.VISIBLE);
                        textUno.setText("Guardar");
                        // Maneja el error de la solicitud si es necesario
                        Log.e("error_php", error.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),"Internet no estable, Guarde Nuevamente.",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("total_preguntas", String.valueOf(cantidad_preguntas));
                params.put("codigo",Codigo);
                params.put("titulo",Titulo);
                params.put("proceso",Proceso);
                params.put("auditor",Nombre_Auditor);
                params.put("responsable",Responsable);
                params.put("descripcion",Descripcion);
                params.put("fecha_programada",Fecha_Programada);

                params.put("num_pregunta", String.valueOf(index+1));
                params.put("pregunta", recopilandoRespuestas[index].pregunta);
                params.put("respuesta", recopilandoRespuestas[index].respuesta);
                params.put("contable",contableSINO[index]);

                params.put("btn_tipo",tipo_boton);
                params.put("btn_seleccionado",arregloBtnSeleccionado[index]);
                params.put("calificacion_final", String.valueOf(CalificacionFinal));

                return params;
            }
        };
        queue.add(request);
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
                    recopilandoRespuestas = new ObjetoRespuesta[cantidad_preguntas];// este arreglo de objetos lo utililizare para guardar las preguntas, al presinar el bóton Guardar.
                    contableSINO = new String[cantidad_preguntas];
                    arregloBtnSi = new Button[cantidad_preguntas];
                    arregloBtnNo = new Button[cantidad_preguntas];
                    arregloBtnNA = new Button[cantidad_preguntas];
                    arregloEditSi = new EditText[cantidad_preguntas];
                    arregloEditNo = new EditText[cantidad_preguntas];
                    arregloEditNA = new EditText[cantidad_preguntas];
                    editPreguntaAbierta = new EditText[cantidad_preguntas];
                    arregloBtnSeleccionado = new String[cantidad_preguntas];


                   // Log.e("Largo del los arreglos","Cantidad"+cantidad_preguntas);
                    for (int i=0; i < arrayPreguntas.length(); i++){
                        JSONObject jsonObjectPreguntas = arrayPreguntas.getJSONObject(i);

                        String pregunta =jsonObjectPreguntas.getString("pregunta");
                        String tipo_boton =jsonObjectPreguntas.getString("tipo_boton");
                        String contable = jsonObjectPreguntas.getString("contable");



                        contableSINO[i] = contable;




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
                                            arregloBtnSeleccionado[finalI] = arregloBtnSi[finalI].getText().toString();
                                            //respuesta(finalI);
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
                                            arregloBtnSeleccionado[finalI] = arregloBtnNo[finalI].getText().toString();
                                            //respuesta(finalI);
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
                                           arregloBtnSeleccionado[finalI] = arregloBtnNA[finalI].getText().toString();
                                           //respuesta(finalI);
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
                                           arregloEditNo[finalI].setText("");
                                           arregloEditSi[finalI].setVisibility(View.VISIBLE);
                                           textView.setVisibility(View.GONE);
                                           arregloEditNo[finalI].setVisibility(View.GONE);
                                           arregloBtnSeleccionado[finalI] = arregloBtnSi[finalI].getText().toString();
                                           //respuesta(finalI);
                                       }
                                   });

                                   arregloBtnNo[i].setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           arregloEditSi[finalI].setText("");
                                           arregloEditSi[finalI].setVisibility(View.GONE);
                                           textView.setVisibility(View.VISIBLE);
                                           arregloEditNo[finalI].setVisibility(View.VISIBLE);
                                           arregloBtnSeleccionado[finalI] = arregloBtnNo[finalI].getText().toString();
                                           //respuesta(finalI);
                                       }
                                   });

                                   linearLayoutHijo.addView(arregloBtnSi[i]);
                                   linearLayoutHijo.addView(arregloEditSi[i]);
                                   linearLayoutHijo.addView(arregloBtnNo[i]);
                                   linearLayoutHijo.addView(textView);
                                   linearLayoutHijo.addView(arregloEditNo[i]);


                        }else if(tipo_boton.equals("Pregunta abierta")){
                           arregloBtnSeleccionado[i] = tipo_boton;
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
