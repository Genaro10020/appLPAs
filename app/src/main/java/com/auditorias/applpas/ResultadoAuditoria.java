package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;


public class ResultadoAuditoria extends AppCompatActivity {

    String Codigo;
    int tamanioArreglo;
    JSONArray arregloConsulta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_auditoria);

        Intent intent = getIntent();
        Codigo = intent.getStringExtra("CODIGO");

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Resultado");

        consultarAuditoriaCodigo();
    }


    public void consultarAuditoriaCodigo(){

        String url = "https://vvnorth.com/lpa/app/resultadoAuditoria.php";

        StringRequest solicitudStringResquest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuestaResultados:",response);
                        try {
                            arregloConsulta = new JSONArray(response);

                            if (arregloConsulta.length()>=1){
                                tamanioArreglo = arregloConsulta.length();
                                mostrandoAuditoriaHallazgos();
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"No contiene preguntas",Toast.LENGTH_LONG);
                                toast.show();
                            }

                        } catch (JSONException e) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Error, posible de conexión"+e,Toast.LENGTH_LONG);
                            toast.show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toas = Toast.makeText(getApplicationContext(),"Posible desconexión de internet, "+error,Toast.LENGTH_SHORT);
                        toas.show();
            }
        }){
          public Map<String,String> getParams(){
              Map<String,String> parametros = new HashMap<>();
              parametros.put("Codigo",Codigo);
              return parametros;
          }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(solicitudStringResquest);
    }


    public void mostrandoAuditoriaHallazgos() throws JSONException {
        Log.e("Tamanio Arreglo",""+tamanioArreglo);
        LinearLayout linearLayout = findViewById(R.id.layout_padre);

        for(int i=0; i < tamanioArreglo;i++){

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsHallazgo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsRespuesta = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);



            params.setMargins(10,0,10,10);
            paramsHallazgo.setMargins(20,0,10,10);
            paramsRespuesta.setMargins(10,1,10,5);


            int colorSubtitulos = R.color.red_400;// lo estoy tomando de res/values/colors.xml
            int textColorRojoObscuro = ContextCompat.getColor(this, colorSubtitulos);

            int colorRespuesta = R.color.respuestaHallazgo;
            int colorRespuestaHallazgos = ContextCompat.getColor(this,colorRespuesta);

            int colorCalificacion = R.color.calificacion;
            int numcolorCalificacion = ContextCompat.getColor(this,colorCalificacion);

            JSONObject objeto = arregloConsulta.getJSONObject(i);
            String pregunta = objeto.getString("pregunta");
            String respuesta = objeto.getString("respuesta");
            String contable = objeto.getString("contable");
            String btn_seleccionado = objeto.getString("btn_seleccionado");
            String Calificacion = objeto.getString("calificacion");

            TextView textViewPregunta = new TextView(this);
            TextView textViewRespuesta = new TextView(this);
            textViewRespuesta.setLayoutParams(paramsRespuesta);



           if (i==0){//Muestro la palabra resultado en la parte superior 1 vez y muestro la primer pregunta, que es la pregunta Numero de nomina
               TextView textoResultado = new TextView(this);
               textoResultado.setLayoutParams(params);
               textoResultado.setText("Resultados");
               textoResultado.setTypeface(null, Typeface.BOLD);
               textoResultado.setGravity(Gravity.CENTER);
               textoResultado.setTextColor(textColorRojoObscuro);
               linearLayout.addView(textoResultado);

               textViewPregunta.setText((i+1)+".-"+pregunta);
               textViewRespuesta.setTypeface(null, Typeface.BOLD);
               textViewRespuesta.setText(respuesta);


           }
           if(i==1){// muestro la segunda pregunta, que es la pregunta Nombre del evaluado.
               textViewPregunta.setText((i+1)+".-"+pregunta);
               textViewRespuesta.setTypeface(null, Typeface.BOLD);
               textViewRespuesta.setText(respuesta);
           }
           if (i==2){
               TextView textViewHallazgos = new TextView(this);//AGREGO EL TITULO HALLAZGOS
               textViewHallazgos.setText("Hallazgos");
               textViewHallazgos.setTypeface(null, Typeface.BOLD);
               textViewHallazgos.setGravity(Gravity.CENTER);
               textViewHallazgos.setLayoutParams(paramsHallazgo);
               textViewHallazgos.setTextColor(textColorRojoObscuro);
               linearLayout.addView(textViewHallazgos);
           }
           if(i>=2){
                   if (contable.equals("Si") && btn_seleccionado.equals("NO")){
                       textViewPregunta.setText((i+1)+".-"+pregunta);
                       textViewRespuesta.setTextColor(colorRespuestaHallazgos);
                       textViewRespuesta.setText(respuesta);
                   }
           }

           if (tamanioArreglo-1 == i){
               TextView textCalificacion = new TextView(this);
               TextView numCalificacion = new TextView(this);
               textCalificacion.setTextSize(30);
               textCalificacion.setGravity(Gravity.CENTER);
               textCalificacion.setText("Calificación: ");

               numCalificacion.setTextColor(numcolorCalificacion);
               numCalificacion.setTextSize(30);
               numCalificacion.setGravity(Gravity.CENTER);
               numCalificacion.setText(Calificacion);
               numCalificacion.setTypeface(null,Typeface.BOLD);
               linearLayout.addView(textCalificacion);
               linearLayout.addView(numCalificacion);

               LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.WRAP_CONTENT,
                       (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
               layoutParams.gravity = Gravity.CENTER;

               Button btnSalir = new Button(this);
               btnSalir.setText("Mis Auditorias");
               btnSalir.setLayoutParams(layoutParams);
               btnSalir.setPadding(10, 0, 10, 0);
               btnSalir.setTextColor(Color.WHITE);
               btnSalir.setBackgroundResource(R.drawable.boton_entrar);

               linearLayout.addView(btnSalir);
               btnSalir.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(ResultadoAuditoria.this,Auditorias.class);
                       startActivity(intent);
                   }
               });
           }

           linearLayout.addView(textViewPregunta);
           linearLayout.addView(textViewRespuesta);

        }
    }
}
