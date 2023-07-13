package com.auditorias.applpas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class PlanDeAccion  extends AppCompatActivity {
    String id_usuario,nombre,tipo_usuario, codigo_auditoria, num_nomina, id_hallazgo, nombre_evaluado,plan_de_accion, feha_compromiso,documentoPDF,comentario_evidencia;
    Button btnGuardar, btnPDF, btnImagen;
    EditText editplanAccion;
    Calendar calendar;
    long currentDate;
    TextView titulo,subtitulo, textUno;
    ImageView fotografia, PDFView;
    Bitmap imageBitmap,bitmapf;
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PDF = 3;
    byte[] fileBytes;
    EditText editComentario;

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

        PDFView = findViewById(R.id.viewPdf);

        TextView textSession = (TextView)findViewById(R.id.textUsuarioSession);
        titulo = (TextView)findViewById(R.id.titulo_toolbar);
        titulo.setText("Plan de Acción");
        subtitulo = (TextView)findViewById(R.id.textSubtitulo);
        editplanAccion = (EditText)findViewById(R.id.editPlan);

        textSession.setText("Responsable: "+nombre+" ("+num_nomina+")");



        btnGuardar = (Button)findViewById(R.id.btnHistorial);
        btnGuardar.setBackgroundResource(R.drawable.icono_guardar);



        textUno = (TextView)findViewById(R.id.textView1);
        textUno.setText("Guardar");

        fotografia = (ImageView)findViewById(R.id.FotografiaTomada);

        LinearLayout layoutBtnDos = (LinearLayout)findViewById(R.id.layoutBtnDos);
        LinearLayout layoutBtnTres = (LinearLayout)findViewById(R.id.layoutBtnTres);
        layoutBtnDos.setVisibility(View.GONE);
        layoutBtnTres.setVisibility(View.GONE);

        btnImagen = (Button)findViewById(R.id.buttonImagen);
        btnPDF = (Button)findViewById(R.id.buttonPDF);


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
                            String status_hallazgo = respuestaJSON.getString("status_hallazgos");
                            String plan = respuestaJSON.getString("plan_de_accion");
                            String fecha_compromiso = respuestaJSON.getString("fecha_compromiso");
                            String comentario = respuestaJSON.getString("comentario");
                            String bandera_img = respuestaJSON.getString("img_evidencia");
                            String bandera_pdf = respuestaJSON.getString("pdf_evidencia");



                            TextView tituloComentario = (TextView)findViewById(R.id.textView6);
                            TextView tituloBotones = (TextView)findViewById(R.id.textView7);
                            LinearLayout linearBotones = (LinearLayout)findViewById(R.id.linearBotones);
                            editComentario = (EditText)findViewById(R.id.editComentario);
                            EditText editPlan = (EditText)findViewById(R.id.editPlan);

                            calendarView = findViewById(R.id.fechaCompromiso);
                            if (status_hallazgo.equals("Pendiente Plan")){
                                        titulo.setText("Plan de Acción");
                                        subtitulo.setText("Favor de crear tu plan de acción,ID Hallazgo: "+id_hallazgo+".");
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
                                                    textUno.setText("Guardando Plan, Espere...");
                                                    btnGuardar.setVisibility(View.GONE);
                                                    guardarPlanDeAccion();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "los campos con (*) son requeridos", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        });


                            }else if(status_hallazgo.equals("Pendiente Evidencia") || status_hallazgo.equals("Rechazada")){
                                        if(status_hallazgo.equals("Pendiente Evidencia")){
                                            titulo.setText("Evidencia");
                                            subtitulo.setText("Subir evidencia puede ser, fotografía, imágen o pdf, ID Hallazgo: "+id_hallazgo+".");
                                        }else if(status_hallazgo.equals("Rechazada")){
                                            titulo.setText("Rechazada");
                                            subtitulo.setText("Favor de subir evidencia corregida, ID Hallazgo: "+id_hallazgo+".");
                                        }


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

                                        btnImagen.setOnClickListener(v -> {
                                            // Verifica si el botón presionado es el botón de imagen

                                            // Crea un diálogo para que el usuario seleccione si desea tomar una foto o elegir de la galería
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PlanDeAccion.this);
                                            builder.setTitle("Seleccionar imagen");
                                            builder.setItems(new CharSequence[]{"Tomar foto", "Elegir de la galería"}, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case 0:
                                                            tomarFoto();
                                                            break;
                                                        case 1:
                                                            desdeGaleria();
                                                            break;
                                                    }
                                                }
                                            });
                                            builder.show();
                                        });

                                        btnPDF.setOnClickListener(v -> {
                                            // Abre el selector de archivos para elegir un PDF
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                            intent.setType("application/pdf");
                                            startActivityForResult(intent, REQUEST_PDF);
                                        });

                                        //GUARDAR EVIDENCIA
                                        btnGuardar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                comentario_evidencia = editComentario.getText().toString();
                                                if (documentoPDF== null && bitmapf==null ){
                                                    Toast.makeText(getApplicationContext(),"Suba evidencias",Toast.LENGTH_SHORT).show();
                                                }else{
                                                     textUno.setText("Guardando Evidencia, Espere...");
                                                     btnGuardar.setVisibility(View.GONE);
                                                    guardarEvidenciaIMGoPDF();
                                                }
                                            }
                                        });

                                    }else if(status_hallazgo.equals("Pendiente Aprobación") || status_hallazgo.equals("Finalizado")){
                                            if(status_hallazgo.equals("Pendiente Aprobación")){
                                                titulo.setText("Aprobación");
                                                subtitulo.setText("Espere a que sea aprobada o rechazada, ID Hallazgo: "+id_hallazgo+".");
                                            }else if(status_hallazgo.equals("Finalizado")){
                                                titulo.setText("Finalizado");
                                                subtitulo.setText("Este hallazgo finalizo con éxito, ID Hallazgo: "+id_hallazgo+".");
                                             }


                                            linearBotones.setVisibility(View.GONE);//Ocultando Btns PNG Y PDF
                                            btnGuardar.setVisibility(View.GONE);
                                            textUno.setVisibility(View.GONE);


                                            editPlan.setEnabled(false);
                                            editPlan.setText(plan);
                                            editComentario.setText(comentario);
                                            editComentario.setEnabled(false);
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
                                    //fotografia.setImageDrawable(null);//formateando la vista


                                if (bandera_img.equals("1")){
                                    String url = "https://vvnorth.com/lpa/app/evidencia/"+codigo_auditoria+"/"+id_hallazgo+"/evidencia.jpeg";
                                    Picasso.get().load(url).into(fotografia);
                                }
                                if (bandera_pdf.equals("1")){
                                    String url2 = "https://vvnorth.com/lpa/app/evidencia/"+codigo_auditoria+"/"+id_hallazgo+"/evidencia.pdf";
                                        LinearLayout linear = new LinearLayout(PlanDeAccion.this);
                                        Button button = new Button(PlanDeAccion.this);

                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                        LinearLayout.LayoutParams paramsbtn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                                        linear.setLayoutParams(layoutParams);
                                        linear.setGravity(Gravity.CENTER);
                                        linear.setOrientation(LinearLayout.VERTICAL);
                                        button.setLayoutParams(paramsbtn);
                                        button.setText("Ver/Descargar PDF");
                                        button.setBackgroundResource(R.drawable.fondo_btn);
                                        button.setTextColor(Color.WHITE);
                                        linear.addView(button);
                                        LinearLayout layoutPrincipal  = (LinearLayout)findViewById(R.id.layoutPrincipal);
                                        layoutPrincipal.addView(linear);


                                        Uri uri = Uri.parse(url2);
                                        cargarPDFEnImageView(uri);

                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                abrirExploradorConRuta(url2);
                                            }
                                        });

                                }


                                // Realizar la carga de la imagen en un hilo separado (Thread) para no bloquear el hilo principal
                                        /*new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    // Crear una URL a partir de la URL de la imagen
                                                    URL url = new URL(url1);

                                                    // Abrir una conexión a la URL
                                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                    connection.setDoInput(true);
                                                    connection.connect();

                                                    // Obtener el InputStream de la conexión
                                                    InputStream inputStream = connection.getInputStream();

                                                    // Decodificar el InputStream en un objeto Bitmap
                                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                                    // Mostrar el Bitmap en el ImageView en el hilo principal
                                                    fotografia.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            fotografia.setImageBitmap(bitmap);
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    //Toast.makeText(getApplicationContext(), "Catch, alcargar la IMG", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            LinearLayout linear = new LinearLayout(PlanDeAccion.this);
                                                           Button button = new Button(PlanDeAccion.this);

                                                           LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                           LinearLayout.LayoutParams paramsbtn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                                                           linear.setLayoutParams(layoutParams);
                                                           linear.setGravity(Gravity.CENTER);
                                                           linear.setOrientation(LinearLayout.VERTICAL);
                                                           button.setLayoutParams(paramsbtn);
                                                           button.setText("Ver/Descargar PDF");
                                                           button.setBackgroundResource(R.drawable.fondo_btn);
                                                           button.setTextColor(Color.WHITE);
                                                           linear.addView(button);
                                                           LinearLayout layoutPrincipal  = (LinearLayout)findViewById(R.id.layoutPrincipal);
                                                           layoutPrincipal.addView(linear);

                                                            String url2 = "https://vvnorth.com/lpa/app/evidencia/"+codigo_auditoria+"/"+id_hallazgo+"/evidencia.pdf";
                                                            Uri uri = Uri.parse(url2);
                                                            cargarPDFEnImageView(uri);

                                                           button.setOnClickListener(new View.OnClickListener() {
                                                               @Override
                                                               public void onClick(View v) {
                                                                   abrirExploradorConRuta(url2);
                                                               }
                                                           });
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();*/
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
            Toast.makeText(getApplicationContext(), "Error :-(, Conexión de Internet"+error, Toast.LENGTH_SHORT).show();
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



    private void abrirExploradorConRuta(String url) {
        if (!url.isEmpty()) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            // La URL es vacía, manejar el caso apropiadamente
        }
    }

    public void cargarPDFEnImageView(Uri pdfUri) {
        try {
            // Inicializar PdfiumCore
            PdfiumCore pdfiumCore = new PdfiumCore(this);

            // Abrir el archivo PDF utilizando la Uri
            ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fileDescriptor);

            // Cargar la primera página del PDF
            pdfiumCore.openPage(pdfDocument, 0);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);

            // Crear un Bitmap con las dimensiones de la página del PDF
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Renderizar la página del PDF en el Bitmap
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0, width, height);

            // Configurar el Bitmap en el ImageView

            PDFView.setImageBitmap(bitmap);

            // Liberar los recursos
            pdfiumCore.closeDocument(pdfDocument);
            fileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tomarFoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void desdeGaleria(){
        // El usuario eligió elegir de la galería
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {//FOTOGRAFIA
                    //SI SE TOMA O SUBE FOTO
                    btnImagen.setBackgroundResource(R.drawable.fondo_btn);
                    btnImagen.setTextColor(Color.WHITE);

                    //COMO ES IMGAGEN AL BTN PDF COLOCAREMOS
                    btnPDF.setBackgroundResource(R.drawable.fondo_btn_default);
                    PDFView.setImageDrawable(null); //formatenado la vista
                    documentoPDF=null; //formateando variable

                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    int newWidth = 1000;
                    int newHeight = 1000;;
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, false);
                    fotografia.setImageBitmap(resizedBitmap);
                    Bitmap bitmap = ((BitmapDrawable)fotografia.getDrawable()).getBitmap();
                    bitmapf=bitmap;
                   // fotografia.setImageURI(data.getData());
                }else if(requestCode == REQUEST_IMAGE_GALLERY && data != null) {//GALERIA
                    //SI SE TOMA O SUBE FOTO
                    btnImagen.setBackgroundResource(R.drawable.fondo_btn);
                    btnImagen.setTextColor(Color.WHITE);

                    PDFView.setImageDrawable(null);; //formatenado la vista
                    documentoPDF=null; //formateando variable
                    btnPDF.setBackgroundResource(R.drawable.fondo_btn_default);
                    btnPDF.setTextColor(Color.BLACK);

                    // La imagen seleccionada de la galería
                    Uri imageUri = data.getData();
                    fotografia.setImageURI(imageUri);
                    Bitmap bitmap = ((BitmapDrawable)fotografia.getDrawable()).getBitmap();
                    bitmapf = bitmap;

                    // Hacer algo con la imagen seleccionada
                }else if(requestCode == REQUEST_PDF && resultCode == RESULT_OK){//PDF

                    /*fotografia.setImageDrawable(null);//reseteo fotografi tomada con camara
                    btnImagen.setBackground(null);*/
                    bitmapf=null;//reseteo fotografi tomada con camara
                    fotografia.setImageDrawable(null);
                    btnImagen.setBackgroundResource(R.drawable.fondo_btn_default);
                    btnImagen.setTextColor(Color.BLACK);
                    //PERO SI ES PDF

                    btnPDF.setBackgroundResource(R.drawable.fondo_btn);
                    btnPDF.setTextColor(Color.WHITE);
                    Uri pdfUri = data.getData();
                    cargarPDFEnImageView(pdfUri);

                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(pdfUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    byte[] fileBytes = getBytesFromInputStream(inputStream);
                     documentoPDF = Base64.encodeToString(fileBytes, Base64.DEFAULT);
                }
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteBuffer.toByteArray();
    }

    public  void guardarEvidenciaIMGoPDF(){
        String Url = "https://vvnorth.com/lpa/app/guardarEvidenciaIMGoPDF.php";

        StringRequest requests = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            textUno.setText("Guardar");
            btnGuardar.setVisibility(View.VISIBLE);
                Log.e("Respuesta","Evidencia: "+response);
                if(response.replaceAll("\"", "").equals("Guardado Exitoso")){
                    Intent intent = new Intent(PlanDeAccion.this,StatusHallazgos.class);
                    intent.putExtra("CODIGO",codigo_auditoria);
                    startActivity(intent);
                }else{
                    // Crear una instancia de Toast
                    Toast toast = Toast.makeText(getApplicationContext(), "No se guardó correctamente" + response, Toast.LENGTH_SHORT);
                    // Obtener el layout del Toast
                    View toastView = toast.getView();
                    // Establecer la gravedad al centro (en este caso, centro vertical y horizontal)
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    // Ajustar la posición manualmente
                    toastView.setPadding(0, 0, 0, 0);
                    // Mostrar el Toast
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textUno.setText("Guardar");
                btnGuardar.setVisibility(View.VISIBLE);
            }
        }){
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                if (bitmapf != null) {
                    String imageData= imageToString(bitmapf);
                    parametros.put("imagen",imageData);
                }
                if(documentoPDF!=null){
                    parametros.put("pdf",documentoPDF);
                }
                parametros.put("codigo",codigo_auditoria);
                parametros.put("id_hallazgo",id_hallazgo);
                parametros.put("comentario_evidencia",comentario_evidencia);

                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(requests);
    }

    private String imageToString(Bitmap bitmapf) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmapf.compress(Bitmap.CompressFormat.JPEG,80, outputStream);
        byte[] imageBytes= outputStream.toByteArray();
        String encodeImage= Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodeImage;
    }


    public void guardarPlanDeAccion(){
        String Url = "https://vvnorth.com/lpa/app/guardarPlanDeAccion.php";

        StringRequest request = new StringRequest(Request.Method.POST, Url,
                response -> {
                    Log.e("consultaHallazgoID:",response);
                    btnGuardar.setVisibility(View.VISIBLE);
                    textUno.setText("Guardar");
                    if (response != null && response.length() > 0) {
                        Toast.makeText(getApplicationContext(), "Se guardo el plan con Éxito.", Toast.LENGTH_SHORT).show();
                        Intent intento = new Intent(this,StatusHallazgos.class);
                        intento.putExtra("CODIGO",codigo_auditoria);
                        startActivity(intento);

                    }else{
                        Toast.makeText(getApplicationContext(), "Al parecer, no se guardaron los datos.", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    btnGuardar.setVisibility(View.VISIBLE);
                    textUno.setText("Guardar");
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
