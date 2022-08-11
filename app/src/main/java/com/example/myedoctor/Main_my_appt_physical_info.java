package com.example.myedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main_my_appt_physical_info extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private ProgressBar progressBar;

    private TextView tvName, tvGender, tvDOB, tvSymptoms, tvSymptomsPeriod, tvApptId, tv_doctorInCharge;
    private String name, gender, dob, symptoms, symptomsPeriod, aptId, doctorId;
    private int state, accountType;

    private Button btnWaitPatiently, btnFinishAppt, btnConfirmFinishAppt, btnApptDone, btnShowNurse, btnWaitConfirm;

    private DatabaseReference physicalReference, patientReference, doctorReference, userReference;

    private Class_Appt physicalAppt_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_my_appt_physical_info);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        //Find view by id
        tvName = findViewById(R.id.tv_patient_name);
        tvGender = findViewById(R.id.tv_patient_gender);
        tvDOB = findViewById(R.id.tv_patient_dob);
        tvSymptoms = findViewById(R.id.tv_symptoms);
        tvSymptomsPeriod = findViewById(R.id.tv_symptoms_period);
        tvApptId = findViewById(R.id.tv_appt_id);
        tv_doctorInCharge = findViewById(R.id.tv_doct_inCharge);

        //Buttons
        btnWaitPatiently = findViewById(R.id.btn_wait_patiently);
        btnFinishAppt = findViewById(R.id.btn_finish_appt);
        btnConfirmFinishAppt = findViewById(R.id.btn_confirm_finish_appt);
        btnApptDone = findViewById(R.id.btn_appt_done);
        btnShowNurse = findViewById(R.id.btn_show_nurse);
        btnWaitConfirm = findViewById(R.id.btn_wait_patient_confirm);

        //Receive data
        Intent intent = getIntent();
        name = intent.getExtras().getString("Name");
        gender = intent.getExtras().getString("Gender");
        dob = intent.getExtras().getString("DOB");
        symptoms = intent.getExtras().getString("Symptoms");
        symptomsPeriod = intent.getExtras().getString("SymptomsPeriod");
        aptId = intent.getExtras().getString("AptID");
        state = intent.getExtras().getInt("State");
        doctorId = intent.getExtras().getString("DoctorID");

        //Setting the values
        tvName.setText(name);
        tvGender.setText(gender);
        tvDOB.setText(dob);
        tvSymptoms.setText(symptoms);
        tvSymptomsPeriod.setText(symptomsPeriod);
        tvApptId.setText(aptId);


        //user firebase auth get uid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        //Get In Charge Doctor's name from firebase
        userReference = FirebaseDatabase.getInstance().getReference("doctors");
        userReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Class_Doctor doctor_info = snapshot.getValue(Class_Doctor.class);
                // Set In Charge Doctor's full name
                tv_doctorInCharge.setText(doctor_info.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_physical_info.this, "Database Went Wrong.(Doctor)", Toast.LENGTH_LONG).show();
            }
        });


        //Check if current user is a doctor or patient
        doctorReference = FirebaseDatabase.getInstance().getReference("doctors");
        doctorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    accountType = 1;
                    if (state == 2) {
                        //After doctor accepts the physical appointment
                        btnWaitPatiently.setVisibility(View.GONE);
                        btnFinishAppt.setVisibility(View.VISIBLE);
                        btnFinishAppt.setOnClickListener(Main_my_appt_physical_info.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_physical_info.this, "Database Went Wrong.(Doctor In Charge)", Toast.LENGTH_LONG).show();
            }
        });
        patientReference = FirebaseDatabase.getInstance().getReference("patients");
        patientReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    accountType = 2;
                    if (state == 2) {
                        //Show instruction after doctor accepted the physical appointment
                        btnWaitPatiently.setVisibility(View.GONE);
                        btnShowNurse.setVisibility(View.VISIBLE);
                    } else if (state == 4) {
                        //If doctor press appointment is finished, patient have to confirm
                        btnConfirmFinishAppt.setVisibility(View.VISIBLE);
                        btnConfirmFinishAppt.setOnClickListener(Main_my_appt_physical_info.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_physical_info.this, "Database Went Wrong.(Patient)", Toast.LENGTH_LONG).show();
            }
        });


        //Check current state of physical appointment
        physicalReference = FirebaseDatabase.getInstance().getReference("physical_apt");
        physicalReference.child(aptId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                physicalAppt_data = snapshot.getValue(Class_Appt.class);
                state = physicalAppt_data.getState();
                if (state == 5) {
                    //If appointment is done and confirmed by both parties
                    btnWaitConfirm.setVisibility(View.GONE);
                    btnApptDone.setVisibility(View.VISIBLE);
                } else if (state == 4) {
                    if (accountType == 1) {
                        //Doctor side, show indicator for dr to wait for patient to confirm appointment is finished.
                        btnWaitConfirm.setVisibility(View.VISIBLE);
                    }else if (accountType == 2){
                        //Patient side, show button for patient to confirm appointment is finished
                        btnShowNurse.setVisibility(View.GONE);
                        btnConfirmFinishAppt.setVisibility(View.VISIBLE);
                        btnConfirmFinishAppt.setOnClickListener(Main_my_appt_physical_info.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_physical_info.this, "Database Went Wrong.(Physical Appointment Data)", Toast.LENGTH_LONG).show();
            }
        });

        if (state == 1){
            btnWaitPatiently.setVisibility(View.VISIBLE);
        }

        //Dismiss Progress Bar
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_finish_appt:
                physicalReference.child(aptId).child("state").setValue(4);
                btnFinishAppt.setVisibility(View.GONE);
                Toast.makeText(Main_my_appt_physical_info.this, "Appointment Finished. Waiting for patient to confirm.", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_confirm_finish_appt:
                physicalReference.child(aptId).child("state").setValue(5);
                btnConfirmFinishAppt.setVisibility(View.GONE);
                Toast.makeText(Main_my_appt_physical_info.this, "Appointment Done. Please proceed to counter.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}