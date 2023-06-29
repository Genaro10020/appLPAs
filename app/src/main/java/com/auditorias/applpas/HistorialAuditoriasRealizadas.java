package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class HistorialAuditoriasRealizadas extends AppCompatActivity {
    String id_usuario, nombre, tipo_usuario,nomina_auditor;
    Button btnCerrar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_auditorias_realizadas);

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Historial");

        SharedPreferences miSession = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        nomina_auditor = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");


        TextView textViewSession = (TextView)findViewById(R.id.textUsuarioSessionH);
        textViewSession.setText("Usuario: " + nombre +" ("+nomina_auditor+")");

        consultarHistorial();


        btnCerrar = (Button)findViewById(R.id.btnCerrar) ;
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences miSession = getSharedPreferences("MiSession",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = miSession.edit();
                editor.clear();
                editor.apply();


                Intent intent = new Intent(HistorialAuditoriasRealizadas.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }



   public void consultarHistorial(){
       String url = "https://vvnorth.com/lpa/app/historialAuditoriasCerradas.php";
       StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
           Log.e("HISTORIA RESPONSE :",""+response);

           try {
               JSONArray arregloAuditorias = new JSONArray(response);
               Log.e("tamanioHistorial:",""+arregloAuditorias.length());
               if (arregloAuditorias.length()>0){

                   for (int i=0; i < arregloAuditorias.length();i++){

                       JSONObject objetoDentrodeArregloAuditorias = arregloAuditorias.getJSONObject(i);

                       // Obtenemos el LinearLayout padre
                        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);

                       // Creamos el LinearLayout hijo
                       LinearLayout linearLayoutHijo = new LinearLayout(this);
                       linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);

                       // Establecer los parámetros de diseño del LinearLayout hijo
                       LinearLayout.LayoutParams layoutParamsHijo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       LinearLayout.LayoutParams layoutParamsTextViewCentrales = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       LinearLayout.LayoutParams layoutParamsTextViewSuperior = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       LinearLayout.LayoutParams layoutParamsTextViewInferior = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                       layoutParamsTextViewSuperior.setMargins(22,22,22,0);
                       layoutParamsTextViewInferior.setMargins(22,0,22,22);
                       layoutParamsTextViewCentrales.setMargins(22,0,22,0);

                       layoutParamsHijo.setMargins(0, 14, 0, 0);


                       linearLayoutHijo.setLayoutParams(layoutParamsHijo);

                       // Agregamos el fondo drawable al LinearLayout hijo
                       linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

                       TextView textCodigo = new TextView(this);
                       TextView textTitulo = new TextView(this);
                       TextView textProceso = new TextView(this);
                       TextView textResponsable = new TextView(this);
                       TextView textRealizada = new TextView(this);
                       TextView textCalificacion = new TextView(this);

                       textCodigo.setLayoutParams(layoutParamsTextViewSuperior);
                       textTitulo.setLayoutParams(layoutParamsTextViewCentrales);
                       textProceso.setLayoutParams(layoutParamsTextViewCentrales);
                       textResponsable.setLayoutParams(layoutParamsTextViewCentrales);
                       textRealizada.setLayoutParams(layoutParamsTextViewCentrales);
                       textCalificacion.setLayoutParams(layoutParamsTextViewInferior);


                       textCodigo.setText(Html.fromHtml("<b>Codigo: </b>"+objetoDentrodeArregloAuditorias.getString("codigo")));
                       textTitulo.setText(Html.fromHtml("<b>Título: </b>"+objetoDentrodeArregloAuditorias.getString("titulo")));
                       textProceso.setText(Html.fromHtml("<b>Proceso: </b>"+objetoDentrodeArregloAuditorias.getString("proceso")));
                       textResponsable.setText(Html.fromHtml("<b>Responsable: </b>"+objetoDentrodeArregloAuditorias.getString("responsable")));
                       textRealizada.setText(Html.fromHtml("<b>Fecha Realizada: </b>"+objetoDentrodeArregloAuditorias.getString("fecha_realizada")));

                       String colorHexadecimal = "#118648";
                       String calificacion = objetoDentrodeArregloAuditorias.getString("calificacion");
                       String textoFormateado = "<b>Calificación: </b><u><font color='" + colorHexadecimal + "'>" + calificacion + "</font></u>";
                       textCalificacion.setText(Html.fromHtml(textoFormateado));

                       linearLayoutHijo.addView(textCodigo);
                       linearLayoutHijo.addView(textTitulo);
                       linearLayoutHijo.addView(textProceso);
                       linearLayoutHijo.addView(textResponsable);
                       linearLayoutHijo.addView(textRealizada);
                       linearLayoutHijo.addView(textCalificacion);
                       linearLayoutPadre.addView(linearLayoutHijo);

                   }



               }else{
                   Toast toas = Toast.makeText(getApplicationContext(),"No Existen Auditorias",Toast.LENGTH_SHORT);
                   toas.show();
               }

           } catch (JSONException e) {
               e.printStackTrace();
           }


       }, error -> {
           Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
       }){
           protected Map<String,String> getParams(){
               Map<String,String> params = new HashMap<>();
               params.put("nomina_auditor",nomina_auditor);
               return params;
           }
       };

       RequestQueue queue = Volley.newRequestQueue(this);
       queue.add(request);
   }
}
