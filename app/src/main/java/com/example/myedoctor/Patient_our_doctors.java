package com.example.myedoctor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Patient_our_doctors extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference userReference;
    RecyclerAdapter_Our_Doctors myAdapter;
    ArrayList<Class_Our_Doctors> list;

    private ImageButton btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_our_doctors);

        //Initialize the progress bar
        progressBar = findViewById(R.id.progressBar_ourDoctors);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBack.requestFocusFromTouch();
                finish();
            }
        });

        //Get reference of health_edu_info from firebase RTDB
        userReference = FirebaseDatabase.getInstance().getReference("doctors");

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Our_Doctors(this, list, 1);

        //Setting our doctors recycler view
        recyclerView = findViewById(R.id.our_doctors_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);

        //Retrieved Doctor Info from firebase RTDB
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
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
                Toast.makeText(Patient_our_doctors.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

