package com.example.myedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Doctor_virtual_appt_info extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvDate, tvTime, tvName, tvGender, tvDOB, tvSymptoms, tvSymptomsPeriod, tvApptId;
    private String date, time, name, gender, dob, symptoms, symptomsPeriod, aptId;
    private LinearLayout acceptDeclineLine;

    private Button btnAccept, btnDecline, btnAccepted, btnDeclined;

    private DatabaseReference virtualReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_virtual_appt_info);

        progressBar = findViewById(R.id.progressBar);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        acceptDeclineLine = findViewById(R.id.linear_accept_decline);
        btnAccepted = findViewById(R.id.btn_accepted);
        btnDeclined = findViewById(R.id.btn_declined);
        progressBar.setVisibility(View.VISIBLE);

        btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);

        btnDecline = findViewById(R.id.btn_decline);
        btnDecline.setOnClickListener(this);

        //Find view by id
        tvDate = findViewById(R.id.tv_appointment_date);
        tvTime = findViewById(R.id.tv_appointment_time);
        tvName = findViewById(R.id.tv_patient_name);
        tvGender = findViewById(R.id.tv_patient_gender);
        tvDOB = findViewById(R.id.tv_patient_dob);
        tvSymptoms = findViewById(R.id.tv_symptoms);
        tvSymptomsPeriod = findViewById(R.id.tv_symptoms_period);
        tvApptId = findViewById(R.id.tv_appt_id);

        //Receive data
        Intent intent = getIntent();
        date = intent.getExtras().getString("Date");
        time = intent.getExtras().getString("Time");
        name = intent.getExtras().getString("Name");
        gender = intent.getExtras().getString("Gender");
        dob = intent.getExtras().getString("DOB");
        symptoms = intent.getExtras().getString("Symptoms");
        symptomsPeriod = intent.getExtras().getString("SymptomsPeriod");
        aptId = intent.getExtras().getString("AptID");

        //Setting the values
        tvDate.setText(date);
        tvTime.setText(time);
        tvName.setText(name);
        tvGender.setText(gender);
        tvDOB.setText(dob);
        tvSymptoms.setText(symptoms);
        tvSymptomsPeriod.setText(symptomsPeriod);
        tvApptId.setText(aptId);

        //Dismiss Progress Bar
        progressBar.setVisibility(View.GONE);

        //Locate virtual appointment location at Firebase Real-Time DB
        virtualReference = FirebaseDatabase.getInstance().getReference("virtual_apt");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_accept:
                //Set appointment state to 2 (accepted)
                acceptDeclineLine.setVisibility(View.GONE);
                btnAccepted.setVisibility(View.VISIBLE);
                virtualReference.child(aptId).child("state").setValue(2);
                Toast.makeText(Doctor_virtual_appt_info.this, "Booking Accepted. Check Your Appointment List.", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_decline:
                //Set appointment state to 3 (declined)
                acceptDeclineLine.setVisibility(View.GONE);
                btnDeclined.setVisibility(View.VISIBLE);
                virtualReference.child(aptId).child("state").setValue(3);
                Toast.makeText(Doctor_virtual_appt_info.this, "Booking Declined.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}