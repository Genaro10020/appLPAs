package com.auditorias.applpas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    Calendar calendar;
    long currentDate;
    TextView titulo;
    ImageView fotografia;
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

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

        Button btnImagen = (Button)findViewById(R.id.buttonImagen);
        TextView textSession = (TextView)findViewById(R.id.textUsuarioSession);
        titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Plan de Acción");
        TextView subtitulo = (TextView)findViewById(R.id.textSubtitulo);
        editplanAccion = (EditText)findViewById(R.id.editPlan);

        textSession.setText("Responsable: "+nombre+" ("+num_nomina+")");

        subtitulo.setText("Favor de crear tu plan de acción para este hallázgo con ID: "+id_hallazgo+".");

        btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);

        TextView textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");

        fotografia = (ImageView)findViewById(R.id.FotografiaTomada);

        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        LinearLayout layoutBtnTres = (LinearLayout)findViewById(R.id.layoutBtnTres);
        layoutBtnDos.setVisibility(View.GONE);
        layoutBtnTres.setVisibility(View.GONE);

        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica si el botón presionado es el botón de imagen

                    // Crea un diálogo para que el usuario seleccione si desea tomar una foto o elegir de la galería
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanDeAccion.this);
                    builder.setTitle("Seleccionar imagen");
                    builder.setItems(new CharSequence[]{"Tomar foto", "Elegir de la galería"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    // El usuario eligió tomar una foto
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
                                    break;
                                case 1:
                                    // El usuario eligió elegir de la galería
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
                                    break;
                            }
                        }
                    });
                    builder.show();
            }
        });

        consultarHallazgoID();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                // La imagen seleccionada de la galería
                Uri imageUri = data.getData();
                fotografia.setImageURI(imageUri);
                // Hacer algo con la imagen seleccionada
                // ...
            }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                // La foto tomada con la cámara
                Uri imageUri = data.getData();
                Log.d("YourActivity", "Image URI: " + imageUri.toString());
                fotografia.setImageURI(imageUri);
                //fotografia.setImageURI(data.getData());
                // Hacer algo con la foto tomada
                // ...
            }
        }
    }


/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            int newWidth = 1000;
            int newHeight = 1000;;
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, false);

            tomar_foto_area.setImageBitmap(resizedBitmap);
            fotografiaTomada = 1;
            leyenda.setText("");
            tomar_foto_area.setImageURI(data.getData());

            Bitmap bitmap = ((BitmapDrawable)tomar_foto_area.getDrawable()).getBitmap();
            ViewGroup.LayoutParams param = drawView.getLayoutParams();
            param.width = 1000;
            param.height = 1000;
            drawView.setLayoutParams(param);
            drawView.setVisibility(View.VISIBLE);
            drawView.setBitmap(bitmap);
            bitmapf=drawView.getBitmap();


        }
    }*/

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
                            String status_hallazgo = respuestaJSON.getString("status_hallazgos");
                            String plan = respuestaJSON.getString("plan_de_accion");
                            String fecha_compromiso = respuestaJSON.getString("fecha_compromiso");



                            TextView tituloComentario = (TextView)findViewById(R.id.textView6);
                            TextView tituloBotones = (TextView)findViewById(R.id.textView7);
                            LinearLayout linearBotones = (LinearLayout)findViewById(R.id.linearBotones);
                            EditText editComentario = (EditText)findViewById(R.id.editComentario);
                            EditText editPlan = (EditText)findViewById(R.id.editPlan);

                            calendarView = findViewById(R.id.fechaCompromiso);
                            if (status_hallazgo.equals("Pendiente Plan")){
                                        titulo.setText("Plan de Acción");
                                        // Obtén la fecha actual
                                        calendar = Calendar.getInstance();
                                        currentDate = calendar.getTimeInMillis();
                                        // Establece la fecha mínima como la fecha actual
                                        calendarView.setMinDate(currentDate);//De esta forma no podran elegir fecha anteriores
                                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                            @Override
                                            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                                // Aquí puedes obtener la fecha seleccionada y realizar las acciones necesarias
                                                feha_compromiso = dayOfMonth + "/" + (month + 1) + "/" + year;
                                                Toast.makeText(getApplicationContext(), "Su fecha compromiso: " + feha_compromiso, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        tituloComentario.setVisibility(View.GONE);
                                        editComentario.setVisibility(View.GONE);
                                        tituloBotones.setVisibility(View.GONE);
                                        linearBotones.setVisibility(View.GONE);


                                                btnGuardar.setOnClickListener(new View.OnClickListener() {//GUARDAR PLAN DE ACCION
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


                            }else if(status_hallazgo.equals("Pendiente Evidencia")){
                                        titulo.setText("Evidencia");
                                        editPlan.setEnabled(false);
                                        editPlan.setText(plan);
                                        // Separar la fecha en día, mes y año utilizando la barra diagonal como referencia
                                        String[] partesFecha = fecha_compromiso.split("/");
                                        // Obtener el día, mes y año por separado
                                        String dia = partesFecha[0];
                                        String mes = partesFecha[1];
                                        String anio = partesFecha[2];


                                        // Crear una nueva instancia de Calendar para la fecha de la base de datos
                                        Calendar calendarBD = Calendar.getInstance();
                                        // Establece la fecha en el objeto Calendar
                                        calendarBD.set(Calendar.YEAR, Integer.parseInt(anio)); // Año
                                        calendarBD.set(Calendar.MONTH, Integer.parseInt(mes)); // Mes (ten en cuenta que los meses se numeran desde 0, por lo que JUNE es 5)
                                        calendarBD.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia)); // Día
                                        calendarView.setDate(calendarBD.getTimeInMillis(), true, true);
                                        // Establecer fecha mínima
                                        calendarView.setMinDate(calendarBD.getTimeInMillis());
                                        // Establecer fecha máxima como la misma fecha seleccionada
                                        calendarView.setMaxDate(calendarBD.getTimeInMillis());
                                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                            @Override
                                            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                                calendarBD.set(Calendar.YEAR, Integer.parseInt(anio)); // Año
                                                calendarBD.set(Calendar.MONTH, Integer.parseInt(mes)); // Mes (ten en cuenta que los meses se numeran desde 0, por lo que JUNE es 5)
                                                calendarBD.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia)); // Día
                                                calendarView.setDate(calendarBD.getTimeInMillis(), true, true);
                                                Toast.makeText(getApplicationContext(), "No es posible cambiar la fecha compromiso.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }


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
