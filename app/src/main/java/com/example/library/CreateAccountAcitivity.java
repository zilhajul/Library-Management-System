package com.example.library;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountAcitivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passEditText;
    private Button createButton;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account_acitivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditTextId);
        emailEditText = findViewById(R.id.emailEditTextId);
        passEditText = findViewById(R.id.passdEditTextId);
        createButton = findViewById(R.id.createButtonId);
        

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String pass = passEditText.getText().toString().trim();
                String role = "student";
                registerUser(email, pass, role);
            }
        });

    }
/*
    private void userRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String pass = passEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()){
            nameEditText.setError("Enter Name");
            emailEditText.setError("Enter Email");
            passEditText.setError("Enter Password");
        }
        else {

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(CreateAccountAcitivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        nameEditText.setText("");
                        emailEditText.setText("");
                        passEditText.setText("");


                    } else {

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(CreateAccountAcitivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateAccountAcitivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });


        }  */


    private void registerUser(String email, String password, String role) {
        String url = "http://192.168.1.196/library_system/register.php"; // আপনার পিসির IP অ্যাড্রেস দিন

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            Toast.makeText(CreateAccountAcitivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            nameEditText.setText("");
                            emailEditText.setText("");
                            passEditText.setText("");

                        } else {
                            Toast.makeText(CreateAccountAcitivity.this, "Error: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateAccountAcitivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // এই ম্যাপটি PHP-তে ডাটা পাঠায়
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }



}



