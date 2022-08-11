package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Patient_signup_1 extends AppCompatActivity implements View.OnClickListener {

    //Sign Up Class_Patient Page 1 Variables
    private TextView aldHaveAcc;
    private Button btnNext;
    private EditText editTextEmail, editTextFullName, editTextPhone, editTextPassword;
    private String email, fullName, phoneNumber, password;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_signup_1);

        //Declare Variables
        aldHaveAcc = findViewById(R.id.txt_ald_have_acc);
        aldHaveAcc.setOnClickListener(this);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        //Edit Texts
        editTextEmail = findViewById(R.id.input_email);
        editTextFullName = findViewById(R.id.input_name);
        editTextPhone = findViewById(R.id.input_phone_number);
        editTextPassword = findViewById(R.id.input_password);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.txt_ald_have_acc:
                startActivity(new Intent(this, Patient_signin.class));
                break;
            case R.id.btn_next:
                continueSignUp();
                break;
        }
    }

    private boolean checkInput() {
        boolean check = false;

        //Get input data from user
        email = editTextEmail.getText().toString();
        fullName = editTextFullName.getText().toString();
        phoneNumber = editTextPhone.getText().toString();
        password = editTextPassword.getText().toString();

        //Check if all field are entered and are valid
        if (email.isEmpty()) {
            editTextEmail.setError("Email is Required.");
            editTextEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a Valid Email.");
            editTextEmail.requestFocus();
        } else if (fullName.isEmpty()) {
            editTextFullName.setError("Full Name is Required.");
            editTextFullName.requestFocus();
        } else if (phoneNumber.isEmpty()) {
            editTextPhone.setError("Phone is Required.");
            editTextPhone.requestFocus();
        } else if (password.isEmpty()) {
            editTextPassword.setError("Password is Required.");
            editTextPassword.requestFocus();
        } else if (password.length() < 6) {
            editTextPassword.setError("Password length should be more than 6 characters.");
            editTextPassword.requestFocus();
        } else {
            check = true;
        }
        return check;
    }

    private void continueSignUp() {
        if (!checkInput()) return;

        //Pass the credential data to Sign Up Class_Patient Page 2
        //We will only register the doctor tgt with other data in Sign Up Class_Patient Page 2
        Intent intent = new Intent(Patient_signup_1.this, Patient_signup_2.class);
        intent.putExtra("email", email);
        intent.putExtra("name", fullName);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}