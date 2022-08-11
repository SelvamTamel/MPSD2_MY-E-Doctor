package com.example.myedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Doctor_signin extends AppCompatActivity implements View.OnClickListener{
    // userType 1 = Class_Doctor
    // userType 2 = Class_Patient

    private TextView signUp, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_sign_in);

        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        signUp = findViewById(R.id.txt_doctor_signup);
        signUp.setOnClickListener(this);

        signIn = findViewById(R.id.btn_doctor_signin);
        signIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);

        forgotPassword = findViewById(R.id.txt_forgot_pass);
        forgotPassword.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_doctor_signin:
                doctorSignIn();
                break;
            case R.id.txt_forgot_pass:
                startActivity(new Intent(Doctor_signin.this, Main_ForgotPassword.class));
                break;
            case R.id.txt_doctor_signup:
                startActivity(new Intent(Doctor_signin.this, Doctor_signup_1.class));
                break;
        }
    }

    private void doctorSignIn() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty()){
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email.");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required.");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password length should be more than 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Locate doctor's credential location at Firebase Real-Time DB
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    userReference = FirebaseDatabase.getInstance().getReference("doctors");
                    userId = user.getUid();

                    //Retrieve doctor's credentials from Real-Time DB
                    userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Create an instance to store data retrieved from Firebase
                            Class_Doctor classDoctorProfile = snapshot.getValue(Class_Doctor.class);

                            //Check if signed in user exists in "doctors" database table
                            //Defined as wrong sign in if user doesn't exists in doctors table
                            if(classDoctorProfile != null){
                                int userType = classDoctorProfile.userType;
                                String userName = classDoctorProfile.fullName;

                                if (userType != 1) {
                                    Toast.makeText(Doctor_signin.this, "This is not a valid doctor account.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }

                                if(user.isEmailVerified()) {
                                    //End the doctor sign in page once signed in
                                    finish();
                                    progressBar.setVisibility(View.GONE);

                                    startActivity(new Intent(Doctor_signin.this, Doctor_dashboard.class));
                                    Toast.makeText(Doctor_signin.this, "Signed in as Dr " + userName, Toast.LENGTH_LONG).show();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Doctor_signin.this, "Your Email is Not Verified. Check Your Email Spam.", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(Doctor_signin.this, "This is not a valid doctor account.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Doctor_signin.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Doctor_signin.this, "Failed to Login, Please Check Your Credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}