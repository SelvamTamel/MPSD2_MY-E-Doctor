package com.example.myedoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main_SignIn extends AppCompatActivity implements View.OnClickListener {

    private Button btn_doctor_signin, btn_patient_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_signin);

        btn_doctor_signin = findViewById(R.id.btn_doctor_signin);
        btn_doctor_signin.setOnClickListener(this);

        btn_patient_signin = findViewById(R.id.btn_patient_signin);
        btn_patient_signin.setOnClickListener(this);
    }


    // userType 1 = Class_Doctor
    // userType 2 = Class_Patient
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_patient_signin:
                startActivity(new Intent(Main_SignIn.this, Patient_signin.class));
                break;
            case R.id.btn_doctor_signin:
                startActivity(new Intent(Main_SignIn.this, Doctor_signin.class));
                break;
        }
    }
}