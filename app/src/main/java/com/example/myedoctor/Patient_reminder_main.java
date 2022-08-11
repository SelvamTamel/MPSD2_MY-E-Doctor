package com.example.myedoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

public class Patient_reminder_main extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnAdd, btnBack;

    private TextView noReminder;
    RecyclerView recyclerView;
    DatabaseReference userReference;
    RecyclerAdapter_Reminder myAdapter;
    ArrayList<Class_Reminder> list;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_reminder_main);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnAdd = findViewById(R.id.btn_add_reminder);
        btnAdd.setOnClickListener(this);

        showMyReminder();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_add_reminder:
                startActivity(new Intent(Patient_reminder_main.this, Patient_reminder.class));
                break;
        }
    }

    private void showMyReminder() {
        noReminder = findViewById(R.id.tv_no_reminder);

        //Initiate array list and adapter
        list = new ArrayList<>();
        myAdapter = new RecyclerAdapter_Reminder(this, list);

        //Setting recycler view
        recyclerView = findViewById(R.id.reminder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        //Locate user's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        userReference = FirebaseDatabase.getInstance().getReference("patients");
        userReference.child(userId).child("medicine_reminder").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Class_Reminder reminder_info = dataSnapshot.getValue(Class_Reminder.class);

                    list.add(reminder_info);
                }
                myAdapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    noReminder.setVisibility(View.VISIBLE);
                    noReminder.setText("No Reminder");
                } else {
                    noReminder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_reminder_main.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }
}