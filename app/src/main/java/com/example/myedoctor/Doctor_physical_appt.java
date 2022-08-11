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

public class Doctor_physical_appt extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference physicalReference;
    RecyclerAdapter_Appt myAdapter;
    ArrayList<Class_Appt> list;

    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView noAppt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_physical_appt);

        progressBar = findViewById(R.id.progressBar_physical_appt);

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

        //Find View by ID
        noAppt = findViewById(R.id.tv_no_appt);

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Appt(this, list,3);

        //Setting recycler view
        recyclerView = findViewById(R.id.physical_appt_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        //Retrieved Physical Appointment Info from firebase RTDB
        physicalReference = FirebaseDatabase.getInstance().getReference("physical_apt");
        physicalReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve Physical Appointment data from firebase
                    Class_Appt physicalApt_info = dataSnapshot.getValue(Class_Appt.class);

                    //Store retrieved physical appointment data to array list
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
                Toast.makeText(Doctor_physical_appt.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}