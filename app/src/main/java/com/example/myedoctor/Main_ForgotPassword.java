package com.example.myedoctor;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Main_ForgotPassword extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth auth;

    private EditText emailEditText;
    private Button resetPassBtn;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_forgot_password);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        resetPassBtn = findViewById(R.id.btn_reset_pass);
        resetPassBtn.setOnClickListener(this);

        emailEditText = findViewById(R.id.input_email);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_reset_pass:
                resetPassword();
                break;
        }
    }

    private void resetPassword(){
        String email = emailEditText.getText().toString();

        if(email.isEmpty()){
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Main_ForgotPassword.this, "Check your email to reset password.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Main_ForgotPassword.this, "Try Again, something wrong happened.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}