package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StatusHallazgos extends AppCompatActivity {
    String id_usuario,nombre,tipo_usuario,num_nomina, Codigo;
    public void onCreate( Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_status_hallazgos);

        SharedPreferences miSession = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        num_nomina = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");

        Intent intent = getIntent();
        Codigo = intent.getStringExtra("CODIGO");

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Status Hallazgos");

        TextView textSession = (TextView)findViewById(R.id.textUsuarioSession);
        textSession.setText("Responsable: "+nombre+" ("+num_nomina+")");

        TextView subtitulo = (TextView)findViewById(R.id.textSubtitulo);
        subtitulo.setText("Status de su plan de acción en hallazgos");

        statusHallazgos();

    }


    public void statusHallazgos(){
        String url = "https://vvnorth.com/lpa/app/statusHallazgos.php";
        StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
            Log.i("Repuesta","statusHallazgos: "+response);
            try {
                JSONArray arregloHallazgos = new JSONArray(response);

                if (arregloHallazgos.length()>0){

                    int bandera_plana = 0; int bandera_evidencia = 0; int bandera_aprobacion = 0;

                    for (int i=0; i <arregloHallazgos.length(); i++){
                        JSONObject objetoDentroArregloHallazgos = arregloHallazgos.getJSONObject(i);


                        // Obtenemos el LinearLayout padre
                        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);

                        // Creamos el LinearLayout hijo
                        LinearLayout linearLayoutHijo = new LinearLayout(this);
                        linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);

                        // Establecer los parámetros de diseño del LinearLayout hijo
                        LinearLayout.LayoutParams layoutParamsHijo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParamsTitulos = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsTitulos.setMargins(0,25,0,10);
                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                        btnParams.gravity = Gravity.CENTER;
                        btnParams.setMargins(0,0,0,10);

                        layoutParamsHijo.setMargins(5, 10, 5, 10);
                        linearLayoutHijo.setPadding(10, 10, 10, 10);

                        linearLayoutHijo.setLayoutParams(layoutParamsHijo);
                        linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

                        TextView textFecha = new TextView(this);
                        TextView textColaborador = new TextView(this);
                        TextView textPregunta = new TextView(this);
                        TextView textRespuesta = new TextView(this);

                        TextView textTituloStatusPlan = new TextView(this);
                        TextView textTituloStatusEvidencia = new TextView(this);
                        TextView textTituloStatusAprobacion = new TextView(this);

                        Button btnAccion = new Button(this);
                        btnAccion.setLayoutParams(btnParams);

                        String texto_en_btn = null;
                        if(bandera_plana == 0){
                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Plan")){
                                textTituloStatusPlan.setText("Pendientes PLAN DE ACCIÓN");
                                int Color = getResources().getColor(R.color.red_400);
                                textTituloStatusPlan.setTextColor(Color);
                                textTituloStatusPlan.setTypeface(null, Typeface.BOLD);
                                textTituloStatusPlan.setLayoutParams(layoutParamsTitulos);
                                linearLayoutPadre.addView(textTituloStatusPlan);
                                bandera_plana=1;

                            }
                        }


                        if(bandera_evidencia == 0){
                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Evidencia")){
                                textTituloStatusEvidencia.setText("Pendientes EVIDENCIA");
                                int Color = getResources().getColor(R.color.red_400);
                                textTituloStatusEvidencia.setTextColor(Color);
                                textTituloStatusEvidencia.setLayoutParams(layoutParamsTitulos);
                                textTituloStatusEvidencia.setTypeface(null,Typeface.BOLD);
                                linearLayoutPadre.addView(textTituloStatusEvidencia);
                                bandera_evidencia=1;

                            }
                        }


                        if(bandera_aprobacion == 0){
                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Aprobación")){
                                textTituloStatusAprobacion.setText("Pendientes APROBACIÓN");
                                int Color = getResources().getColor(R.color.red_400);
                                textTituloStatusAprobacion.setTextColor(Color);
                                textTituloStatusAprobacion.setTypeface(null,Typeface.BOLD);
                                textTituloStatusAprobacion.setLayoutParams(layoutParamsTitulos);
                                linearLayoutPadre.addView(textTituloStatusAprobacion);
                                bandera_aprobacion=1;

                            }
                        }

                        if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Plan")){
                            texto_en_btn = "Crear Plan";
                        }else if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Evidencia")){
                            texto_en_btn = "Subir Evidencia";
                        }else if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Aprobación")){
                            texto_en_btn = "Ver";
                        }


                        textFecha.setText(Html.fromHtml("<b>Fecha del hallázgo: </b>" + objetoDentroArregloHallazgos.getString("fecha_realizada")));
                        textColaborador.setText(Html.fromHtml("<b>Colaborador: </b>" + objetoDentroArregloHallazgos.getString("nombre_evaluado") +" ("+objetoDentroArregloHallazgos.getString("nomina_evaluado")+")"));
                        textPregunta.setText(Html.fromHtml("<b>Pregunta: </b>" + objetoDentroArregloHallazgos.getString("pregunta")));
                        textRespuesta.setText(Html.fromHtml("<b>Respuesta: </b>" + objetoDentroArregloHallazgos.getString("respuesta")));

                        btnAccion.setPadding(10, 0, 10, 0);
                        btnAccion.setTextColor(Color.WHITE);
                        btnAccion.setTextSize(12);
                        btnAccion.setText(texto_en_btn);
                        btnAccion.setBackgroundResource(R.drawable.fondo_btn);

                        linearLayoutHijo.addView(textFecha);
                        linearLayoutHijo.addView(textColaborador);
                        linearLayoutHijo.addView(textPregunta);
                        linearLayoutHijo.addView(textRespuesta);
                        linearLayoutHijo.addView(btnAccion);

                        linearLayoutPadre.addView(linearLayoutHijo);

                    }





                }else{
                    Toast.makeText(getApplicationContext(),"No se encontraron Hallazgos",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error -> {
            Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", Codigo);
                params.put("nomina_responsable", num_nomina);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

}
