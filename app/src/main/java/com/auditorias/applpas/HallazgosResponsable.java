package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import androidx.annotation.Nullable;
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

public class HallazgosResponsable extends AppCompatActivity {
    String id_usuario,nombre,tipo_usuario,num_nomina;
    Button btnCerrar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallazgos_responsables);

        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Hallazgos");

        TextView subTitulo = (TextView)findViewById(R.id.textSubtitulo);
        subTitulo.setText("Listado de Auditorías con Hallazgos.");

        SharedPreferences miSession = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        num_nomina = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");

        TextView subtitulo = (TextView)findViewById(R.id.textUsuarioSession);
        subtitulo.setText("Responsable: "+nombre+" ("+num_nomina+")");

        LinearLayout btnHistorial = (LinearLayout)findViewById(R.id.layoutBtnUno);
        btnHistorial.setVisibility(View.GONE);

        btnCerrar = (Button)findViewById(R.id.btnCerrar) ;
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences miSession = getSharedPreferences("MiSession",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = miSession.edit();
                editor.clear();
                editor.apply();


                Intent intent = new Intent(HallazgosResponsable.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        consultarHallazgosEnAuditorias();
    }

    public void onRestart() {
        super.onRestart();
        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);
        linearLayoutPadre.removeAllViews();//Elimina las vista para volver a cargar
        consultarHallazgosEnAuditorias();
    }

    /*public void onBackPressed() {
        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);
        linearLayoutPadre.removeAllViews(); // Elimina todos los hijos del LinearLayout padre
        consultarHallazgosEnAuditorias(); // Vuelve a consultar y cargar los hallazgos
    }*/


    public void consultarHallazgosEnAuditorias(){
        String url = "https://vvnorth.com/lpa/app/consultarHallazgosEnAuditorias.php";
        StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
            Log.e("Respuesta","consultarHallazgosEnAuditorias: "+response);

            try {
                JSONArray arregloHallazgos = new JSONArray(response);
                if(arregloHallazgos.length()>0){


                    for(int i=0; i < arregloHallazgos.length();i++) {

                        JSONObject objetoDentroArregloHallazgos = arregloHallazgos.getJSONObject(i);


                        // Obtenemos el LinearLayout padre
                        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);

                        // Creamos el LinearLayout hijo
                        LinearLayout linearLayoutHijo = new LinearLayout(this);
                        linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);

                        // Establecer los parámetros de diseño del LinearLayout hijo
                        LinearLayout.LayoutParams layoutParamsHijo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                        btnParams.gravity = Gravity.CENTER;

                        layoutParamsHijo.setMargins(5, 10, 5, 5);
                        linearLayoutHijo.setPadding(10, 10, 10, 10);

                        linearLayoutHijo.setLayoutParams(layoutParamsHijo);
                        linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

                        TextView textCodigo = new TextView(this);
                        TextView textProceso = new TextView(this);
                        TextView textCantidadHallazgos = new TextView(this);
                        TextView textEncontradas = new TextView(this);

                        String Codigo =objetoDentroArregloHallazgos.getString("codigo");

                        textCodigo.setText(Html.fromHtml("<b>Codigo: </b>" + Codigo));
                        textProceso.setText(Html.fromHtml("<b>Proceso: </b>" + objetoDentroArregloHallazgos.getString("proceso")));
                        textCantidadHallazgos.setText(Html.fromHtml("<b>Hallazgos: </b>" + objetoDentroArregloHallazgos.getString("sumaHallazgos")));
                        textEncontradas.setText(Html.fromHtml("<b>Encontrados: </b>" + objetoDentroArregloHallazgos.getString("fecha_realizada")));

                        Button btnVerHallazgos = new Button(this);
                        btnVerHallazgos.setLayoutParams(btnParams);
                        btnVerHallazgos.setPadding(10, 0, 10, 0);
                        btnVerHallazgos.setTextColor(Color.WHITE);
                        btnVerHallazgos.setTextSize(12);
                        btnVerHallazgos.setText("Estatus Hallazgo/s");
                        btnVerHallazgos.setBackgroundResource(R.drawable.fondo_btn);

                        btnVerHallazgos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HallazgosResponsable.this,StatusHallazgos.class);
                                intent.putExtra("CODIGO", Codigo);
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

                                Intent intent = new Intent(HallazgosResponsable.this,Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });





                        linearLayoutHijo.addView(textCodigo);
                        linearLayoutHijo.addView(textProceso);
                        linearLayoutHijo.addView(textCantidadHallazgos);
                        linearLayoutHijo.addView(textEncontradas);
                        linearLayoutHijo.addView(btnVerHallazgos);

                        linearLayoutPadre.addView(linearLayoutHijo);

                    }




                }else{
                    Toast.makeText(getApplicationContext(),"No existen Hallazgos",Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
        }){
            protected Map<String,String> getParams(){
                Map<String,String> parametros = new HashMap<>();
                parametros.put("num_nomina_responsable",num_nomina);
                return parametros;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }
}
