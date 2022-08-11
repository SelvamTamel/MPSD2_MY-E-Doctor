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

public class Patient_health_edu extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference userReference;
    RecyclerAdapter_Health_Edu myAdapter;
    ArrayList<Class_Health_Edu> list;

    private ImageButton btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_health_edu);

        progressBar = findViewById(R.id.progressBar_healthEdu);

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
        userReference = FirebaseDatabase.getInstance().getReference("health_edu_info");

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Health_Edu(this, list);

        //Setting recycler view
        recyclerView = findViewById(R.id.health_edu_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(myAdapter);

        //Retrieved Health Education Info from firebase RTDB
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Retrieve health education data from firebase (title, content)
                    Class_Health_Edu healthEdu_info = dataSnapshot.getValue(Class_Health_Edu.class);

                    //Retrieve the Key of health education logo (logoKey)
                    healthEdu_info.logoKey = dataSnapshot.getKey();

                    //Store retrieved data to array list
                    list.add(healthEdu_info);
                }
                myAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_health_edu.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}