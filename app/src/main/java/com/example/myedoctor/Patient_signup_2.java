package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Patient_signup_2 extends AppCompatActivity implements View.OnClickListener {

    //Variables to store data received from Sign Up Class_Patient Page 1
    String patientEmail, patientFullName, patientPhoneNumber, patientPassword;

    //Variables to store data in Sign Up Class_Patient Page 2
    String patientGender, patientDOB, patientAddress;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseUser registeredUser;

    //Sign Up Class_Doctor PG 2 Variables
    private TextView aldHaveAcc1, titleGender;
    private CheckBox consentCheck;
    private Button completeRegister;
    private ProgressBar progressBar;
    private EditText editTextAddress;
    private TextView textViewDOB;
    private Spinner genderSpinner;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_signup_2);

        //Store data received from Sign Up Class_Patient Page 1
        patientEmail = getIntent().getStringExtra("email");
        patientFullName = getIntent().getStringExtra("name");
        patientPhoneNumber = getIntent().getStringExtra("phoneNumber");
        patientPassword = getIntent().getStringExtra("password");

        //Get Firebase Reference
        mAuth = FirebaseAuth.getInstance();

        //Edit Texts / Spinner / Date Picker
        textViewDOB = findViewById(R.id.input_dob);
        textViewDOB.setOnClickListener(this);

        genderSpinner = findViewById(R.id.input_gender);

        editTextAddress = findViewById(R.id.input_address);
        editTextAddress.setOnClickListener(this);


        //Other Variables
        titleGender = findViewById(R.id.title_gender);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        aldHaveAcc1 = findViewById(R.id.txt_ald_have_acc_1);
        aldHaveAcc1.setOnClickListener(this);

        consentCheck = findViewById(R.id.checkbox_consent);
        consentCheck.setOnClickListener(this);

        completeRegister = findViewById(R.id.btn_complete_register);
        completeRegister.setOnClickListener(this);

        //Progress Bar
        progressBar = findViewById(R.id.progressBar);

        //Initialize gender drop down list
        genderDropdownList();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.input_dob:
                selectDOB();
                break;
            case R.id.btn_complete_register:
                if (!checkInput()) break;
                if (!consentCheck.isChecked()) {
                    Toast.makeText(Patient_signup_2.this, "Please ensure your information is correct before register.", Toast.LENGTH_SHORT).show();
                    consentCheck.requestFocusFromTouch();
                } else {
                    registerUser();
                    Toast.makeText(Patient_signup_2.this, "Registering Patient.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Method to display drop down list (spinner) and select Gender
    private void genderDropdownList() {
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
        adapter.add("Male");
        adapter.add("Female");
        adapter.add("Prefer not to say");
        adapter.add("Select your gender"); // This is hint

        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(adapter.getCount()); // Dislpay Hint

        // Store the patientGender value
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                patientGender = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Patient_signup_2.this, "Nothing has been selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to select Date of Birth
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectDOB() {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Patient_signup_2.this,
                android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        Log.d("Patient_signup_2", "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                        String date = day + "/" + month + "/" + year;
                        textViewDOB.setText(date);

                        // Store the patientDOB value
                        patientDOB = date;
                    }
                },
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.updateDate(year, month, day);
        dialog.show();
    }

    private boolean checkInput() {
        boolean check = false;

        //Get user input of Address
        patientAddress = editTextAddress.getText().toString();

        if (patientGender.isEmpty()) {
            titleGender.setError("Gender is Required.");
            genderSpinner.requestFocus();
        } else if (patientDOB.isEmpty()) {
            textViewDOB.setError("Date of Birth is Required.");
            textViewDOB.requestFocus();
        } else if (patientAddress.isEmpty()) {
            editTextAddress.setError("Address is Required.");
            editTextAddress.requestFocus();
        } else {
            check = true;
        }
        return check;
    }

    //Method to create and register user in Firebase Real-Time DB
    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        //Gender is received from user under selectGender() method
        //DOB is received from user under selectDOB() method

        //Create user in Firebase using Email & Password
        mAuth.createUserWithEmailAndPassword(patientEmail, patientPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Create an instance of classPatient
                            Class_Patient classPatient = new Class_Patient(patientEmail, patientFullName, patientPhoneNumber, patientGender, patientDOB, patientAddress);

                            //Add New Patient information to Firebase Real-Time DB
                            FirebaseDatabase.getInstance().getReference("patients")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(classPatient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Send Email Verification after registering
                                                registeredUser = FirebaseAuth.getInstance().getCurrentUser();
                                                registeredUser.sendEmailVerification();

                                                Toast.makeText(Patient_signup_2.this, "Patient Registered Successfully! Check Email for Verification.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                //Finish current activity and Redirect to Sign In Layout
                                                finish();
                                                startActivity(new Intent(Patient_signup_2.this, Patient_signin.class));
                                            } else {
                                                Toast.makeText(Patient_signup_2.this, "Failed to Register Patient. Try Again.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Patient_signup_2.this, "This email address has been used. Please choose another one.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}