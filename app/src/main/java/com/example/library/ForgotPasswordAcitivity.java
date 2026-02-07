package com.example.library;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordAcitivity extends AppCompatActivity {

    private EditText forgotEmailEditText;
    private EditText codeEditText;
    private Button forgotSendButton, forgotNextButton;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_acitivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        forgotEmailEditText = findViewById(R.id.forgotEmailEditTextId);
        codeEditText = findViewById(R.id.codeEditTextId);
        forgotSendButton = findViewById(R.id.forgotSendButtonId);
        forgotNextButton = findViewById(R.id.forgotNextButtonId);


        forgotNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = forgotEmailEditText.getText().toString().trim();

                mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            codeEditText.setVisibility(View.GONE);
                            forgotSendButton.setVisibility(View.GONE);

                            Toast.makeText(ForgotPasswordAcitivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                            ForgotPasswordAcitivity.this.finish();

                        }
                        else {
                            forgotEmailEditText.setError("Invalid Email");
                        }
                    }
                });
            }
        });

     /*   forgotSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeEditText.getText().toString().trim();

                mAuth.verifyPasswordResetCode(code).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (task.isSuccessful()) {

                        }
                        else {

                        }
                    }
                });
            }
        });
        */

    }
}