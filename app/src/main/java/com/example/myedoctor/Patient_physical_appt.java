package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Patient_physical_appt extends AppCompatActivity implements View.OnClickListener{

    private Button btnSubmit;
    private ImageButton btnBack;
    private EditText symptoms;
    private Spinner symptomsPeriod;
    private ProgressBar progressBar;

    private String patientSymptoms, patientSymptomsPeriod;

    //Variables to set appointment
    private String patientId, patientName, patientGender, patientDOB, patientPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_physical_appt_form);

        progressBar = findViewById(R.id.progressBar);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        symptoms = findViewById(R.id.input_physicalApt_symptoms);
        symptomsPeriod = findViewById(R.id.input_physicalApt_symptomsPeriod);

        //Set up drop down list for Symptoms Period
        symptomsPeriodDropdownList();

        //Locate doctor's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("patients");
        patientId = user.getUid();

        //Retrieve user's credentials from Real-Time DB
        userReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Patient classPatientProfile = snapshot.getValue(Class_Patient.class);

                if(classPatientProfile != null){
                    patientName = classPatientProfile.fullName;
                    patientPhone = classPatientProfile.phoneNumber;
                    patientGender = classPatientProfile.gender;
                    patientDOB = classPatientProfile.dob;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_physical_appt.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_submit:
                if (checkInput()){
                    setAppointment();
                }
                break;
        }
    }

    private boolean checkInput(){
        boolean check = false;

        patientSymptoms = symptoms.getText().toString();

        if (patientSymptoms.isEmpty()){
            symptoms.setError("Symptoms field is required.");
            symptoms.requestFocus();
        }else if (patientSymptomsPeriod.equals("Select a period")){
            Toast.makeText(Patient_physical_appt.this, "You must select symptoms period.", Toast.LENGTH_SHORT).show();
            symptomsPeriod.requestFocusFromTouch();
        }else {
            check = true;
        }
        return check;
    }

    //Method to display drop down list (spinner) for Symptoms Period
    private void symptomsPeriodDropdownList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // Don't display the last item. It is used as hint.
            }
        };
        // Display the drop down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("1 Day");
        adapter.add("3 Days");
        adapter.add("5 Days");
        adapter.add("More than 1 Week");
        adapter.add("Select a period"); // This is hint

        symptomsPeriod.setAdapter(adapter);
        symptomsPeriod.setSelection(adapter.getCount()); // Dislpay Hint

        // Store the patientGender value
        symptomsPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                patientSymptomsPeriod = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Patient_physical_appt.this, "Nothing has been selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAppointment(){
        progressBar.setVisibility(View.VISIBLE);

        Class_Appt appointmentDetail = new Class_Appt(-1, System.currentTimeMillis(), "", patientId, patientName, patientPhone,
                patientGender, patientDOB, patientSymptoms, patientSymptomsPeriod, "", "", 1, 1);

        //Add New Appointment information to Firebase Real-Time DB
        FirebaseDatabase.getInstance().getReference("physical_apt")
                .push()
                .setValue(appointmentDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Patient_physical_appt.this, "Appointment is set! Please Sit for a moment.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            startActivity(new Intent(Patient_physical_appt.this, Patient_dashboard.class));
                        } else {
                            Toast.makeText(Patient_physical_appt.this, "Failed to set appointment. Please Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}