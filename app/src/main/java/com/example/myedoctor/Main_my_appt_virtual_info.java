package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

//State
// 1 = Pending Dr to Accept
// 2 = Accepted
// 3 = Declined
// 4 = Finished Appointment (doctor press)
// 5 = Confirmed Finish Appointment (patient press)
// 6 = Expired
// 7 = After Doctor Click Join Meeting
// 8 = After Patient Click Join Meeting

public class Main_my_appt_virtual_info extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvDate, tvTime, tvName, tvGender, tvDOB, tvSymptoms, tvSymptomsPeriod, tvInstruction_doctor, tvInstruction_patient, tv_doctorInCharge, tvApptId;
    private String date, time, name, gender, dob, symptoms, symptomsPeriod, aptId, doctorId, doctorInCharge, patientName;
    private int state, accountType;
    private long aptTimeMillis;

    private Button btnJoin_doctor, btnJoin_patient, btnFinishAppt, btnConfirmFinishAppt, btnApptDone, btnWaitConfirm, btnExpired, btnDeclined;

    private DatabaseReference virtualReference, patientReference, doctorReference, userReference;

    private Class_Appt physicalAppt_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_my_appt_virtual_info);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        //Buttons
        btnJoin_doctor = findViewById(R.id.btn_join_doctor);
        btnJoin_patient = findViewById(R.id.btn_join_patient);
        btnFinishAppt = findViewById(R.id.btn_finish_appt);
        btnConfirmFinishAppt = findViewById(R.id.btn_confirm_finish_appt);
        btnApptDone = findViewById(R.id.btn_appt_done);
        btnWaitConfirm = findViewById(R.id.btn_wait_patient_confirm);
        btnExpired = findViewById(R.id.btn_appt_expired);
        btnDeclined = findViewById(R.id.btn_appt_declined);

        //Find view by id
        tvDate = findViewById(R.id.tv_appointment_date);
        tvTime = findViewById(R.id.tv_appointment_time);
        tvName = findViewById(R.id.tv_patient_name);
        tvGender = findViewById(R.id.tv_patient_gender);
        tvDOB = findViewById(R.id.tv_patient_dob);
        tvSymptoms = findViewById(R.id.tv_symptoms);
        tvSymptomsPeriod = findViewById(R.id.tv_symptoms_period);
        tvInstruction_doctor = findViewById(R.id.tv_instruction_doctor);
        tvInstruction_patient = findViewById(R.id.tv_instruction_patient);
        tvApptId = findViewById(R.id.tv_appt_id);
        tv_doctorInCharge = findViewById(R.id.tv_doct_inCharge);

        //Receive data
        Intent intent = getIntent();
        aptTimeMillis = intent.getExtras().getLong("aptTimeMillis");
        date = intent.getExtras().getString("Date");
        time = intent.getExtras().getString("Time");
        name = intent.getExtras().getString("Name");
        gender = intent.getExtras().getString("Gender");
        dob = intent.getExtras().getString("DOB");
        symptoms = intent.getExtras().getString("Symptoms");
        symptomsPeriod = intent.getExtras().getString("SymptomsPeriod");
        aptId = intent.getExtras().getString("AptID");
        state = intent.getExtras().getInt("State");
        doctorId = intent.getExtras().getString("DoctorID");

        //Setting the values
        tvDate.setText(date);
        tvTime.setText(time);
        tvName.setText(name);
        tvGender.setText(gender);
        tvDOB.setText(dob);
        tvSymptoms.setText(symptoms);
        tvSymptomsPeriod.setText(symptomsPeriod);
        tvApptId.setText(aptId);

        //user firebase auth get uid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        //Appointment Time
        long timeBeforeFiveMins = aptTimeMillis - 300000;
        long timeAfterOneHour = aptTimeMillis + 3600000;

        //Get In Charge Doctor's name from firebase
        userReference = FirebaseDatabase.getInstance().getReference("doctors");
        userReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Class_Doctor doctor_info = snapshot.getValue(Class_Doctor.class);
                // Set In Charge Doctor's full name
                doctorInCharge = "Dr. " + doctor_info.getFullName();
                tv_doctorInCharge.setText(doctorInCharge);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_virtual_info.this, "Database Went Wrong.(Doctor)", Toast.LENGTH_LONG).show();
            }
        });

        //Check if current user is a doctor or patient
        doctorReference = FirebaseDatabase.getInstance().getReference("doctors");
        doctorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    accountType = 1;
                    if (state == 2){
                        btnJoin_doctor.setVisibility(View.VISIBLE);
                        tvInstruction_doctor.setVisibility(View.VISIBLE);
                        if (System.currentTimeMillis() >= timeAfterOneHour){
                            btnJoin_doctor.setClickable(false);
                            tvInstruction_doctor.setVisibility(View.GONE);
                            btnJoin_doctor.setVisibility(View.GONE);
                        } else if (System.currentTimeMillis() >= timeBeforeFiveMins){
                            btnJoin_doctor.setClickable(true);
                            btnJoin_doctor.setOnClickListener(Main_my_appt_virtual_info.this);
                            btnJoin_doctor.setBackgroundColor(Color.parseColor("#53A9EC"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_virtual_info.this, "Database Went Wrong.(Doctor In Charge)", Toast.LENGTH_LONG).show();
            }
        });
        patientReference = FirebaseDatabase.getInstance().getReference("patients");
        patientReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    accountType = 2;
                    btnJoin_patient.setVisibility(View.VISIBLE);
                    tvInstruction_patient.setVisibility(View.VISIBLE);
                    if (state == 2){
                        if (System.currentTimeMillis() >= timeAfterOneHour){
                            btnJoin_patient.setClickable(false);
                            tvInstruction_patient.setVisibility(View.GONE);
                            btnJoin_patient.setVisibility(View.GONE);
                        } else if (System.currentTimeMillis() >= timeBeforeFiveMins){
                            btnJoin_patient.setClickable(true);
                            btnJoin_patient.setOnClickListener(Main_my_appt_virtual_info.this);
                            btnJoin_patient.setBackgroundColor(Color.parseColor("#53A9EC"));
                        }
                    } else if (state == 5){
                        tvInstruction_patient.setVisibility(View.GONE);
                        btnJoin_patient.setVisibility(View.GONE);
                    }

                    Class_Patient patient_info = snapshot.child(userId).getValue(Class_Patient.class);
                    patientName = patient_info.fullName;

                    if (state == 4) {
                        //If doctor press appointment is finished, patient have to confirm
                        btnJoin_patient.setVisibility(View.GONE);
                        tvInstruction_patient.setVisibility(View.GONE);
                        btnConfirmFinishAppt.setVisibility(View.VISIBLE);
                        btnConfirmFinishAppt.setOnClickListener(Main_my_appt_virtual_info.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_virtual_info.this, "Database Went Wrong.(Patient)", Toast.LENGTH_LONG).show();
            }
        });

        //Check current state of physical appointment
        virtualReference = FirebaseDatabase.getInstance().getReference("virtual_apt");
        virtualReference.child(aptId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                physicalAppt_data = snapshot.getValue(Class_Appt.class);
                state = physicalAppt_data.getState();
                if (state == 5) {
                    //If appointment is done and confirmed by both parties
                    btnConfirmFinishAppt.setVisibility(View.GONE);
                    btnWaitConfirm.setVisibility(View.GONE);
                    btnApptDone.setVisibility(View.VISIBLE);
                } else if (state == 6){
                    //If both party didn't join after 1 hour, appointment is expired
                    btnExpired.setVisibility(View.VISIBLE);
                } else if (state == 3) {
                    //If appointment is declined
                    btnDeclined.setVisibility(View.VISIBLE);
                }else if (state == 4) {
                    if (accountType == 1) {
                        //Doctor side, show indicator for dr to wait for patient to confirm appointment is finished.
                        btnFinishAppt.setVisibility(View.GONE);
                        Toast.makeText(Main_my_appt_virtual_info.this, "Appointment Finished. Waiting for patient to confirm.", Toast.LENGTH_LONG).show();

                        btnWaitConfirm.setVisibility(View.VISIBLE);
                    } else if (accountType == 2) {
                        //Patient side, show button for patient to confirm appointment is finished
                        tvInstruction_patient.setVisibility(View.GONE);
                        btnJoin_patient.setVisibility(View.GONE);
                        btnConfirmFinishAppt.setVisibility(View.VISIBLE);
                        btnConfirmFinishAppt.setOnClickListener(Main_my_appt_virtual_info.this);
                    }
                } else if (state == 7) {
                    if (accountType == 1) {
                        //After finish virtual appointment
                        tvInstruction_doctor.setVisibility(View.GONE);
                        btnJoin_doctor.setVisibility(View.GONE);
                        btnFinishAppt.setVisibility(View.VISIBLE);
                        btnFinishAppt.setOnClickListener(Main_my_appt_virtual_info.this);
                    }
                } else if (state == 8) {
                    if (accountType == 1){
                        //After finish virtual appointment
                        tvInstruction_doctor.setVisibility(View.GONE);
                        btnJoin_doctor.setVisibility(View.GONE);
                        btnFinishAppt.setVisibility(View.VISIBLE);
                        btnFinishAppt.setOnClickListener(Main_my_appt_virtual_info.this);
                    } else if (accountType == 2) {
                        btnJoin_patient.setClickable(false);
                        btnJoin_patient.setBackgroundColor(777272);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Main_my_appt_virtual_info.this, "Database Went Wrong.(Virtual Appointment Data)", Toast.LENGTH_LONG).show();
            }
        });


        if (state == 2) {
            if (System.currentTimeMillis() >= timeAfterOneHour) {
                //If after one hour the appointment hasn't start, the appointment is set to expired
                btnExpired.setVisibility(View.VISIBLE);
                virtualReference.child(aptId).child("state").setValue(6);
            } else if (System.currentTimeMillis() >= timeBeforeFiveMins) {
                //Appointment room is set up 5 mins before appointment time
                setVideoCall();
            }
        }

        //Dismiss Progress Bar
        progressBar.setVisibility(View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_join_doctor:
                virtualReference.child(aptId).child("state").setValue(7);
                launchVideoCall();
                break;
            case R.id.btn_join_patient:
                virtualReference.child(aptId).child("state").setValue(8);
                launchVideoCall();
                break;
            case R.id.btn_finish_appt:
                virtualReference.child(aptId).child("state").setValue(4);
                break;
            case R.id.btn_confirm_finish_appt:
                virtualReference.child(aptId).child("state").setValue(5);
                break;
        }
    }

    //Method to set up viirtual appointment room
    private void setVideoCall() {
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //Method to launch virtual appointment room
    private void launchVideoCall() {
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        if (accountType == 1)
            userInfo.setDisplayName(doctorInCharge);
        else
            userInfo.setDisplayName(patientName);

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom(aptId)
                .setWelcomePageEnabled(false)
                .setUserInfo(userInfo)
                .setSubject("My E Doctor Meeting")
                .build();

        JitsiMeetActivity.launch(Main_my_appt_virtual_info.this, options);
    }
}