package com.example.myedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Doctor_physical_appt_info extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvName, tvGender, tvDOB, tvSymptoms, tvSymptomsPeriod, tvAptId;
    private String name, gender, dob, symptoms, symptomsPeriod, aptId;

    private Button btnAccept, btnAccepted;

    private DatabaseReference dbReference;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_physical_appt_info);

        progressBar = findViewById(R.id.progressBar);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBack.requestFocusFromTouch();
                finish();
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);

        btnAccepted = findViewById(R.id.btn_physical_appt_accepted);

        //Find view by id
        tvName = findViewById(R.id.tv_patient_name);
        tvGender = findViewById(R.id.tv_patient_gender);
        tvDOB = findViewById(R.id.tv_patient_dob);
        tvSymptoms = findViewById(R.id.tv_symptoms);
        tvSymptomsPeriod = findViewById(R.id.tv_symptoms_period);
        tvAptId = findViewById(R.id.tv_appt_id);

        //Receive data
        Intent intent = getIntent();
        name = intent.getExtras().getString("Name");
        gender = intent.getExtras().getString("Gender");
        dob = intent.getExtras().getString("DOB");
        symptoms = intent.getExtras().getString("Symptoms");
        symptomsPeriod = intent.getExtras().getString("SymptomsPeriod");
        aptId = intent.getExtras().getString("AptID");

        //Setting the values
        tvName.setText(name);
        tvGender.setText(gender);
        tvDOB.setText(dob);
        tvSymptoms.setText(symptoms);
        tvSymptomsPeriod.setText(symptomsPeriod);
        tvAptId.setText(aptId);

        //Hide Progress Bar
        progressBar.setVisibility(View.GONE);

        //Get doctor ID, store dr id after dr accepted the physical appointment
        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Locate physical appointment data location at Firebase Real-Time DB
        dbReference = FirebaseDatabase.getInstance().getReference("physical_apt");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_accept:
                //Set appointment state to 2 (accepted)
                dbReference.child(aptId).child("doctorId").setValue(doctorId);
                dbReference.child(aptId).child("state").setValue(2);
                Toast.makeText(Doctor_physical_appt_info.this, "Appointment Accepted. Check Your Appointment List.", Toast.LENGTH_LONG).show();
                btnAccept.setVisibility(View.GONE);
                btnAccepted.setVisibility(View.VISIBLE);
                break;
        }
    }
}