package com.example.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView createAccountButton;
    private Spinner spinner;
    private TextView forgotPassword;
    private FirebaseAuth mAuth;

    String[] roles ;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();


        userNameEditText = findViewById(R.id.userNameEdittextId);
        passwordEditText = findViewById(R.id.passwordEdittextId);
        loginButton = findViewById(R.id.loginButtonId);
        createAccountButton = findViewById(R.id.createAccountButtonId);
        spinner = findViewById(R.id.spinnerId);
        forgotPassword = findViewById(R.id.forgotPasswordId);

        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);



        roles = getResources().getStringArray(R.array.Select_Role);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.sample_layout,R.id.sampleTextViewId, roles);
        spinner.setAdapter(adapter);




    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButtonId){
            String userName = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String value = spinner.getSelectedItem().toString();



            loginUser(userName,password,value);

        }
        else if (view.getId() == R.id.createAccountButtonId){

            Intent intent = new Intent(this, CreateAccountAcitivity.class);
            startActivity(intent);

        }
        else if (view.getId() == R.id.forgotPasswordId){

            Intent intent = new Intent(this, ForgotPasswordAcitivity.class);
            startActivity(intent);

        }


    }

    private void loginUser(String email, String password, String value) {
        String url = "http://192.168.1.196/library_system/login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("Login Success")) {
                            String[] parts = response.split(",");
                            String role = parts[1];



                            if (role.equals(value)) {

                                if (role.equals("Librarian")){

                                    Intent intent = new Intent(MainActivity.this, Librarian.class);
                                    intent.putExtra("user_role", "Librarian");
                                    startActivity(intent);
                                }else {

                                    Intent intent = new Intent(MainActivity.this, Student.class);
                                    intent.putExtra("user_role", "student");
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }


                            } else {
                                Toast.makeText(MainActivity.this, "Invalid Role", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}