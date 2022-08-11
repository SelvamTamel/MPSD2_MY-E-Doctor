package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Doctor_signup_2 extends AppCompatActivity implements View.OnClickListener {

    //Variables to store data received from Sign Up Doctor 1
    String doctorEmail, doctorFullName, doctorPhoneNumber, doctorPassword;

    //Variables to store data in Sign Up Doctor 2
    String doctorSpecialise;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseUser registeredUser;

    //Sign Up Class_Doctor PG 2 Variables
    private TextView aldHaveAcc1, uploadTracker_medical_license, uploadTracker_relevant_certs, title_medical_license, title_relevant_certs;
    private EditText editTextSpecialise;
    private ImageButton add_medical_license, dlt_medical_license, add_relevant_certs, dlt_relevant_certs, btnBack;
    private CheckBox consentCheck;
    private Button completeRegister;
    private Uri uri_medicalLicense, uri_relevantCerts;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_sign_up_2);

        //Capture and store data from Sign Up Class_Doctor 1
        doctorEmail = getIntent().getStringExtra("email");
        doctorFullName = getIntent().getStringExtra("name");
        doctorPhoneNumber = getIntent().getStringExtra("phoneNumber");
        doctorPassword = getIntent().getStringExtra("password");

        //Get Firebase Reference
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Edit Texts
        editTextSpecialise = findViewById(R.id.input_specialise);

        //Other Variables
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        title_medical_license = findViewById(R.id.title_medical_license);
        title_relevant_certs = findViewById(R.id.title_relevant_certs);

        aldHaveAcc1 = findViewById(R.id.txt_ald_have_acc_1);
        aldHaveAcc1.setOnClickListener(this);

        uploadTracker_medical_license = findViewById(R.id.uploadTracker_medical_license);
        uploadTracker_medical_license.setMovementMethod(new ScrollingMovementMethod());

        uploadTracker_relevant_certs = findViewById(R.id.uploadTracker_relevant_certs);
        uploadTracker_relevant_certs.setMovementMethod(new ScrollingMovementMethod());

        add_medical_license = findViewById(R.id.btn_add_medical_license);
        add_medical_license.setOnClickListener(this);

        add_relevant_certs = findViewById(R.id.btn_add_relevant_certs);
        add_relevant_certs.setOnClickListener(this);

        dlt_medical_license = findViewById(R.id.btn_dlt_medical_license);
        dlt_medical_license.setOnClickListener(this);

        dlt_relevant_certs = findViewById(R.id.btn_dlt_relevant_certs);
        dlt_relevant_certs.setOnClickListener(this);

        consentCheck = findViewById(R.id.checkbox_consent);
        consentCheck.setOnClickListener(this);

        completeRegister = findViewById(R.id.btn_complete_register);
        completeRegister.setOnClickListener(this);

        //Progress Bar
        progressBar = findViewById(R.id.progressBar);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_add_medical_license:
                //Select file from internal storage (medical license)
                Intent getMedicalLicenseIntent = new Intent();
                getMedicalLicenseIntent.setType("*/*");
                getMedicalLicenseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startForMedicalLicenseResult.launch(getMedicalLicenseIntent);
                break;
            case R.id.btn_add_relevant_certs:
                //Select file from internal storage (relevant certificates)
                Intent getRelevantCertsIntent = new Intent();
                getRelevantCertsIntent.setType("*/*");
                getRelevantCertsIntent.setAction(Intent.ACTION_GET_CONTENT);
                startForRelevantCertsResult.launch(getRelevantCertsIntent);
                break;
            case R.id.btn_dlt_medical_license:
                deleteFile(1);
                break;
            case R.id.btn_dlt_relevant_certs:
                deleteFile(2);
                break;
            case R.id.btn_complete_register:
                if (!checkInput())break;
                if (!consentCheck.isChecked()) {
                    Toast.makeText(Doctor_signup_2.this, "Please ensure your information is correct before register.", Toast.LENGTH_SHORT).show();
                    consentCheck.requestFocusFromTouch();
                } else {
                    registerUser();
                    Toast.makeText(Doctor_signup_2.this, "Registering Doctor.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Older way startActivityForResult is deprecated
    //Below is newer way to use activity for result
    //Result for selecting Medical License
    ActivityResultLauncher<Intent> startForMedicalLicenseResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    uri_medicalLicense = data.getData();
                    Toast.makeText(Doctor_signup_2.this, data.getData().getLastPathSegment() + " has been selected.", Toast.LENGTH_SHORT).show();
                    uploadTracker_medical_license.setText(data.getData().getLastPathSegment());
                } else {
                    Toast.makeText(Doctor_signup_2.this, "No file has been selected.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Doctor_signup_2.this, "No file has been selected.", Toast.LENGTH_SHORT).show();
            }
        }
    });

    //Result for selecting Relevant Certificates
    ActivityResultLauncher<Intent> startForRelevantCertsResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    uri_relevantCerts = data.getData();
                    Toast.makeText(Doctor_signup_2.this, data.getData().getLastPathSegment() + " has been selected.", Toast.LENGTH_SHORT).show();
                    uploadTracker_relevant_certs.setText(data.getData().getLastPathSegment());
                } else {
                    Toast.makeText(Doctor_signup_2.this, "No file has been selected.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Doctor_signup_2.this, "No file has been selected.", Toast.LENGTH_SHORT).show();
            }
        }
    });

    //Method to delete uploaded files
    @SuppressLint("SetTextI18n")
    private void deleteFile(int file) {

        if (file == 1) { // Delete Uploaded Medical License
            if (uri_medicalLicense == null) {
                Toast.makeText(Doctor_signup_2.this, "No file to be deleted.", Toast.LENGTH_SHORT).show();
            } else {
                uri_medicalLicense = null;
                Toast.makeText(Doctor_signup_2.this, uploadTracker_medical_license.getText() + " has been removed.", Toast.LENGTH_SHORT).show();
                uploadTracker_medical_license.setText("No File is selected");
            }
        } else { // Delete Uploaded Relevant Certificates
            if (uri_relevantCerts == null) {
                Toast.makeText(Doctor_signup_2.this, "No file to be deleted.", Toast.LENGTH_SHORT).show();
            } else {
                uri_relevantCerts = null;
                Toast.makeText(Doctor_signup_2.this, uploadTracker_relevant_certs.getText() + " has been removed.", Toast.LENGTH_SHORT).show();
                uploadTracker_relevant_certs.setText("No File is selected");
            }
        }
    }

    //Check if all data is entered
    private boolean checkInput() {
        boolean check = false;
        doctorSpecialise = editTextSpecialise.getText().toString();

        if (doctorSpecialise.isEmpty()) {
            editTextSpecialise.setError("Specialise is Required.");
            editTextSpecialise.requestFocus();
        } else if (uri_medicalLicense == null || uri_medicalLicense.toString().isEmpty()) {
            add_medical_license.requestFocusFromTouch();
            Toast.makeText(Doctor_signup_2.this, "You must upload Medical License to Register Doctor Account.", Toast.LENGTH_SHORT).show();
        }else{
            check = true;
        }
        return check;
    }

    //Method to create and register user in Firebase Real-Time DB
    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        //Create user in Firebase using Email & Password
        mAuth.createUserWithEmailAndPassword(doctorEmail, doctorPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Upload file medical license to firebase cloud storage
                            final StorageReference fileRef_medicalLicense = storageReference.child("doctors/" + mAuth.getCurrentUser().getUid() + "/medical_license");
                            fileRef_medicalLicense.putFile(uri_medicalLicense).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(Doctor_signup_2.this, "Medical License Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Doctor_signup_2.this, "Failed to upload Medical License.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //Upload file relevant certificates to firebase cloud storage
                            if (uri_relevantCerts != null) {
                                final StorageReference fileRef_relevantCerts = storageReference.child("doctors/" + mAuth.getCurrentUser().getUid() + "/relevant_certificates");
                                fileRef_relevantCerts.putFile(uri_relevantCerts).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(Doctor_signup_2.this, "Relevant Certificates Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Doctor_signup_2.this, "Failed to upload Relevant Certificates.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            //Create an instance of classDoctor
                            Class_Doctor classDoctor = new Class_Doctor(doctorEmail, doctorFullName, doctorPhoneNumber, doctorSpecialise, "Bio is empty.");

                            //Add New Doctor information to Firebase Real-Time DB
                            FirebaseDatabase.getInstance().getReference("doctors")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(classDoctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Send Email Verification after registering
                                                registeredUser = FirebaseAuth.getInstance().getCurrentUser();
                                                registeredUser.sendEmailVerification();

                                                Toast.makeText(Doctor_signup_2.this, "Doctor Registered successfully! Check Email for Account Verification.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                //Finish current activity and Redirect to Sign In Layout
                                                finish();
                                                startActivity(new Intent(Doctor_signup_2.this, Doctor_signin.class));
                                            } else {
                                                Toast.makeText(Doctor_signup_2.this, "Failed to Register Doctor. Try Again.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Doctor_signup_2.this, "This email address has been used. Please choose another one.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}