package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Doctor_profile extends AppCompatActivity implements View.OnClickListener{

    //Profile image uri
    private Uri imageUri = null;

    private String bio;

    //Firebase
    private FirebaseUser user;
    private DatabaseReference userReference;
    private FirebaseAuth auth;
    private StorageReference storageReference, profileImgRef, medicalLicenseRef, relevantCertsRef;

    private String userId;
    private Button signOut, update;
    private TextView editProfileImg, medicalLicenseTextView, relevantCertsTextView;
    private TextView fullNameTextView, emailTextView, phoneNumberTextView, specialiseTextView;
    private EditText bioEditText;
    private ImageView profileImage;
    private ImageButton btnBack;

    private String medicalLicenseUrl, relevantCertsUrl;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        profileImage = findViewById(R.id.doctor_profileImg);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        editProfileImg = findViewById(R.id.doctor_editProfileImg);
        editProfileImg.setOnClickListener(this);

        signOut = findViewById(R.id.btn_doctor_signOut);
        signOut.setOnClickListener(this);

        update = findViewById(R.id.btn_doctor_update);
        update.setOnClickListener(this);

        fullNameTextView = findViewById(R.id.doctor_profileName);
        emailTextView = findViewById(R.id.doctor_email);
        phoneNumberTextView = findViewById(R.id.doctor_phone);
        specialiseTextView = findViewById(R.id.doctor_specialise);
        bioEditText = findViewById(R.id.doctor_bio);

        medicalLicenseTextView = findViewById(R.id.doctor_medical_license);
        medicalLicenseTextView.setOnClickListener(this);
        medicalLicenseTextView.setMovementMethod(new ScrollingMovementMethod());

        relevantCertsTextView = findViewById(R.id.doctor_relevant_certs);
        relevantCertsTextView.setOnClickListener(this);
        relevantCertsTextView.setMovementMethod(new ScrollingMovementMethod());

        //Locate doctor's credential location at Firebase Real-Time DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("doctors");
        userId = user.getUid();

        //Locate doctor's file location at Firebase Cloud Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve doctor's profile image from Cloud Storage
        profileImgRef = storageReference.child("doctors/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        //Retrieve doctor's medical license from Cloud Storage
        medicalLicenseRef = storageReference.child("doctors/"+auth.getCurrentUser().getUid()+"/medical_license");
        medicalLicenseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                //Convert to String and display
                medicalLicenseUrl = uri.toString();
                medicalLicenseTextView.setText(medicalLicenseUrl);
            }
        });

        //Retrieve doctor's relevant certificates from Cloud Storage
        relevantCertsRef = storageReference.child("doctors/"+auth.getCurrentUser().getUid()+"/relevant_certificates");
        relevantCertsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                //Convert to String and display
                relevantCertsUrl = uri.toString();
                relevantCertsTextView.setText(uri.toString());
            }
        });

        //Retrieve doctor's credentials from Real-Time DB
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Doctor classDoctorProfile = snapshot.getValue(Class_Doctor.class);

                if(classDoctorProfile != null){
                    String email = classDoctorProfile.email;
                    String fullName = classDoctorProfile.fullName;
                    String phoneNumber = classDoctorProfile.phoneNumber;
                    String specialise = classDoctorProfile.specialise;
                    String bio = classDoctorProfile.bio;

                    fullNameTextView.setText("Dr. " + fullName);
                    emailTextView.setText(email);
                    phoneNumberTextView.setText(phoneNumber);
                    specialiseTextView.setText(specialise);
                    bioEditText.setText(bio);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Doctor_profile.this, "Something Went Wrong.", Toast.LENGTH_LONG).show();
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
            case R.id.doctor_medical_license:
                medicalLicenseTextView.requestFocusFromTouch();
                showAlertDialog(v,"Are you sure you want to download your medical license?", medicalLicenseUrl);
                break;
            case R.id.doctor_relevant_certs:
                relevantCertsTextView.requestFocusFromTouch();
                showAlertDialog(v,"Are you sure you want to download your relevant certificates?", relevantCertsUrl);
                break;
            case R.id.doctor_editProfileImg:
                editProfileImg.requestFocusFromTouch();
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startForResult.launch(openGalleryIntent);
                break;
            case R.id.btn_doctor_update:
                update();
                break;
            case R.id.btn_doctor_signOut:
                auth.signOut();
                startActivity(new Intent(Doctor_profile.this, Main_SignIn.class));
                break;
        }
    }

    //Newer way to use activity for result
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                if (result.getResultCode() == RESULT_OK) {
                    imageUri = result.getData().getData();
                    profileImage.setImageURI(imageUri);
                }
            }else {
                Toast.makeText(Doctor_profile.this, "No Changes Has Been Made", Toast.LENGTH_LONG).show();
            }
        }
    });

    public void downloadFile(Context context, String fileName, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        downloadManager.enqueue(request);
    }

    public void showAlertDialog(View v, String message, String downloadUrl){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("My E-Class_Doctor");
        alert.setMessage(message);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                downloadFile(Doctor_profile.this,"Download", Environment.DIRECTORY_DOWNLOADS, downloadUrl);
                Toast.makeText(Doctor_profile.this, "Downloading File.", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Toast.makeText(Doctor_profile.this, "No Action has been made.", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }

    private void uploadImageToFirebase(Uri imageUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading profile image...");
        progressDialog.setProgress(0);
        progressDialog.show();

        //Upload image to firebase storage
        final StorageReference fileRef = storageReference.child("doctors/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(Doctor_profile.this,"Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Doctor_profile.this, "Failed to Upload Image.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    private void update(){
        if (imageUri != null){
            uploadImageToFirebase(imageUri);
        }

        //Get doctor input for doctor bio
        bio = bioEditText.getText().toString();

        //Add New Doctor information to Firebase Real-Time DB
        FirebaseDatabase.getInstance().getReference("doctors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bio")
                .setValue(bio).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Doctor_profile.this, "Doctor bio updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Doctor_profile.this, "Failed to update doctor bio.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}