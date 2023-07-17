package com.auditorias.applpas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
                                String tipo_usuario = jsonResponse.getString("tipo_usuario");
                                String num_nomina = jsonResponse.getString("nomina");
                                String id_usuario;
                                String nombre;
                                    if(tipo_usuario.equals("Auditor")){
                                        id_usuario = jsonResponse.getString("id_auditor");
                                        nombre = jsonResponse.getString("auditor");
                                    }else{
                                        id_usuario = jsonResponse.getString("id_responsable");
                                        nombre = jsonResponse.getString("responsable");
                                    }

                                SharedPreferences sessionGuardada = getSharedPreferences("MiSession", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sessionGuardada.edit();
                                editor.putString("ID_USUARIO",id_usuario);
                                editor.putString("NUMERO_NOMINA",num_nomina);
                                editor.putString("NOMBRE",nombre);
                                editor.putString("TIPO_USUARIO",tipo_usuario);
                                editor.commit();

                                    if (tipo_usuario.equals("Auditor")){
                                        enviarVistaAditorias(id_usuario,nombre,tipo_usuario);
                                    }else if(tipo_usuario.equals("Responsable")){
                                        Intent intent = new Intent(this,HallazgosResponsable.class);
                                        startActivity(intent);
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"No exite ese tipo de usuario.",Toast.LENGTH_LONG);
                                        toast.show();
                                    }


                            } catch (JSONException e) {
                                Toast toast = Toast.makeText(getApplicationContext(),"No se encontro Usuario, verifique.",Toast.LENGTH_LONG);
                                toast.show();
                            }


                        }, error -> {
                            Toast.makeText(getApplicationContext(), "Error :-(, Verifique la Conexión.", Toast.LENGTH_SHORT).show();
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
            private void enviarVistaAditorias(String id_usuario, String nombre, String tipo_usuario){
                Intent intent = new Intent(Login.this, Auditorias.class);
                intent.putExtra("ID_USUARIO", id_usuario);
                intent.putExtra("NOMBRE", nombre);
                intent.putExtra("TIPO_USUARIO",tipo_usuario);
                startActivity(intent);
            }
        }