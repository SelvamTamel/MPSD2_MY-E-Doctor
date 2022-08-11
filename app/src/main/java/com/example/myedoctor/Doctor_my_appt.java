package com.example.myedoctor;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Doctor_my_appt extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference virtualReference, physicalReference;
    RecyclerAdapter_Appt myAdapter;
    ArrayList<Class_Appt> list;

    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView noBooking;

    //Display ID 1 = Doctor_virtual_appt, 2 = Doctor_my_appt
    //Type 1 = Virtual Appointment, 2 = Physical Appointment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_my_appt);

        progressBar = findViewById(R.id.progressBar_doctor_my_appt);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBack.requestFocusFromTouch();
                finish();
            }
        });

        //Get Current Log In Doctor's ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String doctorId = user.getUid();

        noBooking = findViewById(R.id.tv_doctor_no_appt);

        //Get reference of health_edu_info from firebase RTDB
        physicalReference = FirebaseDatabase.getInstance().getReference("physical_apt");
        virtualReference = FirebaseDatabase.getInstance().getReference("virtual_apt");

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Appt(this, list, 2);

        //Setting recycler view
        recyclerView = findViewById(R.id.doctor_my_appt_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        progressBar.setVisibility(View.VISIBLE);
        //Retrieved Physical Appointment Info from firebase RTDB
        physicalReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve Physical Appointment data from firebase (title, content)
                    Class_Appt physicalApt_info = dataSnapshot.getValue(Class_Appt.class);

                    //Store retrieved data to array list
                    if (doctorId.equals(physicalApt_info.getDoctorId()) && physicalApt_info.getState() != 1){
                        physicalApt_info.aptId = dataSnapshot.getKey();
                        list.add(physicalApt_info);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Doctor_my_appt.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });

        //Retrieved Virtual Appointment Info from firebase RTDB
        virtualReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve Virtual Appointment data from firebase (title, content)
                    Class_Appt virtualApt_info = dataSnapshot.getValue(Class_Appt.class);

                    //Store retrieved data to array list
                    if (doctorId.equals(virtualApt_info.getDoctorId()) && virtualApt_info.getState() != 1){
                        virtualApt_info.aptId = dataSnapshot.getKey();
                        list.add(virtualApt_info);
                    }
                }
                myAdapter.notifyDataSetChanged();
                if (list.isEmpty()){
                    noBooking.setVisibility(View.VISIBLE);
                    noBooking.setText("No Appointment Yet");
                }else{
                    noBooking.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Doctor_my_appt.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}