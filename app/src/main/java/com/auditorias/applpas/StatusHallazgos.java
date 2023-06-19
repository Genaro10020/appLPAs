package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
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

        LinearLayout btnHistorial = (LinearLayout)findViewById(R.id.layoutBtnUno);
        btnHistorial.setVisibility(View.GONE);

        Button btnHallazgos = (Button)findViewById(R.id.btnHallazgos);
        btnHallazgos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusHallazgos.this,HallazgosResponsable.class);
                startActivity(intent);
            }
        });

        Button btnCerrar = (Button)findViewById(R.id.btnCerrar) ;
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences miSession = getSharedPreferences("MiSession",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = miSession.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(StatusHallazgos.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


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
                    Button[] btnAccion = new Button[arregloHallazgos.length()];

                    for (int i=0; i <arregloHallazgos.length(); i++){
                        JSONObject objetoDentroArregloHallazgos = arregloHallazgos.getJSONObject(i);

                        TextView subtitulo = (TextView)findViewById(R.id.textSubtitulo);
                        subtitulo.setText("Proceso: "+objetoDentroArregloHallazgos.getString("proceso")+", status de su plan de acción en hallazgos");

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
                        TextView textCodigoHallazgo = new TextView(this);

                        TextView textTituloStatusPlan = new TextView(this);
                        TextView textTituloStatusEvidencia = new TextView(this);
                        TextView textTituloStatusAprobacion = new TextView(this);

                        btnAccion[i] = new Button(this);
                        btnAccion[i].setLayoutParams(btnParams);

                        String texto_en_btn = null;
                        String clase = "";
                        String id_hallazgo = objetoDentroArregloHallazgos.getString("id");
                        String nombre_evaluado = objetoDentroArregloHallazgos.getString("nombre_evaluado");

                        textFecha.setText(Html.fromHtml("<b>Fecha del hallázgo: </b>" + objetoDentroArregloHallazgos.getString("fecha_realizada")));
                        textColaborador.setText(Html.fromHtml("<b>Colaborador: </b>" +nombre_evaluado+" ("+objetoDentroArregloHallazgos.getString("nomina_evaluado")+")"));
                        textPregunta.setText(Html.fromHtml("<b>Pregunta: </b>" + objetoDentroArregloHallazgos.getString("pregunta")));
                        textRespuesta.setText(Html.fromHtml("<b>Respuesta: </b>" + objetoDentroArregloHallazgos.getString("respuesta")));
                        textCodigoHallazgo.setText(Html.fromHtml("<b>Código de Hállazgo: </b>" + id_hallazgo));


                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Plan")){
                                if(bandera_plana == 0)
                                {
                                    textTituloStatusPlan.setText("Pendientes PLAN DE ACCIÓN");
                                    int Color = getResources().getColor(R.color.red_400);
                                    textTituloStatusPlan.setTextColor(Color);
                                    textTituloStatusPlan.setTypeface(null, Typeface.BOLD);
                                    textTituloStatusPlan.setLayoutParams(layoutParamsTitulos);
                                    linearLayoutPadre.addView(textTituloStatusPlan);
                                    bandera_plana=1;
                                }
                            }



                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Evidencia")){
                                if(bandera_evidencia == 0){
                                    textTituloStatusEvidencia.setText("Pendientes EVIDENCIA");
                                    int Color = getResources().getColor(R.color.red_400);
                                    textTituloStatusEvidencia.setTextColor(Color);
                                    textTituloStatusEvidencia.setLayoutParams(layoutParamsTitulos);
                                    textTituloStatusEvidencia.setTypeface(null,Typeface.BOLD);
                                    linearLayoutPadre.addView(textTituloStatusEvidencia);
                                    bandera_evidencia=1;
                                }
                            }



                            if(objetoDentroArregloHallazgos.getString("status_hallazgos").equals("Pendiente Aprobación")){
                                if(bandera_aprobacion == 0){
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

                        btnAccion[i].setPadding(10, 0, 10, 0);
                        btnAccion[i].setTextColor(Color.WHITE);
                        btnAccion[i].setTextSize(12);
                        btnAccion[i].setText(texto_en_btn);
                        btnAccion[i].setBackgroundResource(R.drawable.fondo_btn);


                        btnAccion[i].setOnClickListener(v -> {
                            Intent intent = new Intent(StatusHallazgos.this,PlanDeAccion.class);
                            intent.putExtra("ID_HALLAZGO",id_hallazgo);
                            intent.putExtra("NOMBRE_EVALUADO",nombre_evaluado);
                            intent.putExtra("CODIGO_AUDITORIA",Codigo);
                            startActivity(intent);
                        });

                        linearLayoutHijo.addView(textFecha);
                        linearLayoutHijo.addView(textColaborador);
                        linearLayoutHijo.addView(textPregunta);
                        linearLayoutHijo.addView(textRespuesta);
                        linearLayoutHijo.addView(textCodigoHallazgo);
                        linearLayoutHijo.addView(btnAccion[i]);

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
