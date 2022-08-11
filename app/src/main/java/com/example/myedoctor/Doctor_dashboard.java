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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Doctor_dashboard extends AppCompatActivity implements View.OnClickListener {

    //Firebase
    private FirebaseAuth auth;
    private StorageReference storageReference, profileImgRef, profileImgRef_check;

    //Doctor dashboard variables
    private CircleImageView doctorProfile;
    private ImageButton btnMyAppt, btnVirtualAppt, btnPhysicalAppt;
    private ProgressBar progressBar;
    private TextView noAppt, drName;

    RecyclerView recyclerView;
    DatabaseReference dbReference;
    RecyclerAdapter_Appt myAdapter;
    ArrayList<Class_Appt> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);

        doctorProfile = findViewById(R.id.doctor_dashboard_profile);
        doctorProfile.setOnClickListener(this);

        btnVirtualAppt = findViewById(R.id.btn_virtual_appt);
        btnVirtualAppt.setOnClickListener(this);

        btnPhysicalAppt = findViewById(R.id.btn_physical_appt);
        btnPhysicalAppt.setOnClickListener(this);

        btnMyAppt = findViewById(R.id.btn_my_appt);
        btnMyAppt.setOnClickListener(this);

        drName = findViewById(R.id.tv_doctor_dashboard_name);

        //Locate user's file location at Firebase Cloud Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve doctor's profile image from Cloud Storage
        profileImgRef = storageReference.child("doctors/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(doctorProfile);
            }
        });

        //Locate doctor's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("doctors");
        String userId = user.getUid();

        //Set Doctor Name
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Doctor classDoctorProfile = snapshot.getValue(Class_Doctor.class);

                if(classDoctorProfile != null){;
                    drName.setText("Dr. " + classDoctorProfile.getFullName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Doctor_dashboard.this, "Something Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });

        //Show Physical Appointment Awaiting List
        showPhysicalAppt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve doctor's profile from firebase
        //Reload the profile image everytime return to this page, if profile image is changed
        profileImgRef_check = storageReference.child("doctors/" + auth.getCurrentUser().getUid() + "/profile.jpg");
        if (profileImgRef_check != profileImgRef)
            profileImgRef_check.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(doctorProfile);
                }
            });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctor_dashboard_profile:
                startActivity(new Intent(Doctor_dashboard.this, Doctor_profile.class));
                break;
            case R.id.btn_virtual_appt:
                startActivity(new Intent(Doctor_dashboard.this, Doctor_virtual_appt.class));
                break;
            case R.id.btn_my_appt:
                startActivity(new Intent(Doctor_dashboard.this, Doctor_my_appt.class));
                break;
            case R.id.btn_physical_appt:
                startActivity(new Intent(Doctor_dashboard.this, Doctor_physical_appt.class));
                break;
        }
    }

    private void showPhysicalAppt (){
        progressBar = findViewById(R.id.progressBar);

        noAppt = findViewById(R.id.tv_no_appt);

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Appt(this, list,3);

        //Setting recycler view
        recyclerView = findViewById(R.id.doctor_dashboard_physicalAppt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        //Retrieved Physical Appointment Info from firebase RTDB
        dbReference = FirebaseDatabase.getInstance().getReference("physical_apt");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve Physical Appointment data from firebase
                    Class_Appt physicalApt_info = dataSnapshot.getValue(Class_Appt.class);

                    if (physicalApt_info.getState() == 1){
                        physicalApt_info.aptId = dataSnapshot.getKey();
                        list.add(physicalApt_info);
                    }
                }
                myAdapter.notifyDataSetChanged();
                if (list.isEmpty()){
                    noAppt.setVisibility(View.VISIBLE);
                    noAppt.setText("No Appointment Yet");
                }else{
                    noAppt.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Doctor_dashboard.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}