package com.auditorias.applpas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnIngresar = (Button)findViewById(R.id.btnIngresar);
        EditText editNomina = (EditText)findViewById(R.id.nomina);
        EditText editClave = (EditText)findViewById(R.id.clave);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomina = editNomina.getText().toString();
                String clave = editClave.getText().toString();
                verificarLogin(nomina,clave);
            }
        });




    }


    //METODO PARA VERIFICAR SI EXISTE EL USUARIO
            private void verificarLogin(String nomina, String clave){
                String url = "https://vvnorth.com/lpa/app/verificar.php";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        response -> {
                            Log.e("RESPUESTA",""+response);
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String id_auditor = jsonResponse.getString("id_auditor");
                                String nombre_auditor = jsonResponse.getString("auditor");
                                enviarVistaAditorias(id_auditor,nombre_auditor);


                            } catch (JSONException e) {
                                Toast toast = Toast.makeText(getApplicationContext(),"No se encontro Usuario, verifique.",Toast.LENGTH_LONG);
                                toast.show();
                            }


                        }, error -> {
                            Toast.makeText(getApplicationContext(), "Error :-(", Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Usuario", nomina);
                        params.put("Contrasena", clave);
                        return params;
                    }
                };

                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(request);
            }


    //METODO PARA ENVIARTE A LA PANTALL CORRESPONDIENTE SEGUN TIPO USUARIO
            private void enviarVistaAditorias(String id_auditor, String nombre_auditor){
                Intent intent = new Intent(Login.this, Auditorias.class);
                intent.putExtra("ID_AUDITOR", id_auditor);
                intent.putExtra("NOMBRE_AUDITOR", nombre_auditor);
                startActivity(intent);
            }
        }