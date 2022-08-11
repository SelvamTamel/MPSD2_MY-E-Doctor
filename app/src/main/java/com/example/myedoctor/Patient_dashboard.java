package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Patient_dashboard extends AppCompatActivity implements View.OnClickListener {

    //Firebase Variables
    private FirebaseUser user;
    private FirebaseAuth auth;
    private StorageReference storageReference, profileImgRef, profileImgRef_check;
    private DatabaseReference userReference;
    private String userId;

    //Patient Dashboard Variables
    private ImageButton btnScan, btnSOS;
    private TextView scanQR, navReminder, navDataRecord, navEducation, navProfile, namePatient;
    private ImageButton btnReminder, btnOurDoctors, btnHealthEducation, btnConsultation, btnHealthDataTrack, btnDataRecord;
    private ProgressBar progressBar;

    //Our Doctors List Variables
    private CircleImageView patientProfile;
    private ImageButton btnMyAppt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);

        //Patient Name
        namePatient = findViewById(R.id.name_patient_dashboard);
        namePatient.setOnClickListener(this);

        //Medication Reminder Button
        btnReminder = findViewById(R.id.btn_reminder);
        btnReminder.setOnClickListener(this);

        //Patient Dashboard Profile Icon
        patientProfile = findViewById(R.id.patient_dashboard_profile);
        patientProfile.setOnClickListener(this);

        //Profile button
        btnMyAppt = findViewById(R.id.btn_patient_my_appt);
        btnMyAppt.setOnClickListener(this);

        //Scan QR button
        btnScan = findViewById(R.id.btn_scanQR);
        btnScan.setOnClickListener(this);

        scanQR = findViewById(R.id.btn_scan_qr_code);
        scanQR.setOnClickListener(this);

        //SOS Call button
        btnSOS = findViewById(R.id.btn_sos);
        btnSOS.setOnClickListener(this);

        //Our Doctors button
        btnOurDoctors = findViewById(R.id.btn_our_doctors);
        btnOurDoctors.setOnClickListener(this);

        //Health Education button
        btnHealthEducation = findViewById(R.id.btn_healthEducation);
        btnHealthEducation.setOnClickListener(this);

        //Consultation Button
        btnConsultation = findViewById(R.id.btn_consultation);
        btnConsultation.setOnClickListener(this);

        //Health Data Tracker button
        btnHealthDataTrack = findViewById(R.id.btn_healthData_track);
        btnHealthDataTrack.setOnClickListener(this);

        //Data Record button
        btnDataRecord = findViewById(R.id.btn_dataRecord);
        btnDataRecord.setOnClickListener(this);

        //Navigation Bar Data Record
        navDataRecord = findViewById(R.id.btn_nav_dataRecord);
        navDataRecord.setOnClickListener(this);

        //Navigation Bar Education
        navEducation = findViewById(R.id.btn_nav_education);
        navEducation.setOnClickListener(this);

        //Navigation Bar Profile
        navProfile = findViewById(R.id.btn_nav_profile);
        navProfile.setOnClickListener(this);

        //Navigation Bar Reminder
        navReminder = findViewById(R.id.btn_nav_reminder);
        navReminder.setOnClickListener(this);

        //Progress Bar
        progressBar = findViewById(R.id.progressBar_patient_dashboard);

        //Firebase Variables
        //Locate user's file location at Firebase Cloud Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve user's profile image from Cloud Storage
        profileImgRef = storageReference.child("patients/" + auth.getCurrentUser().getUid() + "/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(patientProfile);
            }
        });

        //Locate user's credential location at firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("patients");
        userId = user.getUid();

        //Retrieve user's credentials from Real-Time DB
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Patient classPatientProfile = snapshot.getValue(Class_Patient.class);

                if(classPatientProfile != null){
                    String name = classPatientProfile.fullName;

                    namePatient.setText(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_dashboard.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve patient profile from firebase
        //Reload the profile image everytime return to this page, if profile image is changed
        profileImgRef_check = storageReference.child("patients/" + auth.getCurrentUser().getUid() + "/profile.jpg");
        if (profileImgRef_check != profileImgRef)
            profileImgRef_check.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(patientProfile);
                }
            });

        //Display our doctors list
        displayOurDoctors();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reminder:
            case R.id.btn_nav_reminder:
                startActivity(new Intent(Patient_dashboard.this, Patient_reminder_main.class));
                break;
            case R.id.patient_dashboard_profile:
            case R.id.btn_nav_profile:
                startActivity(new Intent(Patient_dashboard.this, Patient_profile.class));
                break;
            case R.id.btn_patient_my_appt:
                startActivity(new Intent(Patient_dashboard.this, Patient_my_appt.class));
                break;
            case R.id.btn_sos:
                Intent sos = new Intent(Intent.ACTION_DIAL);
                sos.setData(Uri.parse("tel:999"));
                startActivity(sos);
                break;
            case R.id.btn_healthEducation:
            case R.id.btn_nav_education:
                startActivity(new Intent(Patient_dashboard.this, Patient_health_edu.class));
                break;
            case R.id.btn_scanQR:
            case R.id.btn_scan_qr_code:
                scanCode();
                break;
            case R.id.btn_our_doctors:
                Intent ourDoctors = new Intent(Patient_dashboard.this, Patient_our_doctors.class);
                startActivity(ourDoctors);
                break;
            case R.id.btn_consultation:
                startActivity(new Intent(Patient_dashboard.this, Patient_virtual_appt_1.class));
                break;
            case R.id.btn_healthData_track:
                startActivity(new Intent(Patient_dashboard.this, Patient_data_tracker.class));
                break;
            case R.id.btn_dataRecord:
            case R.id.btn_nav_dataRecord:
                startActivity(new Intent(Patient_dashboard.this, Patient_data_record.class));
                break;
        }
    }

    //qrcode reference link: https://www.youtube.com/watch?v=wfucGSKngq4
    //Method to Scan QR Code
    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(Patient_qrcode_scanner.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                if (result.getContents().equals("myedoctor_check_in")) {
                    Intent launchPhysicalLoginFormPage = new Intent(Patient_dashboard.this, Patient_physical_appt.class);
                    startActivity(launchPhysicalLoginFormPage);
                } else {
                    Toast.makeText(this, "Wrong QR Code", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Method to display our doctors list
    private void displayOurDoctors() {
        RecyclerView recyclerView;
        DatabaseReference userReference;
        RecyclerAdapter_Our_Doctors myAdapter;
        ArrayList<Class_Our_Doctors> list;

        //Get reference of health_edu_info from firebase RTDB
        userReference = FirebaseDatabase.getInstance().getReference("doctors");

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Our_Doctors(this, list, 2);

        //Set up recycler view
        recyclerView = findViewById(R.id.patient_dashboard_ourDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);

        //Retrieved Doctor Info from firebase RTDB
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve doctor information from firebase
                    Class_Our_Doctors ourDoctors_info = dataSnapshot.getValue(Class_Our_Doctors.class);

                    //Retrieve doctor user id
                    ourDoctors_info.id = dataSnapshot.getKey();

                    //Store retrieved data to array list
                    list.add(ourDoctors_info);
                }
                myAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_dashboard.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}