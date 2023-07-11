package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class Auditorias extends AppCompatActivity {
    RequestQueue requestQueue;
    String id_usuario;
    String num_nomina;
    String nombre;
    String tipo_usuario;
    Button btnCerrar,btnHistorial;
    TextView texto_auditorias_pendientes;
    LinearLayout linearLayoutPadre;

    //final Context context = getApplicationContext();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // onBackPresed();//Cuando presione atras volvera a cargar la auditoria.


        setContentView(R.layout.activity_auditorias);
        TextView titulo_toolbar =(TextView)findViewById(R.id.titulo_toolbar);

        titulo_toolbar.setText("Auditorías");

        texto_auditorias_pendientes = (TextView)findViewById(R.id.textView2);
        texto_auditorias_pendientes.setText("Auditorias Pendientes");

        btnCerrar = (Button)findViewById(R.id.btnCerrar) ;
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences miSession = getSharedPreferences("MiSession",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = miSession.edit();
                editor.clear();
                editor.apply();


                Intent intent = new Intent(Auditorias.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnHistorial = (Button)findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Auditorias.this,HistorialAuditoriasRealizadas.class);
                startActivity(intent);
            }
        });

       /* Intent intent = getIntent();
        id_usuario= intent.getStringExtra("ID_USUARIO");
        nombre = intent.getStringExtra("NOMBRE");
        tipo_usuario = intent.getStringExtra("TIPO_USUARIO");
        Log.e("","Auditor"+nombre+"id"+id_usuario+"tipo"+tipo_usuario);*/

        SharedPreferences miSession = getSharedPreferences("MiSession",Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        num_nomina = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");

        TextView textViewSession = (TextView)findViewById(R.id.textUsuarioSession) ;
        textViewSession.setText("Usuario: " + nombre +" ("+num_nomina+")");
    }

    protected void onResume(){
        super.onResume();

        if(tipo_usuario.equals("Auditor")){
            Log.e("Es Auditor","SI");

            consultarAuditorias();
        }

    }

    /*Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            consultarAuditorias();
        }
    }*/



    //CONSULTA LAS AUDITORÍAS DEL ID_AUDITOR CORRESPONDIENTE.
    private void consultarAuditorias(){

        linearLayoutPadre = findViewById(R.id.layoutPrincipal);
        linearLayoutPadre.removeAllViews(); // Limpiar vistas anteriores

        String url = "https://vvnorth.com/lpa/app/consultarAuditorias.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("Auditoria", "" + response);
                    try {
                        JSONArray auditoriasArreglo = new JSONArray(response); //arreglo

                        if (auditoriasArreglo.length() != 0) {
                                    for (int i = 0; i < auditoriasArreglo.length(); i++) {

                                        // posicion de arreglo en objeto para extraer
                                        JSONObject jsonObjectAuditoria = auditoriasArreglo.getJSONObject(i);
                                        String id_area = jsonObjectAuditoria.getString("id_area");
                                        String id_proceso = jsonObjectAuditoria.getString("id_proceso");
                                        String codigo = jsonObjectAuditoria.getString("codigo");
                                        String area = jsonObjectAuditoria.getString("area");
                                        String titulo = jsonObjectAuditoria.getString("titulo");
                                        String proceso = jsonObjectAuditoria.getString("proceso");
                                        String nomina_responsable = jsonObjectAuditoria.getString("nomina");
                                        String responsable = jsonObjectAuditoria.getString("responsable");
                                        String fecha_programada = jsonObjectAuditoria.getString("fecha");
                                        String descripcion = jsonObjectAuditoria.getString("descripcion");




                                        // Obtenemos el LinearLayout padre
                                        linearLayoutPadre = findViewById(R.id.layoutPrincipal);

                                        // Creamos el LinearLayout hijo
                                        LinearLayout linearLayoutHijo = new LinearLayout(this);
                                        linearLayoutHijo.setOrientation(LinearLayout.VERTICAL);

                                        // Agregamos el fondo drawable al LinearLayout hijo
                                        linearLayoutHijo.setBackground(getResources().getDrawable(R.drawable.fondo_lista_auditorias));

                                        // Creamos las cuatro líneas de texto
                                        for (int j = 0; j < 6; j++) {
                                            TextView textView = new TextView(this);
                                            textView.setPadding(20, 0, 0, 0);
                                            if (j + 1 == 1) {
                                                //textView.setTextSize(18);
                                                //textView.setTypeface(null, Typeface.BOLD);
                                                textView.setText(Html.fromHtml("<b>Titulo:</b> " + jsonObjectAuditoria.getString("titulo")));
                                            } else if (j + 1 == 2) {
                                                textView.setText(Html.fromHtml("<b>Proceso:</b> " + jsonObjectAuditoria.getString("proceso")));
                                            } else if (j + 1 == 3) {
                                                textView.setText(Html.fromHtml("<b>Responsable:</b>" + jsonObjectAuditoria.getString("responsable")));
                                            } else if (j + 1 == 4) {
                                            textView.setText(Html.fromHtml("<b>Fecha programada:</b>" + jsonObjectAuditoria.getString("fecha")));
                                            } else if (j + 1 == 5) {
                                                textView.setText(Html.fromHtml("<b>Descripción:</b>" + jsonObjectAuditoria.getString("descripcion")));
                                            } else if (j + 1 == 6) {
                                                textView.setPadding(20, 0, 0, 20);
                                                textView.setText(Html.fromHtml("<b>Código:</b>"+codigo));
                                            }

                                            linearLayoutHijo.addView(textView);


                                        }

                                        //Creamos botton y atributos
                                        Button button = new Button(this);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                                        layoutParams.gravity = Gravity.CENTER;
                                        button.setLayoutParams(layoutParams);
                                        button.setPadding(10, 0, 10, 0);
                                        button.setTextColor(Color.WHITE);
                                        button.setTextSize(12);
                                        button.setText("Iniciar Auditoría");
                                        //Agregando el boton y agregnado padding.
                                        linearLayoutHijo.addView(button);
                                        linearLayoutHijo.setPadding(50, 10, 0, 50);
                                        // Set background drawable for the button
                                        Drawable drawable = getResources().getDrawable(R.drawable.fondo_btn);
                                        button.setBackground(drawable);

                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                               // Log.e("","Precionaste"+id_proceso);
                                                enviarAuditando(id_area, id_proceso,codigo,area,titulo,proceso,responsable,fecha_programada,descripcion,nomina_responsable,num_nomina);
                                            }
                                        });

                                        // Creamos un TextView de separación
                                        TextView separador = new TextView(this);
                                        separador.setLayoutParams(new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.MATCH_PARENT));


                                        // Agregamos el LinearLayout hijo al LinearLayout padre
                                        linearLayoutPadre.addView(linearLayoutHijo);
                                        linearLayoutPadre.addView(separador);

                                    }
                        } else {
                                        //Log.e("", "ARREGLO VACIO");
                                        View viewToast = getLayoutInflater().inflate(R.layout.mensaje_positivo, (ViewGroup) findViewById(R.id.layout_positivo));
                                        TextView tituloMensaje=viewToast.findViewById(R.id.tituloMensaje);
                                        TextView cuerpoMensaje=viewToast.findViewById(R.id.cuerpoMensaje);
                                        tituloMensaje.setText("SIN AUDITORÍAS");
                                        cuerpoMensaje.setText("No cuenta con auditorías disponibles.");
                                        Toast toast = new Toast(this);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(viewToast);
                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                        }
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Posiblemente conexión de Internet.", Toast.LENGTH_LONG);
                        toast.show();
                    }


                }, error -> {
            Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", id_usuario);
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


   private void enviarAuditando(String id_area, String id_proceso,String codigo ,String area, String titulo, String proceso,String responsable, String fecha_programada, String descripcion, String nomina_responsable,String nomina_auditor){
        Intent intent = new Intent(Auditorias.this, Auditando.class);
        intent.putExtra("NOMBRE_AUDITOR",nombre);
        intent.putExtra("CODIGO",codigo);
        intent.putExtra("AREA",area);
        intent.putExtra("ID_AREA",id_area);
        intent.putExtra("ID_PROCESO",id_proceso);

        intent.putExtra("TITULO",titulo);
        intent.putExtra("PROCESO",proceso);
        intent.putExtra("NOMINA_AUDITOR",nomina_auditor);
        intent.putExtra("NOMINA_RESPONSABLE",nomina_responsable);
        intent.putExtra("RESPONSABLE",responsable);
        intent.putExtra("FECHA_PROGRAMADA",fecha_programada);
        intent.putExtra("DESCRIPCION",descripcion);
        //startActivity(intent);
        startActivityForResult(intent, 1);
   }
}
