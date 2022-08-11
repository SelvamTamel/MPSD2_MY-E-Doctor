package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Patient_data_record extends AppCompatActivity implements View.OnClickListener {

    private EditText input_bloodSugar, input_heartRate, input_bloodPressure, input_cholesterol;
    private Button btnSave;
    private ImageButton btnBack;
    private TextView dateTitle;
    private ImageView analysis_logo_sugarLevel, analysis_logo_bloodPressure, analysis_logo_heartRate, analysis_logo_cholesterol;
    private ImageView report_logo_cholesterol, report_logo_heartRate, report_logo_bloodPressure, report_logo_bloodSugar;

    private String formatDateTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_data_record);

        //Text View Inputs
        input_bloodSugar = findViewById(R.id.input_bloodSugar);
        input_heartRate = findViewById(R.id.input_heartRate);
        input_bloodPressure = findViewById(R.id.input_bloodPressure);
        input_cholesterol = findViewById(R.id.input_cholesterol);

        analysis_logo_bloodPressure = findViewById(R.id.analysis_logo_bloodPressure);
        analysis_logo_cholesterol = findViewById(R.id.analysis_logo_cholesterol);
        analysis_logo_heartRate = findViewById(R.id.analysis_logo_heartRate);
        analysis_logo_sugarLevel = findViewById(R.id.analysis_logo_sugarLevel);

        dateTitle = findViewById(R.id.txt_date);
        dateTitle.setOnClickListener(this);

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        report_logo_cholesterol = findViewById(R.id.report_logo_cholesterol);
        report_logo_cholesterol.setOnClickListener(this);

        report_logo_heartRate = findViewById(R.id.report_logo_heartRate);
        report_logo_heartRate.setOnClickListener(this);

        report_logo_bloodPressure = findViewById(R.id.report_logo_bloodPressure);
        report_logo_bloodPressure.setOnClickListener(this);

        report_logo_bloodSugar = findViewById(R.id.report_logo_sugarLevel);
        report_logo_bloodSugar.setOnClickListener(this);

        //Get Current Date and Time
        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        dateTitle.setText(formattedDate);

        //Get created date and time of the appointment
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        formatDateTime = dateFormat.format(date);
        dateTitle.setText(formattedDate);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.report_logo_cholesterol:
                startActivity(new Intent(Patient_data_record.this, Patient_cholesterol_analysis_report.class));
                break;
            case R.id.report_logo_heartRate:
                startActivity(new Intent(Patient_data_record.this, Patient_heartRate_analysis_report.class));
                break;
            case R.id.report_logo_bloodPressure:
                startActivity(new Intent(Patient_data_record.this, Patient_bloodPressure_analysis_report.class));
                break;
            case R.id.report_logo_sugarLevel:
                startActivity(new Intent(Patient_data_record.this, Patient_bloodSugar_analysis_report.class));
                break;
            case R.id.btn_save:
                save();
                Toast.makeText(Patient_data_record.this, "Data Saved Successfully .", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void save() {
        //Get all value from text input
        String bloodSugar = input_bloodSugar.getText().toString();
        String heartRate = input_heartRate.getText().toString();
        String bloodPressure = input_bloodPressure.getText().toString();
        String cholesterol = input_cholesterol.getText().toString();

        Class_PatientData classPatientData = new Class_PatientData(bloodSugar, heartRate, bloodPressure, cholesterol);

        //Store patient data in Real-Time DB
        FirebaseDatabase.getInstance().getReference("patients")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("health_data_record").child(formatDateTime).setValue(classPatientData);

        if (Integer.valueOf(bloodPressure) > 100) {
            analysis_logo_bloodPressure.setVisibility(View.VISIBLE);
            report_logo_bloodPressure.setVisibility(View.VISIBLE);
        }
        if (Integer.valueOf(bloodSugar) > 100) {
            analysis_logo_sugarLevel.setVisibility((View.VISIBLE));
            report_logo_bloodSugar.setVisibility((View.VISIBLE));
        }
        if (Integer.valueOf(heartRate) > 100) {
            analysis_logo_heartRate.setVisibility((View.VISIBLE));
            report_logo_heartRate.setVisibility((View.VISIBLE));
        }
        if (Integer.valueOf(cholesterol) > 100) {
            analysis_logo_cholesterol.setVisibility((View.VISIBLE));
            report_logo_cholesterol.setVisibility((View.VISIBLE));
        }
    }
}