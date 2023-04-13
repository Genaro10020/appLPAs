package com.auditorias.applpas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




    }


    /*String url = "https://vvnorth.com/lpa/app/verificar.php";
    StringRequest request = new StringRequest(Request.Method.POST, url,
            response -> {
                Toast.makeText(getApplicationContext(), "Recibiendo",Toast.LENGTH_SHORT).show();
            }, error -> {
                Toast.makeText(getApplicationContext(), "Recibiendo",Toast.LENGTH_SHORT).show();
            }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("correo", "usuario@dominio.com");
            params.put("contrasena", "123456");
            return params;
        }
    };*/
}