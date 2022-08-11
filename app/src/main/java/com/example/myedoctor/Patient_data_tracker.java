
package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Patient_data_tracker extends AppCompatActivity implements View.OnClickListener {

    TextView bloodSugardata, bloodPressuredata, heartRatedata, cholesterolData, tvDate;
    Button btnCheck;
    ImageButton btnBack;

    FirebaseDatabase rootNode;
    DatabaseReference dataReference;

    String date;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_data_tracker);

        tvDate = findViewById(R.id.tv_date);
        tvDate.setOnClickListener(this);

        btnCheck = findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(this);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        bloodSugardata = findViewById(R.id.tv_bloodSugardata);
        bloodPressuredata = findViewById(R.id.tv_bloodPressuredata);
        heartRatedata = findViewById(R.id.tv_heartRatedata);
        cholesterolData = findViewById(R.id.tv_cholesterolData);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_check:
                checkData();
                break;
            case R.id.tv_date:
                pickDate();
                break;
        }
    }

    public void checkData() {
        //Locate user's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        //Locate user's credential location at Firebase Real-Time DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        dataReference = FirebaseDatabase.getInstance().getReference("patients");
        userId = user.getUid();

        dataReference.child(userId).child("health_data_record").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Class_Health_Data classHealthData = snapshot.getValue(Class_Health_Data.class);

                if (classHealthData != null) {
                    String bloodSugar = classHealthData.bloodSugar;
                    String bloodPressure = classHealthData.bloodPressure;
                    String cholesterol = classHealthData.cholesterol;
                    String heartRate = classHealthData.heartRate;

                    bloodSugardata.setText(bloodSugar);
                    bloodPressuredata.setText(bloodPressure);
                    cholesterolData.setText(cholesterol);
                    heartRatedata.setText(heartRate);
                } else {
                    alert("Date does not exits");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void pickDate() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Patient_data_tracker.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                if (month <= 9) {
                    date = day + "-" + 0 + month + "-" + year;
                } else {
                    date = day + "-" + month + "-" + year;
                }
                tvDate.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void alert(String message) {
        AlertDialog dlg = new AlertDialog.Builder(Patient_data_tracker.this)
                .setTitle("Date does not have record in firebase")
                .setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }
}