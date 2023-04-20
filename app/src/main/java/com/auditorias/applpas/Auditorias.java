package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
    String nombre;
    String tipo_usuario;
    //final Context context = getApplicationContext();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditorias);
        TextView titulo_toolbar =(TextView)findViewById(R.id.titulo_toolbar);
        TextView textViewSession = (TextView)findViewById(R.id.textUsuarioSession) ;
        titulo_toolbar.setText("Auditorías");

        Intent intent = getIntent();
        id_usuario= intent.getStringExtra("ID_USUARIO");
        nombre = intent.getStringExtra("NOMBRE");
        tipo_usuario = intent.getStringExtra("TIPO_USUARIO");
        Log.e("","Auditor"+nombre+"id"+id_usuario+"tipo"+tipo_usuario);




        if(tipo_usuario.equals("Auditor")){
            Log.e("Es Auditor","SI");
            textViewSession.setText("Auditor:"+ nombre);
            consultarAuditorias();
        }else{
            textViewSession.setText("Responsable: "+ nombre);
            Log.e("Es Auditor","NO");
        }


    }

    //CONSULTA LAS AUDITORÍAS DEL ID_AUDITOR CORRESPONDIENTE.
    private void consultarAuditorias(){
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
                                        String id_proceso = jsonObjectAuditoria.getString("id_proceso");

                                        // Obtenemos el LinearLayout padre
                                        LinearLayout linearLayoutPadre = findViewById(R.id.layoutPrincipal);

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
                                                textView.setText(Html.fromHtml("<b>Estatus:</b>" + "PENDIENTE"));
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
                                                Log.e("","Precionaste"+id_proceso);
                                                enviarAuditando(id_proceso);
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
                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 300, 10);
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


   private void enviarAuditando(String id_proceso){
        Intent intent = new Intent(Auditorias.this, Auditando.class);
        intent.putExtra("ID_PROCESO",id_proceso);
        startActivity(intent);
   }
}
