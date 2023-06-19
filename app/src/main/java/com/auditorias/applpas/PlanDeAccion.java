package com.auditorias.applpas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlanDeAccion  extends AppCompatActivity {
    String id_usuario,nombre,tipo_usuario, codigo_auditoria, num_nomina, id_hallazgo, nombre_evaluado,plan_de_accion, feha_compromiso;
    Button btnGuardar;
    EditText editplanAccion;

    private CalendarView calendarView;
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_plan_de_accion);

        SharedPreferences miSession = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
        id_usuario = miSession.getString("ID_USUARIO","No existe ID en MiSession");
        nombre = miSession.getString("NOMBRE","No existe NOMBRE en MiSession");
        tipo_usuario = miSession.getString("TIPO_USUARIO","No existe TIPO_USUARIO en MiSession");
        num_nomina = miSession.getString("NUMERO_NOMINA","No existe NUMERO_NOMINA en MiSession");

        Intent intent = getIntent();
        id_hallazgo= intent.getStringExtra("ID_HALLAZGO");
        nombre_evaluado = intent.getStringExtra("NOMBRE_EVALUADO");
        codigo_auditoria = intent.getStringExtra("CODIGO_AUDITORIA");

        TextView textSession = (TextView)findViewById(R.id.textUsuarioSession);
        TextView titulo = (TextView)findViewById(R.id.titulo_toolbar);
        TextView subtitulo = (TextView)findViewById(R.id.textSubtitulo);
        editplanAccion = (EditText)findViewById(R.id.editPlan);

        textSession.setText("Responsable: "+nombre+" ("+num_nomina+")");
        titulo.setText("Plan de Acción"+id_hallazgo);
        subtitulo.setText("Favor de crear tu plan de acción para este hallázgo, id: "+id_hallazgo);

        btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);

        TextView textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");

        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        LinearLayout layoutBtnTres = (LinearLayout)findViewById(R.id.layoutBtnTres);
        layoutBtnDos.setVisibility(View.GONE);
        layoutBtnTres.setVisibility(View.GONE);

        calendarView = findViewById(R.id.fechaCompromiso);
        // Obtén la fecha actual
        Calendar calendar = Calendar.getInstance();
        long currentDate = calendar.getTimeInMillis();
        // Establece la fecha mínima como la fecha actual
        calendarView.setMinDate(currentDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Aquí puedes obtener la fecha seleccionada y realizar las acciones necesarias
                feha_compromiso = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getApplicationContext(), "Su fecha compromiso: " + feha_compromiso, Toast.LENGTH_SHORT).show();
            }
        });

                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        plan_de_accion = editplanAccion.getText().toString();

                        if(feha_compromiso != null && plan_de_accion != null && !feha_compromiso.equals("") && !plan_de_accion.equals("")){
                            guardarPlanDeAccion();
                        }else{
                            Toast.makeText(getApplicationContext(), "los campos con (*) son requeridos", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        consultarHallazgoID();

    }

    public void consultarHallazgoID(){
        String Url = "https://vvnorth.com/lpa/app/consultarHallazgoID.php";

        StringRequest request = new StringRequest(Request.Method.POST, Url,
                response -> {
                    Log.e("consultaHallazgoID:",response);
                    if (response != null && response.length() > 0) {
                        try {
                            JSONObject respuestaJSON = new JSONObject(response);
                            String pregunta = respuestaJSON.getString("pregunta");
                            String respuesta = respuestaJSON.getString("respuesta");

                            TextView textNombreColaborador = (TextView)findViewById(R.id.textColaborador);
                            TextView textPregunta = (TextView)findViewById(R.id.textPregunta);
                            TextView textHallazgo = (TextView)findViewById(R.id.textHallazgo);

                            textNombreColaborador.setText(nombre_evaluado);
                            textPregunta.setText(pregunta);
                            textHallazgo.setText(respuesta);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "No se encontro ID del Hallazo, respuesta vacia", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            Toast.makeText(getApplicationContext(), "Error :-("+error, Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id_hallazgo",id_hallazgo);

                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void guardarPlanDeAccion(){
        String Url = "https://vvnorth.com/lpa/app/guardarPlanDeAccion.php";

        StringRequest request = new StringRequest(Request.Method.POST, Url,
                response -> {
                    Log.e("consultaHallazgoID:",response);
                    if (response != null && response.length() > 0) {
                        Toast.makeText(getApplicationContext(), "Se guardo el plan con Éxito.", Toast.LENGTH_SHORT).show();
                        Intent intento = new Intent(this,StatusHallazgos.class);
                        intento.putExtra("CODIGO",codigo_auditoria);
                        startActivity(intento);

                    }else{
                        Toast.makeText(getApplicationContext(), "Al parecer, no se guardaron los datos.", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            Toast.makeText(getApplicationContext(), "Error :-("+error, Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id_hallazgo",id_hallazgo);
                parametros.put("plan_de_accion",plan_de_accion);
                parametros.put("feha_compromiso",feha_compromiso);


                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

}
